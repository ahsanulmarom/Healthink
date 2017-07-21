package com.healthink.user.healthink;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kontak extends AppCompatActivity {

    EditText cari;
    ImageButton btncari;
    ListView lv;
    Adapter adapter, adapter2;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;
    private static final String TAG = Kontak.class.getSimpleName();
    private Context context = this;
    CheckNetwork cn;
    List<HashMap<String, Object>> fillMaps = new ArrayList<>();
    List<HashMap<String, Object>> fillMaps2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontaklistview);

        lv = (ListView) findViewById(R.id.kontak_listView);
        cari = (EditText) findViewById(R.id.kontak_search);
        btncari = (ImageButton) findViewById(R.id.kontak_btnSearch);

        cn = new CheckNetwork(this);
        if (!cn.isConnected()) {
            Toast.makeText(this, "You are not connected internet. Pease check your connection!", Toast.LENGTH_LONG).show();
        }
        fAuth = FirebaseAuth.getInstance();
        fStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User sedang login
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    getFriend();
                    btncari.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            hideSoftKeyboard(Kontak.this);
                            getFriend(cari.getText().toString().trim());
                        }
                    });
                } else {
                    // User sedang logout
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(Kontak.this, Into.class));
                }
            }
        };

    }

    public void getFriend() {
        fillMaps.clear();
        final FirebaseUser user = fAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference friend = database.getReference("friendList");
        friend.child(user.getUid()).orderByChild("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: testref " + dataSnapshot.getRef());
                Log.e(TAG, "onDataChange: testval " + dataSnapshot.getValue());
                if (dataSnapshot.getValue() == null) {
                    nullFriend();
                } else {
                    for (final DataSnapshot snap : dataSnapshot.getChildren()) {
                        Log.e(TAG, "onDataChange: testid " +  snap.child("id").getValue(String.class));
                        final String id = snap.child("id").getValue(String.class);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference data = database.getReference("userData");
                        data.child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final Map map = new HashMap();
                                map.put("id", user.getUid());
                                map.put("name", dataSnapshot.child("displayName").getValue(String.class));
                                map.put("bio", dataSnapshot.child("bio").getValue(String.class));

                                FirebaseStorage mStorageRef = FirebaseStorage.getInstance();
                                StorageReference storageReference = mStorageRef.getReference("photoProfile");
                                Log.e(TAG, "onDataChange: wkwkwkwkwkwk");
                                storageReference.child(id + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        map.put("pict", uri);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        map.put("pict", R.drawable.ic_dp_web);
                                    }
                                });

                                if (dataSnapshot.child("role").getValue(int.class).equals(1)) {
                                    map.put("badge", R.drawable.logo);
                                } else {
                                    map.put("badge", null);
                                }
                                fillMaps.add((HashMap) map);
                                adapter = new SimpleAdapter(getBaseContext(), fillMaps, R.layout.activity_kontak,
                                        new String[]{"name", "bio", "pict", "badge"},
                                        new int[]{R.id.kontak_displayName, R.id.kontak_bio, R.id.kontak_pictUser, R.id.kontak_badge});
                                lv.setAdapter((ListAdapter) adapter);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void getFriend(String username) {
        fillMaps2.clear();
            final FirebaseUser user = fAuth.getCurrentUser();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference friend = database.getReference("friendList");
            friend.child(user.getUid()).orderByChild("username").equalTo(username).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        getFriend();
                    } else {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            final String id = snap.child("id").getValue(String.class);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference data = database.getReference("userData");
                            data.child(id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final Map map = new HashMap();
                                    map.put("id", user.getUid());
                                    map.put("name", dataSnapshot.child("displayName").getValue(String.class));
                                    map.put("bio", dataSnapshot.child("bio").getValue(String.class));
                                    FirebaseStorage mStorageRef = FirebaseStorage.getInstance();
                                    StorageReference storageReference = mStorageRef.getReference("photoProfile");
                                    storageReference.child(id + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            map.put("pict", Glide.with(Kontak.this).load(uri));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            map.put("pict", R.drawable.ic_dp_web);
                                        }
                                    });
                                    if (dataSnapshot.child("role").getValue(int.class).equals(1)) {
                                        map.put("badge", R.drawable.logo);
                                    } else {
                                        map.put("badge", null);
                                    }
                                    fillMaps2.add((HashMap) map);
                                    adapter2 = new SimpleAdapter(getBaseContext(), fillMaps2, R.layout.activity_kontak,
                                            new String[]{"name", "bio", "pict", "badge"},
                                            new int[]{R.id.kontak_displayName, R.id.kontak_bio, R.id.kontak_pictUser, R.id.kontak_badge});
                                    lv.setAdapter((ListAdapter) adapter2);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }

    public void nullFriend() {
        LinearLayout layoutinput = new LinearLayout(context);   //layout
        layoutinput.setOrientation(LinearLayout.VERTICAL);
        layoutinput.setPadding(50, 50, 50, 50);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Friendlist");
        builder.setMessage("Sorry, you don't have a friend yet. You can add friend by click Add Friend below.");
        builder.setView(layoutinput);
        //negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        //posstive button
        builder.setPositiveButton("Add Friend now", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Kontak.this, Add_friend.class));
            }
        });
        builder.show();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fAuth.addAuthStateListener(fStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (fStateListener != null) {
            fAuth.removeAuthStateListener(fStateListener);
        }
    }
}

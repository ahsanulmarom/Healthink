package com.healthink.user.healthink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Add_friend extends AppCompatActivity {

    private EditText search;
    private ImageButton addFr, btnsearch;
    private TextView name, bioUser;
    private ImageView pictUser;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;
    private static final String TAG = Add_friend.class.getSimpleName();
    CheckNetwork cn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        search = (EditText) findViewById(R.id.addFr_search);
        btnsearch = (ImageButton) findViewById(R.id.addFr_btnSearch);
        addFr = (ImageButton) findViewById(R.id.addFr_btnAddFr);
        name = (TextView) findViewById(R.id.addFr_displayName);
        bioUser = (TextView) findViewById(R.id.addFr_bio);
        pictUser = (ImageView) findViewById(R.id.addFr_userPict);

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
                    btnsearch.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            hideSoftKeyboard(Add_friend.this);
                            getListUser(search.getText().toString().trim());
                        }
                    });
                } else {
                    // User sedang logout
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(Add_friend.this, Into.class));
                }
            }
        };
    }

    public void getListUser(final String username) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference user = database.getReference("userData");
        user.orderByChild("username").equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(Add_friend.this, "Username can't be found", Toast.LENGTH_SHORT).show();
                    name.setText(null);
                    bioUser.setText(null);
                    pictUser.setImageDrawable(null);
                    addFr.setImageDrawable(null);
                } else {
                    for (final DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {  //menemukan username teman
                            String key = singleSnapshot.getKey();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference detilUser = database.getReference("userData");
                            detilUser.child(key).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot dataSnapshot) {
                                    final String nama = dataSnapshot.child("displayName").getValue(String.class);
                                    String bio = dataSnapshot.child("bio").getValue(String.class);
                                    if (dataSnapshot.child("role").getValue(int.class).equals(1)) {
                                        if (this != null) {
                                            name.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.logo20),null);    //buat nandain di role 1
                                        }
                                    }
                                    name.setText(nama + "   ");
                                    bioUser.setText(bio);
                                    pictUser.setImageDrawable(getDrawable(R.drawable.logo));

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    final String key = singleSnapshot.getKey();
                                    final FirebaseUser user = fAuth.getCurrentUser();
                                    final DatabaseReference friendList = database.getReference("friendList");
                                    friendList.child(user.getUid()).orderByChild("id")
                                            .equalTo(key).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.e(TAG, "onDataChange: getref " + dataSnapshot.getValue() );
                                            if(dataSnapshot.getValue() == null) {
                                                addFr.setImageDrawable(getDrawable(R.drawable.ic_add_circle));
                                                addFr.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        friendList.child(user.getUid()).push().child("id").setValue(key);
                                                        friendList.child(user.getUid()).push().child("username").setValue(username);
                                                        Toast.makeText(Add_friend.this, nama + " is your friend, now",
                                                                Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(Add_friend.this, Kontak.class));
                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

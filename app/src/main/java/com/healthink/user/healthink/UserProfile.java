package com.healthink.user.healthink;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by user on 12/07/2017.
 */

public class UserProfile extends AppCompatActivity {

    TextView namatampil;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;
    private static final String TAG = UserProfile.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fAuth = FirebaseAuth.getInstance();
        fStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User sedang login
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    setContentView(R.layout.activity_userprofile);
                    namatampil = (TextView) findViewById(R.id.user_displayName);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference userData = database.getReference("userData");
                    userData.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserData userdata = new UserData();
                            if(dataSnapshot.child("displayName").getValue(String.class) == null ||
                                    dataSnapshot.child("displayName").getValue(String.class) == "") {
                                userdata.setDisplayName(dataSnapshot.child("username").getValue(String.class));
                            } else {
                                userdata.setDisplayName(dataSnapshot.child("displayName").getValue(String.class));
                            }
                            Log.e(TAG, "onDataChange: " + userdata.getDisplayName() );
                            namatampil.setText(userdata.getDisplayName());
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                } else {
                    // User sedang logout
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(UserProfile.this, Into.class));
                }
            }
        };
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
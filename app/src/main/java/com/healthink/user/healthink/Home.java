package com.healthink.user.healthink;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {

    TextView judul, namatampil;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;
    private static final String TAG = Home.class.getSimpleName();
    BottomNavigationView navigation;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar2);
//        myToolbar.setLogo(R.drawable.ic_close);
        setSupportActionBar(myToolbar);



        fAuth = FirebaseAuth.getInstance();
        fStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User sedang login
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    setContentView(R.layout.activity_home);
                    judul = (TextView) findViewById(R.id.judul);
                    namatampil = (TextView) findViewById(R.id.home_displayName);
                    judul.setText("Home");

                    if(savedInstanceState == null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flContent, homefragment.newInstance()).commit();

                    }

                    //homeMenu();
                    navigationMenu();
                } else {
                    // User sedang logout
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(Home.this, Into.class));
                }
            }
        };
        super.onCreate(savedInstanceState);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                // User chose the "Settings" item, show the app settings UI...
                fAuth.signOut();
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void navigationMenu() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            //memanggil salah satu menu navigation
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.navigation_home :
                        judul.setText("Home");
                        fragment = homefragment.newInstance();
                        //homeMenu();
                        break;
                    case R.id.navigation_chat:
                        fragment = chat.newInstance();
                        break;
                    case R.id.navigation_timeline:
                        fragment = timeline.newInstance();
                        break;
                    case R.id.navigation_location:
                        fragment = location.newInstance();
                        break;
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContent, fragment)
                        .commit();

                return false;
            }
        });
    }

    public void homeMenu() {
        FirebaseUser user = fAuth.getCurrentUser();
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

        namatampil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, UserProfile.class));
            }
        });
    }

    public void chatMenu() {
        namatampil.setText("");
    }

    public void timelineMenu() {
        namatampil.setText("");
    }

    public void locationMenu() {
        namatampil.setText("");
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

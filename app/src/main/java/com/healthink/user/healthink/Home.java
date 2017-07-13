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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {

    static TextView judul;
    TextView namatampil;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;
    private static final String TAG = Home.class.getSimpleName();
    BottomNavigationView navigation;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar2);
//        myToolbar.setLogo(R.drawable.ic_close);
        setSupportActionBar(myToolbar);

        setContentView(R.layout.activity_home);
        judul = (TextView) findViewById(R.id.judul);
        fAuth = FirebaseAuth.getInstance();
        fStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User sedang login
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if(savedInstanceState == null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flContent, homefragment.newInstance()).commit();
                        judul.setText("Home");
                    }
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
                fAuth.signOut();
                return true;
            default:
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
                        break;
                    case R.id.navigation_chat:
                        judul.setText("Chat");
                        fragment = chat.newInstance();
                        break;
                    case R.id.navigation_timeline:
                        judul.setText("Timeline");
                        fragment = timeline.newInstance();
                        break;
                    case R.id.navigation_location:
                        judul.setText("Location");
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

package com.healthink.user.healthink;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;

public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

    }
}

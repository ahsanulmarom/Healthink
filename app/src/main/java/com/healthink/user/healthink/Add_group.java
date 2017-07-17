package com.healthink.user.healthink;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Add_group extends AppCompatActivity {

    ImageButton createGr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        createGr = (ImageButton) findViewById(R.id.newGr);
        createGr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Add_group.this, CreateGroup.class));
                finish();
            }
        });
    }
}

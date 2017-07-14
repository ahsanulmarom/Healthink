package com.healthink.user.healthink;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText email, password;
    private Button submit, daftar, reset;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;
    private static final String TAG = SignUp.class.getSimpleName();
    private Context context = this;
    CheckNetwork cn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        cn = new CheckNetwork(this);
        if (!cn.isConnected()) {
            Toast.makeText(this, "You are not connected internet. Pease check your connection!", Toast.LENGTH_SHORT).show();
        }

        fAuth = FirebaseAuth.getInstance();
        fStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User sedang login
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else {
                        // User sedang logout
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                }
            };

            email = (EditText) findViewById(R.id.login_email);
            password = (EditText) findViewById(R.id.login_password);
            submit = (Button) findViewById(R.id.login_btnLogin);
            daftar = (Button) findViewById(R.id.login_btnSignUp);
            reset = (Button) findViewById(R.id.login_reset);

            submit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (email.getText().toString().equalsIgnoreCase("")) {
                        email.setError("This Field is Required");
                    } else if (password.getText().toString().equalsIgnoreCase("")) {
                        password.setError("This Field is Required");
                    } else {
                        login(email.getText().toString(), password.getText().toString());
                    }

                }
            });

            daftar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(Login.this, SignUp.class));
                    finish();
                }
            });

            reset.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    LinearLayout layoutinput = new LinearLayout(context);   //layout
                    layoutinput.setOrientation(LinearLayout.VERTICAL);
                    layoutinput.setPadding(50, 50, 50, 50);

                    final EditText mail = new EditText(context);
                    mail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    mail.setHint("Enter your verified Email!");
                    layoutinput.addView(mail);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Reset Password");
                    builder.setMessage("Please enter your verified and active email address below." +
                            "We will send you an email with instruction for resetting your password.");
                    builder.setView(layoutinput);
                    //negative button
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    //posstive button
                    builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (!cn.isConnected()) {
                                Toast.makeText(Login.this, "You are offline. Pease check your connection!",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                sendEmailResetPassword(mail.getText().toString().trim());
                            }
                        }
                    });
                    builder.show();
                }
            });
    }

    private void login(final String email, String password){
        fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            if (!cn.isConnected()) {
                                Toast.makeText(Login.this, "You are offline. Pease check your connection!",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Login.this, "Sorry, Your Email or Password is Incorrect. Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else if (task.isSuccessful()) {
                            checkIfEmailVerified();
                        }
                    }
                });
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified()) {
            // user is verified, so you can finish this activity or send user to activity which you want.
            startActivity(new Intent(Login.this, Home.class));
        }
        else {
            Toast.makeText(Login.this, "Sorry, Please Check Email and Verify Your Account!",
                    Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
        }
    }

    private void sendEmailResetPassword(String emailAd) {
        fAuth = FirebaseAuth.getInstance();
        fAuth.sendPasswordResetEmail(emailAd).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "The instruction has been sent to your email. Please check your email!",
                            Toast.LENGTH_SHORT).show();
                } else if(!task.isSuccessful()) {
                    Toast.makeText(Login.this, "Request failed. Pelase, try again!",
                            Toast.LENGTH_SHORT).show();
                }
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

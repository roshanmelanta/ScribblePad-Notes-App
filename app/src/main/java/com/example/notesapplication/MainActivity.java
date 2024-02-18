package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private RelativeLayout loginButton, signUp;
    private TextView switchToForgotPassword;

    // Entry point of firebase authentication SDK
    private FirebaseAuth firebaseAuth;

    ProgressBar progressBarLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.signUp);
        switchToForgotPassword = findViewById(R.id.switchToForgotPassword);
        progressBarLogin = findViewById(R.id.progressBarLogin);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null) {
            finish();
            startActivity(new Intent(MainActivity.this, NotesActivity.class)); // switch to main page
        }

        // Switch to signup activity page
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });

        // Switch to forgot password page
        switchToForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        // Login Function
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                    System.out.println("Inside if block");
                }
                else {
                    // Login the user here
                    progressBarLogin.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                checkEmailVerification();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Account does not exist", Toast.LENGTH_SHORT).show();
                                progressBarLogin.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified() == true) {
            Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(MainActivity.this, NotesActivity.class));
        }
        else {
            progressBarLogin.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Kindly verify your email before logging in", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
}
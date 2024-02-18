package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    private EditText signUpEmail, signUpPassword;
    private RelativeLayout signUpButton;
    private TextView switchToLogin;
    
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide();

        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPassword = findViewById(R.id.signUpPassword);
        signUpButton = findViewById(R.id.signUpButton);
        switchToLogin = findViewById(R.id.switchToLogin);
        
        firebaseAuth = FirebaseAuth.getInstance();

        // Switch to login activity
        switchToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Register the user function
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = signUpEmail.getText().toString().trim();
                String password = signUpPassword.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
                else if(password.length() < 7) {
                    Toast.makeText(getApplicationContext(), "Password must be more than 7 digits", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Register user to firebase
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                                sendEmailVerification();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Failed to register user", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    
    // send email verification
    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Verification email sent, kindly verify your email id", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut(); // clears current user from disk cache
                    finish(); // close the current activity
                    startActivity(new Intent(SignUp.this, MainActivity.class)); // switch to login page
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Failed to send verification email", Toast.LENGTH_SHORT).show();
        }
    }
}
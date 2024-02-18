package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText mforgotPasswordField;
    private RelativeLayout mpasswordRecoverButton;
    private TextView mswitchToLogin;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().hide();

        mforgotPasswordField = findViewById(R.id.forgotPasswordField);
        mpasswordRecoverButton = findViewById(R.id.passwordRecoverButton);
        mswitchToLogin = findViewById(R.id.switchToLogin);

        firebaseAuth = FirebaseAuth.getInstance();

        // Switch to login page
        mswitchToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Recovery email function
        mpasswordRecoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mforgotPasswordField.getText().toString().trim();
                if(email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter your email id", Toast.LENGTH_SHORT).show();
                }
                else {
                    // add recover email link code here
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Recovery email sent successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Account does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
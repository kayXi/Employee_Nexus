package com.example.employee_nexus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        login = findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v){
              String eText = email.getText().toString();
              String pText = password.getText().toString();
              loginUser(eText,pText);
            }
        });
    }

    private void loginUser(String eText, String pText) {

        mAuth.signInWithEmailAndPassword(eText, pText).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Login Failed, Invalid email or password", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
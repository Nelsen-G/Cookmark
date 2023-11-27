package com.example.cookmark_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);

        Button signInButton = findViewById(R.id.buttonSignIn);
        Button registerButton = findViewById(R.id.buttonRegister);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                // Logic login , cek ke db
                //sementara ini langsung masuk ke explore
                Intent it = new Intent(Login.this, Explore.class);
                startActivity(it);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Login.this, Register.class);
                startActivity(it);
            }
        });

    }
}
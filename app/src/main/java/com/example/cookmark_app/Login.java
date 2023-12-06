package com.example.cookmark_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookmark_app.databinding.ActivityLoginBinding;
import com.example.cookmark_app.databinding.ActivityRegisterBinding;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = binding.editTextUsername.getText().toString();
                String password = binding.editTextPassword.getText().toString();

                // Logic login , cek ke db
                if(username.equals("") || password.equals("")){
                    Toast.makeText(Login.this, "Please fill the login field!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Boolean checkCredentials = databaseHelper.checkUsernamePassword(username, password);

                    if(checkCredentials == true){
                        Toast.makeText(Login.this, "Login successfully! Welcome " + username + "!", Toast.LENGTH_SHORT).show();
                        Intent it = new Intent(Login.this, MainActivity.class);
                        startActivity(it);
                    }
                    else{
                        Toast.makeText(Login.this, "Wrong username or password, please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Login.this, Register.class);
                startActivity(it);
            }
        });

    }
}
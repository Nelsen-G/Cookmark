package com.example.cookmark_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookmark_app.databinding.ActivityRegisterBinding;

public class Register extends AppCompatActivity {

    ActivityRegisterBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Sign Up button click
                String username = binding.editTextUsername.getText().toString();
                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();
                String confirmPassword = binding.editTextConfirmPassword.getText().toString();

                // validasi input
                if (username.equals("") || email.equals("") || password.equals("") || confirmPassword.equals("")) {
                    Toast.makeText(Register.this, "Please fill up your registration info", Toast.LENGTH_SHORT).show();
                } else if (password.equals(confirmPassword)) {
                    Boolean checkUserEmail = databaseHelper.checkEmail(email);
                    Boolean checkUserUsername = databaseHelper.checkUsername(username);

                    if (!emailValidation()) {
                        Toast.makeText(Register.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!checkUserEmail && !checkUserUsername) {
                            Boolean insert = databaseHelper.insertData(username, email, password);

                            if (insert) {
                                Toast.makeText(Register.this, "Register completed! Please login again", Toast.LENGTH_SHORT).show();
                                Intent it = new Intent(Register.this, Login.class);
                                startActivity(it);
                            } else {
                                Toast.makeText(Register.this, "Registration failed...", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Register.this, "Username or email has been used, please try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(Register.this, "Invalid password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Register.this, Login.class);
                startActivity(it);
            }
        });
    }

    public Boolean emailValidation() {
        String email = binding.editTextEmail.getText().toString();

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
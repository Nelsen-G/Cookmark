package com.example.cookmark_app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.cookmark_app.R;
import com.example.cookmark_app.databinding.ActivityRegisterBinding;
import com.example.cookmark_app.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(this);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading...");

        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Sign Up button click
                String username = binding.editTextUsername.getText().toString().trim();
                String email = binding.editTextEmail.getText().toString().trim();
                String password = binding.editTextPassword.getText().toString().trim();
                String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();

                // validasi input
                String message = "";
                if (username.equals("") || password.equals("") || confirmPassword.equals("")) {
                    message = "Please fill up your registration info";
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    message = "Email is invalid";
                }
                else if (!password.equals(confirmPassword)) {
                    message = "Both password you input are not match";
                }

                if(message != ""){
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                else{
                    //check if email is already used
                    db.collection("users")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().size() > 0) { //email already used
                                            Toast.makeText(getApplicationContext(), "Please used another email", Toast.LENGTH_SHORT).show();
                                        }
                                        else{ //email available, add new user to firebase
                                            addNewUser(username, email, password);
                                        }
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(it);
            }
        });
    }

    public void addNewUser(String username, String email, String password) {
        progressDialog.show();

        db.collection("users")
                .whereEqualTo("user_name", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                Toast.makeText(getApplicationContext(), "Username already in use. Please choose a different one.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                String userId = UUID.randomUUID().toString();
                                User newUser = new User(userId, username, email, password);
                                db.collection("users")
                                        .add(newUser)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                progressDialog.dismiss();
                                                Toast.makeText(RegisterActivity.this, "Registration successful. Please log in.", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

}
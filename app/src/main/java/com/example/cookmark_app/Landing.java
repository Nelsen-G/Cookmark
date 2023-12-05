package com.example.cookmark_app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;

public class Landing extends AppCompatActivity {
    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        TextView logInButton = findViewById(R.id.buttonLogIn);
        logInButton.setOnClickListener(v -> {
            Intent it = new Intent(Landing.this, Login.class);
            startActivity(it);
        });

        Button registerButton = findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(v -> {
            Intent it = new Intent(Landing.this, Register.class);
            startActivity(it);
        });

        Button signInGoogleButton = findViewById(R.id.buttonSignInGoogle);
        ActivityResultLauncher<IntentSenderRequest> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
            if(result.getResultCode() == Activity.RESULT_OK){
                try{
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                    String idToken = credential.getGoogleIdToken();
                    if(idToken != null){
                        String email = credential.getId();
                        Toast.makeText(getApplicationContext(), "Email: " + email, Toast.LENGTH_SHORT).show();
                    }
                }catch(ApiException e){
                    e.printStackTrace();
                }
            }
        });
        signInGoogleButton.setOnClickListener(v -> oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(Landing.this, result -> {
                    IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                    activityResultLauncher.launch(intentSenderRequest);
                })
                .addOnFailureListener(Landing.this, e -> Log.d("TAG", e.getLocalizedMessage())));

        oneTapClient = Identity.getSignInClient(this);
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

    }
}
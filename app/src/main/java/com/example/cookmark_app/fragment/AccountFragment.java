package com.example.cookmark_app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.cookmark_app.About;
import com.example.cookmark_app.Landing;
import com.example.cookmark_app.ManageRecipe;
import com.example.cookmark_app.R;
import com.example.cookmark_app.dialog.EditNameDialog;
import com.example.cookmark_app.dialog.EditPasswordDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class AccountFragment extends Fragment implements EditNameDialog.OnUsernameUpdatedListener {
    private String userId;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    View view;
    TextView userNameTxt, userEmailTxt, profileSettingsTxt;
    ImageView profilePictureImageView;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString("user_id");
        }

        profileSettingsTxt = view.findViewById(R.id.profileSettings);
        userNameTxt = view.findViewById(R.id.userName);
        userEmailTxt = view.findViewById(R.id.userEmail);
        profilePictureImageView = view.findViewById(R.id.profilePicture);

        Button editPic = view.findViewById(R.id.editProfilePicture);
        Button editUsername = view.findViewById(R.id.editUsername);
        Button editPassword = view.findViewById(R.id.editPassword);

        editPic.setVisibility(View.GONE);
        editUsername.setVisibility(View.GONE);
        editPassword.setVisibility(View.GONE);
        profileSettingsTxt.setVisibility(View.GONE);

        if (currentUser != null) {
            userNameTxt.setText(currentUser.getDisplayName());
            userEmailTxt.setText(currentUser.getEmail());
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(requireActivity())
                        .load(currentUser.getPhotoUrl())
                        .into(profilePictureImageView);
            }
        } else {
            retrieveUserInfoFromFirestore(userId, profilePictureImageView);
            editPic.setVisibility(View.VISIBLE);
            editUsername.setVisibility(View.VISIBLE);
            editPassword.setVisibility(View.VISIBLE);
            profileSettingsTxt.setVisibility(View.VISIBLE);

            editPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null){
                        openGallery();
                    }
                }
            });

            editUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null){
                        openEditNameDialog(userId);
                    }
                }
            });

            editPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null){
                        openEditPasswordDialog(userId);
                    }
                }
            });

        }

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri selectedImageUri = data.getData();
                            updateProfilePicture(userId, selectedImageUri);
                        } else {
                            showToast("No data received");
                        }
                    }
                }
        );


        MaterialButton manageRecipesButton = view.findViewById(R.id.manageRecipes);
        manageRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManageRecipe.class);

                Bundle bundle = getArguments();
                if (bundle != null) {
                    String userId = bundle.getString("user_id");
                    intent.putExtra("user_id", userId);
                }

                startActivity(intent);
            }
        });

        MaterialButton aboutPage = view.findViewById(R.id.aboutBtn);
        aboutPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAbout = new Intent(getActivity(), About.class);
                startActivity(intentAbout);
            }
        });

        Button logoutBtn = view.findViewById(R.id.logOutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                showToast("You have been logged out");
                Intent it = new Intent(getActivity(), Landing.class);
                startActivity(it);
            }
        });

        return view;
    }

    private void retrieveUserInfoFromFirestore(String uid, ImageView profilePictureImageView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("user_id", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String userNameDb = document.getString("user_name");
                                String userEmailDb = document.getString("email");

                                if (userNameDb != null) {
                                    userNameTxt.setText(userNameDb);
                                }
                                if (userEmailDb != null) {
                                    userEmailTxt.setText(userEmailDb);
                                }

                                String photoUrl = document.getString("profile_picture");
                                if (photoUrl != null && !photoUrl.isEmpty()) {
                                    Glide.with(requireActivity())
                                            .load(photoUrl)
                                            .placeholder(R.drawable.toolbar_account)
                                            .into(profilePictureImageView);
                                } else {
                                    // Set the default image directly
                                    profilePictureImageView.setImageResource(R.drawable.toolbar_account); // Replace with the actual resource ID of your default image
                                }
                            }
                        } else {
                            showToast("User document does not exist in Firestore");
                        }
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                        showToast("Failed to retrieve user information from Firestore");
                    }
                });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void updateProfilePicture(String userId, Uri imageUri) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String randomImageFileName = UUID.randomUUID().toString() + ".jpg";

        storageRef = storage.getReference().child("users/" + randomImageFileName);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        db.collection("users")
                                .whereEqualTo("user_id", userId)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot userQuerySnapshot = task.getResult();
                                        if (userQuerySnapshot != null && !userQuerySnapshot.isEmpty()) {
                                            DocumentSnapshot document = userQuerySnapshot.getDocuments().get(0);
                                            String documentId = document.getId();

                                            db.collection("users")
                                                    .document(documentId)
                                                    .update("profile_picture", uri.toString())
                                                    .addOnCompleteListener(updateTask -> {
                                                        showToast("Profile picture updated successfully");
                                                        retrieveUserInfoFromFirestore(userId, profilePictureImageView);
                                                    });
                                        } else {
                                            showToast("User document does not exist in Firestore");
                                        }
                                    } else {
                                        Exception e = task.getException();
                                        if (e != null) {
                                            e.printStackTrace();
                                        }
                                        showToast("Failed to retrieve user information from Firestore");
                                    }
                                });

                    });
                })
                .addOnFailureListener(e -> {
                    showToast("Failed to upload image to Storage");
                    e.printStackTrace();
                });
    }

    private void openEditNameDialog(String userId) {
        EditNameDialog editNameDialog = new EditNameDialog(userId);

        editNameDialog.setOnUsernameUpdatedListener(this);
        editNameDialog.show(getChildFragmentManager(), "editNameDialog");
    }

    private void openEditPasswordDialog(String userId) {
        EditPasswordDialog editPasswordDialog = new EditPasswordDialog(userId);
        editPasswordDialog.show(getChildFragmentManager(), "editPasswordDialog");
    }

    @Override
    public void onUsernameUpdated(String newUsername) {
        userNameTxt.setText(newUsername);
    }

    private void showToast(String message) {
        Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
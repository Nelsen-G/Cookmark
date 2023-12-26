package com.example.cookmark_app.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.cookmark_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class EditNameDialog extends AppCompatDialogFragment {
    private static final String TAG = "EditNameDialog";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId;
    private OnUsernameUpdatedListener onUsernameUpdatedListener;
    View rootView;
    EditText editUsername;

    public EditNameDialog(String userId) {
        this.userId = userId;
    }

    public void setOnUsernameUpdatedListener(OnUsernameUpdatedListener listener) {
        this.onUsernameUpdatedListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.dialog_editname, null);

        builder.setView(rootView).setTitle("Edit Username").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Just closing the dialog, no need for anything
            }
        }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editUsername.getText().toString().trim();

                String message = validateUsername(newName);

                if (message != null) {
                    showToast(message);
                } else {
                    updateUsername(newName, userId);
                }
            }
        });

        initializeViewElements();

        return builder.create();
    }

    private void initializeViewElements() {
        editUsername = rootView.findViewById(R.id.editUsernameDialog);
    }

    private void updateUsername(String newName, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check if the new username already exists in the database
        db.collection("users")
                .whereEqualTo("user_name", newName)
                .get()
                .addOnCompleteListener(queryTask -> {
                    if (queryTask.isSuccessful()) {
                        QuerySnapshot usernameQuerySnapshot = queryTask.getResult();
                        if (usernameQuerySnapshot != null && !usernameQuerySnapshot.isEmpty()) {
                            showToast("Username already exists. Please choose a different one.");
                        } else {
                            db.collection("users")
                                    .whereEqualTo("user_id", userId)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot userQuerySnapshot = task.getResult();
                                            if (userQuerySnapshot != null && !userQuerySnapshot.isEmpty()) {
                                                // Ambil dokumen yang pertama
                                                DocumentSnapshot document = userQuerySnapshot.getDocuments().get(0);
                                                String documentId = document.getId();

                                                db.collection("users")
                                                        .document(documentId)
                                                        .update("user_name", newName)
                                                        .addOnCompleteListener(updateTask -> {
                                                            if (updateTask.isSuccessful()) {
                                                                showToast("Username updated!");

                                                                if (onUsernameUpdatedListener != null) {
                                                                    onUsernameUpdatedListener.onUsernameUpdated(newName);
                                                                }
                                                            } else {
                                                                showToast("Failed to update username");
                                                                Log.e(TAG, "Failed to update username", updateTask.getException());
                                                            }
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
                        }
                    } else {
                        Exception e = queryTask.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                        showToast("Failed to check username uniqueness");
                    }
                });
    }


    private void showToast(String message) {
        Toast.makeText(rootView.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private String validateUsername(String username) {
        String message = null;

        if (username.equals("")) {
            message = "Please fill the new username!";
        }

        return message;
    }

    public interface OnUsernameUpdatedListener {
        void onUsernameUpdated(String newUsername);
    }
}

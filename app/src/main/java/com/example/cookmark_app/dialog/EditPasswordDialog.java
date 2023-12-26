package com.example.cookmark_app.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class EditPasswordDialog extends AppCompatDialogFragment {
    private static final String TAG = "EditPasswordDialog";
    private String userId;
    private View rootView;
    private EditText editPassword, confirmPassword;

    public EditPasswordDialog(String userId) {
        this.userId = userId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.dialog_editpassword, null);

        builder.setView(rootView).setTitle("Edit Password").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Just closing the dialog, no need for anything
            }
        }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = editPassword.getText().toString().trim();
                String confirmNewPassword = confirmPassword.getText().toString().trim();

                String message = validatePasswords(newPassword, confirmNewPassword);

                if (message != null) {
                    showToast(message);
                } else {
                    updatePassword(newPassword, userId);
                }
            }
        });

        initializeViewElements();

        return builder.create();
    }

    private void initializeViewElements() {
        editPassword = rootView.findViewById(R.id.editPasswordDialog);
        confirmPassword = rootView.findViewById(R.id.confirmPasswordDialog);
    }

    private void updatePassword(String newPassword, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Assuming there's only one document for the given user ID
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            String documentId = document.getId();

                            db.collection("users")
                                    .document(documentId)
                                    .update("password", newPassword)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            showToast("Password updated!");
                                        } else {
                                            showToast("Failed to update password");
                                            Log.e(TAG, "Failed to update password", updateTask.getException());
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

    private String validatePasswords(String password, String confirmPassword) {
        String message = null;

        if (password.equals("") || confirmPassword.equals("")) {
            message = "Please fill up your registration info";
        } else if (!password.equals(confirmPassword)) {
            message = "Both passwords you input do not match";
        }

        return message;
    }

    private void showToast(String message) {
        Toast.makeText(rootView.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

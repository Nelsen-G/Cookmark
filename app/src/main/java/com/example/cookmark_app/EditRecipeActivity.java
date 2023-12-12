package com.example.cookmark_app;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cookmark_app.adapter.CustomSpinnerAdapter;
import com.example.cookmark_app.adapter.IngredientAdapter;
import com.example.cookmark_app.model.Ingredient;
import com.example.cookmark_app.model.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class EditRecipeActivity extends AppCompatActivity {
    private IngredientAdapter ingredientAdapter;
    private String selectedSpinnerItem, imageUrl, recipeId;
    private List<Ingredient> ingredients;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri imageUri;
    private ImageView uploadedImageView;
    private Button btnUploadRecipeImage, btnAddIngredient, btnUpdateRecipe;
    private EditText editTextRecipeName, editTextHours, editTextMinutes, editTextServings,
            editTextIngredient, editTextCookingSteps, editTextRecipeURL;
    private Spinner spinnerOptions, spinner;
    private CustomSpinnerAdapter adapter;
    private RecyclerView recyclerViewIngredients;

    private StorageReference storageRef;

    private int hours, minutes, servings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        uploadedImageView = findViewById(R.id.uploadedImageView);
        btnUploadRecipeImage = findViewById(R.id.btnUploadRecipeImage);
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            Glide.with(getApplicationContext())
                                    .load(imageUri)
                                    .into(uploadedImageView);
                        }
                    }
                });

        btnUploadRecipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGallery();
            }
        });


        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient();
            }
        });

        spinnerOptions = findViewById(R.id.spinnerOptions);

        spinner = findViewById(R.id.spinnerOptions);
        adapter = new CustomSpinnerAdapter(
                getApplicationContext(),
                R.layout.spinner_dropdown_item,
                Arrays.asList(getResources().getStringArray(R.array.options_array))
        );

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedSpinnerItem = (String) parentView.getItemAtPosition(position);
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                //gaosah udah ada validasi di bawah
            }
        });


        editTextRecipeName = findViewById(R.id.editTextRecipeName);
        editTextHours = findViewById(R.id.editTextHours);
        editTextMinutes = findViewById(R.id.editTextMinutes);
        editTextServings = findViewById(R.id.editTextServings);
        editTextIngredient = findViewById(R.id.editTextIngredient);
        editTextCookingSteps = findViewById(R.id.editTextCookingSteps);
        editTextRecipeURL = findViewById(R.id.editTextRecipeURL);

        recyclerViewIngredients = findViewById(R.id.recyclerViewIngredients);

        ingredients = new ArrayList<>();
        ingredientAdapter = new IngredientAdapter(ingredients);
        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewIngredients.setAdapter(ingredientAdapter);


        recipeId = getIntent().getStringExtra("recipeId");
        loadRecipeDetails(recipeId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        btnUpdateRecipe = findViewById(R.id.btnUpdateRecipe);
        btnUpdateRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID() + ".jpg");

                String recipeName = editTextRecipeName.getText().toString();

                hours = 0;
                String hoursText = editTextHours.getText().toString();
                if (!hoursText.isEmpty()) {
                    hours = Integer.parseInt(hoursText);
                }

                minutes = 0;
                String minutesText = editTextMinutes.getText().toString();
                if (!minutesText.isEmpty()) {
                    minutes = Integer.parseInt(minutesText);
                }

                servings = 0;
                String servingsText = editTextServings.getText().toString();
                if (!servingsText.isEmpty()) {
                    servings = Integer.parseInt(servingsText);
                }

                String cookingSteps = editTextCookingSteps.getText().toString();

                String recipeURL = editTextRecipeURL.getText().toString();

                if (validateInputs()) {
                    if (imageUri == null && TextUtils.isEmpty(imageUrl)) {
                        showToast("Please select an image");
                        return;
                    }

                    if (imageUri != null) {
                        uploadImageAndRecipe(recipeName, hours, minutes, servings, cookingSteps, recipeURL);
                    } else {
                        // Use the existing image URL
                        updateRecipeDetails(recipeName, hours, minutes, servings, cookingSteps, recipeURL);
                    }
                }

            }
        });
    }

    private void uploadImageAndRecipe(String recipeName, int hours, int minutes, int servings, String cookingSteps, String recipeURL) {
        StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrl = uri.toString();
                        updateRecipeDetails(recipeName, hours, minutes, servings, cookingSteps, recipeURL);
                    }).addOnFailureListener(exception -> {
                        Log.e(TAG, "Error getting download URL");
                    });
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "Error uploading recipe image");
                });
    }

    private void updateRecipeDetails(String recipeName, int hours, int minutes, int servings, String cookingSteps, String recipeURL) {
        Recipe updatedRecipe = new Recipe(recipeId, imageUrl, recipeName, hours, minutes, selectedSpinnerItem,
                servings, (ArrayList<Ingredient>) ingredients, cookingSteps, recipeURL, 0);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipes")
                .document(recipeId)
                .update("image", updatedRecipe.getRecipeImage(),
                        "recipeName", updatedRecipe.getRecipeName(),
                        "hours", updatedRecipe.getHours(),
                        "minutes", updatedRecipe.getMinutes(),
                        "selectedSpinnerItem", updatedRecipe.getFoodType(),
                        "servings", updatedRecipe.getServings(),
                        "ingredientList", updatedRecipe.getIngredientListAsString(),
                        "cookingSteps", updatedRecipe.getCookingSteps(),
                        "recipeURL", updatedRecipe.getRecipeURL())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Recipe updated successfully");
                    showToast("Recipe updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating recipe", e);
                    showToast("Failed to update recipe");
                });
    }


    private String getImageFileNameFromUrl(String imageUrl) {
        Uri uri = Uri.parse(imageUrl);
        return uri.getLastPathSegment();
    }
    private void loadRecipeDetails(String recipeId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Id adalah: " + recipeId);

        Query query = db.collection("recipes").whereEqualTo("id", recipeId);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {

                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                        String recipeName = document.getString("recipeName");
                        int hours = document.getLong("hours").intValue();
                        int minutes = document.getLong("minutes").intValue();
                        int servings = document.getLong("servings").intValue();
                        String cookingSteps = document.getString("cookingSteps");
                        String recipeURL = document.getString("recipeURL");

                        editTextRecipeName.setText(recipeName);
                        editTextHours.setText(String.valueOf(hours));
                        editTextMinutes.setText(String.valueOf(minutes));
                        editTextServings.setText(String.valueOf(servings));
                        editTextCookingSteps.setText(cookingSteps);
                        editTextRecipeURL.setText(recipeURL);

                        String ingredientsAsString = document.getString("ingredientListAsString");
                        List<Ingredient> ingredients = deserializeIngredients(ingredientsAsString);
                        if (ingredients != null) {
                            updateIngredients(ingredients);
                        }


                        imageUrl = document.getString("image");
                        Glide.with(EditRecipeActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.img_placeholder)
                                .into(uploadedImageView);

                        String selectedSpinnerItem = document.getString("selectedSpinnerItem");
                        if (selectedSpinnerItem != null) {
                            int index = adapter.getPosition(selectedSpinnerItem);
                            if (index != -1) {
                                spinner.setSelection(index);
                            }
                        }

                    } else {
                        Log.d(TAG, "No such document with id of : " + recipeId);
                    }
                } else {
                    Log.e(TAG, "Gagal ", task.getException());
                }
            }
        });
    }

    private boolean validateInputs() {
        if (uploadedImageView.getDrawable() == null) {
            showToast("Please select an image");
            return false;
        }

        if (editTextRecipeName.getText().toString().isEmpty()) {
            showToast("Please enter a recipe name");
            return false;
        }

        if (editTextHours.getText().toString().isEmpty()) {
            showToast("Please enter hours");
            return false;
        }

        if (editTextMinutes.getText().toString().isEmpty()) {
            showToast("Please enter minutes");
            return false;
        }

        if (editTextServings.getText().toString().isEmpty()) {
            showToast("Please enter servings");
            return false;
        }

        if (ingredients.isEmpty()) {
            showToast("Please add at least one ingredient");
            return false;
        }

        if (editTextCookingSteps.getText().toString().isEmpty()) {
            showToast("Please enter cooking steps");
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    private void addIngredient() {
        String ingredientName = editTextIngredient.getText().toString().trim();
        if (!ingredientName.isEmpty()) {
            Ingredient ingredient = new Ingredient(ingredientName);
            ingredients.add(ingredient);
            ingredientAdapter.notifyDataSetChanged();
            editTextIngredient.setText("");
//            Toast.makeText(requireContext(), "masuk", Toast.LENGTH_SHORT).show();

        }
    }

    public void updateIngredients(List<Ingredient> newIngredients) {
        ingredients.clear();
        ingredients.addAll(newIngredients);
        ingredientAdapter.notifyDataSetChanged();
    }

    private List<Ingredient> deserializeIngredients(String ingredientsAsString) {
        Type listType = new TypeToken<List<Ingredient>>(){}.getType();
        return new Gson().fromJson(ingredientsAsString, listType);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }


}
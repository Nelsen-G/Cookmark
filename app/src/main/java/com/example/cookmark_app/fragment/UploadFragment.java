package com.example.cookmark_app.fragment;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cookmark_app.R;
import com.example.cookmark_app.activity.RecipeDetailActivity;
import com.example.cookmark_app.adapter.CustomSpinnerAdapter;
import com.example.cookmark_app.adapter.IngredientAdapter;
import com.example.cookmark_app.model.Ingredient;
import com.example.cookmark_app.model.Recipe;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private ImageView uploadedImageView;
    private Button btnUploadRecipeImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private EditText editTextIngredient, editTextRecipeName, editTextHours, editTextMinutes, editTextServings, editTextCookingSteps, editTextRecipeURL;
    private Button btnAddIngredient;
    private int hours, minutes, servings;
    private RecyclerView recyclerViewIngredients;
    private ArrayList<Ingredient> ingredientList;
    private IngredientAdapter ingredientAdapter;
    private Button btnUploadRecipe;
    private String selectedSpinnerItem, imagePath;
    private Uri imageUri;
    private String userId, recipeId, recipeName, cookingSteps, recipeURL;
    private StorageReference imageRef;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        uploadedImageView = view.findViewById(R.id.uploadedImageView);
        btnUploadRecipeImage = view.findViewById(R.id.btnUploadRecipeImage);

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            Glide.with(requireContext())
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


        Spinner spinner = view.findViewById(R.id.spinnerOptions);
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(
                requireContext(),
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


        editTextIngredient = view.findViewById(R.id.editTextIngredient);
        btnAddIngredient = view.findViewById(R.id.btnAddIngredient);
        recyclerViewIngredients = view.findViewById(R.id.recyclerViewIngredients);

        ingredientList = new ArrayList<>();
        ingredientAdapter = new IngredientAdapter(ingredientList);

        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewIngredients.setNestedScrollingEnabled(false);
        recyclerViewIngredients.setAdapter(ingredientAdapter);

        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient();
            }
        });


        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString("user_id");
            Log.d("Upload Fragment -> ", "User ID: " + userId);
        }

        btnUploadRecipe = view.findViewById(R.id.btnUploadRecipe);

        btnUploadRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeId = UUID.randomUUID().toString();
//                imagePath = imageUri.getPath();
                imageRef = storageRef.child("images/" + UUID.randomUUID() + ".jpg");

                editTextRecipeName = view.findViewById(R.id.editTextRecipeName);
                recipeName = editTextRecipeName.getText().toString();

                editTextHours = view.findViewById(R.id.editTextHours);
                hours = 0;
                String hoursText = editTextHours.getText().toString();
                if (!hoursText.isEmpty()) {
                    hours = Integer.parseInt(hoursText);
                }

                editTextMinutes = view.findViewById(R.id.editTextMinutes);
                minutes = 0;
                String minutesText = editTextMinutes.getText().toString();
                if (!minutesText.isEmpty()) {
                    minutes = Integer.parseInt(minutesText);
                }

                editTextServings = view.findViewById(R.id.editTextServings);
                servings = 0;
                String servingsText = editTextServings.getText().toString();
                if (!servingsText.isEmpty()) {
                    servings = Integer.parseInt(servingsText);
                }

                editTextCookingSteps = view.findViewById(R.id.editTextCookingSteps);
                cookingSteps = editTextCookingSteps.getText().toString();

                editTextRecipeURL = view.findViewById(R.id.editTextRecipeURL);
                recipeURL = editTextRecipeURL.getText().toString();


                if (validateInputs()) {

                    showUpdateConfirmationDialog();

                }


            }
        });

//        Button btnShowRecipe = view.findViewById(R.id.btnShowRecipe);
//        btnShowRecipe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                db.collection("recipes")
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        Log.d(TAG, document.getId() + " => " + document.getData());
//                                    }
//                                } else {
//                                    Log.w(TAG, "Error getting documents.", task.getException());
//                                }
//                            }
//                        });
//
//            }
//        });

        return view;
    }

    private void showUpdateConfirmationDialog(){
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.custom_dialog);
        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        TextView dialogContent = dialog.findViewById(R.id.dialogContent);
        Button btnNo = dialog.findViewById(R.id.btnNo);
        Button btnYes = dialog.findViewById(R.id.btnYes);

        dialogTitle.setText("Upload Recipe");
        dialogContent.setText("Are you sure you want to upload this recipe?");

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {

                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();

                                Recipe newRecipe = new Recipe(recipeId, userId, imageUrl, recipeName, hours, minutes, selectedSpinnerItem,
                                        servings, ingredientList, cookingSteps, recipeURL, 0, 0);

//                                    Map<String, Object> recipeData = new HashMap<>();
//                                    recipeData.put("image", imageUrl);
//                                    recipeData.put("recipeName", recipeName);
//                                    recipeData.put("hours", hours);
//                                    recipeData.put("minutes", minutes);
//                                    recipeData.put("selectedSpinnerItem", selectedSpinnerItem);
//                                    recipeData.put("servings", servings);
//                                    recipeData.put("ingredientList", ingredientList);
//                                    recipeData.put("cookingSteps", cookingSteps);
//                                    recipeData.put("recipeURL", recipeURL);

                                db.collection("recipes")
                                        .add(newRecipe)
                                        .addOnSuccessListener(documentReference -> {
                                            Log.d(TAG, "Recipe added with ID: " + documentReference.getId());
                                            showToast("Recipe uploaded successfully");
                                            dialog.dismiss();

                                            Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                                            intent.putExtra("recipe", newRecipe);
                                            startActivity(intent);

                                            uploadedImageView.setImageResource(R.drawable.img_placeholder);
                                            editTextRecipeName.setText("");
                                            editTextHours.setText("");
                                            editTextMinutes.setText("");
                                            editTextServings.setText("");
                                            ingredientList.clear();
                                            ingredientAdapter.notifyDataSetChanged();
                                            editTextCookingSteps.setText("");
                                            editTextRecipeURL.setText("");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w(TAG, "Error adding recipe", e);
                                            showToast("Failed to upload recipe");
                                        });

                            }).addOnFailureListener(exception -> {
                                Log.e(TAG, "Error getting download URL");
                            });
                        })
                        .addOnFailureListener(exception -> {
                            Log.e(TAG, "Error uploading recipe image");
                        });




            }
        });

        dialog.show();
    }

    private static final int PLACEHOLDER_RESOURCE_ID = R.drawable.img_placeholder;

    private boolean isPlaceholderDrawable(Drawable drawable) {
        return drawable.getConstantState() != null &&
                drawable.getConstantState().equals(getResources().getDrawable(PLACEHOLDER_RESOURCE_ID).getConstantState());
    }

    private boolean validateInputs() {
        if (uploadedImageView.getDrawable() == null || isPlaceholderDrawable(uploadedImageView.getDrawable())) {
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

        if (ingredientList.isEmpty()) {
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void addIngredient() {
        String ingredientName = editTextIngredient.getText().toString().trim();
        if (!ingredientName.isEmpty()) {
            Ingredient ingredient = new Ingredient(ingredientName);
            ingredientList.add(ingredient);
            ingredientAdapter.notifyDataSetChanged();
            editTextIngredient.setText("");
//            Toast.makeText(requireContext(), "masuk", Toast.LENGTH_SHORT).show();

        }
    }
}
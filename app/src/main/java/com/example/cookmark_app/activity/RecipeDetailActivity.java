package com.example.cookmark_app.activity;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.cookmark_app.R;
import com.example.cookmark_app.adapter.TagTypeAdapter;
import com.example.cookmark_app.interfaces.OnItemClickCallback;
import com.example.cookmark_app.model.Cookmark;
import com.example.cookmark_app.model.Ingredient;
import com.example.cookmark_app.model.Recipe;
import com.example.cookmark_app.utils.CookmarkStatusManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity implements OnItemClickCallback {
    private Recipe recipe;
    private TagTypeAdapter tagAdapter;
    private ImageView recipeCookmarkIcon;
    String userId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        SharedPreferences sp1 = this.getSharedPreferences("Login", MODE_PRIVATE);
        userId = sp1.getString("userid", null);

        if (getIntent().hasExtra("recipe")) {
            recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        } else {
            finish();
            return;
        }

        ImageView backToPrevious = findViewById(R.id.detail_back_iv);
        TextView titleToolbar = findViewById(R.id.detail_toolbar_title_tv);

        if (recipe != null) {
            loadRecipeDetails(recipe.getRecipeId());

            RecyclerView tagContainer = findViewById(R.id.tag_container);

            TextView recipeTitle = findViewById(R.id.detail_title_tv);
            TextView recipeTimeToMake = findViewById(R.id.detail_duration_tv);
            TextView recipeFoodtype = findViewById(R.id.detail_foodtype_tv);
            TextView recipeServings = findViewById(R.id.detail_servings_tv);
            TextView recipeCookingSteps = findViewById(R.id.detail_steps_tv);
            TextView recipeUrl = findViewById(R.id.detail_url_tv);
            TextView recipeUploader = findViewById(R.id.detail_uploader_tv);

            ImageView recipePhoto = findViewById(R.id.detail_photo_iv);
            recipeCookmarkIcon = findViewById(R.id.detail_cookmark);
            initializeCookMarkIcon(recipe);
            recipeCookmarkIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Drawable cookmarkedDrawable = ContextCompat.getDrawable(RecipeDetailActivity.this, R.drawable.ic_cookmarked);
                    Drawable uncookmarkedDrawable = ContextCompat.getDrawable(RecipeDetailActivity.this, R.drawable.ic_uncookmarked);

                    if (recipeCookmarkIcon.getDrawable().getConstantState().equals(cookmarkedDrawable.getConstantState())) {
                        deleteCookMark(recipe.getRecipeId());
                    } else if (recipeCookmarkIcon.getDrawable().getConstantState().equals(uncookmarkedDrawable.getConstantState())) {
                        addCookMark(recipe);
                    }
                }
            });

            int placeholderImage = R.drawable.img_placeholder;

            titleToolbar.setText(recipe.getRecipeName());
            recipeTitle.setText(recipe.getRecipeName());
            if (recipe.getHours() == 0) {
                recipeTimeToMake.setText(String.valueOf(recipe.getMinutes()) + " minutes");
            } else if (recipe.getHours() == 1) {
                recipeTimeToMake.setText(String.valueOf(recipe.getHours()) + " hour " + recipe.getMinutes() + " minutes");
            } else {
                recipeTimeToMake.setText(String.valueOf(recipe.getHours()) + " hours " + recipe.getMinutes() + " minutes");
            }
            recipeFoodtype.setText(recipe.getFoodType());
            recipeServings.setText(String.valueOf(recipe.getServings()) + " Servings");
            recipeCookingSteps.setText(recipe.getCookingSteps());
            recipeUrl.setText(recipe.getRecipeURL());
            recipeUploader.setText(recipe.getUserName());

            if (recipe.getRecipeImage() != null) {
                Glide.with(this)
                        .load(recipe.getRecipeImage())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(placeholderImage)
                        .error(placeholderImage)
                        .into(recipePhoto);
            }

            List<Ingredient> ingredientsList = recipe.getIngredientListFromString(recipe.getIngredientListAsString());
            if (ingredientsList != null) {
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                tagContainer.setLayoutManager(layoutManager);
                tagAdapter = new TagTypeAdapter(new ArrayList<>(ingredientsList), this);
                tagContainer.setAdapter(tagAdapter);
            }

            backToPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String callingFragment = getIntent().getStringExtra("callingFragment");
                    Intent intent = new Intent(RecipeDetailActivity.this, MainActivity.class);

                    if(callingFragment.equals("cookmark")){
                        intent.putExtra("loadFragment", "cookmark");
                    } else if (callingFragment.equals("plan")){
                        intent.putExtra("loadFragment", "plan");
                    } else if (callingFragment.equals("explore")){
                        intent.putExtra("loadFragment", "explore");
                    }

                    startActivity(intent);
                    finish();
                }
            });

            recipeUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showVisitURLWebsiteDialog(String.valueOf(recipeUrl.getText()));
                }
            });

        }
    }

    private void initializeCookMarkIcon(Recipe recipe){
        db.collection("cookmarks")
            .whereEqualTo("userid", userId)
            .whereEqualTo("recipeid", recipe.getRecipeId())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document != null) {
                                recipeCookmarkIcon.setImageResource(R.drawable.ic_cookmarked);
                            } else {
                                recipeCookmarkIcon.setImageResource(R.drawable.ic_uncookmarked);
                            }
                        }
                    }
                }
            });
    }

    private void deleteCookMark(String recipeid) {
        db.collection("cookmarks")
            .whereEqualTo("userid", userId)
            .whereEqualTo("recipeid", recipeid)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document != null) {
                                db.collection("cookmarks")
                                        .document(document.getId())
                                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                recipeCookmarkIcon.setImageResource(R.drawable.ic_uncookmarked);
                                            }
                                        });
                            }
                        }
                    }
                }
            });

        //decrease cookmarked value of recipe
        db.collection("recipes")
            .whereEqualTo("id", recipeid)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document != null) {
                                Long cookmarkCountLong = document.getLong("cookmarkCount");
                                int cookmarkCount = cookmarkCountLong.intValue() - 1;
                                if(cookmarkCount >= 0) {
                                    // Update cookmarkCount value in the recipe document
                                    db.collection("recipes")
                                        .document(document.getId())
                                        .update("cookmarkCount", cookmarkCount)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                String toastMessage = "Oopss you've already uncookmark a recipe";
                                                Toast.makeText(RecipeDetailActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("TAG", "Error updating cookmarkCount", e);
                                            }
                                        });
                                }
                            }
                        }
                    }
                }
            });
    }

    private void addCookMark(Recipe recipe) {
        Cookmark newCookMark = new Cookmark(userId, recipe.getRecipeId());

        //add to cookmarks
        db.collection("cookmarks")
            .add(newCookMark)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    recipeCookmarkIcon.setImageResource(R.drawable.ic_cookmarked);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("TAG", "Error adding document", e);
                }
            });

        //increase cookmarked value of recipe
        db.collection("recipes")
            .whereEqualTo("id", recipe.getRecipeId())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document != null) {
                                Long cookmarkCountLong = document.getLong("cookmarkCount");
                                int cookmarkCount = cookmarkCountLong.intValue() + 1;

                                // Update cookmarkCount value in the recipe document
                                db.collection("recipes")
                                    .document(document.getId())
                                    .update("cookmarkCount", cookmarkCount)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            String toastMessage = "Cookmarked " + recipe.getRecipeName();
                                            Toast.makeText(RecipeDetailActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("TAG", "Error updating cookmarkCount", e);
                                        }
                                    });
                            }
                        }
                    }
                }
            });
    }

    @Override
    public void onItemClicked(Ingredient ingredient) {
        Intent intent = new Intent(this, SearchResultActivity.class);
        ArrayList<Ingredient> selectedIngredients = new ArrayList<>();
        selectedIngredients.add(ingredient);
        intent.putExtra("selectedIngredients", selectedIngredients);
        startActivity(intent);
    }

    private void showVisitURLWebsiteDialog(String url){
        Dialog dialog = new Dialog(RecipeDetailActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        TextView dialogContent = dialog.findViewById(R.id.dialogContent);
        Button btnNo = dialog.findViewById(R.id.btnNo);
        Button btnYes = dialog.findViewById(R.id.btnYes);

        dialogTitle.setText("Feeling tempted?");
        dialogContent.setText("You will be redirected to an external site using the link");

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(url)));
                startActivity(intent);
            }
        });

        dialog.show();
    }

    private void loadRecipeDetails(String recipeId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                        String foodtype = document.getString("selectedSpinnerItem");
                        String ingredientsAsString = document.getString("ingredientListAsString");
                        String userName = document.getString("userName");

                        recipe.setRecipeName(recipeName);
                        recipe.setHours(hours);
                        recipe.setMinutes(minutes);
                        recipe.setServings(servings);
                        recipe.setCookingSteps(cookingSteps);
                        recipe.setRecipeURL(recipeURL);
                        recipe.setIngredientListAsString(ingredientsAsString);
                        recipe.setRecipeImage(document.getString("image"));
                        recipe.setFoodType(foodtype);
                        recipe.setUserName(userName);
                    } else {
                        Log.d(TAG, "No such document with id of : " + recipeId);
                    }
                } else {
                    Log.e(TAG, "Gagal ", task.getException());
                }
            }
        });
    }

    private void navigateToPreviousActivity(Activity activity) {
        Intent upIntent = NavUtils.getParentActivityIntent(activity);

        if (upIntent != null) {
            upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            NavUtils.navigateUpTo(activity, upIntent);
            Log.d(TAG, "navigateToPreviousActivity: ");
        }
    }

    private void refresh() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
        Log.d("TAG", "onRestart: aaa");
    }
}
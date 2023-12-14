package com.example.cookmark_app.adapter.cookmark_app;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookmark_app.R;
import com.example.cookmark_app.adapter.TagTypeAdapter;
import com.example.cookmark_app.model.Ingredient;
import com.example.cookmark_app.model.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {
    private Recipe recipe;
    private TagTypeAdapter tagAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

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
            ImageView recipePhoto = findViewById(R.id.detail_photo_iv);
            int placeholderImage = R.drawable.img_placeholder;

            titleToolbar.setText(recipe.getRecipeName());
            recipeTitle.setText(recipe.getRecipeName());
            recipeTimeToMake.setText(String.valueOf(recipe.getHours()) + " hr");
            recipeFoodtype.setText(recipe.getFoodType());
            recipeServings.setText(String.valueOf(recipe.getServings()) + " Servings");
            recipeCookingSteps.setText(recipe.getCookingSteps());
            recipeUrl.setText(recipe.getRecipeURL());

            if (recipe.getRecipeImage() != null) {
                Glide.with(this)
                        .load(recipe.getRecipeImage())
                        .placeholder(placeholderImage)
                        .error(placeholderImage)
                        .into(recipePhoto);
            }

            List<Ingredient> ingredientsList = recipe.getIngredientListFromString(recipe.getIngredientListAsString());
            if (ingredientsList != null) {
                tagAdapter = new TagTypeAdapter(new ArrayList<>(ingredientsList));
            }
            tagContainer.setAdapter(tagAdapter);
            tagContainer.setLayoutManager(new GridLayoutManager(this, 2));

            backToPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }

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

                        recipe.setRecipeName(recipeName);
                        recipe.setHours(hours);
                        recipe.setMinutes(minutes);
                        recipe.setServings(servings);
                        recipe.setCookingSteps(cookingSteps);
                        recipe.setRecipeURL(recipeURL);
                        recipe.setIngredientListAsString(ingredientsAsString);
                        recipe.setRecipeImage(document.getString("image"));
                        recipe.setFoodType(foodtype);
                    } else {
                        Log.d(TAG, "No such document with id of : " + recipeId);
                    }
                } else {
                    Log.e(TAG, "Gagal ", task.getException());
                }
            }
        });
    }

//    private List<Ingredient> deserializeIngredients(String ingredientsAsString) {
//        List<Ingredient> ingredientsList = new ArrayList<>();
//
//        try {
//            Gson gson = new Gson();
//            JsonArray jsonArray = JsonParser.parseString(ingredientsAsString).getAsJsonArray();
//
//            for (JsonElement jsonElement : jsonArray) {
//                Ingredient ingredient = gson.fromJson(jsonElement, Ingredient.class);
//                ingredientsList.add(ingredient);
//            }
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        }
//
//        return ingredientsList;
//    }

//    private List<Ingredient> deserializeIngredients(String ingredientsAsString) {
//        Type listType = new TypeToken<List<Ingredient>>(){}.getType();
//        return new Gson().fromJson(ingredientsAsString, listType);
//    }
}
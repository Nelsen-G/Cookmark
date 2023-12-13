package com.example.cookmark_app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookmark_app.adapter.TagTypeAdapter;
import com.example.cookmark_app.model.Ingredient;
import com.example.cookmark_app.model.Recipe;

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

            tagAdapter = new TagTypeAdapter(new ArrayList<>(recipe.getIngredientList()));
            tagContainer.setAdapter(tagAdapter);
            tagContainer.setLayoutManager(new GridLayoutManager(this, 2));

            if (recipe.getIngredientList() != null) {
                addTags(recipe.getIngredientList());
            }

            backToPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void addTags(ArrayList<Ingredient> ingredients) {
        ArrayList<Ingredient> newIngredients = new ArrayList<>(recipe.getIngredientList());

        newIngredients.addAll(ingredients);

        recipe.setIngredientList(newIngredients);
        tagAdapter.notifyDataSetChanged();
    }
}
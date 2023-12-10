package com.example.cookmark_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.cookmark_app.R;
import com.example.cookmark_app.model.Recipe;

import java.util.List;


public class ManageRecipeAdapter extends RecyclerView.Adapter<ManageRecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipes;

    public ManageRecipeAdapter(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manage_recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe);

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hehehe
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private ImageView recipeImageView;
        private TextView recipeNameTextView;
        private TextView recipeTypeTextView;
        private TextView cookmarkCountTextView;
        private TextView servingsCountTextView;
        private TextView hoursTextView;
        private TextView minutesTextView;
        private ImageView editButton;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeImageView = itemView.findViewById(R.id.recipeImage);
            recipeNameTextView = itemView.findViewById(R.id.recipeName);
            recipeTypeTextView = itemView.findViewById(R.id.recipeType);
            cookmarkCountTextView = itemView.findViewById(R.id.cookmarkCount);
            servingsCountTextView = itemView.findViewById(R.id.servingsCount);
            hoursTextView = itemView.findViewById(R.id.hours);
            minutesTextView = itemView.findViewById(R.id.minutes);
            editButton = itemView.findViewById(R.id.editButton);
        }

        public void bind(Recipe recipe) {
            Glide.with(itemView.getContext())
                    .load(recipe.getRecipeImage())
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(recipeImageView);

            recipeNameTextView.setText(recipe.getRecipeName());
            recipeTypeTextView.setText(recipe.getFoodType());
            cookmarkCountTextView.setText(recipe.getCookmarkCount() + " cookmarked");
            servingsCountTextView.setText(recipe.getServings() + " servings");
            hoursTextView.setText(recipe.getHours() +"h");
            minutesTextView.setText(recipe.getMinutes() + "m");
        }
    }
}
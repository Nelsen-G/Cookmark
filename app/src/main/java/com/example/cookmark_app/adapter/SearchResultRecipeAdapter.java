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
import com.example.cookmark_app.interfaces.OnItemClickCallback;
import com.example.cookmark_app.model.Recipe;

import java.util.List;

// buat adapter recycler view yg di search result, ada tombol savenya
// ini buat per itemnya
public class SearchResultRecipeAdapter extends RecyclerView.Adapter<SearchResultRecipeAdapter.SearchResultRecipeHolder>{

    // for handling click
    private OnItemClickCallback onItemClickCallback;
    private List<Recipe> recipes;

    public SearchResultRecipeAdapter(OnItemClickCallback onItemClickCallback, List<Recipe> recipes) {
        this.onItemClickCallback = onItemClickCallback;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public SearchResultRecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchresult_list, parent, false);
        return new SearchResultRecipeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultRecipeHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe);
        holder.cookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ketika cookmark diclick maka akan add ke cookmark list
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class SearchResultRecipeHolder extends RecyclerView.ViewHolder{
        private ImageView recipeImageView;
        private TextView recipeNameTextView;
        private TextView recipeTypeTextView;
        private TextView cookmarkCountTextView;
        private TextView servingsCountTextView;
        private TextView hoursTextView;
        private TextView minutesTextView;
        private ImageView cookmarkButton;

        public SearchResultRecipeHolder(@NonNull View itemView) {
            super(itemView);

            recipeImageView = itemView.findViewById(R.id.recipeImageSR);
            recipeNameTextView = itemView.findViewById(R.id.recipeNameSR);
            recipeTypeTextView = itemView.findViewById(R.id.recipeTypeSR);
            cookmarkCountTextView = itemView.findViewById(R.id.cookmarkCountSR);
            servingsCountTextView = itemView.findViewById(R.id.servingsCountSR);
            hoursTextView = itemView.findViewById(R.id.hoursSR);
            minutesTextView = itemView.findViewById(R.id.minutesSR);
            cookmarkButton = itemView.findViewById(R.id.cookmarkButtonSR);
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

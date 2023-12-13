package com.example.cookmark_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.cookmark_app.R;
import com.example.cookmark_app.RecipeDetailActivity;
import com.example.cookmark_app.model.Recipe;

import java.util.ArrayList;

public class RegularRecipeListAdapter extends RecyclerView.Adapter<RegularRecipeListAdapter.ViewHolder> {
    private ArrayList<Recipe> items;
    private Context context;
    private FragmentManager fragmentManager;

    public RegularRecipeListAdapter(ArrayList<Recipe> items, FragmentManager fragmentManager) {
        this.items = items;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_list, parent, false);
        context = parent.getContext();
        return new ViewHolder(inflate, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(items.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Recipe recipe = items.get(position);
                    Intent intent = new Intent(context, RecipeDetailActivity.class);
                    intent.putExtra("recipe", recipe);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cookmarksTxt, titleTxt, durationTxt, servingsTxt;
        private ImageView recipePhoto, cookmarkIcon, foodtypeIcon;
        private int cardLayoutType;

        public ViewHolder(@NonNull View itemView, int cardLayoutType) {
            super(itemView);
            this.cardLayoutType = cardLayoutType;

            titleTxt = itemView.findViewById(R.id.recipe_title);
            cookmarksTxt = itemView.findViewById(R.id.recipe_cookmarks);
            durationTxt = itemView.findViewById(R.id.recipe_duration);
            servingsTxt = itemView.findViewById(R.id.recipe_servings);

            recipePhoto = itemView.findViewById(R.id.recipe_photo);
            cookmarkIcon = itemView.findViewById(R.id.cookmark_icon);
            foodtypeIcon = itemView.findViewById(R.id.foodtype_icon);
        }

        public void bindData(Recipe recipe) {
            titleTxt.setText(recipe.getRecipeName());
            int servings = recipe.getServings();
            servingsTxt.setText(String.valueOf(servings));
            int minutes = recipe.getMinutes();
            durationTxt.setText(String.valueOf(minutes) + " min");
            int cookmarkCount = recipe.getCookmarkCount(); // implement logic to get cookmark count
            cookmarksTxt.setText(String.valueOf(cookmarkCount) + " Cookmarked");

            String imageUrl = recipe.getRecipeImage();
            int placeholderImage = R.drawable.img_placeholder;

            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(placeholderImage)
                    .transform(new GranularRoundedCorners(20, 20, 0, 0))
                    .into(recipePhoto);

        }

    }

}

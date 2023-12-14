package com.example.cookmark_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.cookmark_app.R;
import com.example.cookmark_app.RecipeDetailActivity;
import com.example.cookmark_app.model.Recipe;
import com.example.cookmark_app.utils.CookmarkStatusManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegularRecipeListAdapter extends RecyclerView.Adapter<RegularRecipeListAdapter.ViewHolder> {
    private ArrayList<Recipe> items;
    private Context context;
    private FragmentManager fragmentManager;
    private CookmarkStatusManager cookmarkStatusManager;

    public RegularRecipeListAdapter(ArrayList<Recipe> items, FragmentManager fragmentManager) {
        this.items = items;
        this.fragmentManager = fragmentManager;
        this.cookmarkStatusManager = CookmarkStatusManager.getInstance();
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
                    boolean currentStatus = getCookmarkStatus(recipe.getRecipeId());
                    Intent intent = new Intent(context, RecipeDetailActivity.class);
                    intent.putExtra("recipe", recipe);
                    intent.putExtra("currentCookmarkStatus", currentStatus);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //awalnya static
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cookmarksTxt, titleTxt, durationTxt, servingsTxt, foodtypeTxt;
        private ImageView recipePhoto, cookmarkIcon;

        public ViewHolder(@NonNull View itemView, int cardLayoutType) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.recipe_title);
            cookmarksTxt = itemView.findViewById(R.id.recipe_cookmarks);
            durationTxt = itemView.findViewById(R.id.recipe_duration);
            servingsTxt = itemView.findViewById(R.id.recipe_servings);
            foodtypeTxt = itemView.findViewById(R.id.recipe_foodtype);

            recipePhoto = itemView.findViewById(R.id.recipe_photo);
            cookmarkIcon = itemView.findViewById(R.id.cookmark_icon);
        }

        public void bindData(Recipe recipe) {
            titleTxt.setText(recipe.getRecipeName());
            foodtypeTxt.setText(recipe.getFoodType());
            int servings = recipe.getServings();
            servingsTxt.setText(String.valueOf(servings));
            int minutes = recipe.getMinutes();
            durationTxt.setText(String.valueOf(minutes) + " min");
            int cookmarkCount = recipe.getCookmarkCount();
            cookmarksTxt.setText(String.valueOf(cookmarkCount) + " Cookmarked");

            cookmarkIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        items.get(position);

                        boolean currentStatus = getCookmarkStatus(recipe.getRecipeId());
                        setCookmarkStatus(recipe.getRecipeId(), !currentStatus);

                        cookmarkIcon.setImageResource(getCookmarkStatus(recipe.getRecipeId()) ? R.drawable.ic_cookmarked : R.drawable.ic_uncookmarked);
                        cookmarkIcon.invalidate();

                        String toastMessage = getCookmarkStatus(recipe.getRecipeId()) ? "Cookmarked " + recipe.getRecipeName() : "Uncookmarked " + recipe.getRecipeName();
                        Toast.makeText(itemView.getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });


            String imageUrl = recipe.getRecipeImage();
            int placeholderImage = R.drawable.img_placeholder;

            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(placeholderImage)
                    .transform(new GranularRoundedCorners(20, 20, 0, 0))
                    .into(recipePhoto);

        }

    }

    private boolean getCookmarkStatus(String recipeId) {
        return cookmarkStatusManager.getCookmarkStatus(recipeId);
    }

    private void setCookmarkStatus(String recipeId, boolean isCookmarked) {
        cookmarkStatusManager.setCookmarkStatus(recipeId, isCookmarked);
    }
}



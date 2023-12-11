package com.example.cookmark_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.cookmark_app.R;
import com.example.cookmark_app.model.Recipe;

import java.util.ArrayList;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {

    ArrayList<Recipe> items;
    Context context;

    public RecipeListAdapter(ArrayList<Recipe> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleTxt.setText(items.get(position).getRecipeName());
        int servings = items.get(position).getServings();
        holder.servingsTxt.setText(String.valueOf(servings));
        int minutes = items.get(position).getMinutes();
        holder.durationTxt.setText(String.valueOf(minutes) + " min");
        int cookmarkCount = items.get(position).getCookmarkCount(); //belum ada getCookmarkCount() & logic dapetin jumlah cookmarknya
        holder.cookmarksTxt.setText(String.valueOf(cookmarkCount) + " Cookmarked");

        String imageUrl = items.get(position).getRecipeImage();
        int placeholderImage = R.drawable.img_placeholder;

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(placeholderImage)
                .transform(new GranularRoundedCorners(20, 20, 0, 0))
                .into(holder.recipePhoto);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView cookmarksTxt, titleTxt, durationTxt, servingsTxt;
        ImageView recipePhoto, cookmarkIcon, foodtypeIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.recipe_title);
            cookmarksTxt = itemView.findViewById(R.id.recipe_cookmarks);
            durationTxt = itemView.findViewById(R.id.recipe_duration);
            servingsTxt = itemView.findViewById(R.id.recipe_servings);

            recipePhoto = itemView.findViewById(R.id.recipe_photo);
            cookmarkIcon = itemView.findViewById(R.id.cookmark_icon);
            foodtypeIcon = itemView.findViewById(R.id.foodtype_icon);

        }
    }
}

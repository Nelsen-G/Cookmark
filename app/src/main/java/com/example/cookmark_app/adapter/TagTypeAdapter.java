package com.example.cookmark_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookmark_app.R;
import com.example.cookmark_app.model.Ingredient;

import java.util.ArrayList;

public class TagTypeAdapter extends RecyclerView.Adapter<TagTypeAdapter.TagViewHolder> {
    private ArrayList<Ingredient> tagList;

    public TagTypeAdapter(ArrayList<Ingredient> tagList) {
        this.tagList = tagList;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        return new TagViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Ingredient ingredient = tagList.get(position);
        holder.tagTextView.setText(ingredient.getName());
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tagTextView;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagTextView = itemView.findViewById(R.id.tag_tv);
        }
    }

}


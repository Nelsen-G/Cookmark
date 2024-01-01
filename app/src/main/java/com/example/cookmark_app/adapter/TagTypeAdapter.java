package com.example.cookmark_app.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookmark_app.R;
import com.example.cookmark_app.interfaces.OnItemClickCallback;
import com.example.cookmark_app.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class TagTypeAdapter extends RecyclerView.Adapter<TagTypeAdapter.TagViewHolder> {
    private ArrayList<Ingredient> tagList;
    private OnItemClickCallback onItemClickCallback;

    public TagTypeAdapter(ArrayList<Ingredient> tagList, OnItemClickCallback onItemClickCallback) {
        this.tagList = tagList;
        this.onItemClickCallback = onItemClickCallback;
    }

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

        holder.tagLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickCallback != null) {
                    onItemClickCallback.onItemClicked(ingredient);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    public void updateData(List<Ingredient> newData) {
        tagList.clear();
        tagList.addAll(newData);
        notifyDataSetChanged();
    }

    static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tagTextView;
        CardView tagLayout;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagTextView = itemView.findViewById(R.id.tag_tv);
            tagLayout = itemView.findViewById(R.id.tagLayout);
        }
    }



}


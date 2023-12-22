package com.example.cookmark_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookmark_app.R;
import com.example.cookmark_app.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private List<Ingredient> ingredients;


    public IngredientAdapter(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient);

        holder.btnDeleteIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) { //pembetulan error ini nice
                    ingredients.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);

                    if (ingredients.isEmpty()) {
                        notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private Button btnDeleteIngredient;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewIngredientName);
            btnDeleteIngredient = itemView.findViewById(R.id.btnDeleteIngredient);
        }

        public void bind(Ingredient ingredient) {
            textViewName.setText(ingredient.getName());
        }
    }
}
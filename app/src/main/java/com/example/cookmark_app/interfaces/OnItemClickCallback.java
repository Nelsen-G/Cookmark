package com.example.cookmark_app.interfaces;

import com.example.cookmark_app.model.Ingredient;

public interface OnItemClickCallback {
    // handle click on recycler view ingredients search
    void onItemClicked(Ingredient ingredient);

}

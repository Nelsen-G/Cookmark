package com.example.cookmark_app.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookmark_app.R;
import com.example.cookmark_app.adapter.TagTypeAdapter;
import com.example.cookmark_app.interfaces.OnItemClickCallback;
import com.example.cookmark_app.model.Ingredient;

import java.util.ArrayList;

// a popup to show what ingredients that user selected
public class SearchRecipePopup extends Dialog implements OnItemClickCallback {

    private ArrayList<Ingredient> selectedIngredients;
    private TagTypeAdapter tagTypeAdapter;
    public SearchRecipePopup(@NonNull Context context, ArrayList<Ingredient> ingredients) {
        super(context);
        this.selectedIngredients = ingredients;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_searchrecipe);

        // page logic here
        RecyclerView ingreAddRv = findViewById(R.id.rvIngredientSearch);
        // create adapter and set it to recycler view
        tagTypeAdapter = new TagTypeAdapter(selectedIngredients, this);
        ingreAddRv.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 is the number of columns
        ingreAddRv.setAdapter(tagTypeAdapter);


        // add data from search fragment to recycler view in popup

        Button ingreSearchBtn = findViewById(R.id.buttonSearchRecipe);
        // if button is clicked, it will redirect to search result


        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onItemClicked(Ingredient ingredient) {
        // everytime a tag in selected ingredient clicked
        // it will delete the selected ingredient
        selectedIngredients.remove(ingredient);
        tagTypeAdapter.notifyDataSetChanged();
    }

    public ArrayList<Ingredient> getSelectedIngredients() {
        return selectedIngredients;
    }

}

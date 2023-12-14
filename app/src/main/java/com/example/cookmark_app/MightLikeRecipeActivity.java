package com.example.cookmark_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cookmark_app.adapter.SmallerRecipeListAdapter;
import com.example.cookmark_app.model.Recipe;

import java.util.ArrayList;

public class MightLikeRecipeActivity extends AppCompatActivity {
    private RecyclerView recyclerViewRecipe;
    private RecyclerView.Adapter adapterRecipeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_recipe);

        ArrayList<Recipe> items = (ArrayList<Recipe>) getIntent().getSerializableExtra("items");

        ImageView backToPrevious = findViewById(R.id.seeall_back_iv);
        TextView titleToolBar = findViewById(R.id.seeall_toolbar_title_tv);

        titleToolBar.setText("You might like...");

        initializeRecyclerView(items);

        backToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initializeRecyclerView(ArrayList<Recipe> items) {
        recyclerViewRecipe = findViewById(R.id.seeall_rv);

        recyclerViewRecipe.setLayoutManager(new GridLayoutManager(this, 2));

        adapterRecipeList = new SmallerRecipeListAdapter(items, getSupportFragmentManager());
        recyclerViewRecipe.setAdapter(adapterRecipeList);
    }
}
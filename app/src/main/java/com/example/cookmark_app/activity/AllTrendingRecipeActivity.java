package com.example.cookmark_app.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookmark_app.R;
import com.example.cookmark_app.adapter.SmallerRecipeListAdapter;
import com.example.cookmark_app.model.Recipe;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AllTrendingRecipeActivity extends AppCompatActivity {
    private RecyclerView recyclerViewRecipe;
    private RecyclerView.Adapter adapterRecipeList;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_recipe);

        userId = getIntent().getStringExtra("user_id");

        initializeRecyclerView(new ArrayList<>());

        ImageView backToPrevious = findViewById(R.id.seeall_back_iv);
        TextView titleToolBar = findViewById(R.id.seeall_toolbar_title_tv);

        titleToolBar.setText("All Trendings");
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

        adapterRecipeList = new SmallerRecipeListAdapter(items, getSupportFragmentManager(), userId);
        recyclerViewRecipe.setAdapter(adapterRecipeList);

        connectToDatabase(items, adapterRecipeList);
    }

    private void connectToDatabase(ArrayList<Recipe> items, RecyclerView.Adapter adapterRecipeList) {
        Query query = FirebaseFirestore.getInstance().collection("recipes").orderBy("cookmarkCount", Query.Direction.DESCENDING);
        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        items.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Recipe recipe = document.toObject(Recipe.class);
                            items.add(recipe);
                        }
                        adapterRecipeList.notifyDataSetChanged();
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}

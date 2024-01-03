package com.example.cookmark_app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.cookmark_app.R;
import com.example.cookmark_app.adapter.SmallerRecipeListAdapter;
import com.example.cookmark_app.model.Recipe;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QuickRecipeActivity extends AppCompatActivity {
    private RecyclerView recyclerViewRecipe;
    private ArrayList<Recipe> recipeList, originalRecipeList;

    private RecyclerView.Adapter adapterRecipeList;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_recipe);

        SharedPreferences sp1 = this.getSharedPreferences("Login", MODE_PRIVATE);
        userId = sp1.getString("userid", null);
        originalRecipeList = new ArrayList<>();
        recipeList = new ArrayList<>();

        initializeRecyclerView(recipeList);

        ImageView backToPrevious = findViewById(R.id.seeall_back_iv);
        TextView titleToolBar = findViewById(R.id.seeall_toolbar_title_tv);
        SearchView recipeSearchView = findViewById(R.id.seeall_searchbar);
        recipeSearchView.clearFocus();
        recipeSearchView.setQueryHint("Search Quick-To-Cook Recipe...");

        titleToolBar.setText("Quick Cook");
        backToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recipeSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s)) {
                    updateAdapter(originalRecipeList);
                } else {
                    ArrayList<Recipe> filteredRecipe = new ArrayList<>();
                    for (Recipe r : originalRecipeList) {
                        if (r.getRecipeName().toLowerCase().contains(s.toLowerCase())) {
                            filteredRecipe.add(r);
                        }
                    }
                    updateAdapter(filteredRecipe);
                }
                return true;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refresh();
    }

    private void initializeRecyclerView(ArrayList<Recipe> items) {
        recyclerViewRecipe = findViewById(R.id.seeall_rv);
        recyclerViewRecipe.setLayoutManager(new GridLayoutManager(this, 2));

        adapterRecipeList = new SmallerRecipeListAdapter(items, getSupportFragmentManager(), userId, this);
        recyclerViewRecipe.setAdapter(adapterRecipeList);

        connectToDatabase();
    }

    private void connectToDatabase() {
        Query query = FirebaseFirestore.getInstance().collection("recipes").orderBy("totalMinutes");
        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Recipe> items = new ArrayList<>();
                        items.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Recipe recipe = document.toObject(Recipe.class);
                            items.add(recipe);
                        }
                        originalRecipeList.clear();
                        originalRecipeList.addAll(items);

                        updateAdapter(items);
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void refresh() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
        Log.d("TAG", "onRestart: aaa");
    }

    private void updateAdapter(List<Recipe> recipes) {
        recipeList.clear();
        recipeList.addAll(recipes);
        adapterRecipeList.notifyDataSetChanged();
    }
}

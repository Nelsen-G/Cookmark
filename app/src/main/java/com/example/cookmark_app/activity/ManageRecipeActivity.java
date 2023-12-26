package com.example.cookmark_app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.cookmark_app.R;
import com.example.cookmark_app.adapter.ManageRecipeAdapter;
import com.example.cookmark_app.model.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageRecipeActivity extends AppCompatActivity {
    private static final String TAG = "ManageRecipe";
    private RecyclerView recyclerView;
    private ManageRecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_recipe);

        ImageView backToPrevious = findViewById(R.id.manage_back);
        backToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerViewRecipe);
        recipeList = new ArrayList<>();

        recipeAdapter = new ManageRecipeAdapter(recipeList, new ManageRecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Recipe recipe) {
                Intent intent = new Intent(ManageRecipeActivity.this, EditRecipeActivity.class);
                intent.putExtra("recipeId", recipe.getRecipeId());
                startActivity(intent);
                finish();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recipeAdapter);

        getRecipeData();


    }

    private void getRecipeData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String userId = intent.getStringExtra("user_id");

        db.collection("recipes")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Recipe> recipes = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Recipe recipe = document.toObject(Recipe.class);
                                recipes.add(recipe);
                            }

                            updateAdapter(recipes);
                        } else {
                            Log.w(TAG, "Error");
                        }
                    }
                });
    }

    private void updateAdapter(List<Recipe> recipes) {
        recipeList.clear();
        recipeList.addAll(recipes);
        recipeAdapter.notifyDataSetChanged();

        LinearLayout emptyLayout = findViewById(R.id.emptyLayout);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewRecipe);
        if (recipes.isEmpty()) {
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        } else {
            emptyLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

}
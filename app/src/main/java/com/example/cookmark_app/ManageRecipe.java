package com.example.cookmark_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.cookmark_app.adapter.ManageRecipeAdapter;
import com.example.cookmark_app.model.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageRecipe extends AppCompatActivity {
    private static final String TAG = "ManageRecipe";
    private RecyclerView recyclerView;
    private ManageRecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_recipe);

        recyclerView = findViewById(R.id.recyclerViewRecipe);
        recipeList = new ArrayList<>();
//        recipeAdapter = new ManageRecipeAdapter(recipeList);
        recipeAdapter = new ManageRecipeAdapter(recipeList, new ManageRecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Recipe recipe) {
                Intent intent = new Intent(ManageRecipe.this, EditRecipeActivity.class);
                intent.putExtra("recipeId", recipe.getRecipeId());
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recipeAdapter);

        getRecipeData();


    }

    private void getRecipeData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("recipes")
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
                            Log.w(TAG, "Error fetch", task.getException());
                        }
                    }
                });
    }

    private void updateAdapter(List<Recipe> recipes) {
        recipeList.clear();
        recipeList.addAll(recipes);
        recipeAdapter.notifyDataSetChanged();
    }

}
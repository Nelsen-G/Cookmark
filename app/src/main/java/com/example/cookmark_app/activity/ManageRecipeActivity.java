package com.example.cookmark_app.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

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
    private List<Recipe> recipeList, originalRecipeList;
    private FirebaseFirestore db;
    private LinearLayout emptyLayout;
    String userId;

    private final ActivityResultLauncher<Intent> editRecipeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    getRecipeData();
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_recipe);
        emptyLayout = findViewById(R.id.emptyLayout);
        recyclerView = findViewById(R.id.recyclerViewRecipe);
        ImageView backToPrevious = findViewById(R.id.manage_back);
        backToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageRecipeActivity.this, MainActivity.class);
                intent.putExtra("loadFragment", "account");
                startActivity(intent);
                finish();
            }
        });

        recipeList = new ArrayList<>();

        recipeAdapter = new ManageRecipeAdapter(recipeList, new ManageRecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Recipe recipe) {
                Intent intent = new Intent(ManageRecipeActivity.this, EditRecipeActivity.class);
                intent.putExtra("recipeId", recipe.getRecipeId());
                editRecipeLauncher.launch(intent);
//                startActivity(intent);
//                finish();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recipeAdapter);

        getRecipeData();

        SearchView recipeSearchView = findViewById(R.id.recipeSearchBar);
        recipeSearchView.clearFocus();

        originalRecipeList = new ArrayList<>();
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
    protected void onResume() {
        super.onResume();
        getRecipeData();
    }

    private void updateEmptyLayoutVisibility() {

        if (recipeList.isEmpty()) {
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void getRecipeData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        SharedPreferences sp1 = this.getSharedPreferences("Login", MODE_PRIVATE);
        userId = sp1.getString("userid", null);

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

                            originalRecipeList.clear();
                            originalRecipeList.addAll(recipes);

                            recipeList.clear();
                            recipeList.addAll(recipes);
                            updateAdapter(recipes);

                            updateEmptyLayoutVisibility();

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
        updateEmptyLayoutVisibility();
    }

}
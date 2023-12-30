package com.example.cookmark_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookmark_app.adapter.ManageRecipeAdapter;
import com.example.cookmark_app.adapter.SearchResultRecipeAdapter;
import com.example.cookmark_app.adapter.TagTypeAdapter;
import com.example.cookmark_app.interfaces.OnItemClickCallback;
import com.example.cookmark_app.model.Ingredient;
import com.example.cookmark_app.model.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity implements OnItemClickCallback {


    private RecyclerView rvIngredients;
    private RecyclerView rvRecipes;
    private SearchResultRecipeAdapter searchResultAdapter;
    private TagTypeAdapter tagTypeAdapter;
    private ArrayList<Recipe> recipeList;
    private ArrayList<Ingredient> ingredientList;
    private FirebaseFirestore db;

    private OnItemClickCallback onItemClickCallback;

    public SearchResultActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresult);

        ImageView backToPrevious = findViewById(R.id.manage_back);
        backToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recipeList = new ArrayList<>();
        ingredientList = new ArrayList<>();

        rvIngredients = findViewById(R.id.ingreSearchRv);
        rvRecipes = findViewById(R.id.searchResultRv);


        // ingredient recycler view
        Intent intent = getIntent();
        ArrayList<Ingredient> selectedIngredients = (ArrayList<Ingredient>) intent.getSerializableExtra("selectedIngredients");

        tagTypeAdapter = new TagTypeAdapter(selectedIngredients, this);
        rvIngredients.setLayoutManager(new GridLayoutManager(this, 2));
        rvIngredients.setAdapter(tagTypeAdapter);

        getRecipeData();
        searchResultAdapter = new SearchResultRecipeAdapter(onItemClickCallback, recipeList);
        // recipe recycler view
        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        rvRecipes.setAdapter(searchResultAdapter);

    }

    private void getRecipeData() {
        // ini nanti diganti
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
                            Log.w("search result", "Error fetch", task.getException());
                        }
                    }
                });
    }

    private void updateAdapter(List<Recipe> recipes) {
        recipeList.clear();
        recipeList.addAll(recipes);
        searchResultAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(Ingredient ingredient) {
        // when ingredient item is clicked
    }

    @Override
    public void onItemClicked(Recipe recipe) {
        // when recipe item is clicked
    }
}

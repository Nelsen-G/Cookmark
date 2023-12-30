package com.example.cookmark_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookmark_app.adapter.ManageRecipeAdapter;
import com.example.cookmark_app.adapter.SearchResultRecipeAdapter;
import com.example.cookmark_app.adapter.TagTypeAdapter;
import com.example.cookmark_app.fragment.SearchFragment;
import com.example.cookmark_app.interfaces.OnItemClickCallback;
import com.example.cookmark_app.model.Ingredient;
import com.example.cookmark_app.model.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity implements OnItemClickCallback {


    private RecyclerView rvIngredients;
    private RecyclerView rvRecipes;
    private SearchResultRecipeAdapter searchResultAdapter;
    private TagTypeAdapter tagTypeAdapter;
    private ArrayList<Recipe> recipeList;
    private ArrayList<Ingredient> ingredientList;
    private ArrayList<Ingredient> selectedIngredients;

    private Button addIngredientBtn;
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
        selectedIngredients = (ArrayList<Ingredient>) intent.getSerializableExtra("selectedIngredients");

        tagTypeAdapter = new TagTypeAdapter(selectedIngredients, this);
        rvIngredients.setLayoutManager(new GridLayoutManager(this, 2));
        rvIngredients.setAdapter(tagTypeAdapter);

        getRecipeData();
        searchResultAdapter = new SearchResultRecipeAdapter(onItemClickCallback, recipeList);
        // recipe recycler view
        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        rvRecipes.setAdapter(searchResultAdapter);

        // add button
        addIngredientBtn = findViewById(R.id.addIngredientBtn);
        addIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                            ArrayList<Recipe> recipes = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Recipe recipe = document.toObject(Recipe.class);
                                // filter based on ingredient
                                if(containsAnyIngredient(recipe, selectedIngredients)) {
                                    recipes.add(recipe);
                                }
                            }

                            // sort based on number of matched ingredients
                            Collections.sort(recipes, new Comparator<Recipe>() {
                               @Override
                               public int compare(Recipe recipe1, Recipe recipe2) {
                                   int count1 = countMatchedIngredients(recipe1, selectedIngredients);
                                   int count2 = countMatchedIngredients(recipe2, selectedIngredients);
                                   return Integer.compare(count2, count1);
                               }
                            });
                            updateAdapter(recipes);
                        } else {
                            Log.w("search result", "Error fetch", task.getException());
                        }
                    }
                });
    }

    private int countMatchedIngredients(Recipe recipe, List<Ingredient> selectedIngredients) {
        int count = 0;

        String ingredientListAsString = recipe.getIngredientListAsString();
        List<Ingredient> recipeIngredients = parseIngredientList(ingredientListAsString);

        for (Ingredient userIngredient : selectedIngredients) {
            for (Ingredient recipeIngredient : recipeIngredients) {
                if (userIngredient.getName().equalsIgnoreCase(recipeIngredient.getName())) {
                    count++;
                }
            }
        }

        return count;
    }

    private boolean containsAnyIngredient(Recipe recipe, List<Ingredient> selectedIngredients) {
        // Get the ingredient list as a string from the document
        String ingredientListAsString = recipe.getIngredientListAsString();

        // Parse the string to a List<Ingredient>
        List<Ingredient> recipeIngredients = parseIngredientList(ingredientListAsString);

        // Check if any of the user-specified ingredients are present in the recipe
        for (Ingredient userIngredient : selectedIngredients) {
            for (Ingredient recipeIngredient : recipeIngredients) {
                if (userIngredient.getName().equalsIgnoreCase(recipeIngredient.getName())) {
                    return true; // Recipe contains at least one of the user-specified ingredients
                }
            }
        }

        return false; // Recipe does not contain any of the user-specified ingredients
    }

    private List<Ingredient> parseIngredientList(String ingredientListAsString) {
        // Parse the string to a List<Ingredient>
        List<Ingredient> recipeIngredients = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(ingredientListAsString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String ingredientName = jsonObject.getString("name");
                Ingredient ingredient = new Ingredient(ingredientName);
                recipeIngredients.add(ingredient);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recipeIngredients;
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

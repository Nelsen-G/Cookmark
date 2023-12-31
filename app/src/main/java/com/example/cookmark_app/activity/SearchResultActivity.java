package com.example.cookmark_app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookmark_app.R;
import com.example.cookmark_app.adapter.SmallerRecipeListAdapter;
import com.example.cookmark_app.adapter.TagTypeAdapter;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {
    private RecyclerView rvIngredients, rvRecipes;
    private TextView titleToolBar;
    private ScrollView ingreScrollView;

    private SearchView recipeSearchView;
    private SmallerRecipeListAdapter searchResultAdapter;
    private TagTypeAdapter tagTypeAdapter;
    private ArrayList<Recipe> recipeList, allRecipe;
    private ArrayList<Ingredient> ingredientList, selectedIngredients;

    private Button addIngredientBtn;
    private FirebaseFirestore db;


    public SearchResultActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresult);

        recipeList = new ArrayList<>();
        ingredientList = new ArrayList<>();
        allRecipe = new ArrayList<>();

        ImageView backToPrevious = findViewById(R.id.searchResultBack);
        titleToolBar = findViewById(R.id.searchResultTitleToolbar);
        ingreScrollView = findViewById(R.id.ingreScrollView);

        backToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchResultActivity.this, MainActivity.class);
                intent.putExtra("loadFragment", "search");
                startActivity(intent);
                finish();
            }
        });

        rvIngredients = findViewById(R.id.ingreSearchRv);
        rvRecipes = findViewById(R.id.searchResultRv);

        // ingredient recycler view
        Intent intent = getIntent();
        selectedIngredients = (ArrayList<Ingredient>) intent.getSerializableExtra("selectedIngredients");

        // kalo tag diklik gabisa apa2
        tagTypeAdapter = new TagTypeAdapter(selectedIngredients);
        rvIngredients.setLayoutManager(new GridLayoutManager(this, 2));
        rvIngredients.setAdapter(tagTypeAdapter);
        tagTypeAdapter.notifyDataSetChanged();
        setupIngreScrollViewHeight(tagTypeAdapter.getItemCount());

        getRecipeData();
        SharedPreferences sp1 = this.getSharedPreferences("Login", MODE_PRIVATE);
        String userid=sp1.getString("userid", null);
        searchResultAdapter = new SmallerRecipeListAdapter(recipeList, getSupportFragmentManager(), userid, this);
        // recipe recycler view
        rvRecipes.setLayoutManager(new GridLayoutManager(this, 2));
        rvRecipes.setAdapter(searchResultAdapter);

        // add button
        addIngredientBtn = findViewById(R.id.addIngredientBtn);
        addIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // search bar for searching recipe
        recipeSearchView = findViewById(R.id.recipeSearchBar);
        recipeSearchView.clearFocus();

        recipeSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(TextUtils.isEmpty(s)) {
                    // copy the backup array to the recipeList
                    for (Recipe rec : recipeList) {
                        allRecipe.add(new Recipe(rec));  // Assuming Ingredient has a copy constructor
                    }
                    updateAdapter(allRecipe);
                }
                else {
                    ArrayList<Recipe> filteredRecipe = new ArrayList<>();
                    for (Recipe r : recipeList) {
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

    private void setupIngreScrollViewHeight(int ingredientItemCount) {
        int newHeight = ingredientItemCount <= 2 ? (int) getResources().getDimension(R.dimen.scroll_view_height_small) : (int) getResources().getDimension(R.dimen.scroll_view_height_large);
        ViewGroup.LayoutParams params = ingreScrollView.getLayoutParams();
        params.height = newHeight;
        ingreScrollView.setLayoutParams(params);
    }

    private void getRecipeData() {
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
                            updateRecipeList(recipes);
                        } else {
                            Log.w("search result", "Error fetch", task.getException());
                        }
                    }
                });
    }

    private void updateRecipeList(List<Recipe> recipes) {
        allRecipe.clear();
        allRecipe.addAll(recipes);
        int numOfMatchedRecipes = countMatchedRecipes(allRecipe, selectedIngredients);
        if (numOfMatchedRecipes == 0 || numOfMatchedRecipes == 1) {
            titleToolBar.setText("Result (" + numOfMatchedRecipes + ")");
        } else {
            titleToolBar.setText("Results (" + numOfMatchedRecipes + ")");
        }
        updateAdapter(recipes);
    }

    private int countMatchedRecipes(List<Recipe> recipes, List<Ingredient> selectedIngredients) {
        int count = 0;
        for (Recipe recipe : recipes) {
            if (countMatchedIngredients(recipe, selectedIngredients) > 0) {
                count++;
            }
        }
        return count;
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

}

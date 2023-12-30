package com.example.cookmark_app.fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.cookmark_app.R;
import com.example.cookmark_app.dialog.SearchRecipePopup;
import com.example.cookmark_app.adapter.TagTypeAdapter;
import com.example.cookmark_app.interfaces.OnItemClickCallback;
import com.example.cookmark_app.model.Ingredient;
import com.example.cookmark_app.model.Recipe;

import java.util.ArrayList;

// search page to show all ingredient and let user pick ingredients they have
public class SearchFragment extends Fragment implements OnItemClickCallback {

    private ArrayList<Ingredient> ingredients1;
    private ArrayList<Ingredient> ingredients2;
    private ArrayList<Ingredient> ingredients3;
    private ArrayList<Ingredient> ingredients4;
    private ArrayList<Ingredient> ingredients5;

    private ArrayList<Ingredient> selectedIngredients;

    private SearchView searchBar;
    private View rootView;

    private SearchRecipePopup popup;

    private TextView seeSelected;
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        // fill the arraylist
        fillIngredientData();


        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        initializeRecyclerView(rootView, R.id.rvIngredientsData1, ingredients1);
        initializeRecyclerView(rootView, R.id.rvIngredientsData2, ingredients2);
        initializeRecyclerView(rootView, R.id.rvIngredientsData3, ingredients3);
        initializeRecyclerView(rootView, R.id.rvIngredientsData4, ingredients4);
        initializeRecyclerView(rootView, R.id.rvIngredientsData5, ingredients5);

        // search bar
        searchBar = rootView.findViewById(R.id.ingredientSearchBar);
        searchBar.clearFocus();


        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // change recycler view everytime user type something
                // 1
                ArrayList<Ingredient> filteredIngredients1 = new ArrayList<>();
                for (Ingredient ingredient : ingredients1) {
                    if (ingredient.getName().toLowerCase().contains(newText.toLowerCase())) {
                        filteredIngredients1.add(ingredient);
                    }
                }
                int recyclerViewId1 = getRecyclerViewId(ingredients1);
                initializeRecyclerView(rootView, recyclerViewId1, filteredIngredients1);

                // 2
                ArrayList<Ingredient> filteredIngredients2 = new ArrayList<>();
                for (Ingredient ingredient : ingredients2) {
                    if (ingredient.getName().toLowerCase().contains(newText.toLowerCase())) {
                        filteredIngredients2.add(ingredient);
                    }
                }
                int recyclerViewId2 = getRecyclerViewId(ingredients2);
                initializeRecyclerView(rootView, recyclerViewId2, filteredIngredients2);

                // 3
                ArrayList<Ingredient> filteredIngredients3 = new ArrayList<>();
                for (Ingredient ingredient : ingredients3) {
                    if (ingredient.getName().toLowerCase().contains(newText.toLowerCase())) {
                        filteredIngredients3.add(ingredient);
                    }
                }
                int recyclerViewId3 = getRecyclerViewId(ingredients3);
                initializeRecyclerView(rootView, recyclerViewId3, filteredIngredients3);

                // 4
                ArrayList<Ingredient> filteredIngredients4 = new ArrayList<>();
                for (Ingredient ingredient : ingredients4) {
                    if (ingredient.getName().toLowerCase().contains(newText.toLowerCase())) {
                        filteredIngredients4.add(ingredient);
                    }
                }
                int recyclerViewId4 = getRecyclerViewId(ingredients4);
                initializeRecyclerView(rootView, recyclerViewId4, filteredIngredients4);

                // 5
                ArrayList<Ingredient> filteredIngredients5 = new ArrayList<>();
                for (Ingredient ingredient : ingredients5) {
                    if (ingredient.getName().toLowerCase().contains(newText.toLowerCase())) {
                        filteredIngredients5.add(ingredient);
                    }
                }
                int recyclerViewId5 = getRecyclerViewId(ingredients5);
                initializeRecyclerView(rootView, recyclerViewId5, filteredIngredients5);

                return true;
            }
        });

        // initialized selected ingredients
        selectedIngredients = new ArrayList<>();
        // pass selected ingredient into popup
        popup = new SearchRecipePopup(getContext(), selectedIngredients);

        // when see selected ingredient clicked, it will show popup
        seeSelected = rootView.findViewById(R.id.buttonSeeSelected);
        seeSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup = new SearchRecipePopup(getContext(), selectedIngredients);
                popup.show();
            }
        });

        return rootView;

    }

    private void initializeRecyclerView(View rootView, int recyclerViewId, ArrayList<Ingredient> ingredients) {

        RecyclerView recyclerView = rootView.findViewById(recyclerViewId);

        // Create the adapter and set it to the RecyclerView
        TagTypeAdapter tagTypeAdapter = new TagTypeAdapter(ingredients, this);
        // display the tag into 2 column
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // 2 is the number of columns

        recyclerView.setAdapter(tagTypeAdapter);


    }


    private int getRecyclerViewId(ArrayList<Ingredient> ingredients) {

        if (ingredients == ingredients1) return R.id.rvIngredientsData1;
        if (ingredients == ingredients2) return R.id.rvIngredientsData2;
        if (ingredients == ingredients3) return R.id.rvIngredientsData3;
        if (ingredients == ingredients4) return R.id.rvIngredientsData4;
        if (ingredients == ingredients5) return R.id.rvIngredientsData5;
        return 0;
    }

    private void fillIngredientData() {
        // dairy
        ingredients1 = new ArrayList<>();
        String[] dummy1 = {"Butter", "Milk", "Heavy Cream", "Whipping Cream", "Yogurt", "Cream Cheese", "Cheddar Cheese", "Mozzarella Cheese"};
        for(String s : dummy1) {
            ingredients1.add(new Ingredient(s));
        }

        // meat
        ingredients2 = new ArrayList<>();
        String[] dummy2 = {"Chicken", "Beef", "Pork", "Lamb", "Bacon", "Sausages", "Ground Beef", "Ham", "Duck", "Egg"};
        for(String s : dummy2) {
            ingredients2.add(new Ingredient(s));
        }

        // vegetables
        ingredients3 = new ArrayList<>();
        String[] dummy3 = {"Sawi", "Water Spinach", "Eggplant", "Potato", "Carrot", "Long Beans", "Tomato", "Chili", "Shallots", "Garlic", "Kaffir Lime Leaves", "Bay Leaves", "Sereh", "Chayote Squash"};
        for(String s : dummy3) {
            ingredients3.add(new Ingredient(s));
        }

        // seafood
        ingredients4 = new ArrayList<>();
        String[] dummy4 = {"Fish", "Shrimp", "Crab", "Squid", "Clams", "Sardines", "Anchovies", "Tuna", "Scallops", "Mussels"};
        for(String s : dummy4) {
            ingredients4.add(new Ingredient(s));
        }

        // others
        ingredients5 = new ArrayList<>();
        String[] dummy5 = {"Tofu", "Tempeh", "Noodles", "Rice", "Bread", "Pasta", "Soy Sauce", "Honey", "Vinegar", "Broths"};
        for(String s : dummy5) {
            ingredients5.add(new Ingredient(s));
        }


    }

    @Override
    public void onItemClicked(Ingredient ingredient) {
        // everytime a tag clicked, add that item to recycler view in popup
        // validate that the data added is unique
        if(!popup.getSelectedIngredients().contains(ingredient)) {
            selectedIngredients.add(ingredient);
        }
        else {
            Log.d("search", "You have selected this ingredient");
        }

        popup = new SearchRecipePopup(getContext(), selectedIngredients);
        popup.show();
    }

    @Override
    public void onItemClicked(Recipe recipe) {
        // no usage
    }
}
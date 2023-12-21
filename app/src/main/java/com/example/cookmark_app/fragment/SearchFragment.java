package com.example.cookmark_app.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.cookmark_app.R;
import com.example.cookmark_app.adapter.IngredientAdapter;
import com.example.cookmark_app.adapter.TagTypeAdapter;
import com.example.cookmark_app.model.Ingredient;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private ArrayList<Ingredient> ingredients;
    private ArrayList<String> ingredientAll;
    private ArrayAdapter<String> adapter;
    AutoCompleteTextView searchBar;
    View rootView;

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
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        initializeRecyclerView(rootView, R.id.rvIngredientsData1, getIngredientSet1());
        initializeRecyclerView(rootView, R.id.rvIngredientsData2, getIngredientSet1());
        initializeRecyclerView(rootView, R.id.rvIngredientsData3, getIngredientSet1());
        initializeRecyclerView(rootView, R.id.rvIngredientsData4, getIngredientSet1());
        initializeRecyclerView(rootView, R.id.rvIngredientsData5, getIngredientSet1());

        searchBar = rootView.findViewById(R.id.ingredientSearchBar);
        ingredientAll = new ArrayList<>();
        initializeAutoCompleteList();
        return rootView;

    }

    private void initializeRecyclerView(View rootView, int recyclerViewId, ArrayList<Ingredient> ingredients) {

        RecyclerView recyclerView = rootView.findViewById(recyclerViewId);

        // Create the adapter and set it to the RecyclerView
        TagTypeAdapter tagTypeAdapter = new TagTypeAdapter(ingredients);
        // display the tag into 2 column
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // 2 is the number of columns

        recyclerView.setAdapter(tagTypeAdapter);

    }

    private ArrayList<Ingredient> getIngredientSet1() {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Cheese"));
        ingredients.add(new Ingredient("Salt"));
        ingredients.add(new Ingredient("Egg"));

        return ingredients;
    }

    private ArrayList<Ingredient> getIngredientsSet2() {
        // Define ingredients for the second set
        return null;
    }

    private void initializeAutoCompleteList() {
        ingredientAll.add("Cheese");
        ingredientAll.add("Salt");
        ingredientAll.add("Egg");
        adapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, ingredientAll);
        searchBar.setAdapter(adapter);
    }
}
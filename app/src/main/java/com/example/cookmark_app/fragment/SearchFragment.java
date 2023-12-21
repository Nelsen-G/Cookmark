package com.example.cookmark_app.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cookmark_app.R;
import com.example.cookmark_app.adapter.IngredientAdapter;
import com.example.cookmark_app.adapter.TagTypeAdapter;
import com.example.cookmark_app.model.Ingredient;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

   private ArrayList<Ingredient> ingredients;

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
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        initializeRecyclerView(rootView, R.id.rvIngredientsData);
        return rootView;

    }

    private void initializeRecyclerView(View rootView, int recyclerViewId) {
        ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Cheese"));
        ingredients.add(new Ingredient("Salt"));
        ingredients.add(new Ingredient("Egg"));

        RecyclerView recyclerView = rootView.findViewById(recyclerViewId);

        // Create the adapter and set it to the RecyclerView
        TagTypeAdapter tagTypeAdapter = new TagTypeAdapter(ingredients);
        // display the tag into 2 column
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // 2 is the number of columns

        recyclerView.setAdapter(tagTypeAdapter);

    }
}
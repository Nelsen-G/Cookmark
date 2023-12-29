package com.example.cookmark_app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookmark_app.activity.AllTrendingRecipeActivity;
import com.example.cookmark_app.activity.MightLikeRecipeActivity;
import com.example.cookmark_app.activity.QuickRecipeActivity;
import com.example.cookmark_app.R;
import com.example.cookmark_app.adapter.RegularRecipeListAdapter;
import com.example.cookmark_app.model.Ingredient;
import com.example.cookmark_app.model.Recipe;
import com.example.cookmark_app.utils.MarginCardItemDecoration;
import com.example.cookmark_app.utils.MarginSnapHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ExploreFragment extends Fragment {

    private RecyclerView.Adapter adapterRecipeList;
    private RecyclerView recyclerViewRecipe;
    private ArrayList<Recipe> items;
    private ArrayList<Ingredient> ingredients;
    private String userId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ExploreFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        //get user_id
        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString("user_id");
            Log.d("PlanFragment -> ", "User ID: " + userId);
        }

        initializeRecyclerView(rootView, R.id.cardView1, getAllTrendingRecipesQuery());
        initializeRecyclerView(rootView, R.id.cardView2, getQuickRecipesQuery());
        initializeRecyclerView(rootView, R.id.cardView3, getMightLikeRecipesQuery());

        setSeeAllClickListener(rootView, R.id.seeAll1, AllTrendingRecipeActivity.class);
        setSeeAllClickListener(rootView, R.id.seeAll2, QuickRecipeActivity.class);
        setSeeAllClickListener(rootView, R.id.seeAll3, MightLikeRecipeActivity.class);

        return rootView;
    }

    private void initializeRecyclerView(View rootView, int recyclerViewId, Query query) {
        items = new ArrayList<>();
        ingredients = new ArrayList<>();

        recyclerViewRecipe = rootView.findViewById(recyclerViewId);
        recyclerViewRecipe.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        MarginCardItemDecoration marginDecoration = new MarginCardItemDecoration(getResources().getDimensionPixelSize(R.dimen.card_margin));
        recyclerViewRecipe.addItemDecoration(marginDecoration);

        MarginSnapHelper snapHelper = new MarginSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewRecipe);

        adapterRecipeList = new RegularRecipeListAdapter(items, getChildFragmentManager(), userId);
        recyclerViewRecipe.setAdapter(adapterRecipeList);

        connectToDatabase(items, adapterRecipeList, recyclerViewId, query);

    }

    private void connectToDatabase(ArrayList<Recipe> items, RecyclerView.Adapter adapterRecipeList, int recyclerViewId, Query query) {
        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        items.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Recipe recipe = document.toObject(Recipe.class);
                            items.add(recipe);
                        }
                        adapterRecipeList.notifyDataSetChanged();
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void setSeeAllClickListener(View rootView, int seeAllId, Class<? extends Activity> targetActivity) {
        TextView seeAllTextView = rootView.findViewById(seeAllId);
        seeAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndStartIntent(targetActivity);
            }
        });
    }

    private void createAndStartIntent(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(getActivity(), targetActivity);
        intent.putExtra("items", items);
        startActivity(intent);
    }

    private Query getAllTrendingRecipesQuery() {
        return db.collection("recipes").orderBy("cookmarkCount", Query.Direction.DESCENDING).limit(10);
    }

    private Query getQuickRecipesQuery() {
        return db.collection("recipes").orderBy("totalMinutes").limit(10);
    }

    private Query getMightLikeRecipesQuery() {
        return db.collection("recipes").limit(10);
    }

}
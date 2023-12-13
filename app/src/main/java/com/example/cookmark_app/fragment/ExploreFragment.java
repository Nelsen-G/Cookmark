package com.example.cookmark_app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cookmark_app.AllTrendingRecipeActivity;
import com.example.cookmark_app.MightLikeRecipeActivity;
import com.example.cookmark_app.QuickRecipeActivity;
import com.example.cookmark_app.R;
import com.example.cookmark_app.adapter.RegularRecipeListAdapter;
import com.example.cookmark_app.model.Ingredient;
import com.example.cookmark_app.model.Recipe;
import com.example.cookmark_app.utils.MarginCardItemDecoration;
import com.example.cookmark_app.utils.MarginSnapHelper;

import java.util.ArrayList;

public class ExploreFragment extends Fragment {

    private RecyclerView.Adapter adapterRecipeList;
    private RecyclerView recyclerViewRecipe;

    private ArrayList<Recipe> items;
    private ArrayList<Ingredient> ingredients;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);

        initializeRecyclerView(rootView, R.id.cardView1);
        initializeRecyclerView(rootView, R.id.cardView2);
        initializeRecyclerView(rootView, R.id.cardView3);

        setSeeAllClickListener(rootView, R.id.seeAll1, AllTrendingRecipeActivity.class);
        setSeeAllClickListener(rootView, R.id.seeAll2, QuickRecipeActivity.class);
        setSeeAllClickListener(rootView, R.id.seeAll3, MightLikeRecipeActivity.class);

        return rootView;
    }

    private void initializeRecyclerView(View rootView, int recyclerViewId) {
        items = new ArrayList<>();
        ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Boneless chicken breast"));
        ingredients.add(new Ingredient("Ham"));
        ingredients.add(new Ingredient("Cheese"));

        //contoh aja
        items.add(new Recipe("contohid1","https://www.jocooks.com/wp-content/uploads/2020/07/chicken-cordon-bleu-1-2-1.jpg", "Chicken Cordon Bleu", 2, 120, "Lunch", 4, ingredients, "blabla", "https://www.jocooks.com/recipes/chicken-cordon-bleu/", 16));
        items.add(new Recipe("contohid2" ,"https://cdn.idntimes.com/content-images/community/2023/07/deep-fried-wonton-dark-surfaces-5f33a6106b7b2abdfec00ff311918826-150a7d7471f4188579a02bdf19d55ce6_600x400.jpg", "Deep Fried Wonton", 3, 180, "Snack", 8, ingredients, "blabla", "https://khinskitchen.com/crispy-fried-wonton/", 13));
        items.add(new Recipe("contohid3","https://i0.wp.com/i.gojekapi.com/darkroom/gofood-indonesia/v2/images/uploads/c293bd06-c598-488b-87c4-0d876d06ff24_Go-Biz_20210929_224129.jpeg", "Minced Pork with Rice", 1, 60, "Lunch", 8, ingredients, "blabla", "https://www.urbanasia.com/style/resep-thai-beef-basil-menu-makan-siang-gurih-dan-kaya-rempah-U62187#google_vignette", 12));

        recyclerViewRecipe = rootView.findViewById(recyclerViewId);
        recyclerViewRecipe.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        MarginCardItemDecoration marginDecoration = new MarginCardItemDecoration(getResources().getDimensionPixelSize(R.dimen.card_margin));
        recyclerViewRecipe.addItemDecoration(marginDecoration);

        MarginSnapHelper snapHelper = new MarginSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewRecipe);

        adapterRecipeList = new RegularRecipeListAdapter(items, getChildFragmentManager());
        recyclerViewRecipe.setAdapter(adapterRecipeList);
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
}
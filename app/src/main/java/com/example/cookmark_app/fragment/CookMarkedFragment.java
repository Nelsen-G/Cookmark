package com.example.cookmark_app.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.cookmark_app.R;
import com.example.cookmark_app.adapter.CookMarkedListAdapter;
import com.example.cookmark_app.model.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.Key;
import java.util.ArrayList;

public class CookMarkedFragment extends Fragment {

    private RecyclerView recyclerViewRecipe;
    private RecyclerView.Adapter adapterCookMarkList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId;
    private ArrayList<Recipe> items = new ArrayList<>();;
    View rootView;
    SearchView searchBar;

    public CookMarkedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_cook_marked, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        //get user_id
        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString("user_id");
            Log.d("PlanFragment -> ", "User ID: " + userId);
        }

        initializeSearchBar();
        initializeRecyclerView();

        return rootView;
    }

    private void initializeSearchBar(){
        searchBar = rootView.findViewById(R.id.searchBar);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String recipeName = query;

                ArrayList<Recipe> searchResult = new ArrayList<>();
                for(Recipe recipe : items){
                    if(recipe.getRecipeName().toLowerCase().contains(query.toLowerCase())){
                        searchResult.add(recipe);
                    }
                }

                if(searchResult.size() > 0){
                    CookMarkedListAdapter resultAdapter = new CookMarkedListAdapter(searchResult, getChildFragmentManager(), userId);
                    recyclerViewRecipe.setAdapter(resultAdapter);
                    resultAdapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(rootView.getContext(), "There is no " + query + " in your cookmarked list yet!", Toast.LENGTH_SHORT).show();
                }

                Log.d("TAG", "onQueryTextSubmit: " + query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    recyclerViewRecipe.setAdapter(adapterCookMarkList);
                    adapterCookMarkList.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    private void initializeRecyclerView() {
        recyclerViewRecipe = rootView.findViewById(R.id.cookmarkRecylerView);
        recyclerViewRecipe.setLayoutManager(new GridLayoutManager(rootView.getContext(), 2));

        //update data items
        updateCookMarkedList();

        adapterCookMarkList = new CookMarkedListAdapter(items, getChildFragmentManager(), userId);
        recyclerViewRecipe.setAdapter(adapterCookMarkList);
    }

    private void updateCookMarkedList(){
        items.clear();
        db.collection("cookmarks")
                .whereEqualTo("userid", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String recipeid = document.getString("recipeid");
                                getRecipe(recipeid);
                            }
                        }
                        else{
                            Log.e("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getRecipe(String recipeid){
        db.collection("recipes")
                .whereEqualTo("id", recipeid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Recipe recipe = document.toObject(Recipe.class);
                            items.add(recipe);
                        }
                        adapterCookMarkList.notifyDataSetChanged();
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
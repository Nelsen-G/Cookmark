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
import android.widget.EditText;

import com.example.cookmark_app.R;
import com.example.cookmark_app.activity.MainActivity;
import com.example.cookmark_app.adapter.SmallerRecipeListAdapter;
import com.example.cookmark_app.model.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CookMarkedFragment extends Fragment {

    private RecyclerView recyclerViewRecipe;
    private RecyclerView.Adapter adapterRecipeList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId;
    private ArrayList<Recipe> items = new ArrayList<>();;
    View rootView;
    EditText searchBar;

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
        searchBar = rootView.findViewById(R.id.searchBarEt);
    }

    private void initializeRecyclerView() {
        recyclerViewRecipe = rootView.findViewById(R.id.cookmarkRecylerView);
        recyclerViewRecipe.setLayoutManager(new GridLayoutManager(rootView.getContext(), 2));

        //update data items
        updateCookMarkedList();

        adapterRecipeList = new SmallerRecipeListAdapter(items, getChildFragmentManager(), userId);
        recyclerViewRecipe.setAdapter(adapterRecipeList);
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
                        adapterRecipeList.notifyDataSetChanged();
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
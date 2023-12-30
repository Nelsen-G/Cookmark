package com.example.cookmark_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.cookmark_app.R;
import com.example.cookmark_app.RecipeDetailActivity;
import com.example.cookmark_app.interfaces.OnItemClickCallback;
import com.example.cookmark_app.model.Cookmark;
import com.example.cookmark_app.model.Recipe;
import com.example.cookmark_app.utils.CookmarkStatusManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

// buat adapter recycler view yg di search result, ada tombol savenya
// ini buat per itemnya
public class SearchResultRecipeAdapter extends RecyclerView.Adapter<SearchResultRecipeAdapter.SearchResultRecipeHolder>{

    // for handling click
    private OnItemClickCallback onItemClickCallback;
    private ArrayList<Recipe> recipes;

    private Context context;
    private CookmarkStatusManager cookmarkStatusManager;

    private String userId;

    public SearchResultRecipeAdapter(OnItemClickCallback onItemClickCallback, ArrayList<Recipe> recipes) {
        this.onItemClickCallback = onItemClickCallback;
        this.recipes = recipes;
        this.cookmarkStatusManager = CookmarkStatusManager.getInstance();
    }

    @NonNull
    @Override
    public SearchResultRecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchresult_list, parent, false);
        context = parent.getContext();
        return new SearchResultRecipeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultRecipeHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bindData(recipe);

        // go to detail
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Recipe recipe = recipes.get(position);
                    boolean currentStatus = getCookmarkStatus(recipe.getRecipeId());
                    Intent intent = new Intent(context, RecipeDetailActivity.class);
                    intent.putExtra("recipe", recipe);
                    intent.putExtra("currentCookmarkStatus", currentStatus);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }


    private boolean getCookmarkStatus(String recipeId) {
        return cookmarkStatusManager.getCookmarkStatus(recipeId);
    }

    private void setCookmarkStatus(String recipeId, boolean isCookmarked) {
        cookmarkStatusManager.setCookmarkStatus(recipeId, isCookmarked);
    }

    public static class SearchResultRecipeHolder extends RecyclerView.ViewHolder{
        private ImageView recipeImageView;
        private TextView recipeNameTextView;
        private TextView recipeTypeTextView;
        private TextView cookmarkCountTextView;
        private TextView servingsCountTextView;
        private TextView hoursTextView;
        private TextView minutesTextView;
        private ImageView cookmarkButton;

        public SearchResultRecipeHolder(@NonNull View itemView) {
            super(itemView);

            recipeImageView = itemView.findViewById(R.id.recipeImageSR);
            recipeNameTextView = itemView.findViewById(R.id.recipeNameSR);
            recipeTypeTextView = itemView.findViewById(R.id.recipeTypeSR);
            cookmarkCountTextView = itemView.findViewById(R.id.cookmarkCountSR);
            servingsCountTextView = itemView.findViewById(R.id.servingsCountSR);
            hoursTextView = itemView.findViewById(R.id.hoursSR);
            minutesTextView = itemView.findViewById(R.id.minutesSR);
            cookmarkButton = itemView.findViewById(R.id.cookmarkButtonSR);
        }

        public void bindData(Recipe recipe) {
            Glide.with(itemView.getContext())
                    .load(recipe.getRecipeImage())
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(recipeImageView);

            recipeNameTextView.setText(recipe.getRecipeName());
            recipeTypeTextView.setText(recipe.getFoodType());
            cookmarkCountTextView.setText(recipe.getCookmarkCount() + " cookmarked");
            servingsCountTextView.setText(recipe.getServings() + " servings");
            hoursTextView.setText(recipe.getHours() +"h");
            minutesTextView.setText(recipe.getMinutes() + "m");

            initializeCookMarkIcon(recipe);
            cookmarkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        recipes.get(position);
//
//                        Drawable cookmarkedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_cookmarked);
//                        Drawable uncookmarkedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_uncookmarked);
//
//                        if (cookmarkIcon.getDrawable().getConstantState().equals(cookmarkedDrawable.getConstantState())) {
//                            deleteCookMark(recipe.getRecipeId());
//                        } else if (cookmarkIcon.getDrawable().getConstantState().equals(uncookmarkedDrawable.getConstantState())) {
//                            addCookMark(recipe);
//                        }
//
//                        notifyDataSetChanged();
//                        notifyItemChanged(position);
//                        initializeCookMarkIcon(recipe);
//                    }
                }
            });
            String imageUrl = recipe.getRecipeImage();
            int placeholderImage = R.drawable.img_placeholder;

//            Glide.with(itemView.getContext())
//                    .load(imageUrl)
//                    .placeholder(placeholderImage)
//                    .transform(new GranularRoundedCorners(20, 20, 0, 0))
//                    .into(recipePhoto);
        }

        private void initializeCookMarkIcon(Recipe recipe){
//            db.collection("cookmarks")
//                    .whereEqualTo("userid", userId)
//                    .whereEqualTo("recipeid", recipe.getRecipeId())
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if(task.isSuccessful()){
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    cookmarkIcon.setImageResource(R.drawable.ic_cookmarked);
//                                    return;
//                                }
//                                cookmarkIcon.setImageResource(R.drawable.ic_uncookmarked);
//                            }
//                        }
//                    });
        }

        private void deleteCookMark(String recipeid){
//            db.collection("cookmarks")
//                    .whereEqualTo("userid", userId)
//                    .whereEqualTo("recipeid", recipeid)
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if(task.isSuccessful()){
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    if(document != null){
//                                        db.collection("cookmarks")
//                                                .document(document.getId())
//                                                .delete()
//                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void unused) {
//                                                        String toastMessage = "Oppss you've already uncookmark a recipe";
//                                                        Toast.makeText(itemView.getContext(), toastMessage, Toast.LENGTH_SHORT).show();
//                                                    }
//                                                });
//                                    }
//                                }
//                            }
//                        }
//                    });
//
//            //decrease cookmarked value of recipe
//            db.collection("recipes")
//                    .whereEqualTo("id", recipeid)
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if(task.isSuccessful()){
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    if(document != null){
//                                        Long cookmarkCountLong = document.getLong("cookmarkCount");
//                                        int cookmarkCount = cookmarkCountLong.intValue() - 1;
//
//                                        // Update cookmarkCount value in the recipe document
//                                        db.collection("recipes")
//                                                .document(document.getId())
//                                                .update("cookmarkCount", cookmarkCount)
//                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//                                                        Log.d("TAG", cookmarkCount + "");
//                                                    }
//                                                })
//                                                .addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        Log.e("TAG", "Error updating cookmarkCount", e);
//                                                    }
//                                                });
//                                    }
//                                }
//                            }
//                        }
//                    });
        }

        private void addCookMark(Recipe recipe){
//            Cookmark newCookMark = new Cookmark(userId, recipe.getRecipeId());
//
//            //add to cookmarks
//            db.collection("cookmarks")
//                    .add(newCookMark)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            String toastMessage = "Cookmarked " + recipe.getRecipeName();
//                            Toast.makeText(itemView.getContext(), toastMessage, Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.w("TAG", "Error adding document", e);
//                        }
//                    });
//
//            //increase cookmarked value of recipe
//            db.collection("recipes")
//                    .whereEqualTo("id", recipe.getRecipeId())
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if(task.isSuccessful()){
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    if(document != null){
//                                        Long cookmarkCountLong = document.getLong("cookmarkCount");
//                                        int cookmarkCount = cookmarkCountLong.intValue() + 1;
//
//                                        // Update cookmarkCount value in the recipe document
//                                        db.collection("recipes")
//                                                .document(document.getId())
//                                                .update("cookmarkCount", cookmarkCount)
//                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//                                                        Log.d("TAG", cookmarkCount + "");
//                                                    }
//                                                })
//                                                .addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        Log.e("TAG", "Error updating cookmarkCount", e);
//                                                    }
//                                                });
//                                    }
//                                }
//                            }
//                        }
//                    });
        }

    }
}

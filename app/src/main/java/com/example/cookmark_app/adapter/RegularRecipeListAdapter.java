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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.cookmark_app.R;
import com.example.cookmark_app.activity.RecipeDetailActivity;
import com.example.cookmark_app.model.Cookmark;
import com.example.cookmark_app.model.Recipe;
import com.example.cookmark_app.utils.CookmarkStatusManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RegularRecipeListAdapter extends RecyclerView.Adapter<RegularRecipeListAdapter.ViewHolder> {
    private ArrayList<Recipe> items;
    private Context context;
    private FragmentManager fragmentManager;
    private CookmarkStatusManager cookmarkStatusManager;
    private String userId;

    public RegularRecipeListAdapter(ArrayList<Recipe> items, FragmentManager fragmentManager, String userId) {
        this.items = items;
        this.fragmentManager = fragmentManager;
        this.cookmarkStatusManager = CookmarkStatusManager.getInstance();
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_list, parent, false);
        context = parent.getContext();
        return new ViewHolder(inflate, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(items.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Recipe recipe = items.get(position);
                    boolean currentStatus = getCookmarkStatus(recipe.getRecipeId());
                    Intent intent = new Intent(context, RecipeDetailActivity.class);
                    intent.putExtra("recipe", recipe);
                    intent.putExtra("currentCookmarkStatus", currentStatus);
                    intent.putExtra("callingFragment", "explore");
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //awalnya static
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cookmarksTxt, titleTxt, durationTxt, servingsTxt, foodtypeTxt;
        private ImageView recipePhoto, cookmarkIcon;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        public ViewHolder(@NonNull View itemView, int cardLayoutType) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.recipe_title);
            cookmarksTxt = itemView.findViewById(R.id.recipe_cookmarks);
            durationTxt = itemView.findViewById(R.id.recipe_duration);
            servingsTxt = itemView.findViewById(R.id.recipe_servings);
            foodtypeTxt = itemView.findViewById(R.id.recipe_foodtype);

            recipePhoto = itemView.findViewById(R.id.recipe_photo);
            cookmarkIcon = itemView.findViewById(R.id.cookmark_icon);
        }

        public void bindData(Recipe recipe) {
            titleTxt.setText(recipe.getRecipeName());
            foodtypeTxt.setText(recipe.getFoodType());
            int servings = recipe.getServings();
            servingsTxt.setText(String.valueOf(servings));
            int totalMinutes = recipe.getTotalMinutes();
            durationTxt.setText(String.valueOf(totalMinutes) + " min");
            int cookmarkCount = recipe.getCookmarkCount();
            cookmarksTxt.setText(String.valueOf(cookmarkCount) + " Cookmarked");

            initializeCookMarkIcon(recipe);

            cookmarkIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        items.get(position);

                        Drawable cookmarkedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_cookmarked);
                        Drawable uncookmarkedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_uncookmarked);

                        if (cookmarkIcon.getDrawable().getConstantState().equals(cookmarkedDrawable.getConstantState())) {
                            deleteCookMark(recipe.getRecipeId(), position);
                        } else if (cookmarkIcon.getDrawable().getConstantState().equals(uncookmarkedDrawable.getConstantState())) {
                            addCookMark(recipe, position);
                        }

                        notifyDataSetChanged();
                        notifyItemChanged(position);
                    }
                }
            });

            String imageUrl = recipe.getRecipeImage();
            int placeholderImage = R.drawable.img_placeholder;

            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(placeholderImage)
                    .transform(new GranularRoundedCorners(20, 20, 0, 0))
                    .into(recipePhoto);
        }

        private void initializeCookMarkIcon(Recipe recipe) {
            db.collection("cookmarks")
                    .whereEqualTo("userid", userId)
                    .whereEqualTo("recipeid", recipe.getRecipeId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document != null) {
                                        cookmarkIcon.setImageResource(R.drawable.ic_cookmarked);
                                    }
                                    else{
                                        cookmarkIcon.setImageResource(R.drawable.ic_uncookmarked);
                                    }
                                }
                            }
                        }
                    });
        }

        private void deleteCookMark(String recipeid, int position) {
            db.collection("cookmarks")
                    .whereEqualTo("userid", userId)
                    .whereEqualTo("recipeid", recipeid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document != null) {
                                        db.collection("cookmarks")
                                                .document(document.getId())
                                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        cookmarkIcon.setImageResource(R.drawable.ic_uncookmarked);
                                                        String toastMessage = "Oopss you've already uncookmark a recipe";
                                                        Toast.makeText(itemView.getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    });

            //decrease cookmarked value of recipe
            db.collection("recipes")
                    .whereEqualTo("id", recipeid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document != null) {
                                        Long cookmarkCountLong = document.getLong("cookmarkCount");
                                        int cookmarkCount = cookmarkCountLong.intValue() - 1;
                                        if(cookmarkCount >= 0){
                                            // Update cookmarkCount value in the recipe document
                                            db.collection("recipes")
                                                    .document(document.getId())
                                                    .update("cookmarkCount", cookmarkCount)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            items.get(position).setCookmarkCount(cookmarkCount);
                                                            notifyDataSetChanged();
                                                            notifyItemChanged(position);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e("TAG", "Error updating cookmarkCount", e);
                                                        }
                                                    });
                                        }
                                    }
                                }
                            }
                        }
                    });
        }

        private void addCookMark(Recipe recipe, int position) {
            Cookmark newCookMark = new Cookmark(userId, recipe.getRecipeId());

            //add to cookmarks
            db.collection("cookmarks")
                .add(newCookMark)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        cookmarkIcon.setImageResource(R.drawable.ic_cookmarked);
                        String toastMessage = "Cookmarked " + recipe.getRecipeName();
                        Toast.makeText(itemView.getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });

            //increase cookmarked value of recipe
            db.collection("recipes")
                .whereEqualTo("id", recipe.getRecipeId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document != null) {
                                    Long cookmarkCountLong = document.getLong("cookmarkCount");
                                    int cookmarkCount = cookmarkCountLong.intValue() + 1;

                                    // Update cookmarkCount value in the recipe document
                                    db.collection("recipes")
                                            .document(document.getId())
                                            .update("cookmarkCount", cookmarkCount)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    items.get(position).setCookmarkCount(cookmarkCount);
                                                    notifyDataSetChanged();
                                                    notifyItemChanged(position);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("TAG", "Error updating cookmarkCount", e);
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
        }
    }

    private boolean getCookmarkStatus(String recipeId) {
        return cookmarkStatusManager.getCookmarkStatus(recipeId);
    }

//    private void setCookmarkStatus(String recipeId, boolean isCookmarked) {
//        cookmarkStatusManager.setCookmarkStatus(recipeId, isCookmarked);
//    }
}



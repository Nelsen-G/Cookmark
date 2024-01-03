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

public class CookMarkedListAdapter extends RecyclerView.Adapter<CookMarkedListAdapter.ViewHolder> {
    private ArrayList<Recipe> items;
    private Context context;
    private FragmentManager fragmentManager;
    private CookmarkStatusManager cookmarkStatusManager;
    private String userId;

    public CookMarkedListAdapter(ArrayList<Recipe> items, FragmentManager fragmentManager, String userId) {
        this.items = items;
        this.fragmentManager = fragmentManager;
        this.cookmarkStatusManager = CookmarkStatusManager.getInstance();
        this.userId = userId;
    }

    @NonNull
    @Override
    public CookMarkedListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_list_smaller, parent, false);
        context = parent.getContext();
        return new CookMarkedListAdapter.ViewHolder(inflate, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CookMarkedListAdapter.ViewHolder holder, int position) {
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
                    intent.putExtra("callingFragment", "cookmark");
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

            titleTxt = itemView.findViewById(R.id.recipe_title_smaller);
            cookmarksTxt = itemView.findViewById(R.id.recipe_cookmarks_smaller);
            durationTxt = itemView.findViewById(R.id.recipe_duration_smaller);
            servingsTxt = itemView.findViewById(R.id.recipe_servings_smaller);
            foodtypeTxt = itemView.findViewById(R.id.recipe_foodtype_smaller);

            recipePhoto = itemView.findViewById(R.id.recipe_photo_smaller);
            cookmarkIcon = itemView.findViewById(R.id.cookmark_icon_smaller);
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
                            addCookMark(recipe);
                        }

                        notifyDataSetChanged();
                        notifyItemChanged(position);
                        initializeCookMarkIcon(recipe);
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
                                    cookmarkIcon.setImageResource(R.drawable.ic_cookmarked);
                                    return;
                                }
                                cookmarkIcon.setImageResource(R.drawable.ic_uncookmarked);
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
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        String toastMessage = "Oppss you've already uncookmark a recipe";
                                                        Toast.makeText(itemView.getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                                                        items.remove(position);
                                                        notifyDataSetChanged();
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

                                        // Update cookmarkCount value in the recipe document
                                        db.collection("recipes")
                                                .document(document.getId())
                                                .update("cookmarkCount", cookmarkCount)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("TAG", cookmarkCount + "");
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

        private void addCookMark(Recipe recipe) {
            Cookmark newCookMark = new Cookmark(userId, recipe.getRecipeId());

            //add to cookmarks
            db.collection("cookmarks")
                    .add(newCookMark)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String toastMessage = "Cookmarked " + recipe.getRecipeName();
                            Toast.makeText(itemView.getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                            items.add(recipe);
                            notifyDataSetChanged();
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
                                                        Log.d("TAG", cookmarkCount + "");
                                                        notifyDataSetChanged();
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

    private void setCookmarkStatus(String recipeId, boolean isCookmarked) {
        cookmarkStatusManager.setCookmarkStatus(recipeId, isCookmarked);
    }
}

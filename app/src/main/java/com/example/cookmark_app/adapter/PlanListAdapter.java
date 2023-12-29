package com.example.cookmark_app.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookmark_app.R;
import com.example.cookmark_app.activity.RecipeDetailActivity;
import com.example.cookmark_app.model.MealPlan;
import com.example.cookmark_app.model.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder> {
    private ArrayList<MealPlan> items;
    private Context context;
    private FragmentManager fragmentManager;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PlanListAdapter(ArrayList<MealPlan> items, FragmentManager fragmentManager) {
        this.items = items;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan_list, parent, false);
        context = parent.getContext();
        return new ViewHolder(inflate, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(items.get(position));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("PLan list adapter", "Teken lama");
                int position = holder.getAdapterPosition();
                showDeleteConfirmationDialog(position);
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    MealPlan mealPlan = items.get(position);

                    //find recipe
                    String recipeId = mealPlan.getRecipeid();
                    db.collection("recipes")
                        .whereEqualTo("id", recipeId)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot recipe : task.getResult()) {
                                            Recipe selected = recipe.toObject(Recipe.class);
                                            Intent intent = new Intent(context, RecipeDetailActivity.class);
                                            intent.putExtra("recipe", selected);
                                            context.startActivity(intent);
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView planTimeTv, recipeNameTv, recipeTypeTv, servingTv, durationTv;
        private ImageView planImageIv;
        private int cardLayoutType;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();

        public ViewHolder(@NonNull View itemView, int cardLayoutType) {
            super(itemView);
            this.cardLayoutType = cardLayoutType;

            planTimeTv = itemView.findViewById(R.id.planTimeTv);
            recipeNameTv = itemView.findViewById(R.id.recipeNameTv);
            recipeTypeTv = itemView.findViewById(R.id.recipeTypeTv);
            servingTv = itemView.findViewById(R.id.servingTv);
            durationTv = itemView.findViewById(R.id.durationTv);
            planImageIv = itemView.findViewById(R.id.planImageIv);
        }

        public void bindData(MealPlan plan) {
            Log.d("TAG", ": MASUK BIND ");
            //cari plan
            //preparedtime
            DateTimeFormatter formatter = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                planTimeTv.setText("Cook at " + plan.getPrepareTime());
            }

            Log.d("TAG", plan.getRecipeid());
            //cari recipenya, set recipe details to TextViews
            db.collection("recipes")
                    .whereEqualTo("id", plan.getRecipeid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("TAG", ": DAPET ");
                                    recipeNameTv.setText(document.getString("recipeName"));
                                    recipeTypeTv.setText(document.getString("selectedSpinnerItem"));

                                    int servings = 0;
                                    Long servingsLong = document.getLong("servings");
                                    if (servingsLong != null) {
                                        servings = servingsLong.intValue();
                                    }
                                    servingTv.setText(servings + " servings");

                                    int hours = 0;
                                    int minutes = 0;
                                    Long hoursLong = document.getLong("hours");
                                    Long minutesLong = document.getLong("minutes");
                                    if (minutesLong != null || hoursLong != null) {
                                        minutes = minutesLong.intValue();
                                        hours = hoursLong.intValue();
                                    }

                                    if (hoursLong == 0) {
                                        durationTv.setText(minutes + " min");
                                    } else {
                                        int totalMinutes = (hours * 60) + minutes;
                                        durationTv.setText(totalMinutes + " min");
                                    }

                                    String image = "";
                                    image = document.getString("image");
                                    int placeholderImage = R.drawable.img_placeholder;

                                    Log.d("TAG", "Image URL: " + image);

                                    Glide.with(planImageIv.getContext())
                                            .load(image)
                                            .placeholder(placeholderImage)
                                            .into(planImageIv);
                                }
                            }
                        }
                    });
        }

    }

    private void showDeleteConfirmationDialog(int position) {
        MealPlan selectedPlan = items.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation")
                .setMessage("Mark selected plan as 'done' will delete it from list")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the deletion here

                        db.collection("mealplans")
                            .whereEqualTo("recipeid", selectedPlan.getRecipeid())
                            .whereEqualTo("prepareTime", selectedPlan.getPrepareTime())
                            .whereEqualTo("userid", selectedPlan.getUserid())
                            .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                                        if (!documents.isEmpty()) {
                                            DocumentSnapshot recipe = documents.get(0);
                                            db.collection("mealplans").document(recipe.getId()).delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    items.remove(position);
                                                    notifyDataSetChanged();
                                                    Toast.makeText(context, "Success deleted selected plan", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, "Error deleting document", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(context, "Document not found", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}

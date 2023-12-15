package com.example.cookmark_app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookmark_app.R;
import com.example.cookmark_app.model.MealPlan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder> {
    private ArrayList<MealPlan> items;
    private Context context;
    private FragmentManager fragmentManager;

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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    //view recipe detail
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

                                    int minutes = 0;
                                    Long minutesLong = document.getLong("minutes");
                                    if (minutesLong != null) {
                                        minutes = servingsLong.intValue();
                                    }

                                    if(minutes <= 60){
                                        durationTv.setText(minutes + " minutes");
                                    }
                                    else{
                                        minutes /= 60;
                                        durationTv.setText(minutes + " hour(s)");
                                    }
                                }
                            }
                        }
                    });
        }

    }
}

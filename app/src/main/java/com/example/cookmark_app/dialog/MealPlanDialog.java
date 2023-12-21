package com.example.cookmark_app.dialog;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.cookmark_app.R;
import com.example.cookmark_app.model.MealPlan;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MealPlanDialog extends AppCompatDialogFragment {

    EditText dateET, timeET;
    AutoCompleteTextView recipeAc;
    Button addTimeBtn;
    View rootView;

    private String userId;
    private Calendar calendar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Inside your activity or fragment
    private ArrayAdapter<String> adapter;
    private ArrayList<String> recipeNames;

    private int jam, menit;

    public MealPlanDialog(String userId, Calendar calendar) {
        this.userId = userId;
        this.calendar = calendar;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.dialog_formmealplan, null);

        builder.setView(rootView)
                .setTitle("Create Meal Plan")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String selectedTimeStr = timeET.getText().toString().trim();
                        String recipeName = recipeAc.getText().toString();

                        if (!selectedTimeStr.equals("") && !recipeName.equals("")) {
                            //selectedRecipe
                            db.collection("recipes")
                                    .whereEqualTo("recipeName", recipeName)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot recipe : queryDocumentSnapshots) {
                                                //selectedrecipe
                                                String recipeid = recipe.getString("id");

                                                //selectedDate
                                                Date selectedDate = calendar.getTime();

                                                //selectedTime
                                                DateTimeFormatter inputFormatter = null;
                                                LocalTime selectedTime = null;

                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                    inputFormatter = DateTimeFormatter.ofPattern("HH:mm a", Locale.getDefault());
                                                    selectedTime = LocalTime.parse(selectedTimeStr, inputFormatter);
                                                }

                                                uploadMealPlantoFirebase(userId, selectedDate, selectedTime, recipeid);
                                                return;
                                            }
                                            showToast("Recipe not found!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            showToast("Failed to fetch recipe data!");
                                        }
                                    });
                        }
                    }
                });

        initializeViewElement();
        initializeRecipeNamesList();
        initializeAdapter();

        return builder.create();
    }

    private void initializeViewElement() {
        dateET = rootView.findViewById(R.id.dateET);
        timeET = rootView.findViewById(R.id.prepareTimeET);
        addTimeBtn = rootView.findViewById(R.id.addTimeBtn);
        recipeAc = rootView.findViewById(R.id.recipeAc);

        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(date);
        dateET.setText(formattedDate);

        addTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                jam = c.get(Calendar.HOUR_OF_DAY);
                menit = c.get(Calendar.MINUTE);

                TimePickerDialog timePicker = new TimePickerDialog(rootView.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                jam = hourOfDay;
                                menit = minute;

                                String time = "";
                                if (menit < 10) {
                                    time = jam + ":0" + menit;
                                } else {
                                    time = jam + ":" + menit;
                                }

                                if (jam < 10) {
                                    timeET.setText(String.format(Locale.getDefault(), "0%s AM", time));
                                } else {
                                    timeET.setText(String.format(Locale.getDefault(), "%s PM", time));
                                }
                            }
                        }, jam, menit, true);

                timePicker.show();
            }
        });
    }

    private void uploadMealPlantoFirebase(String userId, Date selectedDate, LocalTime selectedTime, String recipeid) {
        MealPlan newPlan = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            newPlan = new MealPlan(userId, selectedDate, selectedTime, recipeid);

            db.collection("mealplans")
                    .add(newPlan)
                    .addOnSuccessListener(documentReference -> {
                        showToast("Meal plan added successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding recipe", e);
                        showToast("Failed to add meal plan");
                    });
        }
    }

    private void initializeRecipeNamesList() {
        recipeNames = new ArrayList<>();

        db.collection("cookmarks")
                .whereEqualTo("userid", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot cookmark : queryDocumentSnapshots) {
                            String recipeid = cookmark.getString("recipeid");

                            db.collection("recipes")
                                    .whereEqualTo("id", recipeid)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot recipe : queryDocumentSnapshots) {
                                                recipeNames.add(recipe.getString("recipeName"));
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void initializeAdapter() {
        adapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, recipeNames);
        recipeAc.setAdapter(adapter);
    }

    private void showToast(String message) {
        Toast.makeText(rootView.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

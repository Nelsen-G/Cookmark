package com.example.cookmark_app.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookmark_app.R;
import com.example.cookmark_app.activity.RegisterActivity;
import com.example.cookmark_app.adapter.PlanListAdapter;
import com.example.cookmark_app.dialog.MealPlanDialog;
import com.example.cookmark_app.model.Ingredient;
import com.example.cookmark_app.model.MealPlan;
import com.example.cookmark_app.utils.MarginSnapHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PlanFragment extends Fragment {
    private ArrayList<MealPlan> items = new ArrayList<>();
    private ArrayList<Ingredient> ingredients;
    private RecyclerView recyclerViewPlan;
    private RecyclerView.Adapter adapterPlanList;

    CalendarView calendarView;
    Calendar calendar = Calendar.getInstance();
    TextView currentDayTv, emptyMsgTv;
    Button addPlanBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String userId;
    private View rootView;
    private ProgressDialog progressDialog;

    public PlanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_plan, container, false);

        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading...");

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        //get user_id
        SharedPreferences sp1 = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        userId = sp1.getString("userid", null);
        Log.d("TAG", "planFragment: " + userId);

        initializeCalendarView(R.id.calendarView);
        initializeCurrentDay(R.id.currentDay);
        initializeAddPlanButton(R.id.addPlanBtn);
        initializeRecyclerView(R.id.planRecyclerView);
        emptyMsgTv = rootView.findViewById(R.id.emptyMsg);

        return rootView;
    }

    private void initializeCalendarView(int calendarViewId) {
        calendarView = rootView.findViewById(calendarViewId);
        calendarView.setMinDate(System.currentTimeMillis() - 1000);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                Date date = calendar.getTime();
                updateCurrDateTv(date);
                updateMealPlanList();
            }
        });
    }

    private void initializeCurrentDay(int textViewId) {
        currentDayTv = rootView.findViewById(R.id.currentDay);

        Date date = new Date();
        updateCurrDateTv(date);
        calendar.setTime(date);
    }

    private void initializeAddPlanButton(int buttonId) {
        addPlanBtn = rootView.findViewById(R.id.addPlanBtn);
        addPlanBtn.setBackgroundTintList(getResources().getColorStateList(R.color.purple));

        addPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlanFormDialog();
            }
        });
    }

    private void updateMealPlanList() {
        progressDialog.show();
        items.clear();
        // Get meal plan list for the selected date from Firebase
        Date selectedDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String selectedDateStr = dateFormat.format(selectedDate);

        db.collection("mealplans")
                .whereEqualTo("prepareDate", selectedDateStr)
                .whereEqualTo("userid", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String recipeId = document.getString("recipeid");
                                String prepareTimeStr = document.getString("prepareTime");

                                DateTimeFormatter inputFormatter = null;
                                LocalTime prepareTime = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    inputFormatter = DateTimeFormatter.ofPattern("HH:mm a", Locale.getDefault());
                                    prepareTime = LocalTime.parse(prepareTimeStr, inputFormatter);
                                }

                                Date prepareDate = null;
                                try {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                                    prepareDate = dateFormat.parse(selectedDateStr);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                MealPlan mealPlan = new MealPlan(userId, prepareDate, prepareTime, recipeId);
                                items.add(mealPlan);
                                adapterPlanList.notifyDataSetChanged();
                            }

                            if (items.size() == 0) {
                                recyclerViewPlan.setVisibility(View.GONE);
                                emptyMsgTv.setVisibility(View.VISIBLE);
                            } else {
                                recyclerViewPlan.setVisibility(View.VISIBLE);
                                emptyMsgTv.setVisibility(View.GONE);
                            }

                        } else {
                            Log.e("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void initializeRecyclerView(int recyclerViewId) {
        recyclerViewPlan = rootView.findViewById(recyclerViewId);
        recyclerViewPlan.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        updateMealPlanList();

        MarginSnapHelper snapHelper = new MarginSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewPlan);

        adapterPlanList = new PlanListAdapter(items, getChildFragmentManager());
        recyclerViewPlan.setAdapter(adapterPlanList);
    }

    private void showToast(String message) {
        Toast.makeText(rootView.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void updateCurrDateTv(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(date);
        currentDayTv.setText(formattedDate);
    }

    private void openPlanFormDialog() {
        MealPlanDialog mealPlanDialog = new MealPlanDialog(userId, calendar);
        mealPlanDialog.setOnOptionsMenuClosedListener(new MealPlanDialog.OnOptionsMenuClosedListener() {
            @Override
            public void onOptionsMenuClosed() {

                Log.d("Coba", "Masuk");

                Date date = calendar.getTime();
                updateCurrDateTv(date);
                updateMealPlanList();

                //refresh plan meal
                //refresh();
            }


        });
        mealPlanDialog.show(getChildFragmentManager(), "dialog");
    }

//    private void refresh() {
//        Log.d("TAG", "refresh: ");
//        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
//        ft.detach(this).attach(this).commit();
//    }
}

package com.example.cookmark_app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.cookmark_app.R;
import com.example.cookmark_app.databinding.ActivityMainBinding;
import com.example.cookmark_app.fragment.AccountFragment;
import com.example.cookmark_app.fragment.CookMarkedFragment;
import com.example.cookmark_app.fragment.ExploreFragment;
import com.example.cookmark_app.fragment.PlanFragment;
import com.example.cookmark_app.fragment.SearchFragment;
import com.example.cookmark_app.fragment.UploadFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp1 = this.getSharedPreferences("Login", MODE_PRIVATE);
        userId=sp1.getString("userid", null);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new ExploreFragment());
        binding.bottomNavigation.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){

                case R.id.mnExplore:
                    replaceFragment(new ExploreFragment());
                    break;
                case R.id.mnPlan:
                    replaceFragment(new PlanFragment());
                    break;
                case R.id.mnUpload:
                    replaceFragment(new UploadFragment());
                    break;
                case R.id.mnCookmarked:
                    replaceFragment(new CookMarkedFragment());
                    break;
            }
            return true;
        });

        binding.toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if(id == R.id.mnAccount){
                replaceFragment(new AccountFragment());
            }
            return true;
        });

        FloatingActionButton searchBtn = findViewById(R.id.mnSearch);
        searchBtn.setOnClickListener(item -> {
            replaceFragment(new SearchFragment());
        });


        if (getIntent().hasExtra("loadFragment")) {
            String fragmentToLoad = getIntent().getStringExtra("loadFragment");

            if (fragmentToLoad.equals("account")) {
                replaceFragment(new AccountFragment());
            } else if (fragmentToLoad.equals("search")) {
                replaceFragment(new SearchFragment());
            } else if (fragmentToLoad.equals("cookmark")){
                replaceFragment(new CookMarkedFragment());
            } else if (fragmentToLoad.equals("plan")){
                replaceFragment(new PlanFragment());
            } else if (fragmentToLoad.equals("explore")){
                replaceFragment(new ExploreFragment());
            } else {
                replaceFragment(new ExploreFragment());
            }
            //reset
            getIntent().removeExtra("loadFragment");
        }

    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        refresh();
//    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

//    private void refresh() {
//        finish();
//        overridePendingTransition(0, 0);
//        startActivity(getIntent());
//        overridePendingTransition(0, 0);
//        Log.d("TAG", "onRestart: aaa");
//    }
}
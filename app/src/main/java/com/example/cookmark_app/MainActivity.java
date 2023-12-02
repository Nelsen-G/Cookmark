package com.example.cookmark_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Button;

import com.example.cookmark_app.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        FloatingActionButton searchBtn = findViewById(R.id.mnSearch);
        searchBtn.setOnClickListener(item -> {
            replaceFragment(new SearchFragment());
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_layout, fragment);
        fragmentTransaction.commit();
    }
}
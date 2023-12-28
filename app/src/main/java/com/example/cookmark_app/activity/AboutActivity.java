package com.example.cookmark_app.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cookmark_app.R;
import com.example.cookmark_app.databinding.ActivityAboutBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AboutActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    private ActivityAboutBinding binding;
    ImageView igBtn, twtBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView backToPrevious = findViewById(R.id.about_back);
        backToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        igBtn = findViewById(R.id.aboutIg);
        igBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent =
                        new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.instagram.com/cookmark.idn"));
                startActivity(viewIntent);
            }
        });

        twtBtn = findViewById(R.id.aboutTwt);
        twtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent =
                        new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://twitter.com/Cookmark116276"));
                startActivity(viewIntent);
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng cookmarkLocation = new LatLng(-6.22323993599885, 106.64900546692186);
        float zoomLevel = 16.0f;
        mMap.addMarker(new MarkerOptions().position(cookmarkLocation).title("Our Location!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cookmarkLocation, zoomLevel));
        mMap.getUiSettings().setZoomGesturesEnabled(false);
    }
}

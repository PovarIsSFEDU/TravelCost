package com.plukash.travelcost;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartPoint extends AppCompatActivity {

    Button mygeo;
    Button srch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_point);

        Intent receivedCarIntent = getIntent();
        Car car = new Car();
        car.setMarkName(receivedCarIntent.getStringExtra("MarkName"));
        car.setEngine(receivedCarIntent.getStringExtra("Engine"));
        car.setGorod(receivedCarIntent.getDoubleExtra("Gorod", 9.0));
        car.setTrass(receivedCarIntent.getDoubleExtra("Trass", 6.0));
        car.setSmesh(receivedCarIntent.getDoubleExtra("Smesh", 8.0));
        car.setFuel(receivedCarIntent.getStringExtra("Fuel"));

        double weight = receivedCarIntent.getDoubleExtra("weight", 0);


        mygeo = findViewById(R.id.MyGeo);
        mygeo.setVisibility(View.GONE);
        mygeo.setEnabled(false);
        mygeo.setOnClickListener(v -> {
            Intent MapIntent = new Intent(StartPoint.this, MapsActivity.class);

            MapIntent.putExtra("MarkName", car.getMarkName());
            MapIntent.putExtra("Engine", car.getEngine());
            MapIntent.putExtra("Gorod", car.getGorod());
            MapIntent.putExtra("Trass", car.getTrass());
            MapIntent.putExtra("Smesh", car.getSmesh());
            MapIntent.putExtra("Fuel", car.getFuel());

            MapIntent.putExtra("Checker", 0);
            MapIntent.putExtra("Weight", weight);

            startActivity(MapIntent);
        });


        srch = findViewById(R.id.Search);
        srch.setOnClickListener(v -> {
            Intent MapIntent = new Intent(StartPoint.this, MapsActivity.class);

            MapIntent.putExtra("MarkName", car.getMarkName());
            MapIntent.putExtra("Engine", car.getEngine());
            MapIntent.putExtra("Gorod", car.getGorod());
            MapIntent.putExtra("Trass", car.getTrass());
            MapIntent.putExtra("Smesh", car.getSmesh());
            MapIntent.putExtra("Fuel", car.getFuel());

            MapIntent.putExtra("Checker", 1);
            MapIntent.putExtra("Weight", weight);

            startActivity(MapIntent);
        });

        Log.i("Car", car.toString());
    }


}
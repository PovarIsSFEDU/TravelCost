package com.plukash.travelcost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    String[] firms = {"Honda", "Хонда", "Infinity", "Инфинити"};
    Spinner ModelSpinner;
    ArrayList<Car> cars = null;
    ArrayList autos = new ArrayList<>();

    EditText weight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Поля для автозаполнения фирм
        //AutoCompleteTextView autoFirm = findViewById(R.id.firm);
        //ArrayAdapter<String> FirmAdapter =
        //        new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, firms);
        //autoFirm.setAdapter(FirmAdapter);


        XmlPullParser xpp = getResources().getXml(R.xml.volume_finish);
        CarParser parser = new CarParser();
        if (parser.parse(xpp)) {
            cars = parser.getCars();
        }

        //auto = autoFirm.getText().toString();


        for (Car c : cars) {
            autos.add(c.getMarkName() + " " + c.getEngine());

        }

        CreateSpinner(autos);


    }


    public void Proceed(View view) {
        long car_str_ind = ModelSpinner.getSelectedItemId();

        weight = findViewById(R.id.weight);

        double mass;
        String ms = weight.getText().toString();
        if (!ms.equals("")) {
            mass = Double.parseDouble(ms);
        } else {
            mass = 0;
        }

        Car car = new Car();

        car.setMarkName(cars.get((int) car_str_ind).getMarkName());
        car.setEngine(cars.get((int) car_str_ind).getEngine());
        car.setGorod(cars.get((int) car_str_ind).getGorod());
        car.setTrass(cars.get((int) car_str_ind).getTrass());
        car.setSmesh(cars.get((int) car_str_ind).getSmesh());
        car.setFuel(cars.get((int) car_str_ind).getFuel());

        Intent StartPointIntent = new Intent(MainActivity.this, StartPoint.class);
        StartPointIntent.putExtra("MarkName", car.getMarkName());
        StartPointIntent.putExtra("Engine", car.getEngine());
        StartPointIntent.putExtra("Gorod", car.getGorod());
        StartPointIntent.putExtra("Trass", car.getTrass());
        StartPointIntent.putExtra("Smesh", car.getSmesh());
        StartPointIntent.putExtra("Fuel", car.getFuel());
        StartPointIntent.putExtra("weight", mass);


        startActivity(StartPointIntent);
    }

    void CreateSpinner(ArrayList<?> autos) {
        ModelSpinner = findViewById(R.id.ModelSpinner);
        ArrayAdapter<?> ModelAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, autos);
        ModelAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        ModelSpinner.setAdapter(ModelAdapter);
    }


}
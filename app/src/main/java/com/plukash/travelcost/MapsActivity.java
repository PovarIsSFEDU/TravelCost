package com.plukash.travelcost;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MapsActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    int checker;
    EditText locationSearch;
    TextView bottom_price;
    Car car = new Car();
    double weight;
    LatLng latLng;
    LatLng startpoint;


    PolylineOptions line = new PolylineOptions();

    double dist;
    Button search;
    DirectionsRoute[] routes;
    List<com.google.maps.model.LatLng> path;
    LatLngBounds latLngBounds;
    LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
    LatLng endpoint;
    Switch aSwitch;
    double ss;
    private GoogleMap map;
    private boolean permissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        locationSearch = findViewById(R.id.LocSearch);

        Intent intent = getIntent();
        weight = intent.getDoubleExtra("Weight", 0);
        checker = intent.getIntExtra("Checker", 0);

        car.setMarkName(intent.getStringExtra("MarkName"));
        car.setEngine(intent.getStringExtra("Engine"));
        car.setGorod(intent.getDoubleExtra("Gorod", 9.0));
        car.setTrass(intent.getDoubleExtra("Trass", 6.0));
        car.setSmesh(intent.getDoubleExtra("Smesh", 8.0));
        car.setFuel(intent.getStringExtra("Fuel"));

        search = findViewById(R.id.search_button);
        search.setOnClickListener(this::onMapSearch);

        aSwitch = findViewById(R.id.climat_switch);
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String[] bottom_price_text = bottom_price.getText().toString().split("/");
            if (bottom_price_text.length < 2) {
                if (isChecked) {
                    double s = Double.parseDouble(bottom_price_text[0].split(" ")[0]);
                    ss = s;
                    bottom_price.setText(String.format("%.2f рублей", s * 1.07));
                } else {
                    bottom_price.setText(String.format("%.2f рублей", ss));
                }
            }
        });


    }


    //Метод запуска поиска по карте, в зависимости от выбора точки старта.
    //В итоге вызывает метод рисования маршрута и рассчета стоимости
    public void onMapSearch(View view) {
        try {
            String location = locationSearch.getText().toString();
            List<Address> addressList;

            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
                Toast toast = new Toast(this);
                toast.setText("Введите корректный адрес!");
                toast.show();
                return;
            }
            if (addressList != null) {

                if (checker == 3) {
                    map.clear();
                }
                Address address = addressList.get(0);
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
                map.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                //Убирать ли текст в поисковом поле?
                //locationSearch.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            } else {
                Toast toast = new Toast(this);
                toast.setText("Введите адрес!");
                toast.show();
            }
        } catch (Exception e) {
            Toast toast = new Toast(this);
            toast.setText("Введите корректный адрес!");
            toast.show();
            return;
        }

        //TODO - запуск с текущего местоположения

        if (checker == 1) {

            startpoint = latLng;
            checker += 1;
            locationSearch.setText("");
            locationSearch.setHint("Куда поедем?");
        } else if (checker == 2) {
            endpoint = latLng;
            locationSearch.setText("");
            locationSearch.setHint("Откуда поедем?");
            checker += 1;
            TravelCost(startpoint, endpoint);
        } else if (checker == 3) {

            startpoint = latLng;
            checker = 2;
            locationSearch.setText("");
            locationSearch.setHint("Куда поедем?");
        }
    }


    //Метод для верификации пробок на маршруте (сейчас рботает как статический переключатель для таких городов как:
    // Ростов-на-Дону, Москва)
    public boolean TrafficJam() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC+3"));
        Date date = cal.getTime();
        DateFormat dateFormat = DateFormat.getTimeInstance();
        String[] formattedDate = dateFormat.format(date).split(":");

        if (formattedDate[0].equals("7") & Integer.parseInt(formattedDate[1]) >= 30 & formattedDate[2].endsWith("AM")) {
            return true;
        } else if (Integer.parseInt(formattedDate[0]) > 7 & Integer.parseInt(formattedDate[0]) < 9 & formattedDate[2].endsWith("AM")) {
            return true;
        } else if (formattedDate[0].equals("9") & Integer.parseInt(formattedDate[1]) <= 30 & formattedDate[2].endsWith("AM")) {
            return true;
        } else if (formattedDate[0].equals("4") & Integer.parseInt(formattedDate[1]) >= 30 & formattedDate[2].endsWith("PM")) {
            return true;
        } else if (Integer.parseInt(formattedDate[0]) > 4 & Integer.parseInt(formattedDate[0]) < 7 & formattedDate[2].endsWith("PM")) {
            return true;
        } else
            return formattedDate[0].equals("7") & Integer.parseInt(formattedDate[1]) <= 30 & formattedDate[2].endsWith("PM");

    }


    //Метод для определения необходимости зимних поправок
    //TODO - Парсинг значений по регионам, при переезде из региона в регион берется среднее по всем регионам на пути.
    /*
    public boolean ifZima() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC+3"));
        Date date = cal.getTime();
        DateFormat dateFormat = DateFormat.getDateInstance();
        String[] formattedDate = dateFormat.format(date).split(" ");
        return formattedDate[0].equalsIgnoreCase("Nov") ||
                formattedDate[0].equalsIgnoreCase("Dec") ||
                formattedDate[0].equalsIgnoreCase("Jan") ||
                formattedDate[0].equalsIgnoreCase("Feb") ||
                (formattedDate[0].equalsIgnoreCase("Mar") & Integer.parseInt(formattedDate[1]) <= 20);
    }*/

    //Auto-generated method
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        if (checker != 0) {
            locationSearch.setHint("Откуда поедем?");
        }
        enableMyLocation();


    }

    //Auto-generated method
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    //Auto-generated method
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    //Auto-generated method
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    public void TravelCost(LatLng startGeoPoint, LatLng stopGeoPoint) {

        String api_key = BuildConfig.API_KEY;
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey(api_key)
                .build();

        DirectionsApiRequest apiRequest = DirectionsApi.newRequest(geoApiContext);
        apiRequest.origin(new com.google.maps.model.LatLng(startGeoPoint.latitude, startGeoPoint.longitude));
        apiRequest.destination(new com.google.maps.model.LatLng(stopGeoPoint.latitude, stopGeoPoint.longitude));
        apiRequest.mode(TravelMode.DRIVING);//set travelling mode
        apiRequest.language("Russian");


        apiRequest.setCallback(new com.google.maps.PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                routes = result.routes;
                path = routes[0].overviewPolyline.decodePath();
                dist = Double.parseDouble(routes[0].legs[0].distance.humanReadable.split(" ")[0]);
                for (int i = 0; i < path.size(); i++) {
                    line.add(new LatLng(path.get(i).lat, path.get(i).lng));
                    latLngBuilder.include(new LatLng(path.get(i).lat, path.get(i).lng));
                }

                latLngBounds = latLngBuilder.build();

                double fuel_price = 45.8; //AVG for all types
                String fuel = car.getFuel();
                if (fuel.equalsIgnoreCase("АИ-92")) {
                    fuel_price = 45.60;
                } else if (fuel.equalsIgnoreCase("АИ-95")) {
                    fuel_price = 49.90;
                } else if (fuel.equalsIgnoreCase("АИ-98")) {
                    fuel_price = 55.40;
                } else if (fuel.equalsIgnoreCase("ДТ")) {
                    fuel_price = 48.21;
                } else if (fuel.equalsIgnoreCase("Газ")) {
                    fuel_price = 30.0;
                }
                double traffic = TrafficJam() ? 4.8 : 0;
                //double zima = ifZima() ? 0.07 : 1;


                double finish_price = (car.getGorod() + traffic) * /*zima **/  fuel_price * dist * (1 + 0.0006 * weight) / 100;

                runOnUiThread(() -> {
                    /*
                    DisplayMetrics metricsB = new DisplayMetrics();
                    int width = metricsB.widthPixels;
                    int heith = metricsB.heightPixels;
                    */
                    line.width(16f).color(R.color.purple_500);
                    //Todo Сделать получение размеров экрана
                    CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, 1080, 1920, 25);
                    map.moveCamera(track);
                    map.addPolyline(line);


                    bottom_price = findViewById(R.id.Bottom_price);

                    //TODO Сделать парсинг значений для топлива в отдельном модуле.

                    bottom_price.setText(String.format("%.2f рублей", finish_price));
                });
            }

            @Override
            public void onFailure(Throwable e) {
            }
        });


    }


}
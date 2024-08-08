package com.amr.weatherapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String API_KEY = "7f27366b66f1f3f5f9369487311754ff"; // Replace with your weather API key
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private TextView temperatureTextView, minTempTextView, maxTempTextView, pressureTextView, humidityTextView, windTextView, conditionDescTextView, updatedAtTextView, cityNameTextView;
    private ImageView weatherIconImageView;
    private EditText cityEditText;
    private Button searchButton;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        temperatureTextView = findViewById(R.id.temp_tv);
        minTempTextView = findViewById(R.id.min_temp_tv);
        maxTempTextView = findViewById(R.id.max_temp_tv);
        pressureTextView = findViewById(R.id.pressure_tv);
        humidityTextView = findViewById(R.id.humidity_tv);
        windTextView = findViewById(R.id.wind_tv);
        weatherIconImageView = findViewById(R.id.condition_iv);
        conditionDescTextView = findViewById(R.id.conditionDesc_tv);
        updatedAtTextView = findViewById(R.id.updated_at_tv);
        cityNameTextView = findViewById(R.id.name_tv);
        cityEditText = findViewById(R.id.city_et);
        ImageButton searchButton = findViewById(R.id.search_button);

        // Initialize location provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Set search button click listener
        searchButton.setOnClickListener(v -> {
            String cityName = cityEditText.getText().toString().trim();
            if (!cityName.isEmpty()) {
                fetchWeatherDataForCity(cityName);
            }
        });

        // Check and request location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocationAndFetchWeather();
        }
    }

    private void getLocationAndFetchWeather() {
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                fetchWeatherData(latitude, longitude);
                            } else {
                                Log.e(TAG, "Location is null");
                            }
                        }
                    });
        } catch (SecurityException e) {
            Log.e(TAG, "Location permission not granted", e);
        }
    }

    private void fetchWeatherData(double latitude, double longitude) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude
                + "&lon=" + longitude + "&units=metric&appid=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Weather API call failed", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> updateUI(responseData));
                }
            }
        });
    }

    private void fetchWeatherDataForCity(String cityName) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName
                + "&units=metric&appid=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Weather API call failed", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> updateUI(responseData));
                }
            }
        });
    }

    private void updateUI(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

        // Extract city name
        String cityName = jsonObject.get("name").getAsString();

        // Extract temperature, humidity, wind speed, and pressure
        double temp = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
        double minTemp = jsonObject.getAsJsonObject("main").get("temp_min").getAsDouble();
        double maxTemp = jsonObject.getAsJsonObject("main").get("temp_max").getAsDouble();
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
        double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
        int pressure = jsonObject.getAsJsonObject("main").get("pressure").getAsInt();
        String weather = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

        // Update UI elements
        temperatureTextView.setText(String.format("%.0f°C", temp));
        minTempTextView.setText(String.format("Min Temp: %.0f°C", minTemp));
        maxTempTextView.setText(String.format("Max Temp: %.0f°C", maxTemp));
        humidityTextView.setText(String.format("Humidity: %d%%", humidity));
        windTextView.setText(String.format("Wind Speed: %.1f m/s", windSpeed));
        pressureTextView.setText(String.format("Pressure: %d hPa", pressure));
        cityNameTextView.setText(cityName);

        // Set weather icon based on condition
        switch (weather.toLowerCase()) {
            case "clear":
                weatherIconImageView.setImageResource(R.drawable.clear_day);
                conditionDescTextView.setText("Clear");
                break;
            case "clouds":
                weatherIconImageView.setImageResource(R.drawable.ic_cloudy);
                conditionDescTextView.setText("Cloudy");
                break;
            case "rain":
                weatherIconImageView.setImageResource(R.drawable.ic_rainy);
                conditionDescTextView.setText("Rainy");
                break;
            case "snow":
                weatherIconImageView.setImageResource(R.drawable.ic_snow);
                conditionDescTextView.setText("Snowy");
                break;
            default:
                weatherIconImageView.setImageResource(R.drawable.ic_clear);
                conditionDescTextView.setText("Clear");
                break;
        }

        // Update time of data retrieval
        String timeFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
        String currentTime = sdf.format(new Date());
        updatedAtTextView.setText(String.format("Updated at: %s", currentTime));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndFetchWeather();
            } else {
                Log.e(TAG, "Location permission denied");
            }
        }
    }
}

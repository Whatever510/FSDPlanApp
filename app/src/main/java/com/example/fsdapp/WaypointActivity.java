package com.example.fsdapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This class is used during the drive to display the next waypoint and the distance to it.
 */
public class WaypointActivity extends AppCompatActivity implements LocationListener {

    private Location my_location;
    private ArrayList<Location> waypoints;
    private ArrayList<String> waypoint_names;
    private ImageView imageView;
    private TextView textView;
    private LocationManager locationManager;
    private int route = -1;
    private boolean init;
    private String file;
    private int current_index;
    private boolean has_reached_destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waypoint_mode);
        my_location = null;
        imageView = findViewById(R.id.imageView_next);
        textView = findViewById(R.id.textView_info);
        init = true;
        Intent intent = getIntent();
        route = intent.getIntExtra("route", -1);
        file = intent.getStringExtra("file");
        waypoints = new ArrayList<>();
        waypoint_names = new ArrayList<>();
        current_index = 0;
        has_reached_destination = false;
        readWaypointNames(file);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    void startLocationUpdate() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    /**
     * Calculate the distance between the current position of the user and the next waypoint.
     * @param location1 the first distance
     * @param location2 the second distance
     * @return the calculated distance
     */
    public double calculateDistance(Location location1, Location location2) {
        return location1.distanceTo(location2);
    }

    /**
     * This method is called when a location update is received.
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        my_location = location;

        double distance = calculateDistance(my_location, waypoints.get(current_index));
        if (current_index == waypoint_names.size()-1 && distance < 150.0 || has_reached_destination) {
            imageView.setImageResource(R.drawable.check);
            textView.setText(R.string.reached_destination);
            has_reached_destination = true;
        }
        else {
            // Show the linear distance between the current location and the next waypoint
            // And the type of the next waypoint
            String text = "";
            String next_type = (waypoint_names.get(current_index).split("_")[0]).toUpperCase(Locale.ROOT);
            if (distance < 1000) {
                String distance_meters = String.format(Locale.ROOT, "%.2f", distance) + " meters";
                String distance_yards = String.format(Locale.ROOT, "%.2f", distance * 1.0936) + " yards";
                text = next_type + "\n" + distance_meters + "\n" + distance_yards;
            } else {
                // Convert distance to kilometers
                double distance_km = distance / 1000;
                String distance_km_str = String.format(Locale.ROOT, "%.2f", distance_km) + " km";
                String distance_miles = String.format(Locale.ROOT, "%.2f", distance_km * 0.621371) + " miles";
                text = next_type + "\n" + distance_km_str + "\n" + distance_miles;
            }
            textView.setText(text);

            if (init || distance < 100.0) {
                if (!init) {
                    current_index++;
                }
                String name = waypoint_names.get(current_index);
                String[] parts = name.split("_");
                String type = parts[0];
                switch (type) {
                    case "intersection":
                        imageView.setImageResource(R.drawable.crossing_sign);
                        break;
                    case "narrow":
                        imageView.setImageResource(R.drawable.narrow_sign);
                        break;
                    case "construction":
                        imageView.setImageResource(R.drawable.construction_sign);
                        break;
                    case "busy":
                        imageView.setImageResource(R.drawable.busy_sign);
                        break;
                    case "curve":
                        imageView.setImageResource(R.drawable.curve_sign);
                        break;
                    case "unprotected":
                        imageView.setImageResource(R.drawable.unprotected_sign);
                        break;
                    case "roundabout":
                        imageView.setImageResource(R.drawable.roundabout_sign);
                        break;
                    case "merge":
                        imageView.setImageResource(R.drawable.merge_sign);
                        break;
                    case "lanechange":
                        imageView.setImageResource(R.drawable.lanechange_sign);
                        break;
                    case "uturn":
                        imageView.setImageResource(R.drawable.uturn_sign);
                        break;
                    default:
                        imageView.setImageResource(R.drawable.other_sign);
                        break;
                }
                init = false;
            }
        }

    }

    /**
     * Read the waypoint names
     * @param file the file name to be read
     */
    public void readWaypointNames(String file) {
        String file_path = getFilesDir().getAbsolutePath() + "/" + file;
        Log.d("file_path", file_path);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file_path));
            String line = "";
            while ((line = reader.readLine()) != null) {
                waypoint_names.add(line);
                String [] parts = line.split("_");
                double lat = Double.parseDouble(parts[1]);
                double lon = Double.parseDouble(parts[2]);
                Location location = new Location("");
                location.setLatitude(lat);
                location.setLongitude(lon);
                waypoints.add(location);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}

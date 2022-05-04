package com.example.fsdapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This class is the explore activity.
 * It is used to survey the local region
 */
public class ExploreActivity extends AppCompatActivity implements LocationListener {
    private Location location;
    public static final String FILE_NAME = "location_data.txt";
    private File file;
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;
    protected LocationManager locationManager;
    private ArrayList<String> visited;
    // Unique location identifier
    private String locationId;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore_mode);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 5, this);
        visited = new ArrayList<>();
        locationId = "";
        location = new Location("");
        file = new File(this.getFilesDir(), FILE_NAME);
        fileWriter = null;
        bufferedWriter = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 5, this);
    }



    /**
     * Callback Method for the Location Listener
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    /**
     * Create a String containing the type and the location of the user.
     * Separate the value by a comma. Add a new line to the end of the string.
     */
    private String createString(String type, Location location) {
        // Create a unique identifier for the location of the user
        locationId = type + "_" + location.getLatitude() + "_" + location.getLongitude();
        if (visited.contains(locationId) || location == null || location.getLatitude() == 0.0 || location.getLongitude() == 0.0) {
            return "";
        }
        else {
            visited.add(locationId);
        }

        String string = locationId + "," + type + "," + location.getLatitude() + "," + location.getLongitude() + "\n";
        return string;
    }


    /**
     * Write the string to a the file.
     * If the file does not exist, create it.
     * Otherwise append the string to the end of the file.
     */
    private void writeToFile(String string) {
        if (Objects.equals(string, "")) {
            return;
        }
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(string);
            bufferedWriter.close();
            // On success show a toast with a checkmark icon and text "Success"
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            // On failure show a toast with an error icon and text "Failure"
            Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * This method is called when the user clicks the intersection button.
     * @param view the view
     */
    public void onClickIntersection(android.view.View view) {
        writeToFile(createString("intersection", location));
    }

    /**
     * This method is called when the user clocks the sharp curve button
     * @param view the view
     */
    public void onClickSharpCurve(android.view.View view) {
        writeToFile(createString("curve", location));
    }

    /**
     * This method is called when the user clicks the narrow street button
     */
    public void onClickNarrowStreet(android.view.View view) {
        writeToFile(createString("narrow", location));
    }

    /**
     * This method is called when the user clicks the busy area button
     * @param view the view
     */
    public void onClickBusyArea(android.view.View view) {
        writeToFile(createString("busy", location));
    }

    /**
     * This method is called when the user clicks the road works button
     */
    public void onClickRoadWorks(android.view.View view) {
        writeToFile(createString("construction", location));
    }

    /**
     * This method is called when the user clicks the unprotected button
     */
    public void onClickUnprotected(android.view.View view) {
        writeToFile(createString("unprotected", location));
    }

    /**
     * This method is called when the user clicks the roundabout button
     */
    public void onClickRoundabout(android.view.View view) {
        writeToFile(createString("roundabout", location));
    }

    /**
     * This method is called when the user clicks the merge button
     */
    public void onClickMerge(android.view.View view) {
        writeToFile(createString("merge", location));
    }

    /**
     * This method is called when the user clicks the lanechange button
     */
    public void onClickLaneChange(android.view.View view) {
        writeToFile(createString("lanechange", location));
    }

    /**
     * This method is called when the user clicks the uturn button
     */
    public void onClickUturn(android.view.View view) {
        writeToFile(createString("uturn", location));
    }
}

package com.whatever.fsdapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.whatever.fsdapp.R;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * This class is used to plan the routes for the drives.
 */
public class PlanActivity extends AppCompatActivity {

    private MapView mapView = null;
    public static final String FILE_NAME = "location_data.txt";

    private LinkedHashMap<String, Boolean> isMarkerClicked;

    boolean switchOn = false;

    private Switch switchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_mode);
        isMarkerClicked = new LinkedHashMap<>();
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        mapView = findViewById(R.id.mapView);
        switchButton = findViewById(R.id.switchMode);
        setupMap();
    }

    /**
     * Setup the displayed map by creating the map view and placing the markers on the map
     */
    @SuppressLint("MissingPermission")
    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getController().setZoom(19.0);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mapView.setMultiTouchControls(true);
        // Add a copyright overlay
        CopyrightOverlay copyrightOverlay = new CopyrightOverlay(this);
        copyrightOverlay.setCopyrightNotice("Map data ?? OpenStreetMap contributors");
        mapView.getOverlays().add(copyrightOverlay);
        // set view to current location

        CompassOverlay compassOverlay = new CompassOverlay(this, mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            mapView.getController().setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
        }
        placeMarkers();
    }

    /**
     * This method is called when the plan_route button is clicked
     */
    public void onClickPlanRoute(android.view.View view) {
        // Get the current time and date as filename
        String filename = "route_" + System.currentTimeMillis() + ".txt";

        File file = new File(this.getFilesDir(), filename);
        // Create a file in the internal storage
        ArrayList<String> selected_marker_ids = new ArrayList<>();
        for (String marker_id : isMarkerClicked.keySet()) {
            if (Boolean.TRUE.equals(isMarkerClicked.get(marker_id))) {
                selected_marker_ids.add(marker_id);
                Log.d("PlanActivity", "Selected marker: " + marker_id);
            }
        }
        if (selected_marker_ids.size() == 0) {
            Toast.makeText(this, "Please select at least one marker", Toast.LENGTH_SHORT).show();
        }
        else {
            // Write the selected marker ids to the file
            try {
                FileWriter fileWriter = new FileWriter(file);
                for (String marker_id : selected_marker_ids) {
                    fileWriter.write(marker_id + "\n");
                }
                Toast.makeText(this, "Route planned successfully", Toast.LENGTH_SHORT).show();
                fileWriter.close();
            } catch (Exception e) {
                Log.e("PlanActivity", "Error writing to file: " + e.getMessage());

            }
        }

    }

    /**
     * Place the markers on the map
     */
    void placeMarkers() {
        // Open the file specified by the file name
        String filePath = getFilesDir().getAbsolutePath() + "/" + FILE_NAME;
        StringBuilder text = new StringBuilder();
        try {
            // Open the file for reading
            BufferedReader reader = new BufferedReader(
                    new FileReader(filePath));
            String line;
            // Read the file line by line starting from the beginning
            while ((line = reader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            // Close the file
            reader.close();
        }
        catch (Exception e) {
            // Print an error message to the console
            // Show a toast message to the user informing them to first survey the region
            Toast.makeText(this, "Please survey the region first", Toast.LENGTH_LONG).show();
            return;

        }
        Location lastLocation = new Location("");
        lastLocation.setLatitude(0.0);
        lastLocation.setLongitude(0.0);
        // Print the text
        Log.d("PlanActivity", text.toString());

        for (String line : text.toString().split("\n")) {
            String[] parts = line.split(",");
            String id = "";
            String type = "";
            try {
                id = parts[0];
                type = parts[1];
            } catch (Exception e) {
                Log.e("PlanActivity", "Error parsing file: " + e.getMessage());
                // Show a toast that the user deleted all markers and needs to survey the region again
                Toast.makeText(this, "Please survey the region first. You??ve deleted all markers",
                        Toast.LENGTH_LONG).show();
                return;
            }
            double latitude = Double.parseDouble(parts[2]);
            double longitude = Double.parseDouble(parts[3]);

            lastLocation.setLatitude(latitude);
            lastLocation.setLongitude(longitude);
            // Create a marker for the location. Ignore if latitude or longitude is 0.0
            if (latitude != 0.0 && longitude != 0.0) {
                Marker marker = new Marker(mapView);
                marker.setPosition(new GeoPoint(latitude, longitude));
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setTitle(type);
                marker.setId(id);
                // set marker icon according to type
                switch (type) {
                    case "intersection":
                        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_intersection_sign));
                        break;
                    case "narrow":
                        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_narrow_sign));
                        break;
                    case "construction":
                        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_construction_sign));
                        break;
                    case "busy":
                        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_busy_sign));
                        break;
                    case "curve":
                        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_curve_sign));
                        break;
                    case "unprotected":
                        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unprotected_sign));
                        break;
                    case "roundabout":
                        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_roundabout_sign));
                        break;
                    case "merge":
                        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_merge_sign));
                        break;
                    case "lanechange":
                        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_lanechange_sign));
                        break;
                    case "uturn":
                        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_uturn_sign));
                        break;
                    default:
                        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_other_sign));
                        break;
                }
                // isMarkerClicked.put(id, false);
                marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        if(!switchButton.isChecked()) {
                            markerPlanReaction(marker, mapView);
                        }
                        else {
                            markerDeleteRaction(marker, mapView);
                        }
                        Log.d("Plan Activity", "Marker clicked: " + marker.getId());

                        return true;
                    }
                });


                mapView.getController().setCenter(new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude()));
                mapView.getOverlays().add(marker);
            }
        }
    }

    public void markerDeleteRaction(Marker marker, MapView mapView) {
        String id = marker.getId();
        isMarkerClicked.remove(id);
        marker.remove(mapView);
        String filePath = getFilesDir().getAbsolutePath() + "/" + FILE_NAME;
        // Remove the line containing the marker id from the file
        String newText = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.contains(id)) {
                    newText += line + "\n";
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        // Write the new text with the marker id removed
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            bw.write(newText);
            bw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void markerPlanReaction(Marker marker, MapView mapView) {
        if (!isMarkerClicked.containsKey(marker.getId())) {
            isMarkerClicked.put(marker.getId(), true);
        }
        // Marker already in keyset
        else {
            // Change the value in isMarkerClicked to the opposite
            isMarkerClicked.put(marker.getId(), Boolean.FALSE.equals(isMarkerClicked.get(marker.getId())));
        }

        switch (marker.getTitle()) {
            case "intersection":
                if(Boolean.TRUE.equals(isMarkerClicked.get(marker.getId()))) {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_intersection_selected));
                }
                else {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_intersection_sign));
                }
                break;
            case "narrow":
                if(Boolean.TRUE.equals(isMarkerClicked.get(marker.getId()))) {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_narrow_selected));
                }
                else {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_narrow_sign));
                }
                break;
            case "curve":
                if(Boolean.TRUE.equals(isMarkerClicked.get(marker.getId()))) {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_curve_selected));
                }
                else {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_curve_sign));
                }
                break;
            case "lanechange":
                if(Boolean.TRUE.equals(isMarkerClicked.get(marker.getId()))) {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_lanechange_selected));
                }
                else {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_lanechange_sign));
                }
                break;
            case "construction":
                if(Boolean.TRUE.equals(isMarkerClicked.get(marker.getId()))) {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_construction_selected));
                }
                else {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_construction_sign));
                }
                break;
            case "roundabout":
                if(Boolean.TRUE.equals(isMarkerClicked.get(marker.getId()))) {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_roundabout_selected));
                }
                else {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_roundabout_sign));
                }
                break;
            case "busy":
                if(Boolean.TRUE.equals(isMarkerClicked.get(marker.getId()))) {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_busy_selected));
                }
                else {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_busy_sign));
                }
                break;
            case "merge":
                if(Boolean.TRUE.equals(isMarkerClicked.get(marker.getId()))) {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_merge_selected));
                }
                else {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_merge_sign));
                }
                break;
            case "unprotected":
                if(Boolean.TRUE.equals(isMarkerClicked.get(marker.getId()))) {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_unprotected_selected));
                }
                else {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_unprotected_sign));
                }
                break;
            case "uturn":
                if(Boolean.TRUE.equals(isMarkerClicked.get(marker.getId()))) {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_uturn_selected));
                }
                else {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_uturn_sign));
                }
                break;
            default:
                if(Boolean.TRUE.equals(isMarkerClicked.get(marker.getId()))) {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_other_selected));
                }
                else {
                    marker.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_other_sign));
                }
        }
    }
}

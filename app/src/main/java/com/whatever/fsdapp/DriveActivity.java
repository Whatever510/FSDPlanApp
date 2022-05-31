package com.whatever.fsdapp;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import androidx.appcompat.app.AppCompatActivity;
import com.whatever.fsdapp.R;

import java.io.*;
import java.util.ArrayList;

public class DriveActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private OnItemClickListener listViewOnItemClickedListener;
    private ArrayList<String> fileList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drive_mode);
        fileList = new ArrayList<>();
        listView = findViewById(R.id.listview_routes);
        discoverRoutes();
    }

    void discoverRoutes() {
        // Find all files in default storage location that start with route_
        // Extract the number of entries in the file
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // Get directory of default storage location
        String directory = cw.getFilesDir().getAbsolutePath();
        // Get all files in directory
        String[] files = cw.fileList();
        ArrayList<String> entries = new ArrayList<>();
        // Find all files that start with route_
        int counter = 1;
        int count = 0;
        for (String file : files) {
            if (file.startsWith("route_")) {
                try {
                    BufferedReader reader = new BufferedReader(
                            new FileReader(directory + "/" + file));
                    String line;
                    fileList.add(file);
                    while ((line = reader.readLine()) != null) {
                        count++;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String entry = "Route " + counter + ": " + count + " Stops";
                entries.add(entry);
                counter++;
                count = 0;
            }

        }
        // Create an array adapter to display the list of routes
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, entries);
        listView.setAdapter(adapter);
    }

    /**
     * This method is called when the start drive button is clicked.
     */
    public void startDrive(View view) {
        // Check if a route is selected
        if (listView.getCheckedItemPosition() != AdapterView.INVALID_POSITION) {
            // Show a toast message
            Intent intent = new Intent(this, WaypointActivity.class);
            intent.putExtra("route", listView.getCheckedItemPosition());
            intent.putExtra("file", fileList.get(listView.getCheckedItemPosition()));
            startActivity(intent);
        }
        else {
            // Prompt user to select a route
            String message = "Please select a route";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteRoute(View view) {
        // Check if a route is selected
        if (listView.getCheckedItemPosition() != AdapterView.INVALID_POSITION) {
            // Get the selected index
            int index = listView.getCheckedItemPosition();
            // Get the selected route name
            String route = fileList.get(index);
            // Delete the selected route
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File file = cw.getFileStreamPath(route);
            file.delete();
            // Update the list of routes
            discoverRoutes();
        }
        else {
            // Prompt user to select a route
            String message = "Please select a route";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

}

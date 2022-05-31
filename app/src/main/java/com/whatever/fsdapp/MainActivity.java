package com.whatever.fsdapp;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.whatever.fsdapp.R;

/**
 * This is the main activity of the app.
 */
public class MainActivity extends AppCompatActivity {

    private Button button_explore;
    private Button button_plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForPermissions();
        button_explore = findViewById(R.id.button_explore);
        button_explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExploreView(v);
            }
        });

        button_plan = findViewById(R.id.button_planning_mode);
        button_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlanView(v);
            }
        });
    }

    /**
     * Check for permission to access location, network and storage.
     */
    private void checkForPermissions() {
        // Ask for multiple permissions
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }

    /**
     * This method is called when the Explore button is clicked.
     *
     * @param view
     */
    public void openExploreView(View view) {
        Intent intent = new Intent(this, ExploreActivity.class);
        startActivity(intent);
    }

    /**
     * This method is called when the Plan button is clicked.
     */
    public void openPlanView(View view) {
        Intent intent = new Intent(this, PlanActivity.class);
        startActivity(intent);
    }

    /**
     * This method is called when the Drive button is clicked.
     */
    public void openDriveView(View view) {
        Intent intent = new Intent(this, DriveActivity.class);
        startActivity(intent);
    }
}
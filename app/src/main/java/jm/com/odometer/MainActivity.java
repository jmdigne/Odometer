package jm.com.odometer;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private OdometerService odometer;
    // Is the activity bound or not to the Service
    private boolean bound = false;
    public static final int MY_PERMISSION_LOCATION_FINE_ACCESS = 10;


    // A ServiceConnection is used to form a connection with the service.
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            Log.v("* MainActivity *", "onServiceConnected()");
            // Get the Service Binder -  The bound service creates a Binder object.
            // The Binder contains a reference to the bound service. The service sends the Binder
            // back along the connection.
            OdometerService.OdometerBinder odometerBinder =
                    (OdometerService.OdometerBinder) binder;
            // Get a reference of BoundService when the service is connected
            // When the activity receives the Binder, it takes out the Service
            // object and starts to use the service directly.
            odometer = odometerBinder.getOdometer();
            bound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.v("* MainActivity *", "onServiceDisconnected()");
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("* MainActivity *", "onCreate()");
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION_FINE_ACCESS);
        }
    }

    @Override
    // The activity passes an Intent down the connection to the service.
    // The intent contains any additional information the activity needs to pass to the service.
    protected void onStart() {
        super.onStart();
        Log.v("* MainActivity *", "onStart()");
        // Bind the service when the activity starts
        Intent intent = new Intent(this, OdometerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    // Unbind the service when the activity stops
    protected void onStop() {
        super.onStop();
        // Unbind the service using the connection
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }


    private void watchMileage() {
        Log.v("* MainActivity *", "watchMileage() called");
        if (ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.v("* MainActivity *", "Permissions ACCESS_FINE_LOCATION granted");
        }
        // Get the TextView
        final TextView distanceView = (TextView)findViewById(R.id.distance);
        // Get new handler ?? see chap 4
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if (odometer != null) {
                    distance = odometer.getMiles();
                }
                String distanceStr = String.format("%1$,.2f miles", distance);
                distanceView.setText(distanceStr);
                // Post the code to be run again after a dalay of 1000 ms = 1s
                //
                handler.postDelayed(this, 1000);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_LOCATION_FINE_ACCESS: {
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("* MainActivity *", "checkPermissions OK OK");
                    watchMileage();
                } else {
                    Log.v("* MainActivity *", "checkPermissions PAS OK !!!!!!!!!!!!!");
                    Toast toast = Toast.makeText(this, "We needed location access to check distance traveled.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }

}
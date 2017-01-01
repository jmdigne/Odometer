package jm.com.odometer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;

public class MainActivity extends Activity {

    private OdometerService odometer;
    // Is the activity bound or not to the Service
    private boolean bound = false;

    // A ServiceConnection is used to form a connection with the service.
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            // Get the Service Binder -  The bound service creates a Binder object.
            // The Binder contains a reerence to the bound service. The service sends the Binder
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
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Call the watchMileage() to get the distance and update the display
        watchMileage();
    }

    @Override
    // The activity passes an Intent down the connection to the service.
    // The intent contains any additional information the activity needs to pass to the service.
    protected void onStart() {
        super.onStart();
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

    // Display the distance travelled - Get the distance each second and update the display
    private void watchMileage() {
        // Get the TextView
        final TextView distanceView = (TextView)findViewById(R.id.distance);
        // Get new handler ?? voir chap 4
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
}

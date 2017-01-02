package jm.com.odometer;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
//for logging
import android.util.Log;
// The class extends the Service class and not IntentService
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
// Check permission
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class OdometerService extends Service {

    private final IBinder binder = new OdometerBinder();
    // We’re storing the distance traveled in meters and the last location as static private variables.
    private static double distanceInMeters;
    private static Location lastLocation = null;
    // Create the listener as a private variable
    private LocationListener listener;
    private LocationManager locManager;

    public class OdometerBinder extends Binder {
        OdometerService getOdometer() {
            return OdometerService.this;
        }
    }

    @Override
    // To allow the activity to bind to the service, we need to get
    // the service to create the Binder object, and pass it to the
    // activity using its onBind() method.
    // This gets called when the activity binds to the Service
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {

        Log.v("* OdometerService*", "onCreate() called");

        // This is the new LocationListener
        listener = new LocationListener() {
            @Override
            // This method gets called whenever the LocationListener is told the device location has changed.
            // The Location parameter describes the current location.
            public void onLocationChanged(Location location) {
                Log.v("* OdometerService*", "onLocationChanged() called");
                if (lastLocation == null) {
                    // if it’s our first location, set lastLocation to the current Location.
                    lastLocation = location;
                }
                // Trace location changes
                Log.v("* OdometerService*", "LocationListener()" + location.getLongitude() + " " + location.getLatitude());

                // You can find the distance in meters between two locations using the Location distanceTo() method.
                distanceInMeters += location.distanceTo(lastLocation);
                lastLocation = location;
            }

            // We need to override these methods too,but they can be left empty. They get
            // called when the GPS is enabled or disabled, or if its status has changed. We don’t
            // need to react to any of these events.
            @Override
            public void onProviderDisabled(String arg0) {
                Log.v("* OdometerService*", "onProviderDisabled()");
            }

            @Override
            public void onProviderEnabled(String arg0) {
                Log.v("* OdometerService*", "onProviderEnabled()");
            }

            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle bundle) {
                Log.v("* OdometerService*", "onStatusChanged()");
            }
        };

        // A locationManager give you access to the Android location service
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.v("* OdometerService*", "onCreate() " + locManager.getAllProviders().toString());
        Log.v("* OdometerService*", "onCreate() locManager.toString() =  " + locManager.toString());


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listener);
            Log.v("* OdometerService*", "onCreate() requestLocationUpdates() OK =  " + locManager.toString());
        } else {
            Log.v("* OdometerService*", "onCreate() requestLocationUpdates() PAS OK =  ");
        }

            // Register the the location listener withe the location service
            // and how often you want the listener to get updated --> 1000 = 1s and  1 = 1meter
            //locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listener);


    }

    @Override
    public void onDestroy() {
        // Stop the location updates when the service is destroyed
        if (locManager != null && listener != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locManager.removeUpdates(listener);
                locManager = null;
                listener = null;
            }
        }
    }

    // Tell the activity the distance travelled (Meters to Miles conversion)
    public double getMiles() {
        Log.v("* OdometerService*", " getMiles() called - Distance (meters): " + this.distanceInMeters);
        return this.distanceInMeters / 1609.344;
    }

    public double getKMs() {
        Log.v("* OdometerService*", " getKMs() called - Distance (meters): " + this.distanceInMeters);
        return this.distanceInMeters;
    }
}
package jm.com.odometer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


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
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        // This is the new LocationListener
        listener = new LocationListener() {
            @Override
            // This method gets called whenever the LocationListener is told the device location has changed.
            // The Location parameter describes the current location.
            public void onLocationChanged(Location location) {
                if (lastLocation == null) {
                    // if it’s our first location, set lastLocation to the current Location.
                    lastLocation = location;
                }
                // You can ind the distance in meters between two locations using the Location distanceTo() method.
                distanceInMeters += location.distanceTo(lastLocation);
                lastLocation = location;
            }

            // We need to override these methods too,but they can be left empty. They get
            // called when the GPS is enabled or disabled, or if its status has changed. We don’t
            // need to react to any of these events.
            @Override
            public void onProviderDisabled(String arg0) {
            }

            @Override
            public void onProviderEnabled(String arg0) {
            }

            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle bundle) {
            }
        };
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listener);
    }

    @Override
    public void onDestroy() {
        if (locManager != null && listener != null) {
            locManager.removeUpdates(listener);
            locManager = null;
            listener = null;
        }
    }

    public double getMiles() {
        return this.distanceInMeters / 1609.344;
    }
}
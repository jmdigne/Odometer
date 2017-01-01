package jm.com.odometer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class OdometerService extends Service {
    public OdometerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

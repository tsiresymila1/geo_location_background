package ts.mila.geo_position_background.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import java.util.Objects;

import io.flutter.Log;
import ts.mila.geo_position_background.services.LocationService;
import ts.mila.geo_position_background.interfaces.LocationChangeListener;

public class LocationBroadcastReceiver extends BroadcastReceiver {

    private LocationChangeListener callback;
    String TAG = "[BROADCAST RECEIVER]";

    public void setListener(LocationChangeListener callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Getting intent ...");
        if (intent != null && Objects.equals(intent.getAction(), LocationService.ACTION_LOCATION)) {
            Location info = intent.getParcelableExtra(LocationService.ARG_LOCATION);
            this.callback.onLocationChange(info);
        }
    }

}

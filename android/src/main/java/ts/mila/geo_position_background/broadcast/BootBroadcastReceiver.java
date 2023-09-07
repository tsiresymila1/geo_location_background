package ts.mila.geo_position_background.broadcast;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ts.mila.geo_position_background.services.LocationService;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent("ts.mila.geo_position_background.services.BootBroadcastReceiver");
        i.setClass(context, LocationService.class);
        context.startService(i);
    }
}

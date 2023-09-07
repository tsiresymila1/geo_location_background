package ts.mila.geo_position_background.listeners;

import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.util.HashMap;

import io.flutter.plugin.common.EventChannel;
import ts.mila.geo_position_background.interfaces.LocationChangeListener;
import ts.mila.geo_position_background.utils.Util;

public class LocationChangeListenerCallback implements LocationChangeListener {
    EventChannel.EventSink targetEvent;
    BatteryManager batteryManager;

    public LocationChangeListenerCallback(EventChannel.EventSink targetEvent, BatteryManager bmManager) {
        this.targetEvent = targetEvent;
        this.batteryManager = bmManager;
    }

    @Override
    public void onLocationChange(Location location) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                JSONObject map = Util.locationToMap(location);
                HashMap<String, String> hasMap = Util.jsonToMap(map);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    hasMap.put("battery", String.valueOf(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)));
                } else {
                    hasMap.put("battery", "0");
                }
                targetEvent.success(hasMap);
            }
        });
    }
}

package ts.mila.geo_position_background.utils;

import android.location.Location;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class Util {

    public static JSONObject locationToMap(Location location) {
        JSONObject map = new JSONObject();
//        HashMap<String, String> map = new HashMap<>();
        try {
            map.put("latitude", String.valueOf(location.getLatitude()));
            map.put("longitude", String.valueOf(location.getLongitude()));
            map.put("speed", String.valueOf(location.getSpeed()));
            map.put("altitude", String.valueOf(location.getAltitude()));
            map.put("accuracy", String.valueOf(location.getAccuracy()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                map.put("speedometer", String.valueOf(location.getSpeedAccuracyMetersPerSecond()));
            } else {
                map.put("speedometer", "0.0");
            }
            map.put("timestamp", String.valueOf(location.getTime()));
            map.put("bearing", String.valueOf(location.getBearing()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                map.put("is_mock", String.valueOf(location.isMock()));
            } else {
                map.put("is_mock", String.valueOf(false));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    public static HashMap<String, String> jsonToMap(JSONObject json) {
        HashMap<String, String> map = new HashMap<>();
        for (Iterator<String> it = json.keys(); it.hasNext(); ) {
            String key = it.next();
            try {
                map.put(key, json.getString(key));
            } catch (JSONException ignored) {

            }
        }
        return map;
    }
}

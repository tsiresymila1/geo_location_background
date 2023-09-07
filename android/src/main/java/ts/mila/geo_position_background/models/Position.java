package ts.mila.geo_position_background.models;

import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Position extends RealmObject {

    private double latitude;

    private double longitude;

    private double altitude;

    private double speed;

    private double speedometer;

    private double accuracy;

    private double bearing;

    private int timestamp;

    private int battery;

    private boolean isMock;

    public Position(double latitude, double longitude, double altitude, double speed, double speedometer, double accuracy, double bearing, int timestamp, int battery, boolean isMock) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;
        this.speedometer = speedometer;
        this.accuracy = accuracy;
        this.bearing = bearing;
        this.timestamp = timestamp;
        this.battery = battery;
        this.isMock = isMock;
    }

    public Position() {
    }


    public static Position fromJson(HashMap<String, Object> object) {
        return new Position((Double) object.get("latitude"), (Double) object.get("longitude"), (Double) object.get("altitude"), (Double) object.get("speed"), (Double) object.get("speedometer"), (Double) object.get("accuracy"), (Double) object.get("bearing"), (Integer) object.get("timestamp"), (Integer) object.get("battery"), (Boolean) object.get("is_mock"));
    }

    public JSONObject toJsonObject() {
        JSONObject map = new JSONObject();
//        HashMap<String, String> map = new HashMap<>();
        try {
            map.put("latitude", latitude);
            map.put("longitude", longitude);
            map.put("speed", speed);
            map.put("altitude", altitude);
            map.put("accuracy", accuracy);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                map.put("speedometer", speedometer);
            } else {
                map.put("speedometer", "0.0");
            }
            map.put("timestamp", timestamp);
            map.put("bearing", bearing);
            map.put("battery", battery);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                map.put("is_mock", isMock);
            } else {
                map.put("is_mock", false);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

}

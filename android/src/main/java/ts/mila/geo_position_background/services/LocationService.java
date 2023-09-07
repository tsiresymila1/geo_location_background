package ts.mila.geo_position_background.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.location.*;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;


import io.flutter.Log;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import ts.mila.geo_position_background.R;
import ts.mila.geo_position_background.api.ApiClient;
import ts.mila.geo_position_background.utils.Config;
import ts.mila.geo_position_background.utils.LocationPermissionHelper;
import ts.mila.geo_position_background.utils.Util;

public class LocationService extends Service {

    public static final String ACTION_LOCATION = "action.LOCATION";
    public static final String ARG_LOCATION = "arg_location";
    final String NOTIFICATION_ID = "1001";
    final String CHANNEL_ID = "channel_id";
    final String CHANNEL_NAME = "Channel Name";
    final String KEY = "geo_location_bg";
    final String TAG = "[LOCATION SERVICE]";

    Config config;
    ApiClient apiClient;

    SharedPreferences sharedPref;
    Realm backgroundThreadRealm;
    BatteryManager batteryManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = this.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        this.displayForegroundNotification();
        Log.i(TAG, "Service started");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // setup realm
        backgroundThreadRealm = Realm.getDefaultInstance();
        apiClient = new ApiClient(backgroundThreadRealm);
        // setup realm
        startLocation();
        Config config = intent.getParcelableExtra("config");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        this.stopLocation();
        backgroundThreadRealm.close();
        Log.i(TAG, " Service destroyed");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private LocationRequest locationRequest() {
        return new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10 * 1000).build();
    }

    FusedLocationProviderClient fusedLocationClient() {
        return LocationServices.getFusedLocationProviderClient(this);
    }

    @SuppressLint("MissingPermission")
    private void startLocation() {
        Log.i(TAG, "Service MissingPermission");
        if (LocationPermissionHelper.hasLocationPermission(this)) {
            this.fusedLocationClient().requestLocationUpdates(this.locationRequest(), new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {

                        JSONObject map = Util.locationToMap(location);
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                map.put("battery", String.valueOf(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)));
                            }
                        } catch (JSONException ignored) {}
                        Intent i = new Intent(ACTION_LOCATION);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ARG_LOCATION, location);
                        i.putExtras(bundle);
                        sendBroadcast(i);
                        String baseurl = sharedPref.getString("URL", "");
                        boolean cached = sharedPref.getBoolean("cached", false);
                        apiClient.runPost(baseurl, map,cached);
                    }
                }
            }, null);
        }
    }

    private void stopLocation() {
        this.fusedLocationClient().removeLocationUpdates(new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        });
    }

    private void displayForegroundNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, this.CHANNEL_ID);
        builder.setContentTitle("Background service");
        builder.setContentText("Location background service");
        builder.setAutoCancel(false);
        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    this.CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(0, builder.build());
            startForeground(Integer.parseInt(this.NOTIFICATION_ID), builder.build());
        } else {
            notificationManager.notify(0, builder.build());
            startForeground(Integer.parseInt(this.NOTIFICATION_ID), builder.build());
        }
    }


}

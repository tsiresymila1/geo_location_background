package ts.mila.geo_position_background;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.BATTERY_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import ts.mila.geo_position_background.models.Position;
import ts.mila.geo_position_background.services.LocationService;
import ts.mila.geo_position_background.broadcast.LocationBroadcastReceiver;
import ts.mila.geo_position_background.utils.Config;
import ts.mila.geo_position_background.listeners.LocationChangeListenerCallback;


/**
 * GeoPositionBackgroundPlugin
 */
public class GeoPositionBackgroundPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.RequestPermissionsResultListener, EventChannel.StreamHandler {

    BatteryManager batteryManager;
    private MethodChannel channel;
    private EventChannel eventChannel;

    private  EventChannel realmEventChannel;
    private Activity pluginActivity;

    IntentFilter filter;

    private boolean isBackground = false;
    private Context context;

    private ActivityPluginBinding binding;
    final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    final String KEY = "geo_location_bg";
    final String TAG = "[GEO_POSITION_BACKGROUND]";

    EventChannel.EventSink targetEvent;

    Config config;

    SharedPreferences sharedPref;

    LocationBroadcastReceiver broadcastReceiver = new LocationBroadcastReceiver();

    Realm realmDb ;


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        Log.e(TAG, "onAttachedToEngine");
        filter = new IntentFilter(LocationService.ACTION_LOCATION);
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "geo_position_background");
        this.eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "geo_position_background/event");
        this.realmEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "geo_position_background/event/realm");
        context = flutterPluginBinding.getApplicationContext();
        sharedPref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            batteryManager = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        }
        // realm
        Realm.init(context);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("position")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .allowWritesOnUiThread(true)
                .allowQueriesOnUiThread(true)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        realmDb = Realm.getDefaultInstance();
        //
        channel.setMethodCallHandler(this);
        eventChannel.setStreamHandler(this);
        realmEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object arguments, EventChannel.EventSink events) {
                realmDb.where(Position.class).findAll().addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Position>>() {
                    @Override
                    public void onChange(RealmResults<Position> positions, OrderedCollectionChangeSet changeSet) {
                        events.success(positions.asJSON());
                    }
                });
            }

            @Override
            public void onCancel(Object arguments) {

            }
        });

    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
//        Log.e(TAG, "Method channel >>>> " + call.method + " >>>> args ::: " + call.arguments);
        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + Build.VERSION.RELEASE);
                break;
            case "start":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startService();
                }
                result.success(true);
                break;
            case "stop":
                stopService();
                result.success(true);
                break;
            case "purge":
                realmDb.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(@NonNull Realm transaction) {
                        transaction.delete(Position.class);
                        result.success(true);
                    }
                });
                break;
            case "configure":
                HashMap<String, Object> arg = (HashMap<String, Object>) call.arguments;
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("URL", Objects.requireNonNull(arg.get("serverURL")).toString());
                editor.putBoolean("startOnBoot", (Boolean) arg.get("startOnBoot"));
                editor.putBoolean("cached", (Boolean) arg.get("cached"));
                editor.putBoolean("stopOnTerminate", (Boolean) arg.get("stopOnTerminate"));
                config = new Config(
                        Objects.requireNonNull(arg.get("serverURL")).toString(),
                        (Boolean) arg.get("startOnBoot"),
                        (Boolean) arg.get("stopOnTerminate"));
                editor.apply();
                result.success(true);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public List<String> listPermissionNotGranted() {
        List<String> perms = new ArrayList<>();
        if (isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            perms.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            perms.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (isGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                perms.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
        }
        if (isGranted(Manifest.permission.RECEIVE_BOOT_COMPLETED)) {
            perms.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
        }
        if (isGranted(Manifest.permission.FOREGROUND_SERVICE)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                perms.add(Manifest.permission.FOREGROUND_SERVICE);
            }
        }
        return perms;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isGranted(String perm) {
        return pluginActivity.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermissions(int code) {
        List<String> perms = listPermissionNotGranted();
//        Log.e(TAG, "Permission to request ::: " + perms.toString());
        if (perms.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                Log.e(TAG, "Requesting permission ::: " + Arrays.toString(perms.toArray(new String[0])));
                binding.addRequestPermissionsResultListener(this);
                ActivityCompat.requestPermissions(pluginActivity, perms.toArray(new String[0]), code);
            }
        }

    }

    private boolean isRunning() {
        ActivityManager manager = (ActivityManager) pluginActivity.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            Log.i(TAG, "Service running ::: " + service.service.getClassName());
            if ("ts.mila.geo_position_background.services.LocationService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startService() {
        Log.e(TAG, "Starting service ::: ");
        this.requestPermissions(REQUEST_PERMISSIONS_REQUEST_CODE);
        if (!isRunning()) {
            Intent intent = new Intent(context, LocationService.class);
            Bundle b = new Bundle();
            b.putParcelable("config", config);
            intent.putExtras(b);
            if (isBackground) {
                context.startService(intent);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent);
                }
            }
            Log.e(TAG, "Service started ::: ");
        } else {
            Log.e(TAG, "Service already started ::: ");
        }
    }

    private void stopService() {
        Intent intent = new Intent(context, LocationService.class);
        context.stopService(intent);
        context.unregisterReceiver(broadcastReceiver);
    }


    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        eventChannel.setStreamHandler(null);
        Log.e(TAG, "onDetachedFromEngine");
    }


    @Override
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "Getting response of request permission >>> " + Arrays.toString(permissions) + Arrays.toString(grantResults));
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length == permissions.length) {
                List<Boolean> grants = new ArrayList<>();
                for (int g : grantResults) {
                    grants.add(g == PackageManager.PERMISSION_GRANTED);
                }
                return !grants.contains(false);
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        Log.e(TAG, "onAttachedToActivity");
        this.binding = binding;
        this.pluginActivity = binding.getActivity();
        binding.addRequestPermissionsResultListener(this);
        this.requestPermissions(REQUEST_PERMISSIONS_REQUEST_CODE);

    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        Log.e(TAG, "onDetachedFromActivityForConfigChanges");
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        Log.e(TAG, "onReattachedToActivityForConfigChanges");
        this.pluginActivity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        Log.e(TAG, "onDetachedFromActivity");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onListen(Object arguments, EventChannel.EventSink events){
        targetEvent = events;
        BatteryManager systemService = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        broadcastReceiver.setListener(new LocationChangeListenerCallback(events, systemService));
        context.registerReceiver(broadcastReceiver, filter);

    }

    @Override
    public void onCancel(Object arguments) {
        eventChannel.setStreamHandler(null);
        if (targetEvent != null) {
            targetEvent.endOfStream();
        }
    }
}

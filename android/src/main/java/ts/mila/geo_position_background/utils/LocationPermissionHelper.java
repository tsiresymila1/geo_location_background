package ts.mila.geo_position_background.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

public class LocationPermissionHelper {

    @RequiresApi(Build.VERSION_CODES.Q)
    private static final String LOCATION_BACKGROUND_PERMISSION = Manifest.permission.ACCESS_BACKGROUND_LOCATION;

    public static Boolean hasLocationPermission(Context context) {
        boolean hasPermissions = hasLocationCoarsePermission(context);
        if (!hasLocationFinePermission(context)) {
            hasPermissions = false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                && !hasLocationBackgroundPermission(context)
        ) {
            hasPermissions = false;
        }
        return hasPermissions;
    }

    private static Boolean hasLocationCoarsePermission(Context context) {
        String LOCATION_COARSE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
        return ContextCompat.checkSelfPermission(context, LOCATION_COARSE_PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }


    private static Boolean hasLocationFinePermission(Context context) {
        String LOCATION_FINE_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
        return ContextCompat.checkSelfPermission(context, LOCATION_FINE_PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private static Boolean hasLocationBackgroundPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, LOCATION_BACKGROUND_PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }

}

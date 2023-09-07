
import 'dart:async';

import 'package:geo_position_background/models/config.dart';
import 'package:geo_position_background/models/location.dart';
import 'package:permission_handler/permission_handler.dart';
import 'channels/interfaces/geo_position_background_platform_interface.dart';

typedef LocationChangeLister = Function(Location location);

base class GeoPositionBackground {

  /// Get version.
  Future<String?> getPlatformVersion() {
    return GeoPositionBackgroundPlatform.instance.getPlatformVersion();
  }
  /// start service location tracking.
  Future<bool?> start() async {
    if(!await Permission.location.request().isGranted){
      return false;
    }
    return await GeoPositionBackgroundPlatform.instance.start();
  }
  /// stop service location tracking.
  Future<bool?> stop() {
    return GeoPositionBackgroundPlatform.instance.stop();
  }
  /// configure service location tracking.
  Future<bool?> ready(GeoBgConfig config) {
    return GeoPositionBackgroundPlatform.instance.configure(config);
  }

  StreamSubscription<dynamic> onLocationChange(LocationChangeLister callback) {
    return GeoPositionBackgroundPlatform.instance.onLocationChange(callback);
  }
  StreamSubscription<dynamic> onCacheChange(CacheChangeListener callback) {
    return GeoPositionBackgroundPlatform.instance.onCacheChange(callback);
  }

}

final geoBG = GeoPositionBackground();

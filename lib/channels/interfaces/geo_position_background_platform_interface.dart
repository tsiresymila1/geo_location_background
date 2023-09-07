
import 'dart:async';

import 'package:geo_position_background/models/config.dart';
import 'package:geo_position_background/models/location.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import '../geo_position_background_channel.dart';

typedef LocationChangeListener = Function(Location location);
typedef CacheChangeListener = Function(List<Map<String, dynamic>> positions);


abstract class GeoPositionBackgroundPlatform extends PlatformInterface {
  /// Constructs a GeoPositionBackgroundPlatform.
  GeoPositionBackgroundPlatform() : super(token: _token);

  static final Object _token = Object();

  static GeoPositionBackgroundPlatform _instance = ChannelGeoPositionBackground();

  /// The default instance of [GeoPositionBackgroundPlatform] to use.
  ///
  /// Defaults to [MethodChannelGeoPositionBackground].
  static GeoPositionBackgroundPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [GeoPositionBackgroundPlatform] when
  /// they register themselves.
  static set instance(GeoPositionBackgroundPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<bool?> start() {
    throw UnimplementedError('start() has not been implemented.');
  }
  Future<bool?> stop() {
    throw UnimplementedError('stop() has not been implemented.');
  }
  Future<bool?> configure(GeoBgConfig config) {
    throw UnimplementedError('configure() has not been implemented.');
  }

  StreamSubscription<dynamic> onLocationChange(LocationChangeListener callback){
    throw UnimplementedError('onLocationChange(LocationChangeListener callback) has not been implemented.');
  }
  StreamSubscription<dynamic> onCacheChange(CacheChangeListener callback){
    throw UnimplementedError('onCacheChange(CacheChangeListener callback) has not been implemented.');
  }
}

import 'dart:async';
import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:geo_position_background/models/location.dart';
import '../models/config.dart';
import 'interfaces/geo_position_background_platform_interface.dart';

/// An implementation of [GeoPositionBackgroundPlatform] that uses method channels.
class ChannelGeoPositionBackground extends GeoPositionBackgroundPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('geo_position_background');

  @visibleForTesting
  final eventChannel = const EventChannel('geo_position_background/event');

  @visibleForTesting
  final realmEventChannel =
      const EventChannel('geo_position_background/event/realm');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<bool?> start() async {
    return await methodChannel.invokeMethod<bool>('start');
  }

  @override
  Future<bool?> stop() async {
    return await methodChannel.invokeMethod<bool>('stop');
  }

  @override
  Future<bool?> configure(GeoBgConfig config) async {
    return await methodChannel.invokeMethod<bool>('configure', config.toJson());
  }

  @override
  StreamSubscription<dynamic> onLocationChange(
      LocationChangeListener callback) {
    return eventChannel.receiveBroadcastStream().listen((event) {
      Map<String, dynamic> data =
          (event as Map<Object?, Object?>).cast<String, dynamic>();
      Location location = Location.fromJson(data);
      callback(location);
    });
  }

  @override
  StreamSubscription<dynamic> onCacheChange(CacheChangeListener callback) {
    return realmEventChannel.receiveBroadcastStream().listen((event) {
      List<Map<String, dynamic>> data = (event as List<Map<Object?, Object?>>)
          .map((e) => e.cast<String, dynamic>())
          .toList();
      callback(data);
    });
  }
}

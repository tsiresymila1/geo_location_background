import 'dart:async';

import 'package:flutter_test/flutter_test.dart';
import 'package:geo_position_background/geo_position_background.dart';
import 'package:geo_position_background/channels/interfaces/geo_position_background_platform_interface.dart';
import 'package:geo_position_background/channels/geo_position_background_channel.dart';
import 'package:geo_position_background/models/config.dart';
import 'package:geo_position_background/models/location.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockGeoPositionBackgroundPlatform
    with MockPlatformInterfaceMixin
    implements GeoPositionBackgroundPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<bool?> start() => Future.value(true);

  @override
  Future<bool?> stop() => Future.value(true);

  @override
  Future<bool?> configure(GeoBgConfig url) => Future.value(true);

  @override
  StreamSubscription<dynamic> onLocationChange(location) {
    throw Exception("onLocationChange");
  }
  @override
  StreamSubscription<dynamic> onCacheChange(position) {
    throw Exception("onCacheChange");
  }
}

void main() {
  final GeoPositionBackgroundPlatform initialPlatform = GeoPositionBackgroundPlatform.instance;

  test('$ChannelGeoPositionBackground is the default instance', () {
    expect(initialPlatform, isInstanceOf<ChannelGeoPositionBackground>());
  });

  test('getPlatformVersion', () async {
    GeoPositionBackground geoPositionBackgroundPlugin = GeoPositionBackground();
    MockGeoPositionBackgroundPlatform fakePlatform = MockGeoPositionBackgroundPlatform();
    GeoPositionBackgroundPlatform.instance = fakePlatform;

    expect(await geoPositionBackgroundPlugin.getPlatformVersion(), '42');
  });
}

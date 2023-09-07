import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:geo_position_background/channels/geo_position_background_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  ChannelGeoPositionBackground platform = ChannelGeoPositionBackground();
  const MethodChannel channel = MethodChannel('geo_position_background');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}

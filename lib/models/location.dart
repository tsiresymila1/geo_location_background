class Location {
  final double latitude;
  final double longitude;
  final double altitude;
  final double speed;
  final double speedometer;
  final double accuracy;
  final double bearing;
  final int timestamp;
  final int battery;
  final bool isMock;

  Location(
      {required this.latitude,
      required this.longitude,
      required this.altitude,
      required this.speed,
      required this.speedometer,
      required this.accuracy,
      required this.timestamp,
      required this.bearing,
      required this.isMock,
      required this.battery});

  factory Location.fromJson(Map<String, dynamic> data) {
    return Location(
        latitude: double.parse("${data["latitude"]}"),
        longitude: double.parse("${data["longitude"]}"),
        altitude: double.parse("${data["altitude"]}"),
        speed: double.parse("${data["speed"]}"),
        accuracy: double.parse("${data["accuracy"]}"),
        bearing: double.parse("${data["bearing"]}"),
        speedometer: double.parse("${data["speedometer"]}"),
        timestamp: int.parse("${data["timestamp"]}"),
        isMock: bool.parse("${data["is_mock"]}"),
        battery: int.parse("${data["battery"]}"));
  }

  Map<String, dynamic> toJson() {
    return {
      "latitude": latitude,
      "longitude": longitude,
      "altitude": altitude,
      "speed": speed,
      "accuracy": accuracy,
      "timestamp": timestamp,
      "bearing": bearing,
      "speedometer": speedometer,
      "isMock": isMock,
      "battery": battery
    };
  }

  @override
  String toString() {
    return "Location(latitude:$latitude,longitude:$longitude,altitude:$longitude,speed:$speed,accuracy:$accuracy,timestamp:$timestamp,bearing:$bearing,speedometer:$speedometer,isMock: $isMock,battery:$battery)";
  }
}

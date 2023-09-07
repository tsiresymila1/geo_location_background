class GeoBgConfig {
  final String serverURL;
  final bool startOnBoot;
  final bool cached;
  final bool stopOnTerminate;

  GeoBgConfig(
      {required this.serverURL,
      this.startOnBoot = false,
      this.cached = false,
      this.stopOnTerminate = false});

  Map<String, dynamic> toJson(){
    return {
      "serverURL": serverURL,
      "startOnBoot": startOnBoot,
      "cached": cached,
      "stopOnTerminate": stopOnTerminate
    };
  }
}

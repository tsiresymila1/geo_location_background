import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_osm_plugin/flutter_osm_plugin.dart';
import 'package:geo_position_background/geo_position_background.dart';
import 'package:geo_position_background/models/config.dart';
import 'package:geo_position_background/models/location.dart';

class HomPage extends StatefulWidget {
  const HomPage({Key? key}) : super(key: key);

  @override
  State<HomPage> createState() => _HomPageState();
}

class _HomPageState extends State<HomPage> {
  MapController controller = MapController.customLayer(
    initPosition: GeoPoint(latitude: 47.4358055, longitude: 8.4737324),
    customTile: CustomTile(
      sourceName: "openstreetmap",
      tileExtension: ".png",
      minZoomLevel: 2,
      maxZoomLevel: 19,
      urlsServers: [
        TileURLs(
          url: "https://tile.openstreetmap.org/",
          subdomains: [],
        )
      ],
      tileSize: 256,
    ),
  );

  Location? location;

  @override
  void initState() {
    geoBG
        .ready(GeoBgConfig(serverURL: "https://test.com/test"))
        .then((value) async {
      await geoBG.start();
      geoBG.onLocationChange((l) {
        debugPrint("Getting location >>>>>>>>>>>>>>>>>>>>>>>>>> $l");
        controller.goToLocation(GeoPoint(latitude: l.latitude, longitude: l.longitude));
        setState(() {
          location = l;
        });
      });
    }).catchError((e) {
      debugPrint(e.toString());
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Stack(
      children: [
        OSMFlutter(
            controller: controller,
            osmOption: OSMOption(
              userTrackingOption: const UserTrackingOption(
                  enableTracking: true, unFollowUser: true),
              zoomOption: const ZoomOption(
                initZoom: 13,
                minZoomLevel: 3,
                maxZoomLevel: 19,
                stepZoom: 1.0,
              ),
              userLocationMarker: UserLocationMaker(
                personMarker: const MarkerIcon(
                  icon: Icon(
                    Icons.location_on,
                    color: Colors.red,
                    grade: 5,
                    size: 100,
                    shadows: [
                      BoxShadow(color: Colors.black),
                      BoxShadow(color: Colors.black)
                    ],
                  ),
                ),
                directionArrowMarker: const MarkerIcon(
                  icon: Icon(
                    Icons.double_arrow,
                    size: 48,
                  ),
                ),
              ),
              roadConfiguration: const RoadOption(
                roadColor: Colors.grey,
              ),
              markerOption: MarkerOption(
                  defaultMarker: const MarkerIcon(
                icon: Icon(
                  Icons.person_pin_circle,
                  color: Colors.blue,
                  size: 56,
                ),
              )),
            )),
        Positioned(
          bottom: 0,
          left: 0,
          child: Container(
              decoration: BoxDecoration(
                  color: const Color(0xf2ffffff),
                  borderRadius: BorderRadius.circular(4)),
              width: 140,
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text(
                        "Lat:",
                        style: TextStyle(color: Colors.black54),
                      ),
                      Text((location?.latitude.toStringAsFixed(4) ?? "0"),
                          style: const TextStyle(color: Colors.black54))
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text(
                        "Lng:",
                        style: TextStyle(color: Colors.black54),
                      ),
                      Text((location?.longitude.toStringAsFixed(4) ?? "0"),
                          style: const TextStyle(color: Colors.black54))
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text(
                        "Alt:",
                        style: TextStyle(color: Colors.black54),
                      ),
                      Text((location?.altitude.toStringAsFixed(4) ?? "0"),
                          style: const TextStyle(color: Colors.black54))
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text(
                        "Speed:",
                        style: TextStyle(color: Colors.black54),
                      ),
                      Text((location?.speed.toStringAsFixed(4) ?? "0"),
                          style: const TextStyle(color: Colors.black54))
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text(
                        "Acc:",
                        style: TextStyle(color: Colors.black54),
                      ),
                      Text((location?.accuracy.toStringAsFixed(4) ?? "0"),
                          style: const TextStyle(color: Colors.black54))
                    ],
                  )
                ],
              )),
        )
      ],
    ),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          controller.goToLocation(await controller.myLocation());
        },
        child: const Icon(Icons.location_searching_outlined),
      ),
    );
  }
}

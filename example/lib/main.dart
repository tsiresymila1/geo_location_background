import 'package:flutter/material.dart';
import 'package:geo_position_background_example/presentation/pages/home.dart';
import 'package:geo_position_background_example/presentation/pages/setting.dart';
import 'package:go_router/go_router.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      debugShowCheckedModeBanner: false,
      theme: ThemeData.light(useMaterial3: true),
      darkTheme: ThemeData.dark(useMaterial3: true),
      color: Colors.teal,
      // : ColorScheme.fromSeed(seedColor: Colors.teal),
      routerConfig: GoRouter(routes: [
        GoRoute(path: "/", builder: (context, state) => const HomPage()),
        GoRoute(
            path: "/setting", builder: (context, state) => const SettingPage())
      ]),
    );
  }
}

# Door Dash Mobile

This is an Android conversion of the JavaFX game. The Android project keeps a copied version of the original `game.engine` package and builds a native mobile UI around it.

## Open

Open the `android` folder in Android Studio, let Gradle sync, then run the `app` configuration on an emulator or Android device.

## What was converted

- The game engine, cards, cells, monsters, board rules, and CSV data are reused.
- CSV files and visual/audio assets are packaged under `app/src/main/assets`.
- `MainActivity` provides a touch-first mobile interface with role selection, board rendering, roll dice, power-up, status, card, and victory feedback.

The desktop JavaFX project remains in the repository unchanged.

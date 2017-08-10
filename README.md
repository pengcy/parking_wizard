# Street parking reservation app for San Francisco.

The following assumes you have Android Studio Android SDK installed on your computer 
### Build and Compile through Android Studio
1. Download the codebase
2. Unzip it
3. Open Android Studio and select open a project and navigate to the unzipped folder
4. After Android Studio initialized the project, hit the run button

### Build and Compile through Command Line
Connect an Android device to your computer through USB, the open the command line window and type the following one at a time.
```
git clone https://github.com/pengcy/parking_wizard.git
cd parking_wizard
./gradlew build
./gradlew installDebug
adb shell am start -n com.example.parkingwizard/com.example.parkingwizard.ui.screen_map.MapsActivity
```

# More things to do
* Unit tests and instrumental tests
* UI polish
* Consolidating value resources
* Supporting different devices
* Supporting multi languages



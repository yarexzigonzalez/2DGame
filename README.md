1. Download .jar file
2. Locate javafx sdk address and confirm you have java version 23+ and javafx sdk 24+
3. Place .jar file into a folder (ex. into folder named gameproject)
4. Open terminal and type: cd C:\Users\example\Downloads\gameproject (folder name in this case is "gameproject")
5. Then into terminal type: java --module-path "C:\Users\example\Downloads\openjfx-24.0.1_windows-x64_bin-sdk\javafx-sdk-24.0.1\lib" --add-modules javafx.controls,javafx.fxml -jar game-1.0-SNAPSHOT.jar
* Make sure javafx sdk path (address) is accuarate *
* This should successfully run program * 

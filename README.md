# HardNestedGUI
Web-based dockerised GUI for a hard nested attack on MiFare Classic Cards

## Compiling

The project is written for Java 11 and our build process makes use of [Gradle](http://gradle.org/).
simply run:  
Linux / macOS

    ./gradlew build
    
Windows
    
    gradlew build

Dependencies are automatically handled by Gradle.

## Running Instructions (Without Docker, on Kali Linux)

1. Follow the compiling instructions
2. Take the -dist jar in the `build/libs` folder, and place it inside the `binaries` directory.
3. Run the jar file, and navigate to `localhost:5078` in a web browser
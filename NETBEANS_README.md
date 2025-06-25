# Running NotizDesktop in NetBeans

This guide will help you run the NotizDesktop application in NetBeans IDE.

## Main Class

The main class for the application is:

```
notizdesktop.NotizDesktopApplication
```

## Running the Application in NetBeans

### Method 1: Run Project

1. Right-click on the project in the Projects panel
2. Select "Run" or press F6
3. NetBeans should automatically use the main class defined in project.properties

### Method 2: Run File

1. Navigate to `src/main/java/notizdesktop/NotizDesktopApplication.java` in the Projects panel
2. Right-click on the file
3. Select "Run File" or press Shift+F6

### Method 3: Set Main Class Manually

If NetBeans doesn't recognize the main class:

1. Right-click on the project in the Projects panel
2. Select "Properties"
3. Go to "Run" category
4. Set "Main Class" to `notizdesktop.NotizDesktopApplication`
5. Click "OK"
6. Now you can run the project with F6

## Troubleshooting

### Too Many Source Packages

If you see too many source packages and don't know which one to run:

1. Look for the package named `notizdesktop` (not any sub-packages)
2. Find the class `NotizDesktopApplication.java` within that package
3. Right-click on this file and select "Run File"

### Build Issues

If you encounter build issues:

1. Right-click on the project and select "Clean and Build"
2. Check that all required libraries are in the `lib` directory
3. Verify that the main class is correctly set in project properties

### Alternative Run Methods

You can also use the provided scripts:

- On Windows: Double-click `run-main-class.bat`
- On Linux/macOS: Run `./run-main-class.sh`

These scripts will compile and run the main class directly.
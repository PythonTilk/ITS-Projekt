@echo off
REM Windows batch file to run the Notiz Desktop Application
REM Make sure Java 17+ and Maven are installed and in PATH

echo Starting Notiz Desktop Application...
echo.

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher and add it to your PATH
    pause
    exit /b 1
)

REM Check if Maven is available
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven and add it to your PATH
    pause
    exit /b 1
)

REM Compile and run the application
echo Compiling application...
mvn clean compile

if %errorlevel% neq 0 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

echo.
echo Starting application...
echo Make sure MySQL is running and the database is set up!
echo.

REM Run the application with Windows-specific JVM arguments
java -Dfile.encoding=UTF-8 ^
     -Dsun.java2d.dpiaware=true ^
     -Dswing.aatext=true ^
     -Dawt.useSystemAAFontSettings=on ^
     -cp "target/classes;target/dependency/*" ^
     notizdesktop.NotizDesktopApplication

if %errorlevel% neq 0 (
    echo.
    echo Application exited with error code %errorlevel%
    pause
)
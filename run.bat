@echo off
REM Script to run the NotizDesktop application with the correct classpath

REM Set the classpath to include all libraries
set CLASSPATH=build;dist\NotizDesktop.jar

REM Add all JAR files in the lib directory to the classpath
for %%j in (lib\*.jar) do (
  set CLASSPATH=!CLASSPATH!;%%j
)

REM Run the application
java -cp "%CLASSPATH%" notizapp.NotizDesktopApplication

pause
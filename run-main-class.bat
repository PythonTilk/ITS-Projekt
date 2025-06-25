@echo off
REM Script to run the main class of NotizDesktop

echo Running NotizDesktop main class: notizdesktop.NotizDesktopApplication

REM Build the classpath with all dependencies
set CLASSPATH=build

for %%j in (lib\*.jar) do (
  set CLASSPATH=!CLASSPATH!;%%j
)

REM Run the main class
java -cp "%CLASSPATH%" notizdesktop.NotizDesktopApplication
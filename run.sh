#!/bin/bash
# Script to run the NotizDesktop application with the correct classpath

# Set the classpath to include all libraries
CLASSPATH="build:dist/NotizDesktop.jar"

# Add all JAR files in the lib directory to the classpath
for jar in lib/*.jar; do
  CLASSPATH="$CLASSPATH:$jar"
done

# Run the application
java -cp "$CLASSPATH" notizapp.NotizDesktopApplication
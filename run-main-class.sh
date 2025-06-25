#!/bin/bash
# Script to run the main class of NotizDesktop

echo "Running NotizDesktop main class: notizapp.NotizDesktopApplication"

# Build the classpath with all dependencies
CLASSPATH="build"
for jar in lib/*.jar; do
  CLASSPATH="$CLASSPATH:$jar"
done

# Run the main class
java -cp "$CLASSPATH" notizapp.NotizDesktopApplication
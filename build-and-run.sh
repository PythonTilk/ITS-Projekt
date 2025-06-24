#!/bin/bash

# Notiz Desktop Application - Build and Run Script
# This script builds and runs the desktop application

echo "=== Notiz Desktop Application Build Script ==="
echo

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

echo "✅ Prerequisites check passed"
echo

# Clean and compile
echo "🔨 Building application..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo
    
    # Ask user what to do
    echo "Choose an option:"
    echo "1) Run application directly"
    echo "2) Create executable JAR"
    echo "3) Both"
    read -p "Enter choice (1-3): " choice
    
    case $choice in
        1)
            echo "🚀 Running application..."
            mvn exec:java
            ;;
        2)
            echo "📦 Creating executable JAR..."
            mvn clean package
            if [ $? -eq 0 ]; then
                echo "✅ JAR created successfully!"
                echo "📍 Location: target/notiz-desktop-1.0.0-jar-with-dependencies.jar"
                echo "🚀 To run: java -jar target/notiz-desktop-1.0.0-jar-with-dependencies.jar"
            fi
            ;;
        3)
            echo "📦 Creating executable JAR..."
            mvn clean package
            if [ $? -eq 0 ]; then
                echo "✅ JAR created successfully!"
                echo "🚀 Running application..."
                java -jar target/notiz-desktop-1.0.0-jar-with-dependencies.jar
            fi
            ;;
        *)
            echo "Invalid choice. Exiting."
            ;;
    esac
else
    echo "❌ Build failed. Please check the error messages above."
    exit 1
fi
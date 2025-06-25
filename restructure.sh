#!/bin/bash
# Script to restructure the project to a single package

# Create the new package directory
mkdir -p src/main/java/notizapp

# Process each Java file
for file in $(find src/main/java/notizdesktop -name "*.java"); do
  # Get the filename without path
  filename=$(basename "$file")
  
  # Create the new file with updated package declaration
  sed 's/package notizdesktop\(\.[a-z]*\)\{0,1\};/package notizapp;/' "$file" > "src/main/java/notizapp/$filename"
  
  # Update import statements in the new file
  sed -i 's/import notizdesktop\./import notizapp./g' "src/main/java/notizapp/$filename"
done

# Update the build.xml file
sed -i 's/notizdesktop\.NotizDesktopApplication/notizapp.NotizDesktopApplication/g' build.xml

# Update the project.properties file
sed -i 's/main\.class=notizdesktop\.NotizDesktopApplication/main.class=notizapp.NotizDesktopApplication/g' nbproject/project.properties

# Update the run scripts
sed -i 's/notizdesktop\.NotizDesktopApplication/notizapp.NotizDesktopApplication/g' run-main-class.sh
sed -i 's/notizdesktop\.NotizDesktopApplication/notizapp.NotizDesktopApplication/g' run-main-class.bat

# Update the NETBEANS_README.md file
sed -i 's/notizdesktop\.NotizDesktopApplication/notizapp.NotizDesktopApplication/g' NETBEANS_README.md

echo "Project restructured to single package 'notizapp'"
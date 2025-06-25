#!/bin/bash
# Script to fix import statements in all Java files

cd /workspace/ITS-Projekt

# Update all import statements to use the flat package structure
for file in src/main/java/notizapp/*.java; do
  # Remove subpackage imports
  sed -i 's/import notizapp\.[a-z]*\./import notizapp./g' "$file"
done

echo "Import statements updated"
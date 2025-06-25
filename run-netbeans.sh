#!/bin/bash
# Run script for NotizDesktop application in NetBeans environment

# Set Java home if needed
# export JAVA_HOME=/usr/lib/jvm/java-17-openjdk

# Set classpath
CLASSPATH="dist/NotizDesktop.jar"
CLASSPATH="$CLASSPATH:lib/mysql-connector-java-8.0.23.jar"
CLASSPATH="$CLASSPATH:lib/spring-security-crypto-5.7.2.jar"
CLASSPATH="$CLASSPATH:lib/slf4j-api-1.7.36.jar"
CLASSPATH="$CLASSPATH:lib/logback-classic-1.2.12.jar"
CLASSPATH="$CLASSPATH:lib/logback-core-1.2.12.jar"

# Run the application
java -cp "$CLASSPATH" notizdesktop.NotizDesktopApplication
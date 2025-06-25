@echo off
REM Run script for NotizDesktop application in NetBeans environment

REM Set Java home if needed
REM set JAVA_HOME=C:\Program Files\Java\jdk-17

REM Set classpath
set CLASSPATH=dist\NotizDesktop.jar
set CLASSPATH=%CLASSPATH%;lib\mysql-connector-java-8.0.23.jar
set CLASSPATH=%CLASSPATH%;lib\spring-security-crypto-5.7.2.jar
set CLASSPATH=%CLASSPATH%;lib\slf4j-api-1.7.36.jar
set CLASSPATH=%CLASSPATH%;lib\logback-classic-1.2.12.jar
set CLASSPATH=%CLASSPATH%;lib\logback-core-1.2.12.jar

REM Run the application
java -cp %CLASSPATH% notizapp.NotizDesktopApplication

REM Pause to see any error messages
pause
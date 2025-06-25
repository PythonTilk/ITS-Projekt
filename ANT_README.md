# NotizDesktop - Ant Build Instructions

This document provides instructions for building and running the NotizDesktop application using Apache Ant and NetBeans.

## Prerequisites

- Java Development Kit (JDK) 17 or higher
- Apache Ant 1.10.0 or higher
- NetBeans IDE 12.0 or higher (optional)
- MySQL Server 8.0 or higher

## Project Structure

```
NotizDesktop/
├── build.xml                  # Main Ant build file
├── manifest.mf                # JAR manifest file
├── src/                       # Source code
│   ├── main/
│   │   ├── java/              # Java source files
│   │   └── resources/         # Resource files (properties, etc.)
│   └── test/                  # Test source files
├── lib/                       # External libraries
│   ├── mysql-connector-java-8.0.23.jar
│   └── ...
├── nbproject/                 # NetBeans project files
│   ├── project.xml
│   ├── project.properties
│   └── build-impl.xml
└── ANT_README.md              # This file
```

## Building with Ant

### Available Targets

- `clean`: Removes all build artifacts
- `compile`: Compiles the source code
- `jar`: Creates the application JAR file
- `fatjar`: Creates a single JAR with all dependencies included
- `test`: Runs the unit tests
- `javadoc`: Generates JavaDoc documentation
- `run`: Runs the application
- `all`: Cleans, builds, tests, and generates documentation

### Building the Project

To build the project, run:

```bash
ant jar
```

This will compile the source code and create a JAR file in the `dist` directory.

### Running the Application

To run the application, use:

```bash
ant run
```

Or directly run the JAR file:

```bash
java -jar dist/NotizDesktop.jar
```

## Database Setup

Before running the application, make sure to set up the MySQL database:

1. Install MySQL Server if not already installed
2. Run the SQL script to create the database and user:

```bash
mysql -u root -p < its-projekt18.6.sql
```

## Opening in NetBeans

To open the project in NetBeans:

1. Start NetBeans IDE
2. Select File > Open Project
3. Navigate to the project directory and select it
4. The project should open with all the Ant targets available

## Troubleshooting

If you encounter any issues:

1. Make sure all required libraries are in the `lib` directory
2. Verify that the MySQL server is running
3. Check the database connection settings in `src/main/resources/application.properties`
4. Ensure JDK 17 or higher is installed and configured correctly

## Additional Information

- The application uses the MySQL Connector/J library for database connectivity
- Spring Security Crypto is used for password hashing
- SLF4J and Logback are used for logging
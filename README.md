# NoteGO - Notizprojekt

## Overview

NoteGO (ITS-Projekt) is a Java-based note-taking application that allows users to create, edit, and share notes. The application uses a MySQL database to store user information and notes.

## Features

- **User Authentication**: Secure login and registration system
- **Personal Notes**: Create, edit, and manage your private notes
- **Note Sharing**: Share notes with other users in the system
- **Organization**: Organize notes with tags for easy retrieval

## Technical Details

### Database Setup

The project uses a MySQL database. You can find the database schema in the [`its-projekt18.6.sql`](its-projekt18.6.sql) file, which includes:

- `nutzer` table for user information
- `notiz` table for personal notes

You can run MySQL directly on your machine or use Docker for a containerized setup:

```bash
# Run MySQL in Docker
docker run --name mysql -e MYSQL_ROOT_PASSWORD=420 -p 3306:3306 -d mysql:8.0
```

```sql
-- Example database connection
jdbc:mysql://localhost:3306/notizprojekt
```

### Project Structure

The project has been significantly simplified from 25 classes to just 5 core classes:

- `src/NotizProjekt_All/` - Contains all Java source files
  - [`NotizProjekt.java`](src/NotizProjekt_All/NotizProjekt.java) - Main application entry point
  - [`DBVerbindung.java`](src/NotizProjekt_All/DBVerbindung.java) - Database connection handler
  - [`GUI.java`](src/NotizProjekt_All/GUI.java) - Consolidated GUI class containing all UI functionality
  - [`Notiz.java`](src/NotizProjekt_All/Notiz.java) - Unified note model class
  - [`User.java`](src/NotizProjekt_All/User.java) - User model class
- [`build.xml`](build.xml) - Ant build file for compiling the project
- `nbproject/` - NetBeans project configuration
- `lib/` - External libraries (JDBC driver)

### Requirements

- Java Development Kit (JDK) 8 or higher
- MySQL Server 5.7 or higher (or Docker for containerized setup)
- JDBC MySQL Connector (included in the `lib` directory)
- NetBeans IDE (recommended for development)

## Getting Started

Welcome to ITS-Projekt! Follow these simple steps to get up and running:

1. Clone this repository to your local machine
   ```bash
   git clone https://github.com/PythonTilk/ITS-Projekt.git
   ```
2. Import the database schema from [`its-projekt18.6.sql`](its-projekt18.6.sql)
   ```bash
   # Import schema
   mysql -u root -p < its-projekt18.6.sql
   
   # Create application user (if not already in the schema)
   mysql -u root -p -e "CREATE USER IF NOT EXISTS 'notizuser'@'%' IDENTIFIED BY 'notizpassword'; GRANT ALL PRIVILEGES ON notizprojekt.* TO 'notizuser'@'%'; FLUSH PRIVILEGES;"
   ```
3. Open the project in NetBeans or your preferred Java IDE
4. Configure the database connection in the application (see [Database Setup](#database-setup))
5. Build and run the project

That's it! You're ready to start creating and sharing notes. For more detailed instructions on using the application, see the [Usage](#usage) section below.

## Usage

After starting the application:

1. Register a new account or log in with existing credentials
   - Default test account: Username: `root`, Password: `420`
2. Use the main interface to create and manage your notes
3. Access the sharing options to collaborate with other users

```java
// Example login code
DBVerbindung dbConnection = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
dbConnection.open();
```

## Project Simplification

The project has been significantly simplified:

1. **Reduced Class Count**: From 25 classes to just 5 core classes
2. **Consolidated GUI**: All GUI functionality is now in a single class
3. **Unified Note Model**: All note types are now handled by a single class with a type attribute
4. **Removed Test Classes**: Test classes have been removed as they're not needed for the application's functionality

### Changes Made

- **GUI Consolidation**: All GUI classes (GUI_Anmelden, SimpleGUI_MenuTabelle, etc.) have been merged into a single GUI class
- **Model Simplification**: The note model classes have been unified into a single Notiz class with a NotizTyp enum
- **Test Removal**: All test classes have been removed to simplify the codebase
- **Code Cleanup**: Redundant code has been removed and the remaining code has been optimized

### Running the Application

```bash
# Compile the project
javac -d build/classes -cp lib/mysql-connector-java-8.0.28.jar src/NotizProjekt_All/*.java

# Run the application
java -cp build/classes:lib/mysql-connector-java-8.0.28.jar NotizProjekt_All.NotizProjekt
```

## Troubleshooting

### Database Connection Issues

If you encounter database connection problems:

1. Verify MySQL is running:
   ```bash
   # For local MySQL
   sudo systemctl status mysql
   
   # For Docker
   docker ps | grep mysql
   ```

2. Check connection parameters in `DBVerbindung.java`:
   - Host: Usually `localhost` or `127.0.0.1`
   - Database name: `notizprojekt`
   - Username: `notizuser`
   - Password: `notizpassword`

3. Test database connectivity:
   ```bash
   mysql -u notizuser -p -h localhost notizprojekt
   ```

### Compilation Errors

If you encounter compilation errors:

1. Ensure all required libraries are in the `lib` directory
2. Check that your Java version is compatible (JDK 8+)
3. Run the tests to verify basic functionality

## License

This project is licensed under the terms included in the [LICENSE](LICENSE) file.
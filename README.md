# ITS-Projekt

## Overview

ITS-Projekt is a note-taking application that allows users to create, edit, and share notes. The application uses a MySQL database to store user information and notes. It is available in two versions:

1. **Desktop Application**: A Java Swing-based desktop application
2. **Web Application**: A Spring Boot-based web application with a modern drag-and-drop interface

## Features

- **User Authentication**: Secure login and registration system
- **Personal Notes**: Create, edit, and manage your private notes
- **Note Sharing**: Share notes with other users in the system
- **Organization**: Organize notes with tags for easy retrieval
- **Drag-and-Drop Interface** (Web Version): Intuitive interface for organizing notes visually
- **Responsive Design** (Web Version): Access your notes from any device with a web browser

## Technical Details

### Database Setup

The project uses a MySQL database. You can find the database schema in the [`its-projekt18.6.sql`](its-projekt18.6.sql) file, which includes:

- `nutzer` table for user information
- `notiz` table for personal notes
- `geteilte_notizen` table for shared notes

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

#### Desktop Application (main branch)
- `src/NotizProjekt_All/` - Contains all Java source files
  - [`NotizProjekt.java`](src/NotizProjekt_All/NotizProjekt.java) - Main application entry point
  - [`DBVerbindung.java`](src/NotizProjekt_All/DBVerbindung.java) - Database connection handler
  - `GUI_*.java` - User interface components
- [`build.xml`](build.xml) - Ant build file for compiling the project
- `nbproject/` - NetBeans project configuration
- `lib/` - External libraries (JDBC driver)

#### Web Application (html branch)
- `src/main/java/notizprojekt/web/` - Contains all Java source files
  - `controller/` - Web and API controllers
  - `model/` - Entity classes (User, Note)
  - `repository/` - Data access interfaces
  - `service/` - Business logic services
  - `config/` - Application configuration
- `src/main/resources/` - Contains resources
  - `static/` - Static web assets (CSS, JavaScript)
  - `templates/` - Thymeleaf HTML templates
- `pom.xml` - Maven project configuration

### Requirements

#### Desktop Application
- Java Development Kit (JDK) 8 or higher
- MySQL Server 5.7 or higher (or Docker for containerized setup)
- JDBC MySQL Connector (included in the `lib` directory)
- NetBeans IDE (recommended for development)

#### Web Application
- Java Development Kit (JDK) 11 or higher
- MySQL Server 5.7 or higher
- Maven 3.6 or higher
- Spring Boot 2.7.0
- Modern web browser

## Getting Started

Welcome to ITS-Projekt! Follow these simple steps to get up and running:

### Common Setup (Both Versions)

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

### Desktop Application Setup

1. Open the project in NetBeans or your preferred Java IDE
2. Configure the database connection in the application (see [Database Setup](#database-setup))
3. Build and run the project

### Web Application Setup

1. Switch to the web application branch
   ```bash
   git checkout html
   ```
2. Build the project using Maven
   ```bash
   ./mvnw clean package
   ```
3. Run the Spring Boot application
   ```bash
   ./mvnw spring-boot:run
   ```
4. Access the application in your web browser at `http://localhost:12000`

That's it! You're ready to start creating and sharing notes. For more detailed instructions on using the application, see the [Usage](#usage) section below.

## Usage

### Desktop Application

After starting the desktop application:

1. Register a new account or log in with existing credentials
   - Default test account: Username: `root`, Password: `420`
2. Use the main interface to create and manage your notes
3. Access the sharing options to collaborate with other users

```java
// Example login code
DBVerbindung dbConnection = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
dbConnection.open();
```

### Web Application

After starting the web application:

1. Access the application in your web browser at `http://localhost:12000`
2. Register a new account or log in with existing credentials
   - Default test account: Username: `testuser123`, Password: `password123`
3. Use the interactive board to:
   - Create new notes by clicking the "Add Note" button
   - Drag and drop notes to organize them spatially
   - Double-click on a note to edit its content
   - Change note colors to categorize them visually
   - Add tags to notes for better organization
4. Notes are automatically saved when you make changes

## Testing

### Desktop Application Tests

The desktop project includes several test classes to verify database connectivity and functionality:

- [`TestDBConnection.java`](src/NotizProjekt_All/TestDBConnection.java) - Tests basic database connection and retrieves users and notes
- [`TestLogin.java`](src/NotizProjekt_All/TestLogin.java) - Tests user authentication functionality
- [`TestNoteCreation.java`](src/NotizProjekt_All/TestNoteCreation.java) - Tests creating new notes in the database
- [`TestNoteSharing.java`](src/NotizProjekt_All/TestNoteSharing.java) - Tests sharing notes between users

To run the desktop tests:

```bash
# Compile the test classes
javac -d build/classes -cp lib/mysql-connector-java-8.0.28.jar src/NotizProjekt_All/DBVerbindung.java src/NotizProjekt_All/Test*.java

# Run a specific test
java -cp build/classes:lib/mysql-connector-java-8.0.28.jar NotizProjekt_All.TestDBConnection
```

### Web Application Tests

The web application includes a basic test class to verify Spring Boot configuration:

- [`NotizprojektWebApplicationTests.java`](src/test/java/notizprojekt/web/NotizprojektWebApplicationTests.java) - Tests Spring Boot application context loading

To run the web application tests:

```bash
# Run all tests
./mvnw test

# Run a specific test
./mvnw test -Dtest=NotizprojektWebApplicationTests
```

You can also manually test the web application by:

1. Starting the application with `./mvnw spring-boot:run`
2. Accessing the application in your browser at `http://localhost:12000`
3. Testing user registration, login, and note management functionality

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

2. Check connection parameters:
   - Desktop App: Check `DBVerbindung.java`
   - Web App: Check `application.properties`
   - Host: Usually `localhost` or `127.0.0.1`
   - Database name: `notizprojekt`
   - Username: `notizuser`
   - Password: `notizpassword`

3. Test database connectivity:
   ```bash
   mysql -u notizuser -p -h localhost notizprojekt
   ```

### Desktop Application Issues

If you encounter compilation errors in the desktop application:

1. Ensure all required libraries are in the `lib` directory
2. Check that your Java version is compatible (JDK 8+)
3. Run the tests to verify basic functionality

### Web Application Issues

If you encounter issues with the web application:

1. Check the Spring Boot logs for error messages
2. Verify that the application is running on the correct port (12000)
3. Check browser console for JavaScript errors
4. Ensure your database has the required columns:
   ```sql
   -- Check if position and color columns exist
   DESCRIBE notiz;
   
   -- Add columns if they don't exist
   ALTER TABLE notiz ADD COLUMN IF NOT EXISTS position_x INT DEFAULT 0;
   ALTER TABLE notiz ADD COLUMN IF NOT EXISTS position_y INT DEFAULT 0;
   ALTER TABLE notiz ADD COLUMN IF NOT EXISTS color VARCHAR(20) DEFAULT '#FFFF88';
   ```
5. Clear browser cache if you're seeing outdated content

## License

This project is licensed under the terms included in the [LICENSE](LICENSE) file.
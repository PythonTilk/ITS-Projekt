# ITS-Projekt

## Overview

ITS-Projekt is a Java-based note-taking application that allows users to create, edit, and share notes. The application uses a MySQL database to store user information and notes.

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
- `geteilte_notizen` table for shared notes

```sql
-- Example database connection
jdbc:mysql://localhost:3306/its-projekt
```

### Project Structure

- `src/NotizProjekt_All/` - Contains all Java source files
  - `NotizProjekt.java` - Main application entry point
  - `DBVerbindung.java` - Database connection handler
  - `GUI_*.java` - User interface components
- `build.xml` - Ant build file for compiling the project
- `nbproject/` - NetBeans project configuration
- `lib/` - External libraries (JDBC driver)

### Requirements

- Java Development Kit (JDK) 8 or higher
- MySQL Server 5.7 or higher
- NetBeans IDE (recommended for development)

## Getting Started

1. Clone this repository to your local machine
   ```bash
   git clone https://github.com/PythonTilk/ITS-Projekt.git
   ```
2. Import the database schema from [`its-projekt18.6.sql`](its-projekt18.6.sql)
   ```bash
   mysql -u root -p < its-projekt18.6.sql
   ```
3. Open the project in NetBeans or your preferred Java IDE
4. Configure the database connection in the application (see [Database Setup](#database-setup))
5. Build and run the project

## Usage

After starting the application:

1. Register a new account or log in with existing credentials
   - Default test account: Username: `root`, Password: `420`
2. Use the main interface to create and manage your notes
3. Access the sharing options to collaborate with other users

```java
// Example login code
DBVerbindung dbConnection = new DBVerbindung("localhost", "its-projekt", "username", "password");
dbConnection.open();
```

## Testing

The project includes several test classes to verify database connectivity and functionality:

- `TestDBConnection.java` - Tests basic database connection and retrieves users and notes
- `TestLogin.java` - Tests user authentication functionality
- `TestNoteCreation.java` - Tests creating new notes in the database
- `TestNoteSharing.java` - Tests sharing notes between users

To run the tests:

```bash
# Compile the test classes
javac -d build/classes -cp lib/mysql-connector-java-8.0.28.jar src/NotizProjekt_All/DBVerbindung.java src/NotizProjekt_All/Test*.java

# Run a specific test
java -cp build/classes:lib/mysql-connector-java-8.0.28.jar NotizProjekt_All.TestDBConnection
```

## License

This project is licensed under the terms included in the [LICENSE](LICENSE) file.
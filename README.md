# Notiz Desktop Application (Ant Build)

A Java Swing desktop application for note-taking with drag-and-drop functionality, rich text editing, and comprehensive note management features. This application provides a native desktop experience with modern UI design and robust database integration. This version uses Apache Ant for building and is compatible with NetBeans IDE.

## Features

### Core Functionality
- **User Authentication**: Secure login and registration system
- **Note Management**: Create, edit, delete, and organize notes
- **Drag & Drop Interface**: Intuitive note positioning on a virtual board
- **Rich Text Support**: Both plain text and HTML rich text editing
- **Search Functionality**: Find notes by title, content, or tags
- **Theme Support**: Light and dark mode themes

### Note Features
- **Multiple Note Types**: Text, code, image, and checklist notes
- **Privacy Levels**: Private, shared with specific users, or public notes
- **Color Coding**: Customizable note colors for organization
- **Tagging System**: Organize notes with custom tags
- **Collaborative Editing**: Share notes with other users

### User Management
- **Profile Management**: Update display name, email, and password
- **User Search**: Find other users for note sharing
- **Avatar System**: Automatic initial-based avatars

## Technical Architecture

### Technology Stack
- **Java 17+**: Core application language
- **Swing/AWT**: GUI framework
- **MySQL**: Database (shared with web application)
- **Spring Security BCrypt**: Password encryption
- **Apache Ant**: Build and dependency management
- **NetBeans IDE**: Recommended development environment

### Project Structure
```
NotizDesktop/
├── build.xml                  # Main Ant build file
├── manifest.mf                # JAR manifest file
├── src/                       # Source code
│   ├── main/
│   │   ├── java/notizapp/     # Single package structure
│   │   │   ├── NotizDesktopApplication.java    # Main application entry point
│   │   │   ├── DatabaseConfig.java            # Database connection configuration
│   │   │   ├── DesktopNote.java               # Note data model
│   │   │   ├── DesktopUser.java               # User data model
│   │   │   ├── NoteService.java               # Note business logic
│   │   │   ├── UserService.java               # User business logic
│   │   │   ├── LoginFrame.java                # Login window
│   │   │   ├── RegisterDialog.java            # User registration dialog
│   │   │   ├── MainFrame.java                 # Main application window
│   │   │   ├── NoteBoardPanel.java            # Note board with drag-and-drop
│   │   │   ├── NoteEditDialog.java            # Note creation/editing dialog
│   │   │   ├── NoteViewDialog.java            # Read-only note viewer
│   │   │   ├── ProfileDialog.java             # User profile management
│   │   │   └── ThemeManager.java              # Theme management system
│   │   └── resources/         # Resource files (properties, etc.)
│   └── test/                  # Test source files
├── lib/                       # External libraries
│   ├── mysql-connector-java-8.0.23.jar
│   ├── spring-security-crypto-5.7.2.jar
│   ├── slf4j-api-1.7.36.jar
│   ├── logback-classic-1.2.12.jar
│   └── logback-core-1.2.12.jar
├── nbproject/                 # NetBeans project files
│   ├── project.xml
│   ├── project.properties
│   └── build-impl.xml
└── build/                     # Compiled classes (generated)
└── dist/                      # Distribution JARs (generated)
```

> **Note:** The project now uses a flat package structure with all classes in a single `notizapp` package. This simplifies navigation and makes it easier to run the application in NetBeans.

## Installation and Setup

### Prerequisites
- Java 17 or higher
- Apache Ant 1.10.0 or higher
- NetBeans IDE 12.0 or higher (optional)
- MySQL database server
- Network connectivity for database access

### Database Setup
The application requires a MySQL database with the following tables:
- `nutzer` (users)
- `notiz` (notes)

Use the provided SQL script `its-projekt18.6.sql` to create the database schema.

The database schema includes the following user profile columns:
- `display_name` - User's display name (defaults to username if not provided)
- `biography` - User's biography text
- `profile_picture` - Path to user's profile picture
- `b_id` - Business ID for integration with other systems

### Building the Application
```bash
# Clone the repository
git clone <repository-url>
cd ITS-Projekt

# Build the application
ant compile

# Run the application (Linux/macOS)
./run.sh

# Run the application (Windows)
run.bat

# Alternative: Run with Ant
ant run

# Create executable JAR
ant jar

# Run the JAR (Linux/macOS)
java -cp "dist/NotizDesktop.jar:lib/*" notizapp.NotizDesktopApplication

# Run the JAR (Windows)
java -cp "dist/NotizDesktop.jar;lib/*" notizapp.NotizDesktopApplication

# Create executable JAR with all dependencies
ant fatjar
java -jar dist/NotizDesktop-with-dependencies.jar

# Generate JavaDoc documentation
ant javadoc

# Clean the project
ant clean

# Build everything (clean, compile, test, jar, javadoc)
ant all
```

### Troubleshooting Library Issues

If you encounter errors about missing libraries:

1. **MySQL JDBC Driver**: Make sure `mysql-connector-java-8.0.23.jar` is in the `lib` directory
2. **Spring Security**: Make sure `spring-security-crypto-5.7.2.jar` is in the `lib` directory
3. **Apache Commons Logging**: Make sure `commons-logging-1.2.jar` is in the `lib` directory (required by Spring Security)
4. **Logging Libraries**: Make sure `slf4j-api-1.7.36.jar`, `logback-classic-1.2.12.jar`, and `logback-core-1.2.12.jar` are in the `lib` directory

If any libraries are missing, you can download them from Maven Central Repository:

```bash
# MySQL Connector
wget https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.23/mysql-connector-java-8.0.23.jar -P lib/

# Spring Security Crypto
wget https://repo1.maven.org/maven2/org/springframework/security/spring-security-crypto/5.7.2/spring-security-crypto-5.7.2.jar -P lib/

# Apache Commons Logging
wget https://repo1.maven.org/maven2/commons-logging/commons-logging/1.2/commons-logging-1.2.jar -P lib/

# SLF4J API
wget https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar -P lib/

# Logback Classic
wget https://repo1.maven.org/maven2/ch/qos/logback/logback-classic/1.2.12/logback-classic-1.2.12.jar -P lib/

# Logback Core
wget https://repo1.maven.org/maven2/ch/qos/logback/logback-core/1.2.12/logback-core-1.2.12.jar -P lib/
```

After downloading the missing libraries, rebuild the application with `ant clean jar`.

### Configuration
Update the database connection settings in `src/main/resources/application.properties`:
```properties
# Database Connection Settings
spring.datasource.url=jdbc:mysql://localhost:3306/notizprojekt?useSSL=false&serverTimezone=UTC
spring.datasource.username=notizuser
spring.datasource.password=notizpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

Alternatively, you can modify the connection settings in `DatabaseConfig.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/your_database";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";
```

## Usage Guide

### Getting Started
1. **Launch the Application**: Run the main class or use the Maven exec command
2. **Login/Register**: Use existing web application credentials or create a new account
3. **Main Interface**: Access the note board with header navigation

### Creating Notes
1. Click the floating "+" button (bottom-right corner)
2. Fill in note details:
   - Title (required)
   - Content (plain text or rich text)
   - Tag for organization
   - Note type (text, code, image, checklist)
   - Privacy level (private, shared, public)
   - Color selection
3. Save the note

### Managing Notes
- **Edit**: Click the edit button (✏) on any note you own
- **Delete**: Click the delete button (🗑) with confirmation
- **Move**: Drag notes around the board to reposition them
- **View**: Click the view button (👁) on shared notes

### Search and Navigation
- Use the search bar in the header to find notes
- Search works across titles, content, and tags
- Clear search to show all accessible notes

### User Profile
- Click "Profile" in the header to manage your account
- Update display name, email, and password
- Changes sync with the web application

### Theme Switching
- Click the theme toggle button (🌙/☀) in the header
- Switch between light and dark modes
- Theme preference is saved locally

## Feature Overview

| Feature | Status | Description |
|---------|--------|-------------|
| User Authentication | ✅ | Secure login and registration |
| Note CRUD Operations | ✅ | Create, read, update, delete notes |
| Drag & Drop Notes | ✅ | Interactive note positioning |
| Rich Text Editing | ✅ | HTML and plain text support |
| Search Functionality | ✅ | Search across titles, content, tags |
| Theme Support | ✅ | Light and dark modes |
| Note Sharing | ✅ | Share notes with other users |
| Privacy Levels | ✅ | Private, shared, and public notes |
| Profile Management | ✅ | User profile and password management |
| Cross-Platform | ✅ | Runs on Windows, macOS, Linux |

## Development Notes

### Design Principles
- **Native Feel**: Uses platform-appropriate UI patterns and behaviors
- **Performance**: Efficient database operations with connection pooling
- **Maintainability**: Clean separation of concerns with MVC architecture
- **User Experience**: Intuitive interface with modern design elements

### Key Implementation Details
- **Theme System**: Centralized theme management with observer pattern
- **Database Layer**: Robust MySQL integration with connection pooling
- **UI Components**: Custom-styled Swing components with modern design
- **Error Handling**: Comprehensive error handling with user-friendly messages
- **Threading**: Background operations for database calls to maintain UI responsiveness

### Future Enhancements
- **Offline Mode**: Local caching for offline note access
- **File Attachments**: Support for file uploads and attachments
- **Export Features**: Export notes to various formats (PDF, HTML, etc.)
- **Keyboard Shortcuts**: Enhanced keyboard navigation and shortcuts
- **Plugin System**: Extensible architecture for custom note types

## Troubleshooting

### Common Issues
1. **Database Connection**: Verify MySQL is running and credentials are correct
2. **Java Version**: Ensure Java 17+ is installed and configured
3. **Theme Issues**: Restart application if theme switching doesn't work properly
4. **Note Positioning**: If drag-and-drop isn't working, check mouse event handling
5. **Ant Build Errors**: Make sure all required libraries are in the `lib` directory
6. **NetBeans Integration**: Ensure NetBeans can find the Ant installation

### Performance Tips
- **Large Datasets**: Application handles hundreds of notes efficiently
- **Memory Usage**: Restart application if memory usage becomes high
- **Database Performance**: Ensure proper database indexing for search operations
- **Build Performance**: Use `ant clean` before rebuilding if you encounter strange build issues

### Stack Overflow Fix
The project previously had a stack overflow issue in `DatabaseConfig.java` where the `testDatabaseConnection()` method was calling itself recursively. This has been fixed by modifying the method to use `createDirectConnection()` instead of calling itself.

### Package Structure Simplification
The project has been restructured from a multi-package hierarchy to a single package structure:

- **Original Structure**: Used nested packages (`notizdesktop.config`, `notizdesktop.model`, etc.)
- **New Structure**: All classes are in a single `notizapp` package
- **Benefits**: 
  - Easier to navigate in NetBeans
  - Simplified import statements
  - Reduced complexity for finding the main class
  - Eliminated package-related stack overflow issues

## Contributing

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Maintain consistent indentation (4 spaces)

### Testing
- Test all CRUD operations
- Verify theme switching functionality
- Test drag-and-drop behavior
- Validate form inputs and error handling

### Pull Requests
- Create feature branches from `GUI-HTML-Ant`
- Include comprehensive testing
- Update documentation as needed
- Follow existing code patterns
- Ensure Ant build files are updated if necessary

## License

This project is part of the ITS-Projekt coursework. Please refer to the main project license for usage terms.

## Support

For issues, questions, or contributions, please refer to the main project repository or contact the development team.

## NetBeans Integration

### Single Package Structure
The application has been restructured to use a single package structure for easier navigation and execution in NetBeans:

- **All classes are in one package**: `notizapp`
- **No sub-packages**: Eliminates confusion when looking for the main class
- **Simplified imports**: All classes reference each other directly within the same package
- **Easier to run**: Main class is immediately visible in the package explorer

### Opening in NetBeans
1. Start NetBeans IDE
2. Select File > Open Project
3. Navigate to the project directory and select it
4. The project should open with all the Ant targets available

### Running in NetBeans
1. Right-click on the project in the Projects panel
2. Select "Run" or press F6
3. Alternatively, select specific Ant targets from the Navigator panel

#### Running the Main Class Directly
1. Navigate to `src/main/java/notizapp/NotizDesktopApplication.java` in the Projects panel
2. Right-click on the file and select "Run File" (Shift+F6)

### Debugging in NetBeans
1. Set breakpoints in your code
2. Right-click on the project and select "Debug" or press Ctrl+F5
3. Use the NetBeans debugger to step through code and inspect variables

### Troubleshooting NetBeans Execution
If you have trouble running the application in NetBeans:

1. Verify the main class is set correctly:
   - Right-click on the project and select "Properties"
   - Go to "Run" category
   - Ensure "Main Class" is set to `notizapp.NotizDesktopApplication`

2. Check build configuration:
   - Make sure all libraries in the `lib` directory are properly referenced
   - Verify the project.properties file has the correct main.class setting

3. Clean and rebuild:
   - Right-click on the project and select "Clean and Build"
   - Then try running again
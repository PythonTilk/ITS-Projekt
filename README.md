# Notiz Desktop Application

A Java Swing desktop application for note-taking with drag-and-drop functionality, rich text editing, and comprehensive note management features. Built with Apache Ant and compatible with NetBeans IDE.

## 🚀 Quick Download

**Latest Release**: [Download from GitHub Releases](https://github.com/PythonTilk/ITS-Projekt/releases/latest)

- **`NotizDesktop-vX.X.X.tar.gz`** - Complete package for Linux/macOS
- **`NotizDesktop-vX.X.X.zip`** - Complete package for Windows  
- **`NotizDesktop-with-dependencies.jar`** - Standalone JAR (just run with `java -jar`)

## Key Features

- **User Authentication**: Secure login and registration system
- **Note Management**: Create, edit, delete, and organize notes with drag & drop
- **Rich Text Support**: Both plain text and HTML rich text editing
- **Theme Support**: Light and dark mode themes
- **Note Sharing**: Share notes with other users with privacy controls
- **Search Functionality**: Find notes by title, content, or tags

## Recent Updates

- **Simplified Class Structure**: Reduced number of classes for better maintainability
- **Improved Theme Switching**: Fixed theme toggle and UI improvements across all dialogs
- **Automated Release Workflow**: GitHub Actions for automated building and releasing
- **Enhanced UI**: Improved button hover effects and consistent styling
- **Fixed Stack Overflow Issue**: Resolved recursive call in database connection testing

## Prerequisites

- Java 17 or higher
- MySQL database server
- Apache Ant 1.10.0+ (for building from source)

## Quick Start

### Database Setup
```bash
# Run the SQL script to create the database and tables
mysql -u root -p < its-projekt18.6.sql
```

### Running the Application
```bash
# Build and run with Ant
ant compile
ant run

# Or create and run executable JAR
ant fatjar
java -jar dist/NotizDesktop-with-dependencies.jar
```

See [DATABASE_SETUP.md](DATABASE_SETUP.md) for detailed database configuration.

## Project Structure

The application uses a simplified structure with all classes in a single `notizapp` package:

- **`NotizDesktopApplication.java`** - Main entry point
- **`AuthFrame.java`** - Combined login and registration
- **`MainFrame.java`** - Main application window
- **`Note.java`** - Combined Note model and service
- **`User.java`** - Combined User model and service
- **`NoteBoardPanel.java`** - Note board with drag-and-drop
- **`NoteDialog.java`** - Combined note editing/viewing
- **`ProfileDialog.java`** - User profile management
- **`ThemeManager.java`** - Theme management system

## Automated Releases

This project uses GitHub Actions for automated building and releasing:

- **Automatic releases** when version tags are pushed
- **Pre-releases** for testing new features
- **Complete packages** with dependencies and documentation

To create a release:
```bash
git tag v1.0.0
git push origin v1.0.0
```

See [Release Workflow Guide](.github/RELEASE_WORKFLOW.md) for details.

## NetBeans Integration

1. **Open Project**: File > Open Project > Select project directory
2. **Run**: Right-click project > Run (F6)
3. **Debug**: Right-click project > Debug (Ctrl+F5)
4. **Run Main Class**: Navigate to `NotizDesktopApplication.java` > Right-click > Run File

## Troubleshooting

- **Database Connection**: Verify MySQL is running and credentials are correct
- **Missing Libraries**: Check that all required JARs are in the `lib` directory
- **Theme Issues**: Restart application if theme switching doesn't work properly
- **Build Issues**: Use `ant clean` before rebuilding

## Contributing

- Create feature branches from `main`
- Follow Java naming conventions
- Include comprehensive testing
- Update documentation as needed
- Ensure Ant build files are updated if necessary

## License

This project is part of the ITS-Projekt coursework. Please refer to the LICENSE file for usage terms.en try running again
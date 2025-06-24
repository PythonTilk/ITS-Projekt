# Notiz Projekt - Desktop Application

A Java Swing desktop application that provides the same functionality as the web-based note-taking application. This desktop version offers a native experience with drag-and-drop note management, rich text editing, and seamless synchronization with the web application through a shared database.

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
- **Maven**: Build and dependency management

### Project Structure
```
src/main/java/notizprojekt/desktop/
├── NotizDesktopApplication.java    # Main application entry point
├── config/
│   └── DatabaseConfig.java        # Database connection configuration
├── model/
│   ├── DesktopNote.java           # Note data model
│   └── DesktopUser.java           # User data model
├── service/
│   ├── NoteService.java           # Note business logic
│   └── UserService.java           # User business logic
├── ui/
│   ├── LoginFrame.java            # Login window
│   ├── RegisterDialog.java        # User registration dialog
│   ├── MainFrame.java             # Main application window
│   ├── NoteBoardPanel.java        # Note board with drag-and-drop
│   ├── NoteEditDialog.java        # Note creation/editing dialog
│   ├── NoteViewDialog.java        # Read-only note viewer
│   └── ProfileDialog.java         # User profile management
└── util/
    └── ThemeManager.java          # Theme management system
```

## Installation and Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL database (same as web application)
- Access to the existing database schema

### Database Setup
The desktop application uses the same database as the web application. Ensure the following tables exist:
- `nutzer` (users)
- `notiz` (notes)

### Building the Application
```bash
# Clone the repository
git clone <repository-url>
cd ITS-Projekt

# Switch to the desktop branch
git checkout GUI-HTML

# Build the application
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="notizprojekt.desktop.NotizDesktopApplication"
```

### Configuration
Update the database connection settings in `DatabaseConfig.java`:
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

## Features Comparison with Web Application

| Feature | Web App | Desktop App | Status |
|---------|---------|-------------|--------|
| User Authentication | ✅ | ✅ | Complete |
| Note CRUD Operations | ✅ | ✅ | Complete |
| Drag & Drop Notes | ✅ | ✅ | Complete |
| Rich Text Editing | ✅ | ✅ | Complete |
| Search Functionality | ✅ | ✅ | Complete |
| Theme Support | ✅ | ✅ | Complete |
| Note Sharing | ✅ | ✅ | Complete |
| Privacy Levels | ✅ | ✅ | Complete |
| Profile Management | ✅ | ✅ | Complete |
| Responsive Design | ✅ | N/A | Desktop Native |
| Cross-Platform | Browser | Java | Both Supported |

## Development Notes

### Design Principles
- **Consistency**: Maintains visual and functional consistency with the web application
- **Native Feel**: Uses platform-appropriate UI patterns and behaviors
- **Performance**: Efficient database operations with connection pooling
- **Maintainability**: Clean separation of concerns with MVC architecture

### Key Implementation Details
- **Theme System**: Centralized theme management with observer pattern
- **Database Layer**: Shared schema with web application for seamless data sync
- **UI Components**: Custom-styled Swing components matching web design
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

### Performance Tips
- **Large Datasets**: Application handles hundreds of notes efficiently
- **Memory Usage**: Restart application if memory usage becomes high
- **Database Performance**: Ensure proper database indexing for search operations

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
- Create feature branches from `GUI-HTML`
- Include comprehensive testing
- Update documentation as needed
- Follow existing code patterns

## License

This project is part of the ITS-Projekt coursework. Please refer to the main project license for usage terms.

## Support

For issues, questions, or contributions, please refer to the main project repository or contact the development team.
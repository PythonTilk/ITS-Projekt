# Desktop Application Technical Documentation

## Overview

The Notiz Projekt Desktop Application is a Java Swing-based desktop client that provides full feature parity with the web application. It uses the same MySQL database backend, ensuring seamless data synchronization between web and desktop clients.

## Architecture

### Application Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────────┐ │
│  │ LoginFrame  │ │ MainFrame   │ │ Dialogs (Edit/View/etc) │ │
│  └─────────────┘ └─────────────┘ └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                    Business Layer                           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────────┐ │
│  │ UserService │ │ NoteService │ │ ThemeManager            │ │
│  └─────────────┘ └─────────────┘ └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                    Data Layer                               │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────────┐ │
│  │ DesktopUser │ │ DesktopNote │ │ DatabaseConfig          │ │
│  └─────────────┘ └─────────────┘ └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                    Database Layer                           │
│                    MySQL Database                           │
│              (Shared with Web Application)                  │
└─────────────────────────────────────────────────────────────┘
```

### Design Patterns Used

1. **Model-View-Controller (MVC)**: Clear separation between data models, UI components, and business logic
2. **Observer Pattern**: Theme change notifications across UI components
3. **Factory Pattern**: Styled component creation
4. **Singleton Pattern**: Database configuration and theme manager
5. **Repository Pattern**: Data access abstraction in service classes

## Core Components

### 1. NotizDesktopApplication.java
**Purpose**: Main application entry point and initialization

**Key Responsibilities**:
- Application startup and shutdown
- Look and feel configuration
- Initial window display
- System tray integration (future enhancement)

### 2. DatabaseConfig.java
**Purpose**: Centralized database connection management

**Key Features**:
- Connection pooling for performance
- Shared configuration with web application
- Error handling and retry logic
- Connection validation

### 3. ThemeManager.java
**Purpose**: Centralized theme management system

**Features**:
- Light and dark theme support
- Observer pattern for theme change notifications
- Consistent color palette across application
- Theme persistence (future enhancement)

## Data Models

### DesktopUser.java
**Purpose**: User entity representation with methods for avatar generation and display name fallback

### DesktopNote.java
**Purpose**: Note entity representation with support for multiple note types, privacy levels, and collaborative editing

## Service Layer

### UserService.java
**Purpose**: User-related business logic including authentication, registration, and profile management

### NoteService.java
**Purpose**: Note-related business logic including CRUD operations, search, and permission management

## User Interface Components

### LoginFrame.java
**Purpose**: User authentication interface with theme support

### MainFrame.java
**Purpose**: Main application window with header navigation and note board

### NoteBoardPanel.java
**Purpose**: Interactive note board with drag-and-drop functionality

### Dialog Components
- **NoteEditDialog.java**: Note creation and editing
- **NoteViewDialog.java**: Read-only note display
- **ProfileDialog.java**: User profile management
- **RegisterDialog.java**: New user registration

## Database Integration

The desktop application uses the same database schema as the web application, ensuring seamless data synchronization.

## Security Considerations

- BCrypt password hashing
- SQL injection prevention
- Input validation and sanitization
- Role-based access control

## Performance Optimization

- Connection pooling
- Background threading for database operations
- Efficient UI repainting
- Memory management

## Future Enhancements

- Offline mode with local caching
- File attachments support
- Export functionality
- Keyboard shortcuts
- Plugin architecture
- Real-time collaboration

This documentation provides a comprehensive technical overview of the desktop application architecture and implementation.
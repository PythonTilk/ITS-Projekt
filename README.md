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

### Project Structure

- `src/NotizProjekt_All/` - Contains all Java source files
- `build.xml` - Ant build file for compiling the project
- `nbproject/` - NetBeans project configuration

### Requirements

- Java Development Kit (JDK) 8 or higher
- MySQL Server 5.7 or higher
- NetBeans IDE (recommended for development)

## Getting Started

1. Clone this repository to your local machine
2. Import the database schema from [`its-projekt18.6.sql`](its-projekt18.6.sql)
3. Open the project in NetBeans or your preferred Java IDE
4. Configure the database connection in the application
5. Build and run the project

## Usage

After starting the application:

1. Register a new account or log in with existing credentials
2. Use the main interface to create and manage your notes
3. Access the sharing options to collaborate with other users

## License

This project is licensed under the terms included in the [LICENSE](LICENSE) file.
# ITS-Projekt

## Overview

ITS-Projekt is a Java-based note-taking application that allows users to create, edit, and share notes. The application uses a MySQL database to store user information and notes.

## Features

- User authentication system
- Create and edit personal notes
- Share notes with other users
- Organize notes with tags

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

- Java Development Kit (JDK)
- MySQL Server
- NetBeans IDE (recommended for development)

## Getting Started

1. Import the database schema from [`its-projekt18.6.sql`](its-projekt18.6.sql)
2. Open the project in NetBeans or your preferred Java IDE
3. Configure the database connection in the application
4. Build and run the project

## License

This project is licensed under the terms included in the [LICENSE](LICENSE) file.
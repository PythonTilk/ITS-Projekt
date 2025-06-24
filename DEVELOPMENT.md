# Development Guide - ITS-Projekt

Complete guide for setting up and developing the ITS-Projekt note management system.

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Requirements](#requirements)
- [Local Development Setup](#local-development-setup)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [Development Workflow](#development-workflow)
- [Testing](#testing)
- [API Documentation](#api-documentation)
- [Troubleshooting](#troubleshooting)

## Project Overview

ITS-Projekt is a comprehensive note-taking application with two versions:

1. **Web Application** (html branch) - Modern Spring Boot web app with enhanced features ⭐ **Recommended**
2. **Desktop Application** (main branch) - Java Swing desktop application

This guide focuses on the **web application** which includes:

- User authentication and profiles with custom avatars
- Rich text editing with multiple note types (text, rich text, code)
- Note sharing and privacy controls (private, shared, public)
- Drag-and-drop interface with spatial positioning
- Image upload and embedding
- Dark mode toggle
- Search and tagging system
- Mobile-responsive design

## Requirements

### System Requirements
- **Java:** JDK 17 or higher
- **Database:** MySQL 5.7+ or MariaDB 10.3+
- **Build Tool:** Maven 3.6+ (included via wrapper)
- **Browser:** Modern web browser (Chrome, Firefox, Safari, Edge)

### Development Tools (Optional)
- **IDE:** IntelliJ IDEA, Eclipse, or VS Code
- **Database Client:** MySQL Workbench, phpMyAdmin, or DBeaver
- **Git:** For version control

## Local Development Setup

### 1. Clone the Repository

```bash
# Clone the repository
git clone https://github.com/PythonTilk/ITS-Projekt.git
cd ITS-Projekt

# Switch to the web application branch
git checkout html
```

### 2. Install Dependencies

#### Ubuntu/Debian:
```bash
# Update package list
sudo apt update

# Install Java 17
sudo apt install openjdk-17-jdk

# Install MariaDB
sudo apt install mariadb-server

# Start MariaDB service
sudo systemctl start mariadb
sudo systemctl enable mariadb
```

#### macOS:
```bash
# Install Homebrew if not already installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Java 17
brew install openjdk@17

# Install MariaDB
brew install mariadb
brew services start mariadb
```

#### Windows:
1. Download and install [OpenJDK 17](https://adoptium.net/)
2. Download and install [MariaDB](https://mariadb.org/download/)
3. Start MariaDB service from Services panel

### 3. Verify Installation

```bash
# Check Java version
java -version
# Should show: openjdk version "17.x.x"

# Check Maven (using wrapper)
./mvnw --version
# Should show: Apache Maven 3.6.3+

# Check MariaDB
mysql --version
# Should show: mysql Ver 15.1 Distrib 10.x.x-MariaDB
```

## Database Setup

### Option 1: Local MariaDB/MySQL

```bash
# Start MariaDB service (if not already running)
sudo systemctl start mariadb

# Import the database schema
mysql -u root -p < its-projekt18.6.sql

# Verify the setup
mysql -u notizuser -pnotizpassword -e "SHOW TABLES;" notizprojekt
```

### Option 2: Docker Setup

```bash
# Run MariaDB in Docker
docker run --name mariadb-notiz \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=notizprojekt \
  -e MYSQL_USER=notizuser \
  -e MYSQL_PASSWORD=notizpassword \
  -p 3306:3306 -d mariadb:10.6

# Wait for container to start
sleep 10

# Import schema
docker exec -i mariadb-notiz mysql -u root -prootpassword < its-projekt18.6.sql

# Verify setup
docker exec mariadb-notiz mysql -u notizuser -pnotizpassword -e "SHOW TABLES;" notizprojekt
```

### Database Schema Overview

The application uses these main tables:

- **`nutzer`** - User accounts with profiles, authentication
- **`notiz`** - Notes with content, positioning, colors, privacy settings
- **`geteilte_notizen`** - Shared notes management
- **`note_shares`** - Note sharing permissions

**Default Connection:**
- Database: `notizprojekt`
- User: `notizuser`
- Password: `notizpassword`
- Port: `3306`

## Running the Application

### Development Mode

```bash
# Run with Maven wrapper (recommended)
./mvnw spring-boot:run

# Or if you have Maven installed globally
mvn spring-boot:run
```

The application will start on `http://localhost:12000`

### Production Build

```bash
# Build JAR file
./mvnw clean package

# Run the JAR
java -jar target/notizprojekt-web-0.0.1-SNAPSHOT.jar
```

### Configuration

Application settings are in `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/notizprojekt
spring.datasource.username=notizuser
spring.datasource.password=notizpassword

# Server Configuration
server.port=12000
server.address=0.0.0.0

# File Upload Configuration
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=25MB

# Development Settings
spring.thymeleaf.cache=false
logging.level.notizprojekt=DEBUG
```

## Project Structure

```
ITS-Projekt/
├── src/main/java/notizprojekt/web/
│   ├── NotizprojektWebApplication.java    # Main application class
│   ├── config/                            # Configuration classes
│   │   ├── SecurityConfig.java           # Spring Security setup
│   │   ├── DatabaseConfig.java           # Database configuration
│   │   └── CorsFilter.java               # CORS configuration
│   ├── controller/                        # Web controllers
│   │   ├── WebController.java            # Main web endpoints
│   │   └── api/                          # REST API controllers
│   ├── model/                            # Entity classes
│   │   ├── User.java                     # User entity
│   │   └── Note.java                     # Note entity
│   ├── repository/                       # Data access layer
│   │   ├── UserRepository.java           # User data access
│   │   └── NoteRepository.java           # Note data access
│   ├── service/                          # Business logic
│   │   ├── UserService.java              # User management
│   │   └── NoteService.java              # Note management
│   └── dto/                              # Data transfer objects
│       └── NoteDTO.java                  # Note data transfer
├── src/main/resources/
│   ├── application.properties            # Application configuration
│   ├── static/                          # Static web assets
│   │   ├── css/                         # Stylesheets
│   │   ├── js/                          # JavaScript files
│   │   └── sounds/                      # Audio files
│   └── templates/                       # Thymeleaf templates
│       ├── login.html                   # Login page
│       ├── register.html                # Registration page
│       ├── board.html                   # Main notes board
│       └── profile.html                 # User profile page
├── uploads/                             # File upload directory
├── pom.xml                             # Maven configuration
└── its-projekt18.6.sql                 # Database schema
```

## Development Workflow

### 1. Making Changes

```bash
# Create a feature branch
git checkout -b feature/your-feature-name

# Make your changes
# The application will auto-reload thanks to Spring Boot DevTools

# Test your changes
./mvnw test
```

### 2. Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add comments for complex business logic
- Keep methods focused and small

### 3. Database Changes

If you modify the database schema:

1. Update `its-projekt18.6.sql`
2. Test with a fresh database import
3. Update entity classes if needed
4. Run tests to ensure compatibility

### 4. Frontend Changes

- CSS files are in `src/main/resources/static/css/`
- JavaScript files are in `src/main/resources/static/js/`
- Templates use Thymeleaf syntax in `src/main/resources/templates/`
- Changes are auto-reloaded in development mode

## Testing

### Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=NotizprojektWebApplicationTests

# Run tests with coverage
./mvnw test jacoco:report
```

### Manual Testing

1. **Start the application:** `./mvnw spring-boot:run`
2. **Access:** `http://localhost:12000`
3. **Test accounts:**
   - Username: `testuser123`, Password: `password123`
   - Username: `admin`, Password: `admin123`

### Test Scenarios

- [ ] User registration and login
- [ ] Creating different note types (text, rich text, code)
- [ ] Note sharing and privacy settings
- [ ] Image upload functionality
- [ ] Profile management
- [ ] Search and filtering
- [ ] Dark mode toggle
- [ ] Mobile responsiveness

## API Documentation

### Authentication Endpoints

```http
POST /login
POST /register
POST /logout
```

### Note Management

```http
GET    /api/notes          # Get user's notes
POST   /api/notes          # Create new note
PUT    /api/notes/{id}     # Update note
DELETE /api/notes/{id}     # Delete note
POST   /api/notes/{id}/share # Share note
```

### User Management

```http
GET    /api/users/{id}     # Get user profile
PUT    /api/users/{id}     # Update profile
POST   /api/users/{id}/avatar # Upload avatar
```

### File Upload

```http
POST   /api/upload         # Upload image
```

## Troubleshooting

### Common Issues

#### 1. Application Won't Start

**Error:** `Port 12000 already in use`
```bash
# Find and kill process using port 12000
sudo lsof -t -i:12000 | xargs kill -9

# Or change port in application.properties
server.port=12001
```

**Error:** `Database connection failed`
```bash
# Check if MariaDB is running
sudo systemctl status mariadb

# Test connection manually
mysql -u notizuser -pnotizpassword -h localhost notizprojekt

# Restart MariaDB if needed
sudo systemctl restart mariadb
```

#### 2. Build Issues

**Error:** `Java version mismatch`
```bash
# Check Java version
java -version

# Set JAVA_HOME if needed
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

**Error:** `Maven dependencies not downloading`
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Rebuild
./mvnw clean install
```

#### 3. Database Issues

**Error:** `User 'notizuser' doesn't exist`
```sql
-- Connect as root and create user
mysql -u root -p

CREATE USER 'notizuser'@'localhost' IDENTIFIED BY 'notizpassword';
GRANT ALL PRIVILEGES ON notizprojekt.* TO 'notizuser'@'localhost';
FLUSH PRIVILEGES;
```

**Error:** `Table doesn't exist`
```bash
# Re-import the complete schema
mysql -u root -p < its-projekt18.6.sql
```

#### 4. Frontend Issues

**Problem:** Changes not reflecting
- Clear browser cache (Ctrl+F5 or Cmd+Shift+R)
- Check browser console for JavaScript errors (F12)
- Ensure `spring.thymeleaf.cache=false` in development

**Problem:** File uploads not working
- Check `uploads/` directory exists and is writable
- Verify file size limits in `application.properties`
- Check disk space

### Getting Help

1. **Check Logs:** Look for error messages in the console output
2. **Database Connection:** Test database connectivity independently
3. **Browser Console:** Check for JavaScript errors (F12 → Console)
4. **Port Conflicts:** Ensure port 12000 is available
5. **File Permissions:** Check that the application can write to `uploads/`

### Health Check Commands

```bash
# Test database connection
mysql -u notizuser -pnotizpassword -e "SELECT COUNT(*) FROM notizprojekt.nutzer;"

# Test application endpoint
curl -I http://localhost:12000/login

# Check application logs
tail -f logs/application.log  # if logging to file

# Check system resources
free -h  # Memory usage
df -h    # Disk usage
```

### Development Tips

1. **Use DevTools:** Spring Boot DevTools enables automatic restart on code changes
2. **Database GUI:** Use a database client like DBeaver for easier database management
3. **Browser DevTools:** Use F12 to debug frontend issues
4. **Logging:** Increase logging level for debugging: `logging.level.notizprojekt=DEBUG`
5. **Profile Separation:** Use different application properties for development and production

---

**Need more help?** Check the [main README](README.md) or review the [deployment guide](DEPLOYMENT_GUIDE.md) for production setup.
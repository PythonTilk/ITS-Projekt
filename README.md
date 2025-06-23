# ITS-Projekt - Advanced Note Management System

A comprehensive, modern note-taking application with user profiles, collaborative features, and production-ready deployment tools.

## 🚀 Quick Production Setup

Deploy the complete application to your server with a single command:

```bash
curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/install.sh | sudo bash
```

**Automated Setup (No Prompts):**
```bash
DOMAIN="notes.example.com" \
EMAIL="admin@example.com" \
DB_PASSWORD="your-secure-password" \
SKIP_PROMPTS="true" \
curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/install.sh | sudo bash
```

📖 **[Complete Setup Guide](server-setup.md)** | 🚀 **[Quick Deployment Guide](DEPLOYMENT_GUIDE.md)**

## Overview

ITS-Projekt is a comprehensive note-taking application that allows users to create, edit, organize, and share notes with advanced features. The application uses a MySQL/MariaDB database and is available in two versions:

1. **Desktop Application**: A Java Swing-based desktop application (main branch)
2. **Web Application**: A modern Spring Boot-based web application with enhanced features (html branch)

## ✨ Enhanced Features

### 👤 **User Profile System**
- **Custom Profile Pictures**: Upload and manage profile pictures with drag-and-drop support
- **Display Names**: Set custom display names separate from usernames
- **User Biographies**: Add personal descriptions and information
- **Profile Pages**: Dedicated profile pages for each user
- **Author Links**: Click on author names in shared notes to view their profiles

### 🎨 **Visual & Interface Enhancements**
- **Dark Mode Toggle**: Switch between light and dark themes for comfortable viewing
- **Drag-and-Drop Interface**: Intuitive spatial organization of notes on a virtual board
- **Color-Coded Notes**: Assign colors to notes for visual categorization
- **Responsive Design**: Optimized for desktop, tablet, and mobile devices
- **Modern UI**: Clean, intuitive interface with smooth animations

### 📝 **Advanced Note Editing**
- **Rich Text Editor**: Full WYSIWYG editor with formatting options (bold, italic, underline, colors)
- **Code Editor**: Syntax-highlighted code editor with multiple language support
- **Note Types**: Support for text, rich text, and code notes
- **Auto-Save**: Automatic saving of changes as you type
- **Image Upload**: Attach and embed images directly in notes

### 🔒 **Privacy & Sharing**
- **Privacy Levels**: Private, shared, or public note visibility
- **User-Specific Sharing**: Share notes with specific users
- **Author Information**: Notes display author names with profile links
- **Secure Authentication**: BCrypt password encryption
- **Session Management**: Secure user sessions with proper logout

### 🏷️ **Organization & Search**
- **Tagging System**: Organize notes with custom tags
- **Advanced Search**: Search by title, content, tags, or note type
- **User Search**: Find users by username or display name
- **Spatial Positioning**: Remember note positions on the board
- **Note Categories**: Organize by type (text, code, rich text)

### 🚀 **Production Ready**
- **Automated Installation**: One-command server setup with SSL
- **Update Management**: Automated update system with rollback capability
- **Health Monitoring**: Built-in health checks and logging
- **Backup System**: Automated database and application backups
- **Security Hardening**: Production security configurations

## Technical Details

### Database Setup

The project uses a MySQL/MariaDB database with enhanced schema. The [`its-projekt18.6.sql`](its-projekt18.6.sql) file includes:

#### 📊 **Database Tables**
- **`nutzer`**: Enhanced user table with email, timestamps, and security features
- **`notiz`**: Enhanced notes table with positioning, colors, privacy levels, and image support
- **`geteilte_notizen`**: Shared notes management

#### 🚀 **Quick Setup**
The SQL file automatically creates:
- Database (`notizprojekt`)
- Application user (`notizuser` with password `notizpassword`)
- All required tables with proper constraints
- Sample data for testing

#### 🐳 **Database Options**

**Option 1: Local MySQL/MariaDB**
```bash
# Install MariaDB (Ubuntu/Debian)
sudo apt update && sudo apt install mariadb-server

# Start MariaDB
sudo systemctl start mariadb

# Import the database
mysql -u root -p < its-projekt18.6.sql
```

**Option 2: Docker Setup**
```bash
# Run MariaDB in Docker
docker run --name mariadb \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=notizprojekt \
  -e MYSQL_USER=notizuser \
  -e MYSQL_PASSWORD=notizpassword \
  -p 3306:3306 -d mariadb:10.6

# Import schema (after container is running)
docker exec -i mariadb mysql -u root -prootpassword < its-projekt18.6.sql
```

#### 🔗 **Connection Details**
```properties
# Application connection (already configured)
spring.datasource.url=jdbc:mysql://localhost:3306/notizprojekt
spring.datasource.username=notizuser
spring.datasource.password=notizpassword
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

### 🛠️ Requirements

#### Desktop Application (main branch)
- **Java**: JDK 8 or higher
- **Database**: MySQL/MariaDB 5.7+ or Docker
- **IDE**: NetBeans (recommended) or any Java IDE
- **Dependencies**: JDBC MySQL Connector (included)

#### Web Application (html branch) - **Enhanced Version**
- **Java**: JDK 11 or higher
- **Database**: MySQL/MariaDB 5.7+ or Docker
- **Build Tool**: Maven 3.6+
- **Framework**: Spring Boot 2.7.0
- **Browser**: Modern web browser (Chrome, Firefox, Safari, Edge)
- **Features**: File upload support, responsive design

## 🚀 Getting Started

### 🌟 **Production Deployment (Recommended)**

For production servers, use the automated installation:

```bash
# Interactive installation
curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/install.sh | sudo bash

# Or automated installation
DOMAIN="your-domain.com" EMAIL="admin@example.com" DB_PASSWORD="secure-password" \
curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/install.sh | sudo bash
```

This will automatically:
- Install all dependencies (Java, Maven, MySQL, Nginx)
- Configure SSL certificates
- Set up the application as a system service
- Configure firewall and security settings

### 💻 **Development Setup**

#### 1️⃣ **Clone and Setup Database**
```bash
# Clone the repository
git clone https://github.com/PythonTilk/ITS-Projekt.git
cd ITS-Projekt

# Switch to the enhanced web version
git checkout html

# Setup database (choose one option)
# Option A: Local MariaDB
sudo apt install mariadb-server
sudo systemctl start mariadb
mysql -u root -p < its-projekt18.6.sql

# Option B: Docker
docker run --name mariadb -e MYSQL_ROOT_PASSWORD=rootpassword -p 3306:3306 -d mariadb:10.6
sleep 10
docker exec -i mariadb mysql -u root -prootpassword < its-projekt18.6.sql
```

#### 2️⃣ **Run the Application**
```bash
# Build and run (Maven wrapper included)
./mvnw spring-boot:run

# Or if you have Maven installed
mvn spring-boot:run
```

#### 3️⃣ **Access the Application**
- **URL**: `http://localhost:12000`
- **Create Account**: Register a new user or use test account
- **Test Account**: 
  - Username: `testuser123`
  - Password: `password123`

### 🖥️ **Desktop Application (Classic Version)**

#### 1️⃣ **Setup**
```bash
# Clone and switch to main branch
git clone https://github.com/PythonTilk/ITS-Projekt.git
cd ITS-Projekt
git checkout main

# Setup database (same as above)
mysql -u root -p < its-projekt18.6.sql
```

#### 2️⃣ **Run**
1. Open in NetBeans or your preferred Java IDE
2. Build and run the project
3. Use test account: `root` / `420`

### ✅ **Verification**
After setup, you should see:
- ✅ Database connection successful
- ✅ Sample notes loaded
- ✅ User authentication working
- ✅ All enhanced features available (web version)

## 📖 Usage Guide

### 🌟 **Enhanced Web Application Features**

#### 🔐 **Authentication**
1. **Login**: Use existing account or create a new one
2. **Registration**: Create new accounts with email validation
3. **Security**: Passwords are encrypted with BCrypt

#### 👤 **User Profile Management**
1. **Profile Setup**: Click "Profile" in the user menu to access your profile
2. **Profile Picture**: Upload custom profile pictures with drag-and-drop support
3. **Display Name**: Set a custom display name different from your username
4. **Biography**: Add a personal description or bio
5. **View Profiles**: Click on author names in shared notes to view their profiles

#### 📝 **Creating and Managing Notes**
1. **Add Note**: Click the "+" button to create a new note
2. **Note Types**:
   - **📄 Text**: Simple text notes with basic formatting
   - **🎨 Rich Text**: Full WYSIWYG editor with formatting toolbar
   - **💻 Code**: Syntax-highlighted code editor with language support
3. **Auto-Save**: Changes are saved automatically as you type
4. **Author Information**: Notes display author names with clickable profile links

#### 🎨 **Visual Organization**
1. **Drag & Drop**: Move notes around the board to organize spatially
2. **Color Coding**: Right-click notes to change colors
3. **Dark Mode**: Toggle between light and dark themes
4. **Responsive**: Works on desktop, tablet, and mobile

#### 🏷️ **Advanced Features**
1. **Tags**: Add tags for categorization and search
2. **Privacy Levels**: Set notes as private, shared, or public
3. **Image Upload**: Attach images to notes (drag & drop supported)
4. **Search**: Find notes by title, content, or tags
5. **User Search**: Find other users by username or display name

#### 🔄 **Collaboration**
1. **Share Notes**: Share specific notes with other users
2. **Privacy Control**: Manage who can see your notes
3. **User Management**: View and manage shared access
4. **Author Discovery**: Explore other users' profiles through shared notes

#### 🛠️ **Server Management** (Production)
1. **Updates**: Use `./update.sh` for automatic updates
2. **Monitoring**: Check logs with `sudo journalctl -u notizprojekt -f`
3. **Backups**: Automated backups are created before updates
4. **Health Checks**: Built-in health monitoring and alerts

### 🖥️ **Desktop Application (Classic)**

#### Basic Usage
1. **Login**: Use `root` / `420` for testing
2. **Create Notes**: Use the interface to add new notes
3. **Share**: Access sharing options for collaboration

```java
// Example database connection
DBVerbindung dbConnection = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
dbConnection.open();
```

## 🔄 Update Management

### Automatic Updates

The project includes a comprehensive update system for production deployments:

#### **Update Script Usage**
```bash
# Basic update (interactive)
sudo -u notizprojekt /opt/notizprojekt/ITS-Projekt/update.sh

# Check application status
sudo -u notizprojekt /opt/notizprojekt/ITS-Projekt/update.sh --status

# View application logs
sudo -u notizprojekt /opt/notizprojekt/ITS-Projekt/update.sh --logs
```

#### **Scheduled Updates**
```bash
# Setup automatic weekly updates
sudo -u notizprojekt crontab -e
# Add: 0 3 * * 0 /opt/notizprojekt/ITS-Projekt/update.sh
```

#### **Update Features**
- ✅ Automatic backup before updates
- ✅ Health checks after deployment
- ✅ Rollback capability if issues occur
- ✅ Zero-downtime updates
- ✅ Comprehensive logging

### Manual Server Updates

For existing installations without the update script:

```bash
# Download the update script
sudo -u notizprojekt curl -o /opt/notizprojekt/ITS-Projekt/update.sh \
  https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/update.sh
sudo -u notizprojekt chmod +x /opt/notizprojekt/ITS-Projekt/update.sh

# Run the update
sudo -u notizprojekt /opt/notizprojekt/ITS-Projekt/update.sh
```

📖 **[Complete Update Guide](server-setup.md#update-script-usage-guide)** - Detailed update documentation

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

## 🔧 Troubleshooting

### 🗄️ **Database Issues**

#### Connection Problems
```bash
# Check if MariaDB/MySQL is running
sudo systemctl status mariadb
# or for Docker
docker ps | grep mariadb

# Test connection manually
mysql -u notizuser -pnotizpassword -h localhost notizprojekt

# If connection fails, check:
# 1. Database service is running
# 2. User exists and has permissions
# 3. Firewall settings (port 3306)
```

#### Schema Issues
```sql
-- Verify tables exist
SHOW TABLES;

-- Check table structure
DESCRIBE notiz;
DESCRIBE nutzer;

-- Re-import if needed
mysql -u root -p < its-projekt18.6.sql
```

### 🌐 **Web Application Issues**

#### Application Won't Start
```bash
# Check Java version (needs JDK 11+)
java -version

# Check Maven
./mvnw -version

# Clean and rebuild
./mvnw clean package

# Check logs for errors
./mvnw spring-boot:run
```

#### Port Issues
```bash
# Check if port 12000 is in use
netstat -tlnp | grep 12000

# Kill process if needed
sudo kill -9 $(lsof -t -i:12000)
```

#### Browser Issues
1. **Clear Cache**: Ctrl+F5 or Cmd+Shift+R
2. **Check Console**: F12 → Console tab for JavaScript errors
3. **Disable Extensions**: Try incognito/private mode
4. **Update Browser**: Ensure modern browser version

### 🖥️ **Desktop Application Issues**

#### Compilation Errors
```bash
# Check Java version (needs JDK 8+)
java -version

# Verify JDBC driver in lib/
ls -la lib/mysql-connector-java-*.jar

# Clean and rebuild in NetBeans
# Build → Clean and Build Project
```

### 🚨 **Common Solutions**

#### "User 'notizuser' doesn't exist"
```sql
-- Run as root user
CREATE USER 'notizuser'@'localhost' IDENTIFIED BY 'notizpassword';
CREATE USER 'notizuser'@'%' IDENTIFIED BY 'notizpassword';
GRANT ALL PRIVILEGES ON notizprojekt.* TO 'notizuser'@'localhost';
GRANT ALL PRIVILEGES ON notizprojekt.* TO 'notizuser'@'%';
FLUSH PRIVILEGES;
```

#### "Table doesn't exist"
```bash
# Re-import the complete schema
mysql -u root -p < its-projekt18.6.sql
```

#### "Access denied for user"
```sql
-- Reset user permissions
DROP USER IF EXISTS 'notizuser'@'localhost';
DROP USER IF EXISTS 'notizuser'@'%';
-- Then re-run the CREATE USER commands above
```

### 🛠️ **Production Management Commands**

For production installations:

```bash
# Check application status
sudo systemctl status notizprojekt

# View application logs
sudo journalctl -u notizprojekt -f

# Restart application
sudo systemctl restart notizprojekt

# Update application
sudo -u notizprojekt /opt/notizprojekt/ITS-Projekt/update.sh

# Check update logs
tail -f /opt/notizprojekt/ITS-Projekt/update.log

# View backups
ls -la /opt/notizprojekt/backups/
```

### 📞 **Getting Help**

If you're still experiencing issues:

1. **Check Logs**: Look for error messages in application logs
2. **Verify Setup**: Ensure all requirements are met
3. **Test Database**: Verify database connection independently
4. **Browser Console**: Check for JavaScript errors (web version)
5. **Port Conflicts**: Ensure port 12000 is available
6. **Update System**: Try running the update script to get latest fixes

#### Quick Health Check
```bash
# Database connection test
mysql -u notizuser -pnotizpassword -e "SELECT COUNT(*) FROM notizprojekt.nutzer;"

# Application test (should return 200 or 302)
curl -I http://localhost:12000/login

# Production health check
sudo -u notizprojekt /opt/notizprojekt/ITS-Projekt/update.sh --status
```

#### 📚 **Documentation Links**
- 📖 **[Complete Setup Guide](server-setup.md)** - Comprehensive installation and configuration
- 🚀 **[Quick Deployment Guide](DEPLOYMENT_GUIDE.md)** - Fast reference for common tasks
- 🔄 **[Update Script Guide](server-setup.md#update-script-usage-guide)** - Detailed update management

## License

This project is licensed under the terms included in the [LICENSE](LICENSE) file.
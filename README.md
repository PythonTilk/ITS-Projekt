# ITS-Projekt - Enhanced Note-Taking Application

## Overview

ITS-Projekt is a comprehensive note-taking application that allows users to create, edit, organize, and share notes with advanced features. The application uses a MySQL/MariaDB database and is available in two versions:

1. **Desktop Application**: A Java Swing-based desktop application (main branch)
2. **Web Application**: A modern Spring Boot-based web application with enhanced features (html branch)

## ✨ Enhanced Features

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
- **Secure Authentication**: BCrypt password encryption
- **Session Management**: Secure user sessions with proper logout

### 🏷️ **Organization & Search**
- **Tagging System**: Organize notes with custom tags
- **Advanced Search**: Search by title, content, tags, or note type
- **Spatial Positioning**: Remember note positions on the board
- **Note Categories**: Organize by type (text, code, rich text)

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

Welcome to the enhanced ITS-Projekt! Choose your preferred version:

### 🌟 **Recommended: Enhanced Web Application**

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
1. **Login**: Use `testuser123` / `password123` or create a new account
2. **Registration**: Create new accounts with email validation
3. **Security**: Passwords are encrypted with BCrypt

#### 📝 **Creating and Managing Notes**
1. **Add Note**: Click the "+" button to create a new note
2. **Note Types**:
   - **📄 Text**: Simple text notes with basic formatting
   - **🎨 Rich Text**: Full WYSIWYG editor with formatting toolbar
   - **💻 Code**: Syntax-highlighted code editor with language support
3. **Auto-Save**: Changes are saved automatically as you type

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

#### 🔄 **Collaboration**
1. **Share Notes**: Share specific notes with other users
2. **Privacy Control**: Manage who can see your notes
3. **User Management**: View and manage shared access

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

### 📞 **Getting Help**

If you're still experiencing issues:

1. **Check Logs**: Look for error messages in application logs
2. **Verify Setup**: Ensure all requirements are met
3. **Test Database**: Verify database connection independently
4. **Browser Console**: Check for JavaScript errors (web version)
5. **Port Conflicts**: Ensure port 12000 is available

#### Quick Health Check
```bash
# Database connection test
mysql -u notizuser -pnotizpassword -e "SELECT COUNT(*) FROM notizprojekt.nutzer;"

# Application test (should return 200 or 302)
curl -I http://localhost:12000/login
```

## License

This project is licensed under the terms included in the [LICENSE](LICENSE) file.
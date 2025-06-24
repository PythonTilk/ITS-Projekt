# Database Setup Guide

## Quick Setup

### Windows
1. **Install MySQL** (if not already installed)
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Or use MySQL Installer: https://dev.mysql.com/downloads/installer/
2. **Run the SQL script** to create the database and user:
   ```cmd
   mysql -u root -p < its-projekt18.6.sql
   ```
   Or using PowerShell:
   ```powershell
   Get-Content its-projekt18.6.sql | mysql -u root -p
   ```

### Linux/Mac
1. **Install MySQL** (if not already installed)
2. **Run the SQL script** to create the database and user:
   ```bash
   mysql -u root -p < its-projekt18.6.sql
   ```

## What the SQL Script Creates

The `its-projekt18.6.sql` script automatically creates:

- **Database:** `notizprojekt`
- **User:** `notizuser` with password `notizpassword`
- **Tables:** All required tables for the application
- **Sample Data:** Test users and sample notes

### Created Database User
- **Username:** `notizuser`
- **Password:** `notizpassword`
- **Permissions:** Full access to `notizprojekt` database
- **Access:** Both localhost and remote connections

### Sample Users Created
The script creates these test users (all with password "password123"):
- `testuser123`
- `admin`
- `demo`

## Application Configuration

The application is automatically configured to use:
- **Database:** `notizprojekt`
- **Username:** `notizuser`
- **Password:** `notizpassword`

You can modify these settings in `src/main/resources/application.properties` if needed.

## Manual Database Setup (Alternative)

If you prefer to set up the database manually:

1. Create the database:
   ```sql
   CREATE DATABASE notizprojekt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. Create the user:
   ```sql
   CREATE USER 'notizuser'@'localhost' IDENTIFIED BY 'notizpassword';
   CREATE USER 'notizuser'@'%' IDENTIFIED BY 'notizpassword';
   ```

3. Grant permissions:
   ```sql
   GRANT ALL PRIVILEGES ON notizprojekt.* TO 'notizuser'@'localhost';
   GRANT ALL PRIVILEGES ON notizprojekt.* TO 'notizuser'@'%';
   FLUSH PRIVILEGES;
   ```

4. Run the rest of the SQL script to create tables and sample data.

## Running the Application

### Windows
Use one of these methods:

1. **Batch file (recommended):**
   ```cmd
   run-windows.bat
   ```

2. **PowerShell script:**
   ```powershell
   .\run-windows.ps1
   ```

3. **Manual (Command Prompt):**
   ```cmd
   mvn clean compile
   java -Dfile.encoding=UTF-8 -cp "target/classes;target/dependency/*" notizdesktop.NotizDesktopApplication
   ```

4. **Manual (PowerShell):**
   ```powershell
   mvn clean compile
   java -Dfile.encoding=UTF-8 -cp "target/classes;target/dependency/*" notizdesktop.NotizDesktopApplication
   ```

### Linux/Mac
```bash
mvn clean compile exec:java
```

## Troubleshooting

### Windows-Specific Issues

#### High DPI Display Issues
- The application automatically detects Windows and applies DPI scaling fixes
- If text appears too small/large, check your Windows display scaling settings

#### Font Rendering Issues
- The application uses Windows-specific font smoothing
- Make sure ClearType is enabled in Windows settings

#### MySQL Connection Issues on Windows
- Ensure MySQL service is running: `services.msc` → MySQL80 (or your version)
- Check Windows Firewall settings for MySQL (port 3306)
- Try connecting with: `mysql -u notizuser -p -h localhost notizprojekt`

#### Path Issues
- Make sure Java and Maven are in your Windows PATH
- Use `java -version` and `mvn -version` to verify

### General Connection Issues
- Ensure MySQL is running
- Check that the `notizuser` was created successfully
- Verify the database `notizprojekt` exists
- Check firewall settings if connecting remotely

### Permission Issues
- Make sure the user has proper privileges on the database
- Try connecting manually: `mysql -u notizuser -p notizprojekt`

### Configuration Changes
- Modify `src/main/resources/application.properties` to change database settings
- The application will fall back to these defaults if the properties file is not found
- Windows uses UTF-8 encoding by default in the configuration
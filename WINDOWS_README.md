# Windows Setup Guide for Notiz Desktop Application

## Prerequisites

### 1. Install Java 17 or Higher
- **Download:** https://adoptium.net/temurin/releases/
- **Alternative:** https://www.oracle.com/java/technologies/downloads/
- **Verify installation:** Open Command Prompt and run `java -version`

### 2. Install Maven
- **Download:** https://maven.apache.org/download.cgi
- **Installation Guide:** https://maven.apache.org/install.html
- **Verify installation:** Open Command Prompt and run `mvn -version`

### 3. Install MySQL
- **Download:** https://dev.mysql.com/downloads/mysql/
- **MySQL Installer (Recommended):** https://dev.mysql.com/downloads/installer/
- Choose "MySQL Server" during installation
- Remember the root password you set during installation

## Quick Start

### Step 1: Setup Database
1. Open Command Prompt as Administrator
2. Navigate to the project folder
3. Run the database setup:
   ```cmd
   mysql -u root -p < its-projekt18.6.sql
   ```
   Enter your MySQL root password when prompted.

### Step 2: Run the Application
Double-click `run-windows.bat` or run it from Command Prompt:
```cmd
run-windows.bat
```

**Alternative:** Use PowerShell (right-click → "Run with PowerShell"):
```powershell
.\run-windows.ps1
```

## Windows-Specific Features

### High DPI Support
- Automatically detects Windows high DPI displays
- Applies appropriate scaling for crisp text and UI elements
- Works with Windows 10/11 display scaling settings

### Native Windows Look and Feel
- Uses Windows native UI components
- Integrates with Windows theme (Light/Dark mode)
- Native Windows file dialogs and system integration

### Windows Font Rendering
- Optimized for Windows ClearType font smoothing
- Uses system fonts for better integration
- Supports Windows-specific font settings

## Troubleshooting

### "Java is not recognized as an internal or external command"
1. Make sure Java is installed
2. Add Java to your PATH:
   - Open System Properties → Advanced → Environment Variables
   - Add Java installation path to PATH (e.g., `C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot\bin`)

### "Maven is not recognized as an internal or external command"
1. Make sure Maven is installed
2. Add Maven to your PATH:
   - Add Maven bin folder to PATH (e.g., `C:\apache-maven-3.9.x\bin`)

### MySQL Connection Issues
1. **Service not running:**
   - Press `Win + R`, type `services.msc`
   - Find "MySQL80" (or your version) and start it

2. **Firewall blocking connection:**
   - Windows Defender Firewall → Allow an app
   - Add MySQL or allow port 3306

3. **Authentication issues:**
   - Try: `mysql -u root -p` to test root connection
   - Then: `mysql -u notizuser -p notizprojekt` to test app user

### Application Won't Start
1. **Check Java version:** `java -version` (must be 17+)
2. **Check Maven:** `mvn -version`
3. **Check MySQL:** `mysql -u notizuser -p notizprojekt`
4. **Run with verbose output:**
   ```cmd
   java -Dfile.encoding=UTF-8 -verbose:class -cp "target/classes;target/dependency/*" notizdesktop.NotizDesktopApplication
   ```

### High DPI Display Issues
- If UI elements appear too small/large:
  1. Right-click desktop → Display settings
  2. Check your scale setting (100%, 125%, 150%, etc.)
  3. The app should auto-adjust, but you can also try:
     - Right-click app → Properties → Compatibility
     - Check "Override high DPI scaling behavior"

## Development in VS Code on Windows

### Required Extensions
- Extension Pack for Java (Microsoft)
- Maven for Java

### Running from VS Code
1. Open the project folder in VS Code
2. Open `NotizDesktopApplication.java`
3. Click the "Run" button above the `main` method
4. Or press `F5` to run with debugging

### Building from VS Code Terminal
```cmd
mvn clean compile
mvn exec:java
```

## Windows-Specific Configuration

The application automatically detects Windows and applies:
- Windows-specific database connection parameters
- UTF-8 encoding for proper character support
- Windows Look and Feel
- High DPI awareness
- Windows font smoothing

All configurations are in `src/main/resources/application.properties` and can be customized if needed.

## Support

If you encounter issues:
1. Check this troubleshooting guide
2. Verify all prerequisites are installed
3. Check the main `DATABASE_SETUP.md` for additional help
4. Ensure Windows Defender/Antivirus isn't blocking the application
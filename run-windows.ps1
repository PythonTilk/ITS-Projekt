# PowerShell script to run the Notiz Desktop Application on Windows
# Make sure Java 17+ and Maven are installed

Write-Host "Starting Notiz Desktop Application..." -ForegroundColor Green
Write-Host ""

# Check if Java is available
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java found: $($javaVersion[0])" -ForegroundColor Green
} catch {
    Write-Host "ERROR: Java is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Java 17 or higher and add it to your PATH" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Check if Maven is available
try {
    $mavenVersion = mvn -version 2>&1 | Select-Object -First 1
    Write-Host "Maven found: $mavenVersion" -ForegroundColor Green
} catch {
    Write-Host "ERROR: Maven is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Maven and add it to your PATH" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Compile the application
Write-Host ""
Write-Host "Compiling application..." -ForegroundColor Yellow
$compileResult = mvn clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Compilation failed" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "Starting application..." -ForegroundColor Green
Write-Host "Make sure MySQL is running and the database is set up!" -ForegroundColor Yellow
Write-Host ""

# Run the application with Windows-specific JVM arguments
$javaArgs = @(
    "-Dfile.encoding=UTF-8",
    "-Dsun.java2d.dpiaware=true",
    "-Dswing.aatext=true",
    "-Dawt.useSystemAAFontSettings=on",
    "-cp", "target/classes;target/dependency/*",
    "notizdesktop.NotizDesktopApplication"
)

try {
    & java $javaArgs
} catch {
    Write-Host ""
    Write-Host "Application failed to start: $_" -ForegroundColor Red
    Read-Host "Press Enter to exit"
}

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "Application exited with error code $LASTEXITCODE" -ForegroundColor Red
    Read-Host "Press Enter to exit"
}
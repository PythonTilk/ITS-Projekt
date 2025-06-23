# ITS-Projekt Server Setup Guide

This guide provides comprehensive instructions for setting up the ITS-Projekt (Note Management System) on a server for public usage.

## Table of Contents

1. [Quick Start (Automated Installation)](#quick-start-automated-installation)
2. [Prerequisites](#prerequisites)
3. [System Requirements](#system-requirements)
4. [Manual Installation](#manual-installation)
5. [Database Setup](#database-setup)
6. [Application Configuration](#application-configuration)
7. [Security Configuration](#security-configuration)
8. [Reverse Proxy Setup (Nginx)](#reverse-proxy-setup-nginx)
9. [SSL/TLS Configuration](#ssltls-configuration)
10. [Systemd Service Setup](#systemd-service-setup)
11. [Firewall Configuration](#firewall-configuration)
12. [Monitoring and Logging](#monitoring-and-logging)
13. [Backup Strategy](#backup-strategy)
14. [Automatic Updates](#automatic-updates)
15. [Update Script Usage Guide](#update-script-usage-guide)
16. [Troubleshooting](#troubleshooting)

## Quick Start (Automated Installation)

For a quick and automated installation, use the provided installation script. This is the **recommended method** for most users.

### 🚀 One-Command Installation

```bash
# Download and run the installation script
curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/main/install.sh | sudo bash
```

### 📋 What the Installation Script Does

The `install.sh` script automatically:

1. **System Setup**
   - Updates system packages
   - Installs Java 11, Maven, Git, MySQL, Nginx, and Certbot
   - Configures firewall (UFW/firewalld)

2. **Database Configuration**
   - Secures MySQL installation
   - Creates database and user
   - Sets up proper permissions

3. **Application Deployment**
   - Creates dedicated application user
   - Clones and builds the application
   - Configures production settings
   - Sets up systemd service

4. **Web Server & SSL**
   - Configures Nginx reverse proxy
   - Obtains and configures SSL certificate
   - Sets up security headers and optimizations

5. **Monitoring & Backups**
   - Configures automated database backups
   - Sets up log rotation
   - Enables SSL certificate auto-renewal

### 🔧 Installation Script Usage

#### Basic Installation
```bash
# Make the script executable
chmod +x install.sh

# Run the installation (requires root)
sudo ./install.sh
```

#### Get Help
```bash
./install.sh --help
```

#### Uninstall
```bash
sudo ./install.sh --uninstall
```

### 📝 Installation Process

When you run the installation script, it will:

1. **Prompt for Information**
   - Domain name (e.g., `example.com`)
   - Email address for SSL certificate
   - Database password

2. **Automated Setup**
   - Install all required software
   - Configure services
   - Set up SSL certificate
   - Start the application

3. **Completion Summary**
   - Display access URL
   - Show management commands
   - List important file locations

### ⚡ Quick Installation Example

```bash
# Download the repository
git clone https://github.com/PythonTilk/ITS-Projekt.git
cd ITS-Projekt

# Run the installation script
sudo ./install.sh

# Follow the prompts:
# Domain: your-domain.com
# Email: your-email@example.com
# Database Password: [secure password]

# Wait for completion (5-10 minutes)
# Access your application at https://your-domain.com
```

### 🎯 Post-Installation

After successful installation:

1. **Access Your Application**
   - Visit `https://your-domain.com`
   - Create your first user account
   - Start using the note management system

2. **Verify Installation**
   ```bash
   # Check application status
   sudo systemctl status notizprojekt
   
   # View application logs
   sudo journalctl -u notizprojekt -f
   
   # Test SSL certificate
   curl -I https://your-domain.com
   ```

3. **Customize Configuration** (Optional)
   ```bash
   # Edit environment variables
   sudo nano /opt/notizprojekt/.env
   
   # Restart application after changes
   sudo systemctl restart notizprojekt
   ```

---

## Manual Installation

If you prefer manual installation or need custom configuration, follow the detailed steps below.

## Prerequisites

### Software Requirements

- **Operating System**: Ubuntu 20.04 LTS or newer (recommended), CentOS 8+, or Debian 11+
- **Java**: OpenJDK 11 or higher
- **Database**: MySQL 8.0+ or MariaDB 10.5+
- **Web Server**: Nginx (recommended) or Apache
- **Build Tool**: Maven 3.6+
- **Version Control**: Git

### Hardware Requirements

**Minimum Requirements:**
- CPU: 2 cores
- RAM: 2GB
- Storage: 20GB SSD
- Network: 100 Mbps

**Recommended for Production:**
- CPU: 4+ cores
- RAM: 4GB+
- Storage: 50GB+ SSD
- Network: 1 Gbps

## Manual Installation

### 1. Update System

```bash
# Ubuntu/Debian
sudo apt update && sudo apt upgrade -y

# CentOS/RHEL
sudo yum update -y
# or for newer versions
sudo dnf update -y
```

### 2. Install Java

```bash
# Ubuntu/Debian
sudo apt install openjdk-11-jdk -y

# CentOS/RHEL
sudo yum install java-11-openjdk-devel -y
# or
sudo dnf install java-11-openjdk-devel -y

# Verify installation
java -version
javac -version
```

### 3. Install Maven

```bash
# Ubuntu/Debian
sudo apt install maven -y

# CentOS/RHEL
sudo yum install maven -y
# or
sudo dnf install maven -y

# Verify installation
mvn -version
```

### 4. Install Git

```bash
# Ubuntu/Debian
sudo apt install git -y

# CentOS/RHEL
sudo yum install git -y
# or
sudo dnf install git -y
```

### 5. Install Database (MySQL)

```bash
# Ubuntu/Debian
sudo apt install mysql-server -y

# CentOS/RHEL
sudo yum install mysql-server -y
# or
sudo dnf install mysql-server -y

# Start and enable MySQL
sudo systemctl start mysql
sudo systemctl enable mysql

# Secure MySQL installation
sudo mysql_secure_installation
```

### 6. Install Nginx

```bash
# Ubuntu/Debian
sudo apt install nginx -y

# CentOS/RHEL
sudo yum install nginx -y
# or
sudo dnf install nginx -y

# Start and enable Nginx
sudo systemctl start nginx
sudo systemctl enable nginx
```

## Database Setup

### 1. Create Database and User

```bash
# Login to MySQL as root
sudo mysql -u root -p

# Create database
CREATE DATABASE notizprojekt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Create user (replace 'your_password' with a strong password)
CREATE USER 'notizprojekt'@'localhost' IDENTIFIED BY 'your_password';

# Grant privileges
GRANT ALL PRIVILEGES ON notizprojekt.* TO 'notizprojekt'@'localhost';

# Flush privileges
FLUSH PRIVILEGES;

# Exit MySQL
EXIT;
```

### 2. Configure MySQL for Production

Edit MySQL configuration:

```bash
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf
```

Add/modify these settings:

```ini
[mysqld]
# Performance settings
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 2
innodb_flush_method = O_DIRECT

# Security settings
bind-address = 127.0.0.1
skip-networking = false
local-infile = 0

# Character set
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci

# Connection limits
max_connections = 200
max_user_connections = 50
```

Restart MySQL:

```bash
sudo systemctl restart mysql
```

## Application Configuration

### 1. Create Application User

```bash
# Create dedicated user for the application
sudo useradd -r -m -U -d /opt/notizprojekt -s /bin/bash notizprojekt

# Switch to application user
sudo su - notizprojekt
```

### 2. Clone and Build Application

```bash
# Clone the repository
git clone https://github.com/PythonTilk/ITS-Projekt.git
cd ITS-Projekt

# Build the application
mvn clean package -DskipTests

# Create necessary directories
mkdir -p logs uploads backups
```

### 3. Configure Application Properties

Create production configuration:

```bash
nano src/main/resources/application-prod.properties
```

Add the following configuration:

```properties
# Server configuration
server.port=8080
server.address=127.0.0.1

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/notizprojekt?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=notizprojekt
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=false

# File upload configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload-dir=/opt/notizprojekt/uploads

# Session configuration
server.servlet.session.timeout=30m
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.same-site=strict

# Logging configuration
logging.level.root=INFO
logging.level.notizprojekt=INFO
logging.file.name=/opt/notizprojekt/logs/application.log
logging.file.max-size=10MB
logging.file.max-history=10

# Security headers
server.servlet.session.cookie.name=NOTIZPROJEKT_SESSION
```

### 4. Environment Variables

Create environment file:

```bash
nano /opt/notizprojekt/.env
```

```bash
# Database credentials
DB_PASSWORD=your_password

# Application settings
SPRING_PROFILES_ACTIVE=prod
JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC"

# Security
SESSION_SECRET=your_session_secret_key_here
```

Make it secure:

```bash
chmod 600 /opt/notizprojekt/.env
```

## Security Configuration

### 1. Configure Firewall

```bash
# Ubuntu/Debian (UFW)
sudo ufw allow ssh
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw --force enable

# CentOS/RHEL (firewalld)
sudo firewall-cmd --permanent --add-service=ssh
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload
```

### 2. Secure File Permissions

```bash
# Set proper ownership
sudo chown -R notizprojekt:notizprojekt /opt/notizprojekt

# Set secure permissions
sudo chmod 755 /opt/notizprojekt
sudo chmod 644 /opt/notizprojekt/target/*.jar
sudo chmod 755 /opt/notizprojekt/update.sh
sudo chmod 700 /opt/notizprojekt/uploads
sudo chmod 700 /opt/notizprojekt/logs
```

## Reverse Proxy Setup (Nginx)

### 1. Create Nginx Configuration

```bash
sudo nano /etc/nginx/sites-available/notizprojekt
```

```nginx
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;
    
    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com www.your-domain.com;
    
    # SSL Configuration (will be configured later)
    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
    
    # SSL Security Settings
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;
    
    # Security Headers
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin";
    add_header Content-Security-Policy "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline' fonts.googleapis.com; font-src 'self' fonts.gstatic.com; img-src 'self' data:; connect-src 'self';";
    
    # Gzip Compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;
    
    # Client body size limit
    client_max_body_size 10M;
    
    # Proxy settings
    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Port $server_port;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # Buffer settings
        proxy_buffering on;
        proxy_buffer_size 4k;
        proxy_buffers 8 4k;
    }
    
    # Static files caching
    location ~* \.(css|js|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
    
    # Health check endpoint
    location /health {
        access_log off;
        proxy_pass http://127.0.0.1:8080/actuator/health;
        proxy_set_header Host $host;
    }
}
```

### 2. Enable Site

```bash
# Enable the site
sudo ln -s /etc/nginx/sites-available/notizprojekt /etc/nginx/sites-enabled/

# Remove default site
sudo rm -f /etc/nginx/sites-enabled/default

# Test configuration
sudo nginx -t

# Reload Nginx
sudo systemctl reload nginx
```

## SSL/TLS Configuration

### 1. Install Certbot

```bash
# Ubuntu/Debian
sudo apt install certbot python3-certbot-nginx -y

# CentOS/RHEL
sudo yum install certbot python3-certbot-nginx -y
# or
sudo dnf install certbot python3-certbot-nginx -y
```

### 2. Obtain SSL Certificate

```bash
# Replace your-domain.com with your actual domain
sudo certbot --nginx -d your-domain.com -d www.your-domain.com
```

### 3. Auto-renewal Setup

```bash
# Test auto-renewal
sudo certbot renew --dry-run

# Add cron job for auto-renewal
sudo crontab -e

# Add this line:
0 12 * * * /usr/bin/certbot renew --quiet
```

## Systemd Service Setup

### 1. Create Service File

```bash
sudo nano /etc/systemd/system/notizprojekt.service
```

```ini
[Unit]
Description=ITS-Projekt Note Management System
After=network.target mysql.service
Requires=mysql.service

[Service]
Type=simple
User=notizprojekt
Group=notizprojekt
WorkingDirectory=/opt/notizprojekt/ITS-Projekt
Environment=SPRING_PROFILES_ACTIVE=prod
EnvironmentFile=/opt/notizprojekt/.env
ExecStart=/usr/bin/java $JAVA_OPTS -jar target/notizprojekt-0.0.1-SNAPSHOT.jar
ExecStop=/bin/kill -TERM $MAINPID
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=notizprojekt

# Security settings
NoNewPrivileges=true
PrivateTmp=true
ProtectSystem=strict
ProtectHome=true
ReadWritePaths=/opt/notizprojekt

[Install]
WantedBy=multi-user.target
```

### 2. Enable and Start Service

```bash
# Reload systemd
sudo systemctl daemon-reload

# Enable service
sudo systemctl enable notizprojekt

# Start service
sudo systemctl start notizprojekt

# Check status
sudo systemctl status notizprojekt
```

## Firewall Configuration

### 1. Configure iptables (Alternative to UFW/firewalld)

```bash
# Create iptables rules
sudo nano /etc/iptables/rules.v4
```

```bash
*filter
:INPUT DROP [0:0]
:FORWARD DROP [0:0]
:OUTPUT ACCEPT [0:0]

# Allow loopback
-A INPUT -i lo -j ACCEPT

# Allow established connections
-A INPUT -m conntrack --ctstate RELATED,ESTABLISHED -j ACCEPT

# Allow SSH
-A INPUT -p tcp --dport 22 -j ACCEPT

# Allow HTTP and HTTPS
-A INPUT -p tcp --dport 80 -j ACCEPT
-A INPUT -p tcp --dport 443 -j ACCEPT

# Allow ping
-A INPUT -p icmp --icmp-type echo-request -j ACCEPT

COMMIT
```

## Monitoring and Logging

### 1. Log Rotation

```bash
sudo nano /etc/logrotate.d/notizprojekt
```

```bash
/opt/notizprojekt/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 notizprojekt notizprojekt
    postrotate
        systemctl reload notizprojekt
    endscript
}
```

### 2. Monitoring Script

```bash
nano /opt/notizprojekt/monitor.sh
```

```bash
#!/bin/bash

# Simple monitoring script
LOG_FILE="/opt/notizprojekt/logs/monitor.log"
APP_URL="http://localhost:8080/login"

check_app() {
    if curl -s -f "$APP_URL" > /dev/null; then
        echo "$(date): Application is healthy" >> "$LOG_FILE"
        return 0
    else
        echo "$(date): Application is down!" >> "$LOG_FILE"
        # Restart application
        sudo systemctl restart notizprojekt
        return 1
    fi
}

check_app
```

Make it executable and add to cron:

```bash
chmod +x /opt/notizprojekt/monitor.sh

# Add to crontab
crontab -e

# Check every 5 minutes
*/5 * * * * /opt/notizprojekt/monitor.sh
```

## Backup Strategy

### 1. Database Backup Script

```bash
nano /opt/notizprojekt/backup-db.sh
```

```bash
#!/bin/bash

BACKUP_DIR="/opt/notizprojekt/backups"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="notizprojekt"
DB_USER="notizprojekt"
DB_PASS="your_password"

mkdir -p "$BACKUP_DIR"

# Create database backup
mysqldump -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" | gzip > "$BACKUP_DIR/db_backup_$DATE.sql.gz"

# Keep only last 7 days of backups
find "$BACKUP_DIR" -name "db_backup_*.sql.gz" -mtime +7 -delete

echo "Database backup completed: $BACKUP_DIR/db_backup_$DATE.sql.gz"
```

### 2. Full Backup Script

```bash
nano /opt/notizprojekt/backup-full.sh
```

```bash
#!/bin/bash

BACKUP_DIR="/opt/notizprojekt/backups"
DATE=$(date +%Y%m%d_%H%M%S)
APP_DIR="/opt/notizprojekt"

mkdir -p "$BACKUP_DIR"

# Create full backup
tar -czf "$BACKUP_DIR/full_backup_$DATE.tar.gz" \
    --exclude="$APP_DIR/backups" \
    --exclude="$APP_DIR/logs" \
    --exclude="$APP_DIR/ITS-Projekt/target" \
    "$APP_DIR"

# Keep only last 3 full backups
find "$BACKUP_DIR" -name "full_backup_*.tar.gz" -mtime +3 -delete

echo "Full backup completed: $BACKUP_DIR/full_backup_$DATE.tar.gz"
```

### 3. Schedule Backups

```bash
# Add to crontab
crontab -e

# Database backup every 6 hours
0 */6 * * * /opt/notizprojekt/backup-db.sh

# Full backup daily at 2 AM
0 2 * * * /opt/notizprojekt/backup-full.sh
```

## Automatic Updates

The project includes an `update.sh` script for automatic updates. To set up automatic updates:

### 1. Configure Auto-updates

```bash
# Make the update script executable
chmod +x /opt/notizprojekt/ITS-Projekt/update.sh

# Test the update script
sudo -u notizprojekt /opt/notizprojekt/ITS-Projekt/update.sh --help
```

### 2. Schedule Updates (Optional)

```bash
# Add to crontab for weekly updates (Sundays at 3 AM)
sudo crontab -e

# Add this line:
0 3 * * 0 sudo -u notizprojekt /opt/notizprojekt/ITS-Projekt/update.sh
```

### 3. Manual Update

```bash
# Switch to application user
sudo su - notizprojekt

# Run update
cd ITS-Projekt
./update.sh
```

## Update Script Usage Guide

The `update.sh` script provides comprehensive update management for your ITS-Projekt installation. This section covers all aspects of using the update script effectively.

### 🔄 Overview

The update script (`update.sh`) is a powerful tool that:
- Automatically pulls the latest code changes
- Builds and deploys the updated application
- Manages application lifecycle (stop/start)
- Creates backups before updates
- Performs health checks after updates
- Handles rollbacks if needed

### 📍 Script Location

After installation, the update script is located at:
```bash
/opt/notizprojekt/ITS-Projekt/update.sh
```

### 🚀 Basic Usage

#### Full Update Process
```bash
# Switch to application user
sudo su - notizprojekt

# Navigate to project directory
cd ITS-Projekt

# Run full update
./update.sh
```

#### Alternative (as root/sudo)
```bash
# Run as application user from anywhere
sudo -u notizprojekt /opt/notizprojekt/ITS-Projekt/update.sh
```

### 🛠️ Command Options

The update script supports several command-line options:

#### Show Help
```bash
./update.sh --help
# or
./update.sh -h
```

#### Stop Application Only
```bash
./update.sh --stop
```

#### Start Application Only
```bash
./update.sh --start
```

#### Check Application Status
```bash
./update.sh --status
```

#### View Application Logs
```bash
./update.sh --logs
```

### 📋 Update Process Details

When you run `./update.sh` (full update), the script performs these steps:

#### 1. **Prerequisites Check**
- Verifies Git, Java, and Maven are installed
- Checks if running as correct user
- Validates project directory structure

#### 2. **Backup Creation**
- Creates timestamped backup of current JAR file
- Stores backups in `/opt/notizprojekt/backups/`
- Automatically removes old backups (keeps last 5)

#### 3. **Application Stop**
- Gracefully stops the running application
- Waits up to 30 seconds for clean shutdown
- Force kills if necessary

#### 4. **Source Code Update**
- Stashes any local changes (if present)
- Fetches latest changes from Git repository
- Pulls updates from current branch

#### 5. **Application Build**
- Runs `mvn clean package -DskipTests`
- Compiles and packages the application
- Validates build success

#### 6. **Application Start**
- Starts the application with configured JVM options
- Saves process ID for management
- Monitors startup process

#### 7. **Health Check**
- Waits for application to be ready
- Tests HTTP endpoint response
- Verifies application is healthy

#### 8. **Cleanup**
- Rotates log files
- Cleans up temporary files
- Reports update status

### 📊 Update Script Output

The script provides detailed, color-coded output:

```bash
[2024-01-15 10:30:00] Starting automatic update process...
[SUCCESS] All prerequisites are met.
[2024-01-15 10:30:05] Creating backup...
[SUCCESS] Backup created at /opt/notizprojekt/backups/backup_20240115_103005
[2024-01-15 10:30:10] Stopping application...
[SUCCESS] Application stopped gracefully.
[2024-01-15 10:30:15] Updating source code...
[SUCCESS] Source code updated successfully.
[2024-01-15 10:30:45] Building application...
[SUCCESS] Application built successfully.
[2024-01-15 10:31:00] Starting application...
[SUCCESS] Application started successfully with PID 12345
[2024-01-15 10:31:10] Performing health check...
[SUCCESS] Application is healthy and responding.
[SUCCESS] Update completed successfully!

✓ Update completed successfully!
ℹ Application is running at: http://localhost:8080
ℹ Logs: /opt/notizprojekt/app.log
ℹ Update log: /opt/notizprojekt/ITS-Projekt/update.log
```

### 🔍 Monitoring Updates

#### View Update Logs
```bash
# View recent update activity
tail -f /opt/notizprojekt/ITS-Projekt/update.log

# View application logs
tail -f /opt/notizprojekt/app.log

# View system service logs
sudo journalctl -u notizprojekt -f
```

#### Check Update History
```bash
# View update log file
cat /opt/notizprojekt/ITS-Projekt/update.log

# Check backup history
ls -la /opt/notizprojekt/backups/
```

### ⚙️ Configuration

#### Environment Variables
The update script respects these environment variables:

```bash
# Java options for the application
JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC"

# Spring profile
SPRING_PROFILES_ACTIVE=prod
```

#### Customizing Update Behavior
Edit the script variables at the top of `update.sh`:

```bash
# Project configuration
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKUP_DIR="$PROJECT_DIR/backups"
LOG_FILE="$PROJECT_DIR/update.log"
PID_FILE="$PROJECT_DIR/app.pid"
JAR_FILE="$PROJECT_DIR/target/notizprojekt-0.0.1-SNAPSHOT.jar"
JAVA_OPTS="-Xmx512m -Xms256m"
```

### 🔄 Automated Updates

#### Setup Automatic Updates
```bash
# Edit crontab for application user
sudo -u notizprojekt crontab -e

# Add automatic update schedule
# Weekly updates on Sunday at 3 AM
0 3 * * 0 /opt/notizprojekt/ITS-Projekt/update.sh

# Daily updates at 2 AM (more frequent)
0 2 * * * /opt/notizprojekt/ITS-Projekt/update.sh

# Check every 6 hours
0 */6 * * * /opt/notizprojekt/ITS-Projekt/update.sh
```

#### View Scheduled Updates
```bash
# Check current cron jobs
sudo -u notizprojekt crontab -l

# View cron execution logs
sudo tail -f /var/log/cron
```

### 🛡️ Safety Features

#### Automatic Backups
- Creates backup before each update
- Keeps last 5 backups automatically
- Backup location: `/opt/notizprojekt/backups/`

#### Rollback Capability
```bash
# Manual rollback to previous version
cd /opt/notizprojekt/backups
ls -la  # Find backup to restore

# Stop current application
sudo -u notizprojekt /opt/notizprojekt/ITS-Projekt/update.sh --stop

# Restore backup (replace with actual backup file)
sudo -u notizprojekt cp backup_20240115_103005/notizprojekt-0.0.1-SNAPSHOT.jar \
  /opt/notizprojekt/ITS-Projekt/target/

# Start application
sudo -u notizprojekt /opt/notizprojekt/ITS-Projekt/update.sh --start
```

#### Health Checks
- Verifies application responds after update
- Automatic restart if health check fails
- Detailed error reporting

### 🚨 Troubleshooting Updates

#### Update Fails to Start
```bash
# Check application logs
./update.sh --logs

# Check system service status
sudo systemctl status notizprojekt

# Manual restart
sudo systemctl restart notizprojekt
```

#### Build Failures
```bash
# Check Maven build logs
cd /opt/notizprojekt/ITS-Projekt
sudo -u notizprojekt mvn clean package

# Check Java version
java -version

# Check disk space
df -h
```

#### Git Update Issues
```bash
# Check Git status
cd /opt/notizprojekt/ITS-Projekt
sudo -u notizprojekt git status

# Reset local changes
sudo -u notizprojekt git reset --hard HEAD
sudo -u notizprojekt git clean -fd

# Force update
sudo -u notizprojekt git pull --force
```

#### Permission Issues
```bash
# Fix ownership
sudo chown -R notizprojekt:notizprojekt /opt/notizprojekt

# Fix script permissions
sudo chmod +x /opt/notizprojekt/ITS-Projekt/update.sh
```

### 📈 Best Practices

#### 1. **Regular Updates**
- Schedule weekly automatic updates
- Monitor update logs regularly
- Test updates in staging environment first

#### 2. **Backup Management**
- Verify backups are created successfully
- Periodically test backup restoration
- Consider external backup storage

#### 3. **Monitoring**
- Set up alerts for failed updates
- Monitor application performance after updates
- Keep update logs for troubleshooting

#### 4. **Maintenance Windows**
- Schedule updates during low-traffic periods
- Notify users of planned maintenance
- Have rollback plan ready

### 🔧 Advanced Usage

#### Custom Update Scripts
Create custom update hooks:

```bash
# Pre-update hook
cat > /opt/notizprojekt/pre-update.sh << 'EOF'
#!/bin/bash
echo "Running pre-update tasks..."
# Add custom pre-update logic here
EOF

# Post-update hook
cat > /opt/notizprojekt/post-update.sh << 'EOF'
#!/bin/bash
echo "Running post-update tasks..."
# Add custom post-update logic here
EOF

chmod +x /opt/notizprojekt/pre-update.sh
chmod +x /opt/notizprojekt/post-update.sh
```

#### Integration with CI/CD
```bash
# Webhook endpoint for automatic updates
curl -X POST https://your-domain.com/webhook/update \
  -H "Authorization: Bearer your-token"
```

### 📋 Update Checklist

Before running updates:
- [ ] Verify backup space available
- [ ] Check application is running normally
- [ ] Review recent changes in repository
- [ ] Ensure maintenance window if needed
- [ ] Have rollback plan ready

After updates:
- [ ] Verify application starts successfully
- [ ] Test key functionality
- [ ] Check application logs for errors
- [ ] Monitor performance metrics
- [ ] Verify SSL certificate still valid

This comprehensive guide ensures you can effectively manage updates for your ITS-Projekt installation with confidence and safety.

## Troubleshooting

### Common Issues

#### 1. Application Won't Start

```bash
# Check logs
sudo journalctl -u notizprojekt -f

# Check application logs
tail -f /opt/notizprojekt/logs/application.log

# Check if port is in use
sudo netstat -tlnp | grep :8080
```

#### 2. Database Connection Issues

```bash
# Test database connection
mysql -u notizprojekt -p notizprojekt

# Check MySQL status
sudo systemctl status mysql

# Check MySQL logs
sudo tail -f /var/log/mysql/error.log
```

#### 3. Nginx Issues

```bash
# Check Nginx status
sudo systemctl status nginx

# Test Nginx configuration
sudo nginx -t

# Check Nginx logs
sudo tail -f /var/log/nginx/error.log
```

#### 4. SSL Certificate Issues

```bash
# Check certificate status
sudo certbot certificates

# Renew certificate manually
sudo certbot renew

# Check certificate expiry
openssl x509 -in /etc/letsencrypt/live/your-domain.com/cert.pem -text -noout | grep "Not After"
```

### Performance Tuning

#### 1. JVM Tuning

Edit `/opt/notizprojekt/.env`:

```bash
# For 4GB RAM server
JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# For 8GB RAM server
JAVA_OPTS="-Xmx4g -Xms2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

#### 2. MySQL Tuning

Edit `/etc/mysql/mysql.conf.d/mysqld.cnf`:

```ini
# For 4GB RAM server
innodb_buffer_pool_size = 2G
innodb_log_file_size = 512M

# For 8GB RAM server
innodb_buffer_pool_size = 4G
innodb_log_file_size = 1G
```

#### 3. Nginx Tuning

Edit `/etc/nginx/nginx.conf`:

```nginx
worker_processes auto;
worker_connections 1024;

# Buffer sizes
client_body_buffer_size 128k;
client_max_body_size 10m;
client_header_buffer_size 1k;
large_client_header_buffers 4 4k;
output_buffers 1 32k;
postpone_output 1460;
```

### Security Checklist

- [ ] Firewall configured and enabled
- [ ] SSL/TLS certificate installed and auto-renewal configured
- [ ] Database user has minimal required privileges
- [ ] Application runs as non-root user
- [ ] File permissions properly set
- [ ] Security headers configured in Nginx
- [ ] Regular backups scheduled
- [ ] Monitoring and alerting set up
- [ ] Log rotation configured
- [ ] System updates scheduled

### Maintenance Tasks

#### Daily
- Check application logs for errors
- Verify backup completion
- Monitor disk space usage

#### Weekly
- Review security logs
- Check SSL certificate status
- Update system packages

#### Monthly
- Review and rotate logs
- Test backup restoration
- Performance analysis
- Security audit

## Support and Documentation

For additional support:

1. **Application Logs**: `/opt/notizprojekt/logs/application.log`
2. **System Logs**: `sudo journalctl -u notizprojekt`
3. **Nginx Logs**: `/var/log/nginx/`
4. **MySQL Logs**: `/var/log/mysql/`

## Conclusion

This setup provides a robust, secure, and scalable deployment of the ITS-Projekt note management system. Regular maintenance and monitoring will ensure optimal performance and security.

Remember to:
- Keep all software updated
- Monitor logs regularly
- Test backups periodically
- Review security settings quarterly
- Document any custom configurations

For production environments, consider additional measures such as:
- Load balancing for high availability
- Database replication
- CDN for static assets
- Advanced monitoring solutions (Prometheus, Grafana)
- Centralized logging (ELK stack)
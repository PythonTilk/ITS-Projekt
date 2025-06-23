# Server Setup Guide for ITS-Projekt Notes Application

This guide provides comprehensive instructions for setting up the ITS-Projekt notes application on a server for public usage.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [System Requirements](#system-requirements)
3. [Installation Steps](#installation-steps)
4. [Database Setup](#database-setup)
5. [Application Configuration](#application-configuration)
6. [Building the Application](#building-the-application)
7. [Running the Application](#running-the-application)
8. [Production Deployment](#production-deployment)
9. [SSL/HTTPS Setup](#ssl-https-setup)
10. [Monitoring and Maintenance](#monitoring-and-maintenance)
11. [Troubleshooting](#troubleshooting)

## Prerequisites

- Ubuntu 20.04 LTS or newer (or equivalent Linux distribution)
- Root or sudo access
- Domain name (optional but recommended for production)
- Basic knowledge of Linux command line

## System Requirements

### Minimum Requirements
- **CPU**: 1 vCPU
- **RAM**: 1 GB
- **Storage**: 10 GB
- **Network**: 1 Mbps

### Recommended for Production
- **CPU**: 2+ vCPUs
- **RAM**: 2+ GB
- **Storage**: 20+ GB SSD
- **Network**: 10+ Mbps

## Installation Steps

### 1. Update System Packages

```bash
sudo apt update && sudo apt upgrade -y
```

### 2. Install Java 17

```bash
# Install OpenJDK 17
sudo apt install openjdk-17-jdk -y

# Verify installation
java -version
javac -version

# Set JAVA_HOME (add to ~/.bashrc for persistence)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
```

### 3. Install MySQL/MariaDB

```bash
# Install MariaDB
sudo apt install mariadb-server mariadb-client -y

# Secure the installation
sudo mysql_secure_installation

# Start and enable MariaDB
sudo systemctl start mariadb
sudo systemctl enable mariadb
```

### 4. Install Git and Other Dependencies

```bash
sudo apt install git curl wget unzip -y
```

### 5. Install Nginx (for reverse proxy)

```bash
sudo apt install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx
```

## Database Setup

### 1. Create Database and User

```bash
# Login to MySQL/MariaDB
sudo mysql -u root -p

# Create database and user
CREATE DATABASE notizprojekt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'notizuser'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON notizprojekt.* TO 'notizuser'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 2. Import Database Schema

If you have an existing database dump:

```bash
mysql -u notizuser -p notizprojekt < database_dump.sql
```

Or create the tables manually using the application's auto-creation feature.

## Application Configuration

### 1. Clone the Repository

```bash
# Create application directory
sudo mkdir -p /opt/notizprojekt
sudo chown $USER:$USER /opt/notizprojekt

# Clone the repository
cd /opt/notizprojekt
git clone https://github.com/PythonTilk/ITS-Projekt.git .
git checkout html  # Use the html branch
```

### 2. Configure Application Properties

Create or edit the application configuration:

```bash
# Create application-prod.properties
cat > src/main/resources/application-prod.properties << EOF
# Server Configuration
server.port=12000
server.address=0.0.0.0

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/notizprojekt?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=notizuser
spring.datasource.password=your_secure_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# Security Configuration
spring.security.user.name=admin
spring.security.user.password=admin_password

# Logging Configuration
logging.level.notizprojekt=INFO
logging.level.org.springframework.security=INFO
logging.file.name=/opt/notizprojekt/logs/application.log

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Session Configuration
server.servlet.session.timeout=30m
EOF
```

### 3. Create Logs Directory

```bash
mkdir -p /opt/notizprojekt/logs
```

## Building the Application

### 1. Make Setup Script Executable

```bash
chmod +x setup.sh
```

### 2. Run Setup Script

```bash
./setup.sh
```

Or build manually:

```bash
# Set JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Build the application
./mvnw clean package -DskipTests

# Verify the JAR file was created
ls -la target/notizprojekt-web-*.jar
```

## Running the Application

### 1. Test Run

```bash
# Test the application
cd /opt/notizprojekt
java -jar target/notizprojekt-web-*.jar --spring.profiles.active=prod
```

### 2. Create Systemd Service

Create a systemd service for automatic startup:

```bash
sudo tee /etc/systemd/system/notizprojekt.service > /dev/null << EOF
[Unit]
Description=Notizprojekt Notes Application
After=network.target mysql.service

[Service]
Type=simple
User=www-data
Group=www-data
WorkingDirectory=/opt/notizprojekt
ExecStart=/usr/bin/java -jar /opt/notizprojekt/target/notizprojekt-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=notizprojekt

Environment=JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
Environment=SPRING_PROFILES_ACTIVE=prod

[Install]
WantedBy=multi-user.target
EOF
```

### 3. Set Permissions and Start Service

```bash
# Set ownership
sudo chown -R www-data:www-data /opt/notizprojekt

# Reload systemd and start service
sudo systemctl daemon-reload
sudo systemctl enable notizprojekt
sudo systemctl start notizprojekt

# Check status
sudo systemctl status notizprojekt
```

## Production Deployment

### 1. Configure Nginx Reverse Proxy

Create Nginx configuration:

```bash
sudo tee /etc/nginx/sites-available/notizprojekt > /dev/null << 'EOF'
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;  # Replace with your domain

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_proxied expired no-cache no-store private must-revalidate auth;
    gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/xml+rss application/javascript;

    # Main application
    location / {
        proxy_pass http://127.0.0.1:12000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket support
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # Buffer settings
        proxy_buffering on;
        proxy_buffer_size 128k;
        proxy_buffers 4 256k;
        proxy_busy_buffers_size 256k;
    }

    # Static files caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        proxy_pass http://127.0.0.1:12000;
        proxy_set_header Host $host;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Health check endpoint
    location /actuator/health {
        proxy_pass http://127.0.0.1:12000;
        access_log off;
    }

    # Deny access to sensitive files
    location ~ /\. {
        deny all;
    }

    # Logs
    access_log /var/log/nginx/notizprojekt_access.log;
    error_log /var/log/nginx/notizprojekt_error.log;
}
EOF
```

### 2. Enable Nginx Site

```bash
# Enable the site
sudo ln -s /etc/nginx/sites-available/notizprojekt /etc/nginx/sites-enabled/

# Remove default site (optional)
sudo rm -f /etc/nginx/sites-enabled/default

# Test Nginx configuration
sudo nginx -t

# Restart Nginx
sudo systemctl restart nginx
```

### 3. Configure Firewall

```bash
# Install UFW if not already installed
sudo apt install ufw -y

# Configure firewall
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow ssh
sudo ufw allow 'Nginx Full'

# Enable firewall
sudo ufw --force enable
```

## SSL/HTTPS Setup

### 1. Install Certbot

```bash
sudo apt install certbot python3-certbot-nginx -y
```

### 2. Obtain SSL Certificate

```bash
# Replace with your domain
sudo certbot --nginx -d your-domain.com -d www.your-domain.com
```

### 3. Auto-renewal Setup

```bash
# Test auto-renewal
sudo certbot renew --dry-run

# The cron job is automatically created, but you can verify:
sudo crontab -l | grep certbot
```

## Monitoring and Maintenance

### 1. Log Monitoring

```bash
# Application logs
sudo journalctl -u notizprojekt -f

# Nginx logs
sudo tail -f /var/log/nginx/notizprojekt_access.log
sudo tail -f /var/log/nginx/notizprojekt_error.log

# System logs
sudo tail -f /var/log/syslog
```

### 2. Health Checks

Create a simple health check script:

```bash
cat > /opt/notizprojekt/health-check.sh << 'EOF'
#!/bin/bash

# Check if application is running
if curl -f -s http://localhost:12000/actuator/health > /dev/null; then
    echo "$(date): Application is healthy"
else
    echo "$(date): Application is down, restarting..."
    sudo systemctl restart notizprojekt
fi
EOF

chmod +x /opt/notizprojekt/health-check.sh

# Add to crontab (check every 5 minutes)
(crontab -l 2>/dev/null; echo "*/5 * * * * /opt/notizprojekt/health-check.sh >> /opt/notizprojekt/logs/health-check.log 2>&1") | crontab -
```

### 3. Database Backup

```bash
# Create backup script
cat > /opt/notizprojekt/backup-db.sh << 'EOF'
#!/bin/bash

BACKUP_DIR="/opt/notizprojekt/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/notizprojekt_backup_$DATE.sql"

mkdir -p $BACKUP_DIR

mysqldump -u notizuser -p'your_secure_password' notizprojekt > $BACKUP_FILE

# Keep only last 7 days of backups
find $BACKUP_DIR -name "notizprojekt_backup_*.sql" -mtime +7 -delete

echo "$(date): Database backup completed: $BACKUP_FILE"
EOF

chmod +x /opt/notizprojekt/backup-db.sh

# Add to crontab (daily backup at 2 AM)
(crontab -l 2>/dev/null; echo "0 2 * * * /opt/notizprojekt/backup-db.sh >> /opt/notizprojekt/logs/backup.log 2>&1") | crontab -
```

### 4. System Updates

```bash
# Create update script
cat > /opt/notizprojekt/update-system.sh << 'EOF'
#!/bin/bash

echo "$(date): Starting system update..."

# Update packages
sudo apt update && sudo apt upgrade -y

# Clean up
sudo apt autoremove -y
sudo apt autoclean

echo "$(date): System update completed"
EOF

chmod +x /opt/notizprojekt/update-system.sh

# Add to crontab (weekly updates on Sunday at 3 AM)
(crontab -l 2>/dev/null; echo "0 3 * * 0 /opt/notizprojekt/update-system.sh >> /opt/notizprojekt/logs/updates.log 2>&1") | crontab -
```

## Troubleshooting

### Common Issues

#### 1. Application Won't Start

```bash
# Check Java version
java -version

# Check if port is in use
sudo netstat -tlnp | grep :12000

# Check application logs
sudo journalctl -u notizprojekt -n 50

# Check database connection
mysql -u notizuser -p notizprojekt -e "SELECT 1;"
```

#### 2. Database Connection Issues

```bash
# Check MySQL/MariaDB status
sudo systemctl status mariadb

# Check database user permissions
mysql -u root -p -e "SELECT User, Host FROM mysql.user WHERE User='notizuser';"

# Test connection
mysql -u notizuser -p notizprojekt
```

#### 3. Nginx Issues

```bash
# Check Nginx status
sudo systemctl status nginx

# Test configuration
sudo nginx -t

# Check error logs
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

### Performance Optimization

#### 1. JVM Tuning

Edit the systemd service file to add JVM options:

```bash
sudo systemctl edit notizprojekt
```

Add:

```ini
[Service]
Environment="JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

#### 2. Database Optimization

```sql
-- Add indexes for better performance
USE notizprojekt;

-- Index on user ID for notes
CREATE INDEX idx_notiz_user ON notiz(B_id);

-- Index on privacy level for sharing
CREATE INDEX idx_notiz_privacy ON notiz(privacy_level);

-- Index on shared_with for faster sharing queries
CREATE INDEX idx_notiz_shared ON notiz(shared_with);
```

#### 3. Nginx Optimization

Add to Nginx configuration:

```nginx
# Worker processes
worker_processes auto;
worker_connections 1024;

# Keepalive
keepalive_timeout 65;
keepalive_requests 100;

# Client settings
client_max_body_size 10M;
client_body_timeout 60;
client_header_timeout 60;
```

### Security Considerations

1. **Change Default Passwords**: Update all default passwords in production
2. **Regular Updates**: Keep system and application updated
3. **Firewall**: Use UFW or iptables to restrict access
4. **SSL/TLS**: Always use HTTPS in production
5. **Database Security**: Use strong passwords and limit database access
6. **Backup**: Regular backups with encryption
7. **Monitoring**: Set up log monitoring and alerting

### Support and Updates

- **Repository**: https://github.com/PythonTilk/ITS-Projekt
- **Branch**: html (for web version)
- **Issues**: Report issues on GitHub
- **Updates**: Pull latest changes regularly

```bash
# Update application
cd /opt/notizprojekt
git pull origin html
./mvnw clean package -DskipTests
sudo systemctl restart notizprojekt
```

---

**Note**: Replace `your-domain.com` and `your_secure_password` with your actual domain and secure passwords throughout this guide.
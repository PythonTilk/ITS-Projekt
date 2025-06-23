# ITS-Projekt Server Setup Guide

## 🚀 Production Server Deployment Guide

This guide provides comprehensive instructions for deploying the ITS-Projekt note-taking application on a production server for public usage.

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Server Requirements](#server-requirements)
3. [Database Setup](#database-setup)
4. [Application Deployment](#application-deployment)
5. [Web Server Configuration](#web-server-configuration)
6. [Security Configuration](#security-configuration)
7. [SSL/HTTPS Setup](#sslhttps-setup)
8. [Monitoring and Logging](#monitoring-and-logging)
9. [Backup Strategy](#backup-strategy)
10. [Troubleshooting](#troubleshooting)

## 📋 Prerequisites

### System Requirements
- **Operating System**: Ubuntu 20.04 LTS or newer (recommended), CentOS 8+, or Debian 11+
- **RAM**: Minimum 2GB, recommended 4GB+
- **Storage**: Minimum 10GB free space, recommended 50GB+ for user uploads
- **CPU**: 2+ cores recommended
- **Network**: Public IP address and domain name (optional but recommended)

### Required Software
- **Java**: OpenJDK 11 or newer
- **Database**: MySQL 8.0+ or MariaDB 10.6+
- **Web Server**: Nginx (recommended) or Apache
- **Process Manager**: systemd (included in most Linux distributions)
- **Firewall**: UFW or iptables
- **SSL Certificate**: Let's Encrypt (free) or commercial certificate

## 🖥️ Server Requirements

### Minimum Hardware Specifications
```
CPU: 1 vCPU (2+ recommended)
RAM: 2GB (4GB+ recommended)
Storage: 20GB SSD (50GB+ recommended)
Network: 100 Mbps (1 Gbps recommended)
```

### Recommended Cloud Providers
- **DigitalOcean**: $20/month droplet (2 vCPU, 4GB RAM)
- **AWS EC2**: t3.medium instance
- **Google Cloud**: e2-medium instance
- **Vultr**: $12/month instance (2 vCPU, 4GB RAM)
- **Linode**: $20/month instance (2 vCPU, 4GB RAM)

## 🗄️ Database Setup

### Option 1: Local MySQL/MariaDB Installation

#### Ubuntu/Debian
```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install MariaDB
sudo apt install mariadb-server mariadb-client -y

# Secure MariaDB installation
sudo mysql_secure_installation

# Start and enable MariaDB
sudo systemctl start mariadb
sudo systemctl enable mariadb

# Import database schema
mysql -u root -p < its-projekt18.6.sql

# Verify installation
mysql -u notizuser -pnotizpassword -e "SHOW DATABASES;"
```

#### CentOS/RHEL/Rocky Linux
```bash
# Update system packages
sudo dnf update -y

# Install MariaDB
sudo dnf install mariadb-server mariadb -y

# Start and enable MariaDB
sudo systemctl start mariadb
sudo systemctl enable mariadb

# Secure MariaDB installation
sudo mysql_secure_installation

# Import database schema
mysql -u root -p < its-projekt18.6.sql
```

### Option 2: Docker Database Setup

```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Create persistent data directory
sudo mkdir -p /opt/mariadb/data

# Run MariaDB container
docker run -d \
  --name mariadb-production \
  --restart unless-stopped \
  -e MYSQL_ROOT_PASSWORD=your_secure_root_password \
  -e MYSQL_DATABASE=notizprojekt \
  -e MYSQL_USER=notizuser \
  -e MYSQL_PASSWORD=your_secure_password \
  -v /opt/mariadb/data:/var/lib/mysql \
  -p 3306:3306 \
  mariadb:10.11

# Wait for container to start
sleep 30

# Import database schema
docker exec -i mariadb-production mysql -u root -pyour_secure_root_password < its-projekt18.6.sql
```

### Option 3: Managed Database Service

#### AWS RDS
```bash
# Create RDS MySQL instance via AWS CLI
aws rds create-db-instance \
  --db-instance-identifier notizprojekt-db \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --master-username admin \
  --master-user-password your_secure_password \
  --allocated-storage 20 \
  --vpc-security-group-ids sg-xxxxxxxxx \
  --db-name notizprojekt
```

#### DigitalOcean Managed Database
```bash
# Create via DigitalOcean CLI
doctl databases create notizprojekt-db \
  --engine mysql \
  --size db-s-1vcpu-1gb \
  --region nyc1
```

### Database Security Configuration

```sql
-- Connect as root and secure the database
mysql -u root -p

-- Create application user with limited privileges
CREATE USER 'notizuser'@'localhost' IDENTIFIED BY 'your_very_secure_password';
CREATE USER 'notizuser'@'%' IDENTIFIED BY 'your_very_secure_password';

-- Grant only necessary privileges
GRANT SELECT, INSERT, UPDATE, DELETE ON notizprojekt.* TO 'notizuser'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON notizprojekt.* TO 'notizuser'@'%';

-- Remove test databases and users
DROP DATABASE IF EXISTS test;
DELETE FROM mysql.user WHERE User='';
DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');

FLUSH PRIVILEGES;
```

## 🚀 Application Deployment

### Step 1: Install Java

#### Ubuntu/Debian
```bash
# Install OpenJDK 11
sudo apt install openjdk-11-jdk -y

# Verify installation
java -version
javac -version
```

#### CentOS/RHEL/Rocky Linux
```bash
# Install OpenJDK 11
sudo dnf install java-11-openjdk java-11-openjdk-devel -y

# Set JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-11-openjdk' >> ~/.bashrc
source ~/.bashrc
```

### Step 2: Create Application User

```bash
# Create dedicated user for the application
sudo useradd -r -m -U -d /opt/notizprojekt -s /bin/bash notizapp

# Create necessary directories
sudo mkdir -p /opt/notizprojekt/{app,logs,uploads,config}
sudo chown -R notizapp:notizapp /opt/notizprojekt
```

### Step 3: Deploy Application

```bash
# Switch to application user
sudo su - notizapp

# Clone the repository
cd /opt/notizprojekt
git clone https://github.com/PythonTilk/ITS-Projekt.git app
cd app

# Switch to the web application branch
git checkout html

# Build the application
./mvnw clean package -DskipTests

# Copy the JAR file
cp target/notizprojekt-web-*.jar /opt/notizprojekt/notizprojekt-web.jar
```

### Step 4: Production Configuration

Create production configuration file:

```bash
# Create production properties file
sudo nano /opt/notizprojekt/config/application-production.properties
```

```properties
# Production Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/notizprojekt?useSSL=true&requireSSL=true&serverTimezone=UTC
spring.datasource.username=notizuser
spring.datasource.password=your_very_secure_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Connection Pool Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Server Configuration
server.port=8080
server.address=127.0.0.1
server.compression.enabled=true
server.compression.mime-types=text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json
server.compression.min-response-size=1024

# Security Configuration
spring.security.user.name=admin
spring.security.user.password=your_admin_password_here

# Thymeleaf Configuration (disable cache in production)
spring.thymeleaf.cache=true

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
spring.servlet.multipart.enabled=true
spring.servlet.multipart.location=/opt/notizprojekt/uploads

# Static Resources
spring.web.resources.static-locations=classpath:/static/,file:/opt/notizprojekt/uploads/
spring.web.resources.cache.period=31536000

# Logging Configuration
logging.level.org.springframework.web=WARN
logging.level.org.hibernate=WARN
logging.level.notizprojekt=INFO
logging.file.name=/opt/notizprojekt/logs/application.log
logging.file.max-size=10MB
logging.file.max-history=30

# Actuator Configuration (for monitoring)
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
management.endpoints.web.base-path=/actuator

# Session Configuration
server.servlet.session.timeout=30m
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.same-site=strict
```

### Step 5: Create Systemd Service

```bash
# Create systemd service file
sudo nano /etc/systemd/system/notizprojekt.service
```

```ini
[Unit]
Description=ITS-Projekt Note-Taking Application
After=network.target mariadb.service
Wants=mariadb.service

[Service]
Type=simple
User=notizapp
Group=notizapp
WorkingDirectory=/opt/notizprojekt
ExecStart=/usr/bin/java -Xms512m -Xmx2g -Dspring.profiles.active=production -Dspring.config.location=file:/opt/notizprojekt/config/application-production.properties -jar /opt/notizprojekt/notizprojekt-web.jar
ExecStop=/bin/kill -15 $MAINPID
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=notizprojekt

# Security settings
NoNewPrivileges=yes
PrivateTmp=yes
ProtectSystem=strict
ProtectHome=yes
ReadWritePaths=/opt/notizprojekt/logs /opt/notizprojekt/uploads

[Install]
WantedBy=multi-user.target
```

### Step 6: Start and Enable Service

```bash
# Reload systemd configuration
sudo systemctl daemon-reload

# Enable and start the service
sudo systemctl enable notizprojekt
sudo systemctl start notizprojekt

# Check service status
sudo systemctl status notizprojekt

# View logs
sudo journalctl -u notizprojekt -f
```

## 🌐 Web Server Configuration

### Nginx Configuration (Recommended)

#### Install Nginx

```bash
# Ubuntu/Debian
sudo apt install nginx -y

# CentOS/RHEL/Rocky Linux
sudo dnf install nginx -y

# Start and enable Nginx
sudo systemctl start nginx
sudo systemctl enable nginx
```

#### Create Nginx Configuration

```bash
# Create site configuration
sudo nano /etc/nginx/sites-available/notizprojekt
```

```nginx
# ITS-Projekt Nginx Configuration
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
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;
    
    # Security Headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    add_header Content-Security-Policy "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: blob:; font-src 'self'; connect-src 'self'; media-src 'self'; object-src 'none'; child-src 'none'; frame-src 'none'; worker-src 'none'; frame-ancestors 'none'; form-action 'self'; base-uri 'self';" always;
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;
    
    # Gzip Compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types
        text/plain
        text/css
        text/xml
        text/javascript
        application/json
        application/javascript
        application/xml+rss
        application/atom+xml
        image/svg+xml;
    
    # Client body size (for file uploads)
    client_max_body_size 50M;
    
    # Proxy settings
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header X-Forwarded-Host $host;
    proxy_set_header X-Forwarded-Port $server_port;
    
    # Main application
    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }
    
    # Static files (uploads)
    location /uploads/ {
        alias /opt/notizprojekt/uploads/;
        expires 1y;
        add_header Cache-Control "public, immutable";
        access_log off;
    }
    
    # Health check endpoint
    location /actuator/health {
        proxy_pass http://127.0.0.1:8080/actuator/health;
        access_log off;
    }
    
    # Deny access to sensitive files
    location ~ /\. {
        deny all;
        access_log off;
        log_not_found off;
    }
    
    location ~ \.(sql|conf|config)$ {
        deny all;
        access_log off;
        log_not_found off;
    }
}
```

#### Enable Site Configuration

```bash
# Enable the site
sudo ln -s /etc/nginx/sites-available/notizprojekt /etc/nginx/sites-enabled/

# Remove default site
sudo rm -f /etc/nginx/sites-enabled/default

# Test Nginx configuration
sudo nginx -t

# Restart Nginx
sudo systemctl restart nginx
```

### Apache Configuration (Alternative)

```bash
# Install Apache
sudo apt install apache2 -y

# Enable required modules
sudo a2enmod proxy proxy_http ssl rewrite headers

# Create virtual host
sudo nano /etc/apache2/sites-available/notizprojekt.conf
```

```apache
<VirtualHost *:80>
    ServerName your-domain.com
    ServerAlias www.your-domain.com
    
    # Redirect to HTTPS
    RewriteEngine On
    RewriteCond %{HTTPS} off
    RewriteRule ^(.*)$ https://%{HTTP_HOST}%{REQUEST_URI} [R=301,L]
</VirtualHost>

<VirtualHost *:443>
    ServerName your-domain.com
    ServerAlias www.your-domain.com
    
    # SSL Configuration
    SSLEngine on
    SSLCertificateFile /etc/letsencrypt/live/your-domain.com/fullchain.pem
    SSLCertificateKeyFile /etc/letsencrypt/live/your-domain.com/privkey.pem
    
    # Security Headers
    Header always set X-Frame-Options "SAMEORIGIN"
    Header always set X-Content-Type-Options "nosniff"
    Header always set X-XSS-Protection "1; mode=block"
    Header always set Strict-Transport-Security "max-age=31536000; includeSubDomains; preload"
    
    # Proxy Configuration
    ProxyPreserveHost On
    ProxyPass /uploads/ !
    ProxyPass / http://127.0.0.1:8080/
    ProxyPassReverse / http://127.0.0.1:8080/
    
    # Static files
    Alias /uploads /opt/notizprojekt/uploads
    <Directory "/opt/notizprojekt/uploads">
        Require all granted
        ExpiresActive On
        ExpiresDefault "access plus 1 year"
    </Directory>
</VirtualHost>
```

## 🔒 Security Configuration

### Firewall Setup

#### UFW (Ubuntu/Debian)
```bash
# Enable UFW
sudo ufw enable

# Allow SSH (adjust port if needed)
sudo ufw allow 22/tcp

# Allow HTTP and HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Allow MySQL only from localhost (if using local database)
sudo ufw allow from 127.0.0.1 to any port 3306

# Check status
sudo ufw status verbose
```

#### Firewalld (CentOS/RHEL/Rocky Linux)
```bash
# Enable firewalld
sudo systemctl enable --now firewalld

# Allow HTTP and HTTPS
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https

# Allow SSH
sudo firewall-cmd --permanent --add-service=ssh

# Reload firewall
sudo firewall-cmd --reload

# Check status
sudo firewall-cmd --list-all
```

### Fail2Ban Setup

```bash
# Install Fail2Ban
sudo apt install fail2ban -y  # Ubuntu/Debian
sudo dnf install fail2ban -y  # CentOS/RHEL/Rocky Linux

# Create custom configuration
sudo nano /etc/fail2ban/jail.local
```

```ini
[DEFAULT]
bantime = 3600
findtime = 600
maxretry = 5
backend = systemd

[sshd]
enabled = true
port = ssh
logpath = %(sshd_log)s

[nginx-http-auth]
enabled = true
filter = nginx-http-auth
port = http,https
logpath = /var/log/nginx/error.log

[nginx-limit-req]
enabled = true
filter = nginx-limit-req
port = http,https
logpath = /var/log/nginx/error.log
maxretry = 10
```

```bash
# Start and enable Fail2Ban
sudo systemctl enable --now fail2ban

# Check status
sudo fail2ban-client status
```

### System Updates and Security

```bash
# Set up automatic security updates (Ubuntu/Debian)
sudo apt install unattended-upgrades -y
sudo dpkg-reconfigure -plow unattended-upgrades

# Configure automatic updates
sudo nano /etc/apt/apt.conf.d/50unattended-upgrades
```

```
Unattended-Upgrade::Allowed-Origins {
    "${distro_id}:${distro_codename}-security";
    "${distro_id}ESMApps:${distro_codename}-apps-security";
    "${distro_id}ESM:${distro_codename}-infra-security";
};

Unattended-Upgrade::AutoFixInterruptedDpkg "true";
Unattended-Upgrade::MinimalSteps "true";
Unattended-Upgrade::Remove-Unused-Dependencies "true";
Unattended-Upgrade::Automatic-Reboot "false";
```

## 🔐 SSL/HTTPS Setup

### Let's Encrypt with Certbot

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y  # For Nginx
sudo apt install certbot python3-certbot-apache -y  # For Apache

# Obtain SSL certificate
sudo certbot --nginx -d your-domain.com -d www.your-domain.com

# Test automatic renewal
sudo certbot renew --dry-run

# Set up automatic renewal
echo "0 12 * * * /usr/bin/certbot renew --quiet" | sudo crontab -
```

### Manual SSL Certificate Setup

If using a commercial SSL certificate:

```bash
# Create SSL directory
sudo mkdir -p /etc/ssl/certs/notizprojekt
sudo mkdir -p /etc/ssl/private/notizprojekt

# Copy certificate files
sudo cp your-certificate.crt /etc/ssl/certs/notizprojekt/
sudo cp your-private-key.key /etc/ssl/private/notizprojekt/
sudo cp ca-bundle.crt /etc/ssl/certs/notizprojekt/

# Set proper permissions
sudo chmod 644 /etc/ssl/certs/notizprojekt/*
sudo chmod 600 /etc/ssl/private/notizprojekt/*
```

## 📊 Monitoring and Logging

### Log Management

```bash
# Configure log rotation
sudo nano /etc/logrotate.d/notizprojekt
```

```
/opt/notizprojekt/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 notizapp notizapp
    postrotate
        systemctl reload notizprojekt
    endscript
}
```

### System Monitoring

#### Install and Configure Netdata (Optional)

```bash
# Install Netdata
bash <(curl -Ss https://my-netdata.io/kickstart.sh)

# Configure Netdata
sudo nano /etc/netdata/netdata.conf
```

```ini
[global]
    bind socket to IP = 127.0.0.1
    default port = 19999
    
[web]
    allow connections from = localhost 127.0.0.1
```

#### Basic Monitoring Script

```bash
# Create monitoring script
sudo nano /opt/notizprojekt/monitor.sh
```

```bash
#!/bin/bash

# ITS-Projekt Monitoring Script
LOG_FILE="/opt/notizprojekt/logs/monitor.log"
DATE=$(date '+%Y-%m-%d %H:%M:%S')

# Check if application is running
if ! systemctl is-active --quiet notizprojekt; then
    echo "[$DATE] ERROR: Application is not running" >> $LOG_FILE
    systemctl restart notizprojekt
fi

# Check database connection
if ! mysql -u notizuser -pnotizpassword -e "SELECT 1" notizprojekt &>/dev/null; then
    echo "[$DATE] ERROR: Database connection failed" >> $LOG_FILE
fi

# Check disk space
DISK_USAGE=$(df /opt/notizprojekt | awk 'NR==2 {print $5}' | sed 's/%//')
if [ $DISK_USAGE -gt 80 ]; then
    echo "[$DATE] WARNING: Disk usage is ${DISK_USAGE}%" >> $LOG_FILE
fi

# Check memory usage
MEMORY_USAGE=$(free | awk 'NR==2{printf "%.2f", $3*100/$2}')
if (( $(echo "$MEMORY_USAGE > 90" | bc -l) )); then
    echo "[$DATE] WARNING: Memory usage is ${MEMORY_USAGE}%" >> $LOG_FILE
fi

echo "[$DATE] INFO: Health check completed" >> $LOG_FILE
```

```bash
# Make script executable
sudo chmod +x /opt/notizprojekt/monitor.sh

# Add to crontab
echo "*/5 * * * * /opt/notizprojekt/monitor.sh" | sudo crontab -u notizapp -
```

## 💾 Backup Strategy

### Database Backup

```bash
# Create backup script
sudo nano /opt/notizprojekt/backup-db.sh
```

```bash
#!/bin/bash

# Database backup script
BACKUP_DIR="/opt/notizprojekt/backups"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="notizprojekt"
DB_USER="notizuser"
DB_PASS="your_secure_password"

# Create backup directory
mkdir -p $BACKUP_DIR

# Create database backup
mysqldump -u $DB_USER -p$DB_PASS $DB_NAME > $BACKUP_DIR/notizprojekt_$DATE.sql

# Compress backup
gzip $BACKUP_DIR/notizprojekt_$DATE.sql

# Remove backups older than 30 days
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete

echo "Database backup completed: notizprojekt_$DATE.sql.gz"
```

### Application Backup

```bash
# Create application backup script
sudo nano /opt/notizprojekt/backup-app.sh
```

```bash
#!/bin/bash

# Application backup script
BACKUP_DIR="/opt/notizprojekt/backups"
DATE=$(date +%Y%m%d_%H%M%S)
APP_DIR="/opt/notizprojekt"

# Create backup directory
mkdir -p $BACKUP_DIR

# Backup uploads directory
tar -czf $BACKUP_DIR/uploads_$DATE.tar.gz -C $APP_DIR uploads/

# Backup configuration
tar -czf $BACKUP_DIR/config_$DATE.tar.gz -C $APP_DIR config/

# Remove backups older than 30 days
find $BACKUP_DIR -name "*.tar.gz" -mtime +30 -delete

echo "Application backup completed: uploads_$DATE.tar.gz, config_$DATE.tar.gz"
```

### Automated Backup Schedule

```bash
# Make scripts executable
sudo chmod +x /opt/notizprojekt/backup-*.sh

# Add to crontab for daily backups
sudo crontab -u notizapp -e
```

```cron
# Daily database backup at 2 AM
0 2 * * * /opt/notizprojekt/backup-db.sh

# Daily application backup at 3 AM
0 3 * * * /opt/notizprojekt/backup-app.sh
```

### Remote Backup (Optional)

```bash
# Install rclone for cloud backups
curl https://rclone.org/install.sh | sudo bash

# Configure rclone (follow interactive setup)
rclone config

# Create remote backup script
sudo nano /opt/notizprojekt/backup-remote.sh
```

```bash
#!/bin/bash

# Remote backup script
LOCAL_BACKUP_DIR="/opt/notizprojekt/backups"
REMOTE_NAME="your-cloud-storage"  # Name from rclone config
REMOTE_PATH="notizprojekt-backups"

# Sync backups to cloud storage
rclone sync $LOCAL_BACKUP_DIR $REMOTE_NAME:$REMOTE_PATH

echo "Remote backup sync completed"
```

## 🔧 Troubleshooting

### Common Issues and Solutions

#### Application Won't Start

```bash
# Check service status
sudo systemctl status notizprojekt

# Check logs
sudo journalctl -u notizprojekt -n 50

# Check Java process
ps aux | grep java

# Check port availability
sudo netstat -tlnp | grep 8080
```

#### Database Connection Issues

```bash
# Test database connection
mysql -u notizuser -pnotizpassword -h localhost notizprojekt

# Check database service
sudo systemctl status mariadb

# Check database logs
sudo tail -f /var/log/mysql/error.log
```

#### High Memory Usage

```bash
# Check memory usage
free -h
top -p $(pgrep java)

# Adjust JVM memory settings in systemd service
sudo systemctl edit notizprojekt
```

```ini
[Service]
ExecStart=
ExecStart=/usr/bin/java -Xms256m -Xmx1g -Dspring.profiles.active=production -Dspring.config.location=file:/opt/notizprojekt/config/application-production.properties -jar /opt/notizprojekt/notizprojekt-web.jar
```

#### SSL Certificate Issues

```bash
# Check certificate validity
sudo certbot certificates

# Renew certificate manually
sudo certbot renew

# Test SSL configuration
openssl s_client -connect your-domain.com:443 -servername your-domain.com
```

#### File Upload Issues

```bash
# Check upload directory permissions
ls -la /opt/notizprojekt/uploads/

# Fix permissions if needed
sudo chown -R notizapp:notizapp /opt/notizprojekt/uploads/
sudo chmod -R 755 /opt/notizprojekt/uploads/

# Check disk space
df -h /opt/notizprojekt/
```

### Performance Optimization

#### Database Optimization

```sql
-- Connect to MySQL and optimize
mysql -u root -p

-- Optimize tables
USE notizprojekt;
OPTIMIZE TABLE nutzer, notiz, geteilte_notizen;

-- Check slow queries
SHOW VARIABLES LIKE 'slow_query_log';
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;
```

#### Application Performance

```bash
# Enable JVM performance monitoring
sudo nano /etc/systemd/system/notizprojekt.service
```

Add JVM options:
```
-XX:+UseG1GC -XX:+UseStringDeduplication -XX:+OptimizeStringConcat
```

#### Nginx Optimization

```nginx
# Add to nginx.conf
worker_processes auto;
worker_connections 1024;

# Enable caching
location ~* \.(jpg|jpeg|png|gif|ico|css|js)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

### Health Check Endpoints

Create a simple health check script:

```bash
# Create health check script
sudo nano /opt/notizprojekt/health-check.sh
```

```bash
#!/bin/bash

# Health check script
APP_URL="http://localhost:8080/actuator/health"
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $APP_URL)

if [ $RESPONSE -eq 200 ]; then
    echo "Application is healthy"
    exit 0
else
    echo "Application health check failed (HTTP $RESPONSE)"
    exit 1
fi
```

## 📞 Support and Maintenance

### Regular Maintenance Tasks

#### Weekly Tasks
- Review application logs
- Check disk space usage
- Verify backup integrity
- Update system packages

#### Monthly Tasks
- Review security logs
- Update SSL certificates (if needed)
- Database optimization
- Performance review

#### Quarterly Tasks
- Security audit
- Dependency updates
- Backup strategy review
- Disaster recovery testing

### Emergency Procedures

#### Application Recovery

```bash
# Stop application
sudo systemctl stop notizprojekt

# Restore from backup
sudo -u notizapp cp /opt/notizprojekt/backups/notizprojekt-web-backup.jar /opt/notizprojekt/notizprojekt-web.jar

# Start application
sudo systemctl start notizprojekt
```

#### Database Recovery

```bash
# Stop application
sudo systemctl stop notizprojekt

# Restore database
zcat /opt/notizprojekt/backups/notizprojekt_YYYYMMDD_HHMMSS.sql.gz | mysql -u root -p notizprojekt

# Start application
sudo systemctl start notizprojekt
```

## 🎯 Final Checklist

Before going live, ensure:

- [ ] Database is properly secured and backed up
- [ ] Application is running as a non-root user
- [ ] SSL certificate is installed and configured
- [ ] Firewall is properly configured
- [ ] Monitoring and logging are set up
- [ ] Backup strategy is implemented and tested
- [ ] Security headers are configured
- [ ] Performance optimization is applied
- [ ] Health checks are working
- [ ] Documentation is updated

## 📚 Additional Resources

- [Spring Boot Production Best Practices](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)
- [Nginx Security Best Practices](https://nginx.org/en/docs/http/securing_http.html)
- [MySQL Security Best Practices](https://dev.mysql.com/doc/refman/8.0/en/security-guidelines.html)
- [Let's Encrypt Documentation](https://letsencrypt.org/docs/)
- [Fail2Ban Documentation](https://www.fail2ban.org/wiki/index.php/Main_Page)

---

**Note**: Replace `your-domain.com`, `your_secure_password`, and other placeholder values with your actual configuration values before deployment.

This guide provides a comprehensive setup for production deployment. Always test in a staging environment before deploying to production.
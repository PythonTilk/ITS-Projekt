# ITS-Projekt Server Setup Guide

This guide provides comprehensive instructions for setting up the ITS-Projekt note-taking application on a production server with full HTTPS support, reverse proxy, and security configurations.

## Table of Contents

1. [Quick Setup (Automated)](#quick-setup-automated)
2. [Manual Setup](#manual-setup)
3. [Configuration Options](#configuration-options)
4. [Production Deployment](#production-deployment)
5. [Security Considerations](#security-considerations)
6. [Troubleshooting](#troubleshooting)
7. [Maintenance](#maintenance)

## Quick Setup (Automated)

### Prerequisites

- Ubuntu 20.04+ or Debian 10+ server
- Root access (sudo privileges)
- Domain name pointing to your server IP (for SSL)
- Internet connection

### One-Command Setup

For the default setup with `notes.tilk.tech` domain:

```bash
curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/setup.sh | sudo bash
```

For a custom domain:

```bash
curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/setup.sh | sudo DOMAIN="your-domain.com" EMAIL="admin@your-domain.com" bash
```

### Local Setup Script

If you prefer to download and review the script first:

```bash
# Download the setup script
wget https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/setup.sh

# Make it executable
chmod +x setup.sh

# Run with default settings
sudo ./setup.sh

# Or with custom settings
sudo DOMAIN="your-domain.com" EMAIL="admin@your-domain.com" ./setup.sh
```

## Configuration Options

The setup script supports several environment variables for customization:

| Variable | Default | Description |
|----------|---------|-------------|
| `DOMAIN` | `notes.tilk.tech` | Domain name for the application |
| `EMAIL` | `admin@tilk.tech` | Email for SSL certificate registration |
| `APP_PORT` | `12000` | Internal application port |
| `SETUP_NGINX` | `true` | Install and configure Nginx reverse proxy |
| `SETUP_SSL` | `true` | Setup SSL certificates with Let's Encrypt |
| `DB_PASSWORD` | Auto-generated | Database password (32-character random) |

### Examples

```bash
# Minimal setup without SSL (development)
sudo SETUP_SSL=false ./setup.sh

# Custom domain with specific port
sudo DOMAIN="notes.example.com" APP_PORT="8080" ./setup.sh

# Skip Nginx (direct access only)
sudo SETUP_NGINX=false ./setup.sh
```

## Manual Setup

If you prefer to set up the components manually, follow these steps:

### 1. System Update

```bash
sudo apt update && sudo apt upgrade -y
```

### 2. Install Java 17

```bash
sudo apt install -y openjdk-17-jdk
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
```

### 3. Install MariaDB

```bash
sudo apt install -y mariadb-server mariadb-client

# Secure installation
sudo mysql_secure_installation

# Create database and user
sudo mysql -u root -p << EOF
CREATE DATABASE notizprojekt;
CREATE USER 'notizuser'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON notizprojekt.* TO 'notizuser'@'localhost';
FLUSH PRIVILEGES;
EXIT;
EOF
```

### 4. Install Nginx (Optional)

```bash
sudo apt install -y nginx
sudo systemctl start nginx
sudo systemctl enable nginx
```

### 5. Deploy Application

```bash
# Create application directory
sudo mkdir -p /opt/notizprojekt
cd /opt/notizprojekt

# Clone repository
sudo git clone https://github.com/PythonTilk/ITS-Projekt.git .
sudo git checkout html

# Build application
sudo chmod +x mvnw
sudo ./mvnw clean package -DskipTests

# Create uploads directory
sudo mkdir -p uploads
sudo chmod 755 uploads
```

### 6. Configure Application

Create `/opt/notizprojekt/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/notizprojekt?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=notizuser
spring.datasource.password=your_secure_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Server Configuration
server.port=12000
server.address=0.0.0.0

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
spring.web.resources.static-locations=classpath:/static/,file:/opt/notizprojekt/uploads/

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Logging Configuration
logging.level.org.springframework.web=WARN
logging.level.org.hibernate=WARN
logging.level.notizprojekt=INFO
```

### 7. Create Systemd Service

Create `/etc/systemd/system/notizprojekt.service`:

```ini
[Unit]
Description=ITS-Projekt Note Taking Application
After=network.target mariadb.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/notizprojekt
ExecStart=/usr/bin/java -jar /opt/notizprojekt/target/notizprojekt-1.0.jar --spring.config.location=file:/opt/notizprojekt/application.properties
Restart=always
RestartSec=10

# Environment
Environment=JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Security
NoNewPrivileges=true
PrivateTmp=true

[Install]
WantedBy=multi-user.target
```

Enable and start the service:

```bash
sudo systemctl daemon-reload
sudo systemctl enable notizprojekt
sudo systemctl start notizprojekt
```

## Production Deployment

### Nginx Reverse Proxy Configuration

Create `/etc/nginx/sites-available/notizprojekt`:

```nginx
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;

    # Proxy settings
    location / {
        proxy_pass http://localhost:12000;
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
    }

    # Handle file uploads
    location /api/notes/upload {
        proxy_pass http://localhost:12000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Increase upload size limits
        client_max_body_size 50M;
        proxy_request_buffering off;
    }

    # Static files caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        proxy_pass http://localhost:12000;
        proxy_set_header Host $host;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

Enable the site:

```bash
sudo ln -s /etc/nginx/sites-available/notizprojekt /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl reload nginx
```

### SSL/HTTPS Setup with Let's Encrypt

Install Certbot:

```bash
sudo apt install -y snapd
sudo snap install core
sudo snap refresh core
sudo snap install --classic certbot
sudo ln -s /snap/bin/certbot /usr/bin/certbot
```

Obtain SSL certificate:

```bash
sudo certbot --nginx -d your-domain.com -d www.your-domain.com
```

Test automatic renewal:

```bash
sudo certbot renew --dry-run
```

### Firewall Configuration

```bash
# Install UFW if not already installed
sudo apt install -y ufw

# Allow SSH
sudo ufw allow 22/tcp

# Allow HTTP and HTTPS
sudo ufw allow 'Nginx Full'

# Enable firewall
sudo ufw --force enable

# Check status
sudo ufw status
```

## Security Considerations

### Database Security

1. **Strong Passwords**: Use strong, randomly generated passwords for database users
2. **Limited Privileges**: Grant only necessary privileges to application users
3. **Regular Updates**: Keep MariaDB updated with security patches

### Application Security

1. **File Uploads**: Configure appropriate file size limits and validation
2. **HTTPS Only**: Always use HTTPS in production
3. **Security Headers**: Implement security headers via Nginx
4. **Regular Updates**: Keep Java and application dependencies updated

### Server Security

1. **Firewall**: Use UFW to restrict access to necessary ports only
2. **SSH Security**: Disable password authentication, use key-based authentication
3. **Regular Updates**: Keep the operating system updated
4. **Monitoring**: Implement log monitoring and intrusion detection

### Recommended Additional Security Measures

```bash
# Disable password authentication for SSH
sudo sed -i 's/#PasswordAuthentication yes/PasswordAuthentication no/' /etc/ssh/sshd_config
sudo systemctl restart ssh

# Install fail2ban for intrusion prevention
sudo apt install -y fail2ban

# Configure automatic security updates
sudo apt install -y unattended-upgrades
sudo dpkg-reconfigure -plow unattended-upgrades
```

## Troubleshooting

### Common Issues

#### Application Won't Start

Check the service status and logs:

```bash
sudo systemctl status notizprojekt
sudo journalctl -u notizprojekt -f
```

Common causes:
- Java not installed or wrong version
- Database connection issues
- Port already in use
- Missing application.properties file

#### Database Connection Issues

Test database connectivity:

```bash
mysql -u notizuser -p notizprojekt
```

Check MariaDB status:

```bash
sudo systemctl status mariadb
sudo journalctl -u mariadb -f
```

#### Nginx Issues

Test Nginx configuration:

```bash
sudo nginx -t
```

Check Nginx status:

```bash
sudo systemctl status nginx
sudo journalctl -u nginx -f
```

#### SSL Certificate Issues

Check certificate status:

```bash
sudo certbot certificates
```

Test renewal:

```bash
sudo certbot renew --dry-run
```

### Log Locations

- **Application Logs**: `sudo journalctl -u notizprojekt -f`
- **Nginx Access Logs**: `/var/log/nginx/access.log`
- **Nginx Error Logs**: `/var/log/nginx/error.log`
- **MariaDB Logs**: `/var/log/mysql/error.log`
- **System Logs**: `/var/log/syslog`

### Performance Tuning

#### Java Application

Add JVM options to the systemd service:

```ini
ExecStart=/usr/bin/java -Xmx2g -Xms1g -XX:+UseG1GC -jar /opt/notizprojekt/target/notizprojekt-1.0.jar
```

#### MariaDB

Edit `/etc/mysql/mariadb.conf.d/50-server.cnf`:

```ini
[mysqld]
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
max_connections = 200
```

#### Nginx

Edit `/etc/nginx/nginx.conf`:

```nginx
worker_processes auto;
worker_connections 1024;
keepalive_timeout 65;
client_max_body_size 50M;
```

## Maintenance

### Regular Tasks

#### Daily
- Monitor application logs for errors
- Check disk space usage
- Verify application accessibility

#### Weekly
- Review security logs
- Check for system updates
- Monitor database performance

#### Monthly
- Update application dependencies
- Review and rotate logs
- Test backup and restore procedures

### Backup Strategy

#### Database Backup

Create automated backup script `/opt/scripts/backup-db.sh`:

```bash
#!/bin/bash
BACKUP_DIR="/opt/backups"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="notizprojekt"
DB_USER="notizuser"
DB_PASS="your_password"

mkdir -p $BACKUP_DIR
mysqldump -u $DB_USER -p$DB_PASS $DB_NAME > $BACKUP_DIR/notizprojekt_$DATE.sql
gzip $BACKUP_DIR/notizprojekt_$DATE.sql

# Keep only last 30 days of backups
find $BACKUP_DIR -name "notizprojekt_*.sql.gz" -mtime +30 -delete
```

Add to crontab:

```bash
# Daily database backup at 2 AM
0 2 * * * /opt/scripts/backup-db.sh
```

#### Application Backup

```bash
# Backup application files and uploads
tar -czf /opt/backups/app_$(date +%Y%m%d).tar.gz /opt/notizprojekt/uploads /opt/notizprojekt/application.properties
```

### Updates

#### Application Updates

```bash
cd /opt/notizprojekt
sudo git pull origin html
sudo ./mvnw clean package -DskipTests
sudo systemctl restart notizprojekt
```

#### System Updates

```bash
sudo apt update && sudo apt upgrade -y
sudo systemctl restart notizprojekt  # If Java was updated
```

### Monitoring

#### Health Check Script

Create `/opt/scripts/health-check.sh`:

```bash
#!/bin/bash
APP_URL="https://your-domain.com"
STATUS=$(curl -s -o /dev/null -w "%{http_code}" $APP_URL)

if [ $STATUS -eq 200 ]; then
    echo "$(date): Application is healthy (HTTP $STATUS)"
else
    echo "$(date): Application is down (HTTP $STATUS)"
    # Send alert (email, Slack, etc.)
fi
```

#### Resource Monitoring

```bash
# Check disk usage
df -h

# Check memory usage
free -h

# Check CPU usage
top

# Check application process
ps aux | grep java

# Check network connections
netstat -tulpn | grep :12000
```

## Support and Documentation

### Default Login Credentials

After setup, you can log in with these test accounts:
- Username: `testuser1`, Password: `password123`
- Username: `testuser2`, Password: `password123`

**Important**: Change these passwords in production!

### Useful Commands

```bash
# Application management
sudo systemctl start notizprojekt
sudo systemctl stop notizprojekt
sudo systemctl restart notizprojekt
sudo systemctl status notizprojekt

# View logs
sudo journalctl -u notizprojekt -f
sudo journalctl -u notizprojekt --since "1 hour ago"

# Nginx management
sudo systemctl reload nginx
sudo nginx -t

# SSL certificate management
sudo certbot renew
sudo certbot certificates

# Database access
mysql -u notizuser -p notizprojekt
```

### Getting Help

If you encounter issues:

1. Check the troubleshooting section above
2. Review application and system logs
3. Verify all services are running
4. Check firewall and network configuration
5. Consult the project repository: https://github.com/PythonTilk/ITS-Projekt

For additional support, please create an issue in the GitHub repository with:
- Operating system and version
- Error messages and logs
- Steps to reproduce the issue
- Configuration details (without sensitive information)
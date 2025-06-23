# Server Setup Guide

This guide provides comprehensive instructions for setting up the ITS-Projekt note-taking application on a server for production usage with full automation.

## 🚀 Quick Start (Recommended)

For a fully automated production setup with Nginx reverse proxy and SSL:

```bash
sudo bash setup.sh
```

Or download and run directly:

```bash
curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/setup.sh | sudo bash
```

### Custom Domain Setup

To set up with your own domain:

```bash
DOMAIN="notes.example.com" EMAIL="admin@example.com" sudo bash setup.sh
```

### Environment Variables

The setup script supports these environment variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `DOMAIN` | `notes.tilk.tech` | Your domain name |
| `EMAIL` | `admin@tilk.tech` | Email for SSL certificates |
| `DB_PASSWORD` | `notizpassword` | Database root password |
| `APP_PORT` | `12000` | Application port (internal) |
| `SETUP_NGINX` | `true` | Install Nginx reverse proxy |
| `SETUP_SSL` | `true` | Setup SSL with Let's Encrypt |

## 📋 Prerequisites

- **Operating System**: Ubuntu 20.04+ or Debian 11+
- **Access**: Root or sudo privileges
- **Resources**: Minimum 2GB RAM, 10GB disk space
- **Network**: Internet connection and domain pointing to server
- **DNS**: Domain should resolve to your server IP (for SSL)

## 🔧 What the Setup Script Does

The automated production setup script performs:

### Core Installation
1. **System Updates**: Updates all packages to latest versions
2. **Java 11**: Installs OpenJDK 11 with proper JAVA_HOME configuration
3. **MariaDB**: Installs and configures database server
4. **Database Schema**: Downloads and imports the application database
5. **Application Build**: Clones repository and builds the Spring Boot application

### Production Features
6. **Nginx Reverse Proxy**: Configures professional web server with:
   - Security headers (XSS protection, content type options, etc.)
   - File upload optimization (50MB limit)
   - Static file caching
   - WebSocket support
7. **SSL/HTTPS**: Automatic Let's Encrypt certificate with:
   - Domain validation
   - Automatic renewal setup
   - HTTPS redirect
8. **Systemd Service**: Auto-start service with proper logging
9. **Firewall**: UFW configuration for HTTP/HTTPS access
10. **Security**: Production-ready security configurations

## 🌐 Production Architecture

```
Internet → Nginx (Port 80/443) → Java App (Port 12000) → MariaDB (Port 3306)
```

- **Public Access**: HTTPS via domain name
- **Security**: SSL encryption, security headers, firewall
- **Performance**: Nginx caching, optimized proxy settings
- **Reliability**: Systemd auto-restart, proper logging

## 📖 Manual Setup Instructions

If you need to set up manually or troubleshoot:

### 1. System Preparation

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y software-properties-common curl wget git
```

### 2. Install Java 11

```bash
sudo apt install -y openjdk-11-jdk
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
echo 'export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64' >> ~/.bashrc
```

### 3. Install and Configure MariaDB

```bash
sudo apt install -y mariadb-server mariadb-client
sudo systemctl start mariadb
sudo systemctl enable mariadb

# Set root password
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_password';"

# Import database schema
wget https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/its-projekt18.6.sql
sudo mysql -u root -p < its-projekt18.6.sql
```

### 4. Deploy Application

```bash
sudo mkdir -p /opt/notizprojekt
cd /opt/notizprojekt
sudo git clone https://github.com/PythonTilk/ITS-Projekt.git .
sudo git checkout html
sudo chmod +x mvnw
sudo ./mvnw clean package -DskipTests
sudo mkdir -p uploads && sudo chmod 755 uploads
```

### 5. Install and Configure Nginx

```bash
sudo apt install -y nginx

# Create site configuration
sudo tee /etc/nginx/sites-available/notizprojekt << 'EOF'
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;

    location / {
        proxy_pass http://localhost:12000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    location /api/notes/upload {
        proxy_pass http://localhost:12000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        client_max_body_size 50M;
        proxy_request_buffering off;
    }
}
EOF

# Enable site
sudo ln -sf /etc/nginx/sites-available/notizprojekt /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t && sudo systemctl reload nginx
```

### 6. Setup SSL with Let's Encrypt

```bash
sudo snap install --classic certbot
sudo ln -sf /snap/bin/certbot /usr/bin/certbot
sudo certbot --nginx -d your-domain.com --email admin@your-domain.com --agree-tos --non-interactive --redirect
```

### 7. Create Application Configuration

```bash
sudo tee /opt/notizprojekt/application.properties << 'EOF'
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/notizprojekt
spring.datasource.username=notizuser
spring.datasource.password=notizpassword
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
EOF
```

### 8. Create Systemd Service

```bash
sudo tee /etc/systemd/system/notizprojekt.service << 'EOF'
[Unit]
Description=ITS-Projekt Note-Taking Application
After=network.target mariadb.service
Wants=mariadb.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/notizprojekt
ExecStart=/usr/bin/java -Xms256m -Xmx1g -jar /opt/notizprojekt/target/notizprojekt-web-0.0.1-SNAPSHOT.jar --spring.config.location=file:/opt/notizprojekt/application.properties
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable notizprojekt
sudo systemctl start notizprojekt
```

### 9. Configure Firewall

```bash
sudo ufw allow 'Nginx Full'
sudo ufw allow 22/tcp
sudo ufw --force enable
```

## 👤 Default Credentials

After setup, you can log in with these test accounts:

| Username | Password | Purpose |
|----------|----------|---------|
| `testuser1` | `password123` | Testing account 1 |
| `testuser2` | `password123` | Testing account 2 |

## 🛠️ Management Commands

### Application Management
```bash
# Check application status
sudo systemctl status notizprojekt

# View application logs
sudo journalctl -u notizprojekt -f

# Restart application
sudo systemctl restart notizprojekt

# Stop application
sudo systemctl stop notizprojekt
```

### Nginx Management
```bash
# Check Nginx status
sudo systemctl status nginx

# Test Nginx configuration
sudo nginx -t

# Reload Nginx configuration
sudo systemctl reload nginx

# View Nginx logs
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

### SSL Certificate Management
```bash
# Check certificate status
sudo certbot certificates

# Renew certificates manually
sudo certbot renew

# Test automatic renewal
sudo certbot renew --dry-run
```

### Database Management
```bash
# Connect to database
sudo mysql -u root -p notizprojekt

# Backup database
sudo mysqldump -u root -p notizprojekt > backup_$(date +%Y%m%d).sql

# Check database status
sudo systemctl status mariadb
```

## 🔍 Troubleshooting

### Application Issues

**Application won't start:**
```bash
# Check detailed logs
sudo journalctl -u notizprojekt -n 100

# Verify Java installation
java -version

# Check if port is available
sudo netstat -tlnp | grep 12000
```

**Database connection issues:**
```bash
# Check MariaDB status
sudo systemctl status mariadb

# Test database connection
sudo mysql -u notizuser -p notizprojekt

# Check database exists
sudo mysql -u root -p -e "SHOW DATABASES;"
```

### Nginx Issues

**Nginx configuration errors:**
```bash
# Test configuration
sudo nginx -t

# Check Nginx logs
sudo tail -f /var/log/nginx/error.log

# Verify site is enabled
ls -la /etc/nginx/sites-enabled/
```

### SSL Issues

**Certificate problems:**
```bash
# Check certificate status
sudo certbot certificates

# Check domain resolution
dig your-domain.com

# Manual certificate renewal
sudo certbot --nginx -d your-domain.com
```

### Performance Issues

**High memory usage:**
```bash
# Check system resources
htop
free -h
df -h

# Adjust JVM memory in systemd service
sudo systemctl edit notizprojekt
```

## 🔒 Security Best Practices

### Production Security Checklist

- [ ] Change default database passwords
- [ ] Use strong passwords for all accounts
- [ ] Enable automatic security updates
- [ ] Set up log monitoring
- [ ] Configure fail2ban for SSH protection
- [ ] Regular security audits
- [ ] Keep SSL certificates updated
- [ ] Monitor application logs for suspicious activity

### Firewall Configuration

```bash
# Basic security rules
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw enable
```

### Database Security

```bash
# Secure MariaDB installation
sudo mysql_secure_installation

# Create application-specific user
sudo mysql -u root -p << 'EOF'
CREATE USER 'notizapp'@'localhost' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON notizprojekt.* TO 'notizapp'@'localhost';
FLUSH PRIVILEGES;
EOF
```

## 📊 Performance Optimization

### JVM Tuning

Edit `/etc/systemd/system/notizprojekt.service`:

```ini
ExecStart=/usr/bin/java -Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -jar ...
```

### Nginx Optimization

Add to Nginx configuration:

```nginx
# Enable gzip compression
gzip on;
gzip_types text/plain text/css application/json application/javascript text/xml application/xml;

# Enable caching
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

### Database Optimization

```sql
-- Add indexes for better performance
CREATE INDEX idx_notiz_user ON notiz(B_id);
CREATE INDEX idx_notiz_privacy ON notiz(privacy_level);
CREATE INDEX idx_notiz_shared ON notiz(shared_with);
```

## 💾 Backup Strategy

### Automated Backup Script

```bash
#!/bin/bash
# /opt/backup.sh

BACKUP_DIR="/opt/backups"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# Database backup
mysqldump -u root -p$DB_PASSWORD notizprojekt > $BACKUP_DIR/db_$DATE.sql

# Files backup
tar -czf $BACKUP_DIR/files_$DATE.tar.gz /opt/notizprojekt/uploads/

# Configuration backup
cp /opt/notizprojekt/application.properties $BACKUP_DIR/config_$DATE.properties

# Keep only last 7 days
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +7 -delete
find $BACKUP_DIR -name "*.properties" -mtime +7 -delete
```

### Cron Job for Daily Backups

```bash
# Add to crontab
echo "0 2 * * * /opt/backup.sh" | sudo crontab -
```

## 📈 Monitoring

### Log Monitoring

```bash
# Monitor application logs
sudo journalctl -u notizprojekt -f

# Monitor Nginx access logs
sudo tail -f /var/log/nginx/access.log

# Monitor system resources
watch -n 1 'free -h && df -h'
```

### Health Check Script

```bash
#!/bin/bash
# /opt/health-check.sh

# Check if application is responding
if curl -f -s http://localhost:12000/health > /dev/null; then
    echo "Application: OK"
else
    echo "Application: FAILED"
    sudo systemctl restart notizprojekt
fi

# Check SSL certificate expiry
if openssl x509 -checkend 604800 -noout -in /etc/letsencrypt/live/your-domain.com/cert.pem; then
    echo "SSL Certificate: OK"
else
    echo "SSL Certificate: Expires soon"
fi
```

## 🆘 Support

For issues and questions:

1. **Check Logs**: Always start with application and system logs
2. **Documentation**: Review this guide and the troubleshooting section
3. **GitHub Issues**: Check the repository for known issues
4. **Community**: Ask questions in the project discussions

### Common Issues and Solutions

| Issue | Solution |
|-------|----------|
| Port 80/443 already in use | Stop conflicting service: `sudo systemctl stop apache2` |
| SSL certificate failed | Check domain DNS resolution and try manual setup |
| Application crashes | Check JVM memory settings and database connection |
| File upload fails | Verify upload directory permissions and Nginx config |
| Database connection timeout | Check MariaDB status and restart if needed |

## 🔄 Updates and Maintenance

### Application Updates

```bash
cd /opt/notizprojekt
sudo git pull origin html
sudo ./mvnw clean package -DskipTests
sudo systemctl restart notizprojekt
```

### System Updates

```bash
sudo apt update && sudo apt upgrade -y
sudo systemctl restart notizprojekt
sudo systemctl restart nginx
```

This comprehensive setup provides a production-ready deployment with security, performance, and reliability features suitable for public usage.
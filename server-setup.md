# ITS-Projekt Server Setup Guide

## 🚀 Simple Server Deployment Guide

This guide provides straightforward instructions for deploying the ITS-Projekt note-taking application on a server for public usage with minimal complexity.

## 📋 Table of Contents

1. [Quick Setup](#quick-setup)
2. [Prerequisites](#prerequisites)
3. [Automated Setup](#automated-setup)
4. [Manual Setup](#manual-setup)
5. [Production Deployment with Domain](#production-deployment-with-domain)
6. [Configuration](#configuration)
7. [Troubleshooting](#troubleshooting)

## 🚀 Quick Setup

**TL;DR**: Run this one command to set everything up automatically:

```bash
curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/setup.sh | sudo bash
```

Then access your application at `http://your-server-ip:12000`

## 📋 Prerequisites

### System Requirements
- **Operating System**: Ubuntu 18.04+ or Debian 10+ (other Linux distributions may work)
- **RAM**: Minimum 1GB, recommended 2GB+
- **Storage**: Minimum 5GB free space
- **Network**: Public IP address

### What Gets Installed Automatically
- **Java**: OpenJDK 11
- **Database**: MariaDB
- **Application**: ITS-Projekt web application
- **Basic firewall rules**

## 🤖 Automated Setup

The setup script (`setup.sh`) will automatically:

1. ✅ Install Java 11
2. ✅ Install and configure MariaDB
3. ✅ Create database and import schema
4. ✅ Download and build the application
5. ✅ Create systemd service
6. ✅ Configure firewall
7. ✅ Start the application

### Usage

```bash
# Download and run the setup script
curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/setup.sh | sudo bash

# Or download first, then run
wget https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/setup.sh
chmod +x setup.sh
sudo ./setup.sh
```

### Custom Configuration

You can set environment variables before running the script:

```bash
# Custom database password
export DB_PASSWORD="your_custom_password"

# Custom application port
export APP_PORT="8080"

# Custom domain (for future SSL setup)
export DOMAIN="your-domain.com"

# Run setup with custom settings
curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/setup.sh | sudo bash
```

## 📖 Manual Setup

If you prefer to set up manually or the automated script doesn't work for your system:

### Step 1: Install Dependencies

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 11
sudo apt install openjdk-11-jdk -y

# Install MariaDB
sudo apt install mariadb-server -y

# Install Git and other tools
sudo apt install git curl wget unzip -y
```

### Step 2: Configure Database

```bash
# Start MariaDB
sudo systemctl start mariadb
sudo systemctl enable mariadb

# Set root password (use 'notizpassword' for simplicity)
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'notizpassword';"

# Download and import database schema
cd /tmp
wget https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/its-projekt18.6.sql
mysql -u root -pnotizpassword < its-projekt18.6.sql
```

### Step 3: Deploy Application

```bash
# Create application directory
sudo mkdir -p /opt/notizprojekt
cd /opt/notizprojekt

# Clone repository
sudo git clone https://github.com/PythonTilk/ITS-Projekt.git .
sudo git checkout html

# Build application
sudo ./mvnw clean package -DskipTests

# Create uploads directory
sudo mkdir -p uploads
```

### Step 4: Configure Application

```bash
# Create simple configuration
sudo tee /opt/notizprojekt/application.properties > /dev/null <<EOF
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
EOF
```

### Step 5: Create Service

```bash
# Create systemd service
sudo tee /etc/systemd/system/notizprojekt.service > /dev/null <<EOF
[Unit]
Description=ITS-Projekt Note-Taking Application
After=network.target mariadb.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/notizprojekt
ExecStart=/usr/bin/java -jar /opt/notizprojekt/target/notizprojekt-web-0.0.1-SNAPSHOT.jar --spring.config.location=file:/opt/notizprojekt/application.properties
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# Start service
sudo systemctl daemon-reload
sudo systemctl enable notizprojekt
sudo systemctl start notizprojekt
```

## 🌐 Production Deployment with Domain

For production deployment with a custom domain and HTTPS, follow these additional steps after completing the basic setup.

### Step 1: Install and Configure Nginx

```bash
# Install Nginx
sudo apt update
sudo apt install nginx -y

# Create Nginx configuration for your domain
sudo tee /etc/nginx/sites-available/notizprojekt > /dev/null <<EOF
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;

    location / {
        proxy_pass http://localhost:12000;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        
        # WebSocket support (if needed)
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
EOF

# Enable the site
sudo ln -s /etc/nginx/sites-available/notizprojekt /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default

# Test and restart Nginx
sudo nginx -t
sudo systemctl restart nginx
sudo systemctl enable nginx
```

### Step 2: Configure Firewall for Web Traffic

```bash
# Allow HTTP and HTTPS traffic
sudo ufw allow 'Nginx Full'
sudo ufw allow 22/tcp  # Keep SSH access

# Remove direct access to application port (optional, for security)
sudo ufw delete allow 12000/tcp

# Enable firewall
sudo ufw --force enable
```

### Step 3: Set Up SSL with Let's Encrypt

```bash
# Install Certbot
sudo apt install snapd -y
sudo snap install core; sudo snap refresh core
sudo snap install --classic certbot

# Create symlink
sudo ln -s /snap/bin/certbot /usr/bin/certbot

# Get SSL certificate (replace your-domain.com with your actual domain)
sudo certbot --nginx -d your-domain.com -d www.your-domain.com

# Test automatic renewal
sudo certbot renew --dry-run
```

### Step 4: Update DNS Records

Point your domain to your server's IP address:

```
A Record: your-domain.com → your-server-ip
A Record: www.your-domain.com → your-server-ip
```

### Step 5: Verify Production Setup

After completing the above steps:

1. **HTTP Access**: `http://your-domain.com` (should redirect to HTTPS)
2. **HTTPS Access**: `https://your-domain.com` (secure connection)
3. **Application**: Should be accessible through your domain

### Production Configuration Example

For a domain like `notes.tilk.tech`, your Nginx config would be:

```nginx
server {
    listen 80;
    server_name notes.tilk.tech;

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
}
```

Then run: `sudo certbot --nginx -d notes.tilk.tech`

## ⚙️ Configuration

### Basic Firewall Setup (Optional)

```bash
# Allow application port
sudo ufw allow 12000/tcp

# Allow SSH (if needed)
sudo ufw allow 22/tcp

# Enable firewall
sudo ufw --force enable
```

### Access Your Application

After setup is complete, you can access your application at:

- **Local**: `http://localhost:12000`
- **Remote**: `http://your-server-ip:12000`

### Default Login Credentials

- **Username**: `testuser123`
- **Password**: `password123`

Or create a new account using the registration form.

## 🔧 Troubleshooting

### Common Issues

#### Application Won't Start
```bash
# Check service status
sudo systemctl status notizprojekt

# Check logs
sudo journalctl -u notizprojekt -f

# Restart service
sudo systemctl restart notizprojekt
```

#### Database Connection Issues
```bash
# Test database connection
mysql -u notizuser -pnotizpassword notizprojekt

# Restart MariaDB
sudo systemctl restart mariadb

# Re-import database if needed
mysql -u root -pnotizpassword < /tmp/its-projekt18.6.sql
```

#### Port Already in Use
```bash
# Check what's using port 12000
sudo netstat -tlnp | grep 12000

# Kill process if needed
sudo kill -9 $(sudo lsof -t -i:12000)

# Restart application
sudo systemctl restart notizprojekt
```

#### Can't Access from External IP
```bash
# Check if application is binding to all interfaces
sudo netstat -tlnp | grep 12000

# Make sure firewall allows the port
sudo ufw allow 12000/tcp

# Check if your cloud provider has security groups blocking the port
```

### Useful Commands

```bash
# Check application status
sudo systemctl status notizprojekt

# View application logs
sudo journalctl -u notizprojekt -f

# Restart application
sudo systemctl restart notizprojekt

# Check database status
sudo systemctl status mariadb

# Test database connection
mysql -u notizuser -pnotizpassword -e "SHOW DATABASES;"

# Check open ports
sudo netstat -tlnp

# Check disk space
df -h

# Check memory usage
free -h
```

## 📝 Notes

- The application runs on port 12000 by default
- Database credentials: `notizuser` / `notizpassword`
- Application files are stored in `/opt/notizprojekt/`
- Uploads are stored in `/opt/notizprojekt/uploads/`
- For production use, consider changing default passwords
- For HTTPS, you'll need to set up a reverse proxy (Nginx/Apache) with SSL certificates

## 🆘 Getting Help

If you encounter issues:

1. Check the application logs: `sudo journalctl -u notizprojekt -f`
2. Verify database connection: `mysql -u notizuser -pnotizpassword notizprojekt`
3. Ensure all services are running: `sudo systemctl status notizprojekt mariadb`
4. Check firewall settings: `sudo ufw status`
5. Verify port availability: `sudo netstat -tlnp | grep 12000`

---

**Quick Start Summary:**
1. Run: `curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/setup.sh | sudo bash`
2. Wait for installation to complete
3. Access: `http://your-server-ip:12000`
4. Login with: `testuser123` / `password123`
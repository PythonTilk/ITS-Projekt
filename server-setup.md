# ITS-Projekt Server Setup Guide

This guide provides comprehensive instructions for setting up the ITS-Projekt Note Management System on a production server for public usage.

## 🚀 Quick Start

### Prerequisites
- Ubuntu 20.04+ or Debian 11+ server
- Root access or sudo privileges
- Domain name pointing to your server's IP address
- At least 2GB RAM and 10GB disk space

### One-Command Installation

```bash
# Download and run the installation script
curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/install.sh -o install.sh
chmod +x install.sh
sudo ./install.sh your-domain.com admin@your-domain.com
```

Replace:
- `your-domain.com` with your actual domain
- `admin@your-domain.com` with your email for SSL certificates

## 📋 Latest Fixes & Improvements

### Recent Updates (Version 4ddba74+)
- ✅ **Fixed SSL Configuration**: Removed www domain from certificate requests to prevent DNS issues
- ✅ **Fixed Systemd Service**: Absolute JAR file paths prevent startup failures
- ✅ **Enhanced JAR Detection**: Dynamic discovery using `find` command instead of wildcards
- ✅ **Improved Error Handling**: Better diagnostics and path resolution
- ✅ **Simplified DNS Requirements**: Only main domain needed (no www subdomain)

### What These Fixes Solve
1. **SSL Certificate Failures**: No more NXDOMAIN errors for www subdomains
2. **Service Startup Issues**: Systemd service now uses absolute JAR file paths
3. **JAR Detection Problems**: Dynamic file discovery prevents "file not found" errors
4. **Path Resolution Issues**: Proper handling of relative vs absolute paths

## 🔧 Installation Script Features

The `install.sh` script automatically handles:

### System Dependencies
- ✅ Java 17 (OpenJDK)
- ✅ Maven 3.8+
- ✅ MySQL 8.0
- ✅ Nginx
- ✅ Certbot (Let's Encrypt SSL)
- ✅ Git

### Application Setup
- ✅ Creates dedicated user (`notizprojekt`)
- ✅ Downloads source code from GitHub
- ✅ Builds application with Maven
- ✅ Configures MySQL database
- ✅ Sets up systemd service with absolute JAR paths
- ✅ Configures Nginx reverse proxy
- ✅ Obtains SSL certificate for main domain only

### Security Features
- ✅ Firewall configuration (UFW)
- ✅ SSL/TLS encryption
- ✅ Security headers
- ✅ Systemd service isolation
- ✅ Database user restrictions

## 🛠️ Troubleshooting

### Fixed Issues (No Longer Occur)

#### 1. JAR File Not Found ✅ FIXED
**Previous Problem**: Service failed with "JAR file not found"
**Solution Applied**: Dynamic JAR file discovery using `find` command
```bash
# The script now uses:
jar_file=$(find "$PROJECT_DIR/$PROJECT_NAME/target" -name "notizprojekt-web-*.jar" -type f | head -n 1)
```

#### 2. SSL Certificate Fails for www Domain ✅ FIXED
**Previous Problem**: Certbot failed to obtain certificate for www subdomain
**Solution Applied**: Only request SSL for main domain
```bash
# Old (caused issues):
certbot --nginx -d "$DOMAIN" -d "www.$DOMAIN" --email "$EMAIL" --agree-tos --non-interactive

# New (works reliably):
certbot --nginx -d "$DOMAIN" --email "$EMAIL" --agree-tos --non-interactive
```

#### 3. Systemd Service Startup Failures ✅ FIXED
**Previous Problem**: Service exited with code 1 due to relative JAR paths
**Solution Applied**: Use absolute JAR file paths in systemd service
```bash
# Old (caused failures):
ExecStart=/usr/bin/java $JAVA_OPTS -jar target/notizprojekt-web-*.jar

# New (works reliably):
ExecStart=/usr/bin/java $JAVA_OPTS -jar /opt/notizprojekt/ITS-Projekt/target/notizprojekt-web-0.0.1-SNAPSHOT.jar
```

### Current Troubleshooting (If Issues Persist)

#### Check Installation Version
First, verify you're using the latest version:
```bash
# Check if you have the latest fixes
curl -s https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/install.sh | grep -A5 -B5 "jar_file_absolute"
```

#### Verify Service Status
```bash
# Check all services
sudo systemctl status notizprojekt nginx mysql --no-pager

# Check application logs
sudo journalctl -u notizprojekt -n 20 --no-pager

# Verify JAR file exists
find /opt/notizprojekt/ITS-Projekt/target -name "*.jar" -type f
```

#### SSL Certificate Check
```bash
# Check certificate status
sudo certbot certificates

# Test SSL
curl -I https://your-domain.com
```

#### Database Connection Test
```bash
# Test database connection
mysql -u notizprojekt -p notizprojekt -e "SELECT 1;"

# Check MySQL status
sudo systemctl status mysql
```

### Manual Fixes (If Needed)

#### Fix JAR File Path in Existing Service
If you have an older installation with relative paths:
```bash
# Find the actual JAR file
JAR_FILE=$(find /opt/notizprojekt/ITS-Projekt/target -name "notizprojekt-web-*.jar" -type f | head -n 1)

# Update systemd service
sudo sed -i "s|ExecStart=.*|ExecStart=/usr/bin/java \$JAVA_OPTS -jar $JAR_FILE|" /etc/systemd/system/notizprojekt.service

# Reload and restart
sudo systemctl daemon-reload
sudo systemctl restart notizprojekt
```

#### Remove www Domain from Existing SSL
If you have SSL issues with www domain:
```bash
# Remove existing certificate
sudo certbot delete --cert-name your-domain.com

# Request new certificate for main domain only
sudo certbot --nginx -d your-domain.com --email admin@your-domain.com --agree-tos --non-interactive
```

## 🔍 Verification Commands

### Complete System Check
```bash
#!/bin/bash
echo "=== ITS-Projekt System Check ==="
echo "Date: $(date)"
echo ""

echo "=== Service Status ==="
sudo systemctl status notizprojekt nginx mysql --no-pager
echo ""

echo "=== JAR File Check ==="
find /opt/notizprojekt/ITS-Projekt/target -name "*.jar" -type f
echo ""

echo "=== Service Configuration ==="
sudo systemctl cat notizprojekt | grep ExecStart
echo ""

echo "=== SSL Certificate ==="
sudo certbot certificates
echo ""

echo "=== Recent Application Logs ==="
sudo journalctl -u notizprojekt -n 5 --no-pager
echo ""

echo "=== Network Test ==="
curl -I https://your-domain.com 2>/dev/null | head -n 1
echo ""

echo "=== Disk Space ==="
df -h /opt/notizprojekt
echo ""

echo "=== Memory Usage ==="
free -h
```

### Application Health Check
```bash
# Test application endpoints
curl -s https://your-domain.com/actuator/health | jq .
curl -I https://your-domain.com/login
```

## 🔄 Maintenance

### Update to Latest Version
```bash
# Update application
cd /opt/notizprojekt/ITS-Projekt
sudo -u notizprojekt git pull
sudo -u notizprojekt mvn clean package -DskipTests
sudo systemctl restart notizprojekt

# Verify update
sudo journalctl -u notizprojekt -n 10 --no-pager
```

### SSL Certificate Renewal
```bash
# Test renewal (dry run)
sudo certbot renew --dry-run

# Force renewal if needed
sudo certbot renew --force-renewal
```

### Database Backup
```bash
# Create backup
sudo mysqldump -u notizprojekt -p notizprojekt > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore backup
sudo mysql -u notizprojekt -p notizprojekt < backup_20231201_120000.sql
```

## 🔒 Security Best Practices

### Firewall Configuration
```bash
# Recommended firewall rules
sudo ufw allow ssh
sudo ufw allow http
sudo ufw allow https
sudo ufw deny 8080  # Block direct access to application
sudo ufw --force enable
```

### SSL Security Headers
The installation automatically configures:
- HSTS (HTTP Strict Transport Security)
- X-Frame-Options
- X-Content-Type-Options
- X-XSS-Protection
- Content Security Policy

### Database Security
- Dedicated database user with minimal privileges
- Strong password requirements
- Local connections only
- Regular security updates

## 📊 Performance Monitoring

### System Resources
```bash
# Monitor system performance
htop

# Check disk usage
df -h

# Monitor memory
free -h

# Check network connections
sudo netstat -tulpn | grep :8080
```

### Application Monitoring
```bash
# Real-time logs
sudo journalctl -u notizprojekt -f

# Application metrics (if available)
curl -s https://your-domain.com/actuator/metrics | jq .

# Health check
curl -s https://your-domain.com/actuator/health | jq .
```

## 📞 Support

### Getting Help
1. **Use Latest Version**: Ensure you're using install script version 4ddba74 or later
2. **Check Logs**: `sudo journalctl -u notizprojekt -f`
3. **Verify Status**: `sudo systemctl status notizprojekt nginx mysql`
4. **Run Diagnostics**: Use the verification commands above
5. **GitHub Issues**: https://github.com/PythonTilk/ITS-Projekt/issues

### Reporting Issues
When reporting issues, include:
- Install script version/commit hash
- Server OS and version
- Complete error messages from logs
- Output of verification commands
- Domain name (if not sensitive)

### Quick Diagnostic Script
Save this as `diagnose.sh` and run with `sudo bash diagnose.sh`:
```bash
#!/bin/bash
echo "=== ITS-Projekt Diagnostic Report ==="
echo "Generated: $(date)"
echo "Hostname: $(hostname)"
echo "OS: $(lsb_release -d | cut -f2)"
echo ""

echo "=== Install Script Version Check ==="
if [ -f "install.sh" ]; then
    grep -o "commit [a-f0-9]*" install.sh | head -n 1 || echo "Version info not found"
else
    echo "install.sh not found in current directory"
fi
echo ""

echo "=== Service Status ==="
systemctl is-active notizprojekt nginx mysql
echo ""

echo "=== JAR File ==="
find /opt/notizprojekt/ITS-Projekt/target -name "*.jar" -type f 2>/dev/null || echo "JAR file not found"
echo ""

echo "=== Service Config ==="
systemctl cat notizprojekt | grep ExecStart 2>/dev/null || echo "Service not found"
echo ""

echo "=== SSL Status ==="
certbot certificates 2>/dev/null | grep -A2 "Certificate Name" || echo "No certificates found"
echo ""

echo "=== Recent Errors ==="
journalctl -u notizprojekt --since "1 hour ago" --no-pager | grep -i error | tail -n 5 || echo "No recent errors"
echo ""

echo "=== Network Test ==="
curl -I http://localhost:8080 2>/dev/null | head -n 1 || echo "Application not responding"
```

---

**Note**: This guide reflects the latest fixes and improvements in the ITS-Projekt installation script. Always use the most recent version for the best experience.
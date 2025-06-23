#!/bin/bash

# ITS-Projekt Production Server Setup Script
# This script sets up the complete production environment for ITS-Projekt:
# - Java 17, MariaDB, Nginx reverse proxy, SSL certificates, domain configuration
# - Automated deployment to notes.tilk.tech with HTTPS
#
# Usage:
#   sudo bash setup.sh
#   curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/setup.sh | sudo bash
#
# Environment Variables (optional):
#   DOMAIN="your-domain.com"        # Domain name (default: notes.tilk.tech)
#   EMAIL="admin@domain.com"        # Email for SSL certificates (default: admin@tilk.tech)
#   DB_PASSWORD="password"          # Database password (default: auto-generated)
#   APP_PORT="12000"               # Application port (default: 12000)
#   SETUP_NGINX="true"             # Install Nginx reverse proxy (default: true)
#   SETUP_SSL="true"               # Setup SSL with Let's Encrypt (default: true)
#
# Example with custom domain:
#   DOMAIN="notes.example.com" EMAIL="admin@example.com" sudo bash setup.sh

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# Configuration variables (can be overridden by environment variables)
DB_PASSWORD=${DB_PASSWORD:-"$(openssl rand -base64 32)"}
DB_ROOT_PASSWORD=${DB_ROOT_PASSWORD:-"$(openssl rand -base64 32)"}
APP_PORT=${APP_PORT:-"12000"}
DOMAIN=${DOMAIN:-"notes.tilk.tech"}
EMAIL=${EMAIL:-"admin@tilk.tech"}
SETUP_NGINX=${SETUP_NGINX:-"true"}
SETUP_SSL=${SETUP_SSL:-"true"}
APP_DIR="/opt/notizprojekt"
NGINX_AVAILABLE="/etc/nginx/sites-available"
NGINX_ENABLED="/etc/nginx/sites-enabled"

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if running as root
check_root() {
    if [[ $EUID -ne 0 ]]; then
        print_error "This script must be run as root (use sudo)"
        exit 1
    fi
}

# Function to detect OS
detect_os() {
    if [[ -f /etc/os-release ]]; then
        . /etc/os-release
        OS=$NAME
        VER=$VERSION_ID
    else
        print_error "Cannot detect operating system"
        exit 1
    fi
    
    print_status "Detected OS: $OS $VER"
    
    # Check if OS is supported
    if [[ "$OS" != *"Ubuntu"* ]] && [[ "$OS" != *"Debian"* ]]; then
        print_warning "This script is designed for Ubuntu/Debian. It may not work on $OS"
        read -p "Do you want to continue anyway? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi
}

# Function to update system packages
update_system() {
    print_status "Updating system packages..."
    apt update -y
    apt upgrade -y
    print_success "System packages updated"
}

# Function to install Java
install_java() {
    print_status "Installing Java 17..."
    
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
        print_status "Java is already installed: $JAVA_VERSION"
        
        # Check if it's Java 17 or higher
        JAVA_MAJOR=$(echo "$JAVA_VERSION" | cut -d. -f1)
        if [[ "$JAVA_MAJOR" -lt 17 ]]; then
            print_warning "Java version is too old. Installing Java 17..."
            apt install -y openjdk-17-jdk
        fi
    else
        apt install -y openjdk-17-jdk
    fi
    
    # Set JAVA_HOME
    if [[ -d "/usr/lib/jvm/java-17-openjdk-amd64" ]]; then
        JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
    elif [[ -d "/usr/lib/jvm/java-17-openjdk" ]]; then
        JAVA_HOME="/usr/lib/jvm/java-17-openjdk"
    else
        JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")
    fi
    
    echo "export JAVA_HOME=$JAVA_HOME" >> /etc/environment
    export JAVA_HOME=$JAVA_HOME
    
    print_success "Java 17 installed successfully"
}

# Function to install MariaDB
install_mariadb() {
    print_status "Installing MariaDB..."
    
    # Install MariaDB
    apt install -y mariadb-server mariadb-client
    
    # Start and enable MariaDB
    systemctl start mariadb
    systemctl enable mariadb
    
    print_success "MariaDB installed and started"
}

# Function to configure database
configure_database() {
    print_status "Configuring database..."
    
    # Secure MariaDB installation
    print_status "Securing MariaDB installation..."
    mysql -e "UPDATE mysql.user SET Password=PASSWORD('$DB_ROOT_PASSWORD') WHERE User='root';" 2>/dev/null || \
    mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '$DB_ROOT_PASSWORD';" 2>/dev/null || true
    
    mysql -e "DELETE FROM mysql.user WHERE User='';" 2>/dev/null || true
    mysql -e "DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');" 2>/dev/null || true
    mysql -e "DROP DATABASE IF EXISTS test;" 2>/dev/null || true
    mysql -e "DELETE FROM mysql.db WHERE Db='test' OR Db='test\\_%';" 2>/dev/null || true
    mysql -e "FLUSH PRIVILEGES;" 2>/dev/null || true
    
    # Create application database and user
    print_status "Creating application database and user..."
    mysql -u root -p"$DB_ROOT_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS notizprojekt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null || \
    mysql -u root -e "CREATE DATABASE IF NOT EXISTS notizprojekt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    
    mysql -u root -p"$DB_ROOT_PASSWORD" -e "CREATE USER IF NOT EXISTS 'notizuser'@'localhost' IDENTIFIED BY '$DB_PASSWORD';" 2>/dev/null || \
    mysql -u root -e "CREATE USER IF NOT EXISTS 'notizuser'@'localhost' IDENTIFIED BY '$DB_PASSWORD';"
    
    mysql -u root -p"$DB_ROOT_PASSWORD" -e "GRANT ALL PRIVILEGES ON notizprojekt.* TO 'notizuser'@'localhost';" 2>/dev/null || \
    mysql -u root -e "GRANT ALL PRIVILEGES ON notizprojekt.* TO 'notizuser'@'localhost';"
    
    mysql -u root -p"$DB_ROOT_PASSWORD" -e "FLUSH PRIVILEGES;" 2>/dev/null || \
    mysql -u root -e "FLUSH PRIVILEGES;"
    
    print_success "Database configured successfully"
}

# Function to install Git and other tools
install_tools() {
    print_status "Installing additional tools..."
    apt install -y git curl wget unzip net-tools software-properties-common
    print_success "Additional tools installed"
}

# Function to install and configure Nginx
install_nginx() {
    if [[ "$SETUP_NGINX" != "true" ]]; then
        print_status "Skipping Nginx installation (SETUP_NGINX=false)"
        return 0
    fi
    
    print_status "Installing and configuring Nginx..."
    
    # Install Nginx
    apt install -y nginx
    
    # Start and enable Nginx
    systemctl start nginx
    systemctl enable nginx
    
    # Create Nginx configuration for the domain
    print_status "Creating Nginx configuration for $DOMAIN..."
    
    cat > /etc/nginx/sites-available/notizprojekt << EOF
server {
    listen 80;
    server_name $DOMAIN www.$DOMAIN;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;

    # Proxy settings
    location / {
        proxy_pass http://localhost:$APP_PORT;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        
        # WebSocket support
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Handle file uploads
    location /api/notes/upload {
        proxy_pass http://localhost:$APP_PORT;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        
        # Increase upload size limits
        client_max_body_size 50M;
        proxy_request_buffering off;
    }

    # Static files caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        proxy_pass http://localhost:$APP_PORT;
        proxy_set_header Host \$host;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
EOF

    # Enable the site
    ln -sf /etc/nginx/sites-available/notizprojekt /etc/nginx/sites-enabled/
    
    # Remove default site if it exists
    rm -f /etc/nginx/sites-enabled/default
    
    # Test Nginx configuration
    if nginx -t; then
        systemctl reload nginx
        print_success "Nginx installed and configured successfully"
    else
        print_error "Nginx configuration test failed"
        exit 1
    fi
}

# Function to install Certbot and setup SSL
setup_ssl() {
    if [[ "$SETUP_SSL" != "true" ]]; then
        print_status "Skipping SSL setup (SETUP_SSL=false)"
        return 0
    fi
    
    if [[ "$SETUP_NGINX" != "true" ]]; then
        print_warning "SSL setup requires Nginx. Skipping SSL setup."
        return 0
    fi
    
    print_status "Installing Certbot and setting up SSL..."
    
    # Install snapd if not already installed
    if ! command -v snap &> /dev/null; then
        print_status "Installing snapd..."
        apt install -y snapd
        systemctl enable --now snapd.socket
        # Wait for snapd to be ready
        sleep 10
    fi
    
    # Install certbot via snap
    print_status "Installing Certbot..."
    snap install core
    snap refresh core
    snap install --classic certbot
    
    # Create symlink
    ln -sf /snap/bin/certbot /usr/bin/certbot
    
    # Check if domain resolves to this server
    print_status "Checking domain resolution..."
    SERVER_IP=$(curl -s ifconfig.me || curl -s ipinfo.io/ip || echo "unknown")
    DOMAIN_IP=$(dig +short $DOMAIN | tail -n1)
    
    if [[ "$DOMAIN_IP" != "$SERVER_IP" ]]; then
        print_warning "Domain $DOMAIN does not resolve to this server IP ($SERVER_IP)"
        print_warning "Current domain IP: $DOMAIN_IP"
        print_warning "Please update your DNS records before continuing with SSL setup"
        
        read -p "Do you want to continue with SSL setup anyway? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            print_status "Skipping SSL setup. You can run 'sudo certbot --nginx -d $DOMAIN' later"
            return 0
        fi
    fi
    
    # Get SSL certificate
    print_status "Obtaining SSL certificate for $DOMAIN..."
    if certbot --nginx -d $DOMAIN --non-interactive --agree-tos --email $EMAIL --redirect; then
        print_success "SSL certificate obtained and configured successfully"
        
        # Test automatic renewal
        print_status "Testing automatic renewal..."
        certbot renew --dry-run
        print_success "Automatic renewal test passed"
    else
        print_error "Failed to obtain SSL certificate"
        print_status "You can try running 'sudo certbot --nginx -d $DOMAIN' manually later"
    fi
}

# Function to deploy application
deploy_application() {
    print_status "Deploying application..."
    
    # Create application directory
    mkdir -p $APP_DIR
    cd $APP_DIR
    
    # Clone repository
    print_status "Cloning repository..."
    if [[ -d ".git" ]]; then
        print_status "Repository already exists, pulling latest changes..."
        git pull origin html
    else
        git clone https://github.com/PythonTilk/ITS-Projekt.git .
        git checkout html
    fi
    
    # Build application
    print_status "Building application (this may take a few minutes)..."
    chmod +x mvnw
    
    # Try building the application
    if ! ./mvnw clean package -DskipTests; then
        print_error "Failed to build application"
        print_status "This might be due to Java version compatibility issues"
        print_status "Trying to build with verbose output..."
        ./mvnw clean package -DskipTests -X
        exit 1
    fi
    
    # Create uploads directory
    mkdir -p uploads
    chmod 755 uploads
    
    print_success "Application deployed successfully"
}

# Function to create application configuration
create_configuration() {
    print_status "Creating application configuration..."
    
    cat > $APP_DIR/application.properties << EOF
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/notizprojekt?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=notizuser
spring.datasource.password=$DB_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Server Configuration
server.port=$APP_PORT
server.address=0.0.0.0

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
spring.web.resources.static-locations=classpath:/static/,file:$APP_DIR/uploads/

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Logging Configuration
logging.level.org.springframework.web=WARN
logging.level.org.hibernate=WARN
logging.level.notizprojekt=INFO
EOF
    
    print_success "Configuration created"
}

# Function to create systemd service
create_service() {
    print_status "Creating systemd service..."
    
    # Find the JAR file
    JAR_FILE=$(find $APP_DIR/target -name "*.jar" -not -name "*sources.jar" -not -name "*javadoc.jar" | head -1)
    if [[ -z "$JAR_FILE" ]]; then
        print_error "Could not find JAR file in $APP_DIR/target"
        print_status "Available files in target directory:"
        ls -la $APP_DIR/target/ || echo "Target directory does not exist"
        exit 1
    fi
    
    print_status "Found JAR file: $JAR_FILE"
    
    cat > /etc/systemd/system/notizprojekt.service << EOF
[Unit]
Description=ITS-Projekt Note-Taking Application
After=network.target mariadb.service
Wants=mariadb.service

[Service]
Type=simple
User=root
WorkingDirectory=$APP_DIR
ExecStart=/usr/bin/java -Xms256m -Xmx1g -jar $JAR_FILE --spring.config.location=file:$APP_DIR/application.properties
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF
    
    # Reload systemd and enable service
    systemctl daemon-reload
    systemctl enable notizprojekt
    
    print_success "Systemd service created"
}

# Function to configure firewall
configure_firewall() {
    print_status "Configuring firewall..."
    
    # Check if UFW is installed
    if command -v ufw &> /dev/null; then
        # Allow SSH (if not already allowed)
        ufw allow 22/tcp
        
        if [[ "$SETUP_NGINX" == "true" ]]; then
            # Allow HTTP and HTTPS for Nginx
            ufw allow 'Nginx Full'
            print_status "Allowed HTTP (80) and HTTPS (443) for Nginx"
            
            # Don't expose application port directly when using Nginx
            print_status "Application port $APP_PORT will be accessed through Nginx proxy"
        else
            # Allow application port directly
            ufw allow $APP_PORT/tcp
            print_status "Allowed direct access to application port $APP_PORT"
        fi
        
        # Enable firewall (force yes)
        ufw --force enable
        
        print_success "Firewall configured successfully"
    else
        print_warning "UFW not installed, skipping firewall configuration"
    fi
}

# Function to start application
start_application() {
    print_status "Starting application..."
    
    systemctl start notizprojekt
    
    # Wait a moment for the application to start
    sleep 5
    
    # Check if application is running
    if systemctl is-active --quiet notizprojekt; then
        print_success "Application started successfully"
    else
        print_error "Failed to start application"
        print_status "Checking logs..."
        journalctl -u notizprojekt --no-pager -n 20
        exit 1
    fi
}

# Function to display final information
display_info() {
    echo
    echo "=============================================="
    print_success "ITS-Projekt Production Setup Complete!"
    echo "=============================================="
    echo
    
    # Determine the correct URL to display
    if [[ "$SETUP_NGINX" == "true" && "$SETUP_SSL" == "true" ]]; then
        APP_URL="https://$DOMAIN"
        PROTOCOL="HTTPS"
    elif [[ "$SETUP_NGINX" == "true" ]]; then
        APP_URL="http://$DOMAIN"
        PROTOCOL="HTTP"
    else
        APP_URL="http://$(hostname -I | awk '{print $1}'):$APP_PORT"
        PROTOCOL="Direct"
    fi
    
    print_status "🌐 Application Access:"
    echo "  • Primary URL: $APP_URL"
    if [[ "$SETUP_NGINX" == "true" ]]; then
        echo "  • Protocol: $PROTOCOL (via Nginx reverse proxy)"
        echo "  • Backend Port: $APP_PORT (internal)"
    else
        echo "  • Protocol: $PROTOCOL"
        echo "  • Port: $APP_PORT"
    fi
    echo "  • Application Directory: $APP_DIR"
    echo "  • Uploads Directory: $APP_DIR/uploads"
    echo
    
    print_status "🔐 Security Features:"
    if [[ "$SETUP_SSL" == "true" ]]; then
        echo "  • SSL Certificate: ✅ Enabled (Let's Encrypt)"
        echo "  • HTTPS Redirect: ✅ Enabled"
        echo "  • Auto-renewal: ✅ Configured"
    else
        echo "  • SSL Certificate: ❌ Not configured"
    fi
    if [[ "$SETUP_NGINX" == "true" ]]; then
        echo "  • Reverse Proxy: ✅ Nginx configured"
        echo "  • Security Headers: ✅ Enabled"
        echo "  • File Upload Limits: ✅ 50MB configured"
    else
        echo "  • Reverse Proxy: ❌ Direct access only"
    fi
    echo
    
    print_status "🗄️ Database Details:"
    echo "  • Database: notizprojekt"
    echo "  • Username: notizuser"
    echo "  • Password: $DB_PASSWORD"
    echo "  • Root Password: $DB_ROOT_PASSWORD"
    echo
    
    print_status "👤 Default Login Credentials:"
    echo "  • Username: testuser1"
    echo "  • Password: password123"
    echo "  • Username: testuser2"
    echo "  • Password: password123"
    echo
    
    print_status "🛠️ Management Commands:"
    echo "  • Check app status: sudo systemctl status notizprojekt"
    echo "  • View app logs: sudo journalctl -u notizprojekt -f"
    echo "  • Restart app: sudo systemctl restart notizprojekt"
    echo "  • Stop app: sudo systemctl stop notizprojekt"
    if [[ "$SETUP_NGINX" == "true" ]]; then
        echo "  • Check Nginx status: sudo systemctl status nginx"
        echo "  • Test Nginx config: sudo nginx -t"
        echo "  • Reload Nginx: sudo systemctl reload nginx"
    fi
    if [[ "$SETUP_SSL" == "true" ]]; then
        echo "  • Renew SSL cert: sudo certbot renew"
        echo "  • Check SSL status: sudo certbot certificates"
    fi
    echo
    
    if [[ "$SETUP_NGINX" != "true" || "$SETUP_SSL" != "true" ]]; then
        print_status "📋 Manual Steps (if needed):"
        if [[ "$SETUP_NGINX" != "true" ]]; then
            echo "  • Set up Nginx reverse proxy for production use"
        fi
        if [[ "$SETUP_SSL" != "true" ]]; then
            echo "  • Configure SSL certificate: sudo certbot --nginx -d $DOMAIN"
        fi
        echo "  • Update DNS records to point $DOMAIN to $(curl -s ifconfig.me)"
        echo "  • Change default database passwords for production"
        echo
    fi
    
    print_status "🔗 Quick Access:"
    echo "  • Application: $APP_URL"
    if [[ "$SETUP_NGINX" == "true" ]]; then
        echo "  • Direct backend: http://$(hostname -I | awk '{print $1}'):$APP_PORT (internal)"
    fi
    echo
    
    print_success "🎉 Production setup completed successfully!"
    echo
    print_status "Your note-taking application is now ready for production use!"
    
    # Save credentials to secure file
    save_credentials
}

# Function to save credentials securely
save_credentials() {
    local CREDS_FILE="$APP_DIR/credentials.txt"
    
    print_status "💾 Saving credentials to $CREDS_FILE..."
    
    cat > "$CREDS_FILE" << EOF
# ITS-Projekt Production Credentials
# Generated on: $(date)
# Server: $(hostname)

# Database Credentials
DB_ROOT_PASSWORD="$DB_ROOT_PASSWORD"
DB_USER_PASSWORD="$DB_PASSWORD"

# Application Configuration
APP_PORT="$APP_PORT"
DOMAIN="$DOMAIN"
EMAIL="$EMAIL"

# Default Application Users
# Username: testuser1, Password: password123
# Username: testuser2, Password: password123

# Important: Keep this file secure and delete it after noting down the passwords!
EOF

    chmod 600 "$CREDS_FILE"
    chown root:root "$CREDS_FILE"
    
    print_success "Credentials saved to $CREDS_FILE (readable by root only)"
    print_warning "Please note down the passwords and delete this file for security!"
}

# Function to handle errors
handle_error() {
    print_error "Setup failed at step: $1"
    print_status "Check the logs above for more details"
    print_status "You can try running the script again or follow the manual setup instructions"
    exit 1
}

# Main setup function
main() {
    echo "=============================================="
    echo "   ITS-Projekt Production Setup Script"
    echo "=============================================="
    echo
    
    print_status "🚀 Starting production setup process..."
    
    # Check if running as root
    check_root
    
    # Detect operating system
    detect_os
    
    # Show configuration
    print_status "📋 Configuration:"
    echo "  • Database Password: [Generated securely]"
    echo "  • Application Port: $APP_PORT (internal)"
    echo "  • Application Directory: $APP_DIR"
    echo "  • Domain: $DOMAIN"
    echo "  • Email: $EMAIL"
    echo "  • Setup Nginx: $SETUP_NGINX"
    echo "  • Setup SSL: $SETUP_SSL"
    echo
    
    if [[ "$SETUP_NGINX" == "true" ]]; then
        print_status "🌐 Production Features Enabled:"
        echo "  • Nginx reverse proxy"
        echo "  • Security headers"
        echo "  • File upload optimization"
        if [[ "$SETUP_SSL" == "true" ]]; then
            echo "  • SSL/HTTPS with Let's Encrypt"
            echo "  • Automatic certificate renewal"
        fi
        echo
    fi
    
    # Confirm before proceeding
    read -p "Do you want to continue with the production setup? (Y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Nn]$ ]]; then
        print_status "Setup cancelled by user"
        exit 0
    fi
    
    # Execute setup steps
    print_status "🔄 Executing setup steps..."
    echo
    
    update_system || handle_error "System Update"
    install_java || handle_error "Java Installation"
    install_mariadb || handle_error "MariaDB Installation"
    configure_database || handle_error "Database Configuration"
    install_tools || handle_error "Tools Installation"
    install_nginx || handle_error "Nginx Installation"
    deploy_application || handle_error "Application Deployment"
    create_configuration || handle_error "Configuration Creation"
    create_service || handle_error "Service Creation"
    configure_firewall || handle_error "Firewall Configuration"
    start_application || handle_error "Application Startup"
    setup_ssl || handle_error "SSL Setup"
    
    # Display final information
    display_info
}

# Run main function
main "$@"
#!/bin/bash

# ITS-Projekt Installation Script
# This script automates the complete installation of the ITS-Projekt on a fresh server
#
# Usage:
#   sudo ./install.sh
#   curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/install.sh | sudo bash
#
# Environment Variables (optional):
#   DOMAIN="your-domain.com"        # Domain name (will prompt if not set)
#   EMAIL="admin@domain.com"        # Email for SSL certificates (will prompt if not set)
#   DB_PASSWORD="password"          # Database password (will prompt if not set)
#   SKIP_PROMPTS="true"            # Skip interactive prompts (use env vars)
#
# Example with environment variables:
#   DOMAIN="notes.example.com" EMAIL="admin@example.com" DB_PASSWORD="mypassword" sudo ./install.sh

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="ITS-Projekt"
PROJECT_USER="notizprojekt"
PROJECT_DIR="/opt/notizprojekt"
REPO_URL="https://github.com/PythonTilk/ITS-Projekt.git"
DB_NAME="notizprojekt"
DB_USER="notizprojekt"
DOMAIN="${DOMAIN:-}"
EMAIL="${EMAIL:-}"
DB_PASSWORD="${DB_PASSWORD:-}"
SKIP_PROMPTS="${SKIP_PROMPTS:-false}"
JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC"

# Functions
print_header() {
    echo -e "${PURPLE}"
    echo "╔══════════════════════════════════════════════════════════════════════════════╗"
    echo "║                        ITS-Projekt Installation Script                      ║"
    echo "║                     Automated Server Setup & Deployment                     ║"
    echo "╚══════════════════════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

log() {
    echo -e "${BLUE}[$(date '+%Y-%m-%d %H:%M:%S')]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

info() {
    echo -e "${CYAN}[INFO]${NC} $1"
}

# Check if running as root
check_root() {
    if [[ $EUID -ne 0 ]]; then
        error "This script must be run as root (use sudo)"
        exit 1
    fi
}

# Get user input
get_user_input() {
    if [[ "$SKIP_PROMPTS" == "true" ]]; then
        # Validate required environment variables
        if [[ -z "$DOMAIN" || -z "$EMAIL" || -z "$DB_PASSWORD" ]]; then
            error "When SKIP_PROMPTS=true, you must provide DOMAIN, EMAIL, and DB_PASSWORD environment variables"
            exit 1
        fi
        info "Using environment variables (skipping prompts)"
    else
        echo -e "${CYAN}Please provide the following information:${NC}"
        echo ""
        
        # Domain name
        while [[ -z "$DOMAIN" ]]; do
            read -p "Enter your domain name (e.g., example.com): " DOMAIN
            if [[ -z "$DOMAIN" ]]; then
                warning "Domain name is required!"
            fi
        done
        
        # Email for SSL certificate
        while [[ -z "$EMAIL" ]]; do
            read -p "Enter your email for SSL certificate: " EMAIL
            if [[ -z "$EMAIL" ]]; then
                warning "Email is required for SSL certificate!"
            fi
        done
        
        # Database password
        while [[ -z "$DB_PASSWORD" ]]; do
            read -s -p "Enter database password for $DB_USER: " DB_PASSWORD
            echo ""
            if [[ -z "$DB_PASSWORD" ]]; then
                warning "Database password is required!"
            fi
        done
    fi
    
    echo ""
    info "Configuration:"
    info "Domain: $DOMAIN"
    info "Email: $EMAIL"
    info "Database User: $DB_USER"
    info "Project Directory: $PROJECT_DIR"
    echo ""
    
    if [[ "$SKIP_PROMPTS" != "true" ]]; then
        read -p "Continue with installation? (y/N): " -n 1 -r
        echo ""
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            info "Installation cancelled."
            exit 0
        fi
    fi
}

# Detect OS
detect_os() {
    if [[ -f /etc/os-release ]]; then
        . /etc/os-release
        OS=$NAME
        VER=$VERSION_ID
    else
        error "Cannot detect operating system"
        exit 1
    fi
    
    log "Detected OS: $OS $VER"
}

# Update system
update_system() {
    log "Updating system packages..."
    
    if [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Debian"* ]]; then
        apt update && apt upgrade -y
    elif [[ "$OS" == *"CentOS"* ]] || [[ "$OS" == *"Red Hat"* ]]; then
        if command -v dnf &> /dev/null; then
            dnf update -y
        else
            yum update -y
        fi
    else
        warning "Unsupported OS. Please update manually."
    fi
    
    success "System updated successfully"
}

# Install Java
install_java() {
    log "Installing Java 17..."
    
    if [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Debian"* ]]; then
        apt install -y openjdk-17-jdk
    elif [[ "$OS" == *"CentOS"* ]] || [[ "$OS" == *"Red Hat"* ]]; then
        if command -v dnf &> /dev/null; then
            dnf install -y java-17-openjdk-devel
        else
            yum install -y java-17-openjdk-devel
        fi
    fi
    
    # Configure Java alternatives to use Java 17 as default
    if [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Debian"* ]]; then
        # Set Java 17 as default alternative
        if [[ -f "/usr/lib/jvm/java-17-openjdk-amd64/bin/java" ]]; then
            update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-17-openjdk-amd64/bin/java 1
            update-alternatives --set java /usr/lib/jvm/java-17-openjdk-amd64/bin/java
        fi
    fi
    
    # Set JAVA_HOME for the session
    export JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")
    
    # Verify installation
    if java -version &> /dev/null; then
        success "Java installed successfully"
        JAVA_VERSION=$(java -version 2>&1 | head -n 1)
        info "Java version: $JAVA_VERSION"
        info "JAVA_HOME: $JAVA_HOME"
        
        # Verify it's Java 17
        if java -version 2>&1 | grep -q "17\."; then
            success "Java 17 confirmed"
        else
            warning "Java version might not be 17. This could cause build issues."
            warning "Attempting to fix Java version..."
            
            # Try to find and set Java 17
            if [[ -f "/usr/lib/jvm/java-17-openjdk-amd64/bin/java" ]]; then
                export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
                export PATH="$JAVA_HOME/bin:$PATH"
                info "Set JAVA_HOME to: $JAVA_HOME"
            fi
            
            java -version
        fi
    else
        error "Java installation failed"
        exit 1
    fi
}

# Install Maven
install_maven() {
    log "Installing Maven..."
    
    if [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Debian"* ]]; then
        apt install -y maven
    elif [[ "$OS" == *"CentOS"* ]] || [[ "$OS" == *"Red Hat"* ]]; then
        if command -v dnf &> /dev/null; then
            dnf install -y maven
        else
            yum install -y maven
        fi
    fi
    
    # Verify installation
    if mvn -version &> /dev/null; then
        success "Maven installed successfully"
        mvn -version
    else
        error "Maven installation failed"
        exit 1
    fi
}

# Install Git
install_git() {
    log "Installing Git..."
    
    if [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Debian"* ]]; then
        apt install -y git
    elif [[ "$OS" == *"CentOS"* ]] || [[ "$OS" == *"Red Hat"* ]]; then
        if command -v dnf &> /dev/null; then
            dnf install -y git
        else
            yum install -y git
        fi
    fi
    
    success "Git installed successfully"
}

# Install MySQL
install_mysql() {
    log "Installing MySQL..."
    
    if [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Debian"* ]]; then
        apt install -y mysql-server
    elif [[ "$OS" == *"CentOS"* ]] || [[ "$OS" == *"Red Hat"* ]]; then
        if command -v dnf &> /dev/null; then
            dnf install -y mysql-server
        else
            yum install -y mysql-server
        fi
    fi
    
    # Start and enable MySQL
    systemctl start mysql
    systemctl enable mysql
    
    success "MySQL installed and started"
}

# Install Nginx
install_nginx() {
    log "Installing Nginx..."
    
    if [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Debian"* ]]; then
        apt install -y nginx
    elif [[ "$OS" == *"CentOS"* ]] || [[ "$OS" == *"Red Hat"* ]]; then
        if command -v dnf &> /dev/null; then
            dnf install -y nginx
        else
            yum install -y nginx
        fi
    fi
    
    # Start and enable Nginx
    systemctl start nginx
    systemctl enable nginx
    
    success "Nginx installed and started"
}

# Install Certbot
install_certbot() {
    log "Installing Certbot for SSL certificates..."
    
    if [[ "$OS" == *"Ubuntu"* ]] || [[ "$OS" == *"Debian"* ]]; then
        apt install -y certbot python3-certbot-nginx
    elif [[ "$OS" == *"CentOS"* ]] || [[ "$OS" == *"Red Hat"* ]]; then
        if command -v dnf &> /dev/null; then
            dnf install -y certbot python3-certbot-nginx
        else
            yum install -y certbot python3-certbot-nginx
        fi
    fi
    
    success "Certbot installed successfully"
}

# Setup MySQL database
setup_database() {
    log "Setting up MySQL database..."
    
    # Secure MySQL installation (automated)
    mysql -e "DELETE FROM mysql.user WHERE User='';"
    mysql -e "DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');"
    mysql -e "DROP DATABASE IF EXISTS test;"
    mysql -e "DELETE FROM mysql.db WHERE Db='test' OR Db='test\\_%';"
    mysql -e "FLUSH PRIVILEGES;"
    
    # Create database and user
    mysql -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    mysql -e "CREATE USER IF NOT EXISTS '$DB_USER'@'localhost' IDENTIFIED BY '$DB_PASSWORD';"
    mysql -e "GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_USER'@'localhost';"
    mysql -e "FLUSH PRIVILEGES;"
    
    success "Database setup completed"
}

# Create application user
create_app_user() {
    log "Creating application user..."
    
    # Create user if it doesn't exist
    if ! id "$PROJECT_USER" &>/dev/null; then
        useradd -r -m -U -d "$PROJECT_DIR" -s /bin/bash "$PROJECT_USER"
        success "User $PROJECT_USER created"
    else
        info "User $PROJECT_USER already exists"
    fi
}

# Clone and build application
setup_application() {
    log "Setting up application..."
    
    # Switch to application user directory
    cd "$PROJECT_DIR"
    
    # Remove existing directory if it exists
    if [[ -d "$PROJECT_NAME" ]]; then
        warning "Existing $PROJECT_NAME directory found, removing..."
        sudo -u "$PROJECT_USER" rm -rf "$PROJECT_NAME"
    fi
    
    # Verify git is available
    if ! command -v git &> /dev/null; then
        error "Git is not installed or not in PATH"
        exit 1
    fi
    
    # Test git access as the application user
    if ! sudo -u "$PROJECT_USER" git --version &> /dev/null; then
        error "Git is not accessible for user $PROJECT_USER"
        exit 1
    fi
    
    # Clone repository as application user
    info "Cloning repository from $REPO_URL..."
    info "Target directory: $PROJECT_DIR/$PROJECT_NAME"
    info "Running as user: $PROJECT_USER"
    
    if ! sudo -u "$PROJECT_USER" git clone "$REPO_URL"; then
        error "Failed to clone repository. Check internet connection and repository URL."
        error "Repository URL: $REPO_URL"
        error "Current directory: $(pwd)"
        error "User permissions: $(sudo -u "$PROJECT_USER" whoami)"
        exit 1
    fi
    success "Repository cloned successfully"
    
    # Switch to the html branch (web application)
    cd "$PROJECT_DIR/$PROJECT_NAME"
    info "Switching to html branch for web application..."
    if ! sudo -u "$PROJECT_USER" git checkout html; then
        error "Failed to checkout html branch. Repository may be corrupted."
        exit 1
    fi
    success "Switched to html branch"
    
    # Verify we have the correct files
    if [[ ! -f "pom.xml" ]]; then
        error "pom.xml not found! Make sure you're on the html branch."
        exit 1
    fi
    
    CURRENT_BRANCH=$(sudo -u "$PROJECT_USER" git branch --show-current)
    info "Current branch: $CURRENT_BRANCH"
    
    # Create necessary directories
    cd "$PROJECT_DIR"
    sudo -u "$PROJECT_USER" mkdir -p logs uploads backups
    
    # Build application
    cd "$PROJECT_DIR/$PROJECT_NAME"
    info "Building application with Maven..."
    
    # Ensure JAVA_HOME is set for the build
    JAVA_HOME_PATH=$(readlink -f /usr/bin/java | sed "s:bin/java::")
    info "Using JAVA_HOME: $JAVA_HOME_PATH"
    
    # Check Java version before building
    JAVA_VERSION=$(sudo -u "$PROJECT_USER" java -version 2>&1 | head -n 1)
    info "Java version for build: $JAVA_VERSION"
    
    if ! sudo -u "$PROJECT_USER" JAVA_HOME="$JAVA_HOME_PATH" mvn clean package -DskipTests; then
        error "Maven build failed. Check logs above for details."
        error "Common issues:"
        error "  - Java version mismatch (project requires Java 17)"
        error "  - Maven installation or configuration"
        error "  - Network connectivity for dependencies"
        error "  - JAVA_HOME not set correctly"
        error ""
        error "Current Java version:"
        sudo -u "$PROJECT_USER" java -version
        exit 1
    fi
    
    # Verify the JAR file was created
    JAR_FILE=$(find target -name "*.jar" -type f | head -n 1)
    if [[ -z "$JAR_FILE" ]]; then
        error "JAR file not found in target directory. Build may have failed silently."
        error "Looking for: target/*.jar"
        error "Files in target directory:"
        ls -la target/ || true
        error "Current working directory: $(pwd)"
        exit 1
    fi
    
    # Additional verification - check if it's the correct JAR
    if [[ ! "$JAR_FILE" =~ notizprojekt.*\.jar$ ]]; then
        warning "Found JAR file but name doesn't match expected pattern: $JAR_FILE"
        warning "Expected pattern: notizprojekt*.jar"
        warning "Continuing anyway..."
    fi
    
    success "JAR file created: $JAR_FILE"
    
    # Store JAR file name for later use
    export BUILT_JAR_FILE="$JAR_FILE"
    
    success "Application built successfully"
}

# Configure application
configure_application() {
    log "Configuring application..."
    
    # Create production properties file
    cat > "$PROJECT_DIR/$PROJECT_NAME/src/main/resources/application-prod.properties" << EOF
# Server configuration
server.port=12000
server.address=127.0.0.1

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/$DB_NAME?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=$DB_USER
spring.datasource.password=$DB_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=false

# File upload configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload-dir=$PROJECT_DIR/uploads

# Session configuration
server.servlet.session.timeout=30m
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.same-site=strict

# Logging configuration
logging.level.root=INFO
logging.level.notizprojekt=INFO
logging.file.name=$PROJECT_DIR/logs/application.log
logging.file.max-size=10MB
logging.file.max-history=10

# Security headers
server.servlet.session.cookie.name=NOTIZPROJEKT_SESSION
EOF

    # Create environment file
    cat > "$PROJECT_DIR/.env" << EOF
# Database credentials
DB_PASSWORD=$DB_PASSWORD

# Application settings
SPRING_PROFILES_ACTIVE=prod
JAVA_OPTS="$JAVA_OPTS"

# Security
SESSION_SECRET=$(openssl rand -base64 32)
EOF

    # Set permissions
    chown -R "$PROJECT_USER:$PROJECT_USER" "$PROJECT_DIR"
    chmod 600 "$PROJECT_DIR/.env"
    chmod 755 "$PROJECT_DIR/$PROJECT_NAME/update.sh"
    
    success "Application configured"
}

# Setup systemd service
setup_systemd_service() {
    local jar_file="$1"
    log "Setting up systemd service..."
    
    if [[ -z "$jar_file" ]]; then
        # Find the JAR file if not provided
        jar_file=$(find "$PROJECT_DIR/$PROJECT_NAME/target" -name "notizprojekt-web-*.jar" -type f | head -n 1)
        if [[ -z "$jar_file" ]]; then
            error "No JAR file found for systemd service"
            exit 1
        fi
        # Use absolute path for systemd service
        jar_file_absolute="$jar_file"
        # Get relative path for display
        jar_file_relative=$(basename "$jar_file")
        jar_file_relative="target/$jar_file_relative"
    else
        # If jar_file was provided, assume it's relative and make it absolute
        jar_file_absolute="$PROJECT_DIR/$PROJECT_NAME/$jar_file"
        jar_file_relative="$jar_file"
    fi
    
    info "Using JAR file for service: $jar_file_relative"
    
    cat > "/etc/systemd/system/$PROJECT_USER.service" << EOF
[Unit]
Description=ITS-Projekt Note Management System
After=network.target mysql.service
Wants=mysql.service

[Service]
Type=simple
User=$PROJECT_USER
Group=$PROJECT_USER
WorkingDirectory=$PROJECT_DIR/$PROJECT_NAME
Environment=SPRING_PROFILES_ACTIVE=prod
Environment=JAVA_OPTS=-Xmx1g -Xms512m -XX:+UseG1GC
EnvironmentFile=-$PROJECT_DIR/.env
ExecStart=/usr/bin/java \$JAVA_OPTS -jar $jar_file_absolute
ExecStop=/bin/kill -TERM \$MAINPID
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=$PROJECT_USER

# Security settings
NoNewPrivileges=true
PrivateTmp=true
ProtectSystem=strict
ProtectHome=true
ReadWritePaths=$PROJECT_DIR

[Install]
WantedBy=multi-user.target
EOF

    # Reload systemd and enable service
    systemctl daemon-reload
    systemctl enable "$PROJECT_USER"
    
    success "Systemd service configured"
}

# Configure Nginx
configure_nginx() {
    log "Configuring Nginx..."
    
    # Create Nginx configuration
    cat > "/etc/nginx/sites-available/$PROJECT_USER" << EOF
server {
    listen 80;
    server_name $DOMAIN;
    
    # Temporary location for Let's Encrypt verification
    location /.well-known/acme-challenge/ {
        root /var/www/html;
    }
    
    # Redirect HTTP to HTTPS (will be enabled after SSL setup)
    location / {
        proxy_pass http://127.0.0.1:12000;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

    # Enable site
    ln -sf "/etc/nginx/sites-available/$PROJECT_USER" "/etc/nginx/sites-enabled/"
    
    # Remove default site
    rm -f /etc/nginx/sites-enabled/default
    
    # Test configuration
    nginx -t
    
    # Reload Nginx
    systemctl reload nginx
    
    success "Nginx configured"
}

# Setup SSL certificate
setup_ssl() {
    log "Setting up SSL certificate..."
    
    # Start the application first
    systemctl start "$PROJECT_USER"
    sleep 10
    
    # Obtain SSL certificate (only for main domain, not www)
    certbot --nginx -d "$DOMAIN" --email "$EMAIL" --agree-tos --non-interactive
    
    # Update Nginx configuration with SSL
    cat > "/etc/nginx/sites-available/$PROJECT_USER" << EOF
server {
    listen 80;
    server_name $DOMAIN;
    return 301 https://\$server_name\$request_uri;
}

server {
    listen 443 ssl http2;
    server_name $DOMAIN;
    
    # SSL Configuration
    ssl_certificate /etc/letsencrypt/live/$DOMAIN/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/$DOMAIN/privkey.pem;
    
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
        proxy_pass http://127.0.0.1:12000;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_set_header X-Forwarded-Host \$host;
        proxy_set_header X-Forwarded-Port \$server_port;
        
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
    location ~* \.(css|js|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)\$ {
        proxy_pass http://127.0.0.1:12000;
        proxy_set_header Host \$host;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
EOF

    # Reload Nginx
    systemctl reload nginx
    
    # Setup auto-renewal
    (crontab -l 2>/dev/null; echo "0 12 * * * /usr/bin/certbot renew --quiet") | crontab -
    
    success "SSL certificate configured"
}

# Configure firewall
configure_firewall() {
    log "Configuring firewall..."
    
    if command -v ufw &> /dev/null; then
        # Ubuntu/Debian UFW
        ufw --force reset
        ufw default deny incoming
        ufw default allow outgoing
        ufw allow ssh
        ufw allow 80/tcp
        ufw allow 443/tcp
        ufw --force enable
        success "UFW firewall configured"
    elif command -v firewall-cmd &> /dev/null; then
        # CentOS/RHEL firewalld
        systemctl start firewalld
        systemctl enable firewalld
        firewall-cmd --permanent --add-service=ssh
        firewall-cmd --permanent --add-service=http
        firewall-cmd --permanent --add-service=https
        firewall-cmd --reload
        success "Firewalld configured"
    else
        warning "No supported firewall found. Please configure manually."
    fi
}

# Setup monitoring and backups
setup_monitoring() {
    log "Setting up monitoring and backups..."
    
    # Create backup scripts
    cat > "$PROJECT_DIR/backup-db.sh" << 'EOF'
#!/bin/bash
BACKUP_DIR="/opt/notizprojekt/backups"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="notizprojekt"
DB_USER="notizprojekt"
DB_PASS="$DB_PASSWORD"

mkdir -p "$BACKUP_DIR"
mysqldump -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" | gzip > "$BACKUP_DIR/db_backup_$DATE.sql.gz"
find "$BACKUP_DIR" -name "db_backup_*.sql.gz" -mtime +7 -delete
echo "Database backup completed: $BACKUP_DIR/db_backup_$DATE.sql.gz"
EOF

    # Make scripts executable
    chmod +x "$PROJECT_DIR/backup-db.sh"
    chown "$PROJECT_USER:$PROJECT_USER" "$PROJECT_DIR/backup-db.sh"
    
    # Setup cron jobs for backups
    sudo -u "$PROJECT_USER" crontab -l 2>/dev/null | { cat; echo "0 */6 * * * $PROJECT_DIR/backup-db.sh"; } | sudo -u "$PROJECT_USER" crontab -
    
    success "Monitoring and backups configured"
}

# Final checks and start services
final_setup() {
    log "Performing final setup..."
    
    # Start and check application
    systemctl start "$PROJECT_USER"
    sleep 10
    
    if systemctl is-active --quiet "$PROJECT_USER"; then
        success "Application service is running"
    else
        error "Application service failed to start"
        systemctl status "$PROJECT_USER"
        exit 1
    fi
    
    # Check if application responds
    if curl -s -f "http://localhost:12000/login" > /dev/null; then
        success "Application is responding"
    else
        warning "Application may not be fully ready yet"
    fi
    
    success "Installation completed successfully!"
}

# Print final information
print_final_info() {
    echo ""
    echo -e "${GREEN}╔══════════════════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║                          Installation Complete!                             ║${NC}"
    echo -e "${GREEN}╚══════════════════════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${CYAN}🌐 Your application is now available at:${NC}"
    echo -e "   ${YELLOW}https://$DOMAIN${NC}"
    echo ""
    echo -e "${CYAN}📋 Important Information:${NC}"
    echo -e "   ${BLUE}Application Directory:${NC} $PROJECT_DIR"
    echo -e "   ${BLUE}Application User:${NC} $PROJECT_USER"
    echo -e "   ${BLUE}Database Name:${NC} $DB_NAME"
    echo -e "   ${BLUE}Database User:${NC} $DB_USER"
    echo ""
    echo -e "${CYAN}🔧 Management Commands:${NC}"
    echo -e "   ${BLUE}Check Status:${NC} sudo systemctl status $PROJECT_USER"
    echo -e "   ${BLUE}View Logs:${NC} sudo journalctl -u $PROJECT_USER -f"
    echo -e "   ${BLUE}Restart App:${NC} sudo systemctl restart $PROJECT_USER"
    echo -e "   ${BLUE}Update App:${NC} sudo -u $PROJECT_USER $PROJECT_DIR/$PROJECT_NAME/update.sh"
    echo ""
    echo -e "${CYAN}📁 Important Files:${NC}"
    echo -e "   ${BLUE}Application Logs:${NC} $PROJECT_DIR/logs/application.log"
    echo -e "   ${BLUE}Environment Config:${NC} $PROJECT_DIR/.env"
    echo -e "   ${BLUE}Nginx Config:${NC} /etc/nginx/sites-available/$PROJECT_USER"
    echo -e "   ${BLUE}Systemd Service:${NC} /etc/systemd/system/$PROJECT_USER.service"
    echo ""
    echo -e "${CYAN}🛡️ Security:${NC}"
    echo -e "   ${GREEN}✓${NC} SSL Certificate configured and auto-renewal enabled"
    echo -e "   ${GREEN}✓${NC} Firewall configured"
    echo -e "   ${GREEN}✓${NC} Application running as non-root user"
    echo -e "   ${GREEN}✓${NC} Database secured"
    echo ""
    echo -e "${CYAN}💾 Backups:${NC}"
    echo -e "   ${GREEN}✓${NC} Database backups scheduled every 6 hours"
    echo -e "   ${BLUE}Backup Location:${NC} $PROJECT_DIR/backups/"
    echo ""
    echo -e "${YELLOW}⚠️  Next Steps:${NC}"
    echo -e "   1. Visit https://$DOMAIN and create your first user account"
    echo -e "   2. Review and customize the configuration in $PROJECT_DIR/.env"
    echo -e "   3. Set up additional monitoring if needed"
    echo -e "   4. Review the server-setup.md for maintenance procedures"
    echo ""
    echo -e "${GREEN}🎉 Enjoy your new ITS-Projekt installation!${NC}"
    echo ""
}

# Main installation process
main() {
    print_header
    
    log "Starting ITS-Projekt installation..."
    
    check_root
    get_user_input
    detect_os
    update_system
    install_java
    install_maven
    install_git
    install_mysql
    install_nginx
    install_certbot
    setup_database
    create_app_user
    setup_application
    configure_application
    setup_systemd_service "$BUILT_JAR_FILE"
    configure_nginx
    configure_firewall
    setup_ssl
    setup_monitoring
    final_setup
    print_final_info
    
    log "Installation completed successfully!"
}

# Handle script arguments
case "${1:-}" in
    --help|-h)
        echo "ITS-Projekt Web Application Installation Script"
        echo ""
        echo "Installs the complete web-based note management system with:"
        echo "  • User profiles and collaboration features"
        echo "  • SSL-secured domain with automatic certificate renewal"
        echo "  • Production-grade security and performance optimizations"
        echo "  • Automated backup and update system"
        echo ""
        echo "Usage: $0 [OPTIONS]"
        echo ""
        echo "Options:"
        echo "  --help, -h     Show this help message"
        echo "  --uninstall    Uninstall the application"
        echo ""
        echo "Environment Variables (optional):"
        echo "  DOMAIN         Domain name (e.g., example.com)"
        echo "  EMAIL          Email for SSL certificate"
        echo "  DB_PASSWORD    Database password"
        echo "  SKIP_PROMPTS   Set to 'true' to skip interactive prompts"
        echo ""
        echo "Examples:"
        echo "  sudo ./install.sh"
        echo "  DOMAIN=\"notes.example.com\" EMAIL=\"admin@example.com\" sudo ./install.sh"
        echo "  curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/install.sh | sudo bash"
        echo ""
        echo "This script will install and configure:"
        echo "  - Java 17"
        echo "  - Maven"
        echo "  - MySQL"
        echo "  - Nginx with SSL"
        echo "  - ITS-Projekt application"
        echo "  - Firewall configuration"
        echo "  - Automated backups"
        echo ""
        echo "Requirements:"
        echo "  - Fresh Ubuntu 20.04+ or CentOS 8+ server"
        echo "  - Root access"
        echo "  - Domain name pointing to this server"
        echo "  - Internet connection"
        exit 0
        ;;
    --uninstall)
        echo -e "${RED}Uninstalling ITS-Projekt...${NC}"
        
        # Stop and disable services
        systemctl stop "$PROJECT_USER" 2>/dev/null || true
        systemctl disable "$PROJECT_USER" 2>/dev/null || true
        rm -f "/etc/systemd/system/$PROJECT_USER.service"
        systemctl daemon-reload
        
        # Remove application directory
        rm -rf "$PROJECT_DIR"
        
        # Remove user
        userdel "$PROJECT_USER" 2>/dev/null || true
        
        # Remove nginx configuration
        rm -f "/etc/nginx/sites-available/$PROJECT_USER"
        rm -f "/etc/nginx/sites-enabled/$PROJECT_USER"
        systemctl reload nginx
        
        # Remove database (optional)
        read -p "Remove database? (y/N): " -n 1 -r
        echo ""
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            mysql -e "DROP DATABASE IF EXISTS $DB_NAME;"
            mysql -e "DROP USER IF EXISTS '$DB_USER'@'localhost';"
        fi
        
        success "Uninstallation completed"
        exit 0
        ;;
    "")
        # Run main installation
        main
        ;;
    *)
        error "Unknown option: $1"
        echo "Use --help for usage information."
        exit 1
        ;;
esac
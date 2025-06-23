#!/bin/bash

# ITS-Projekt Automated Setup Script
# This script sets up the ITS-Projekt note-taking application on Ubuntu/Debian systems

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration variables (can be overridden by environment variables)
DB_PASSWORD=${DB_PASSWORD:-"notizpassword"}
APP_PORT=${APP_PORT:-"12000"}
DOMAIN=${DOMAIN:-""}
APP_DIR="/opt/notizprojekt"

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
    print_status "Installing Java 11..."
    
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
        print_status "Java is already installed: $JAVA_VERSION"
        
        # Check if it's Java 11 or higher
        if [[ "$JAVA_VERSION" =~ ^1\.[0-7]\. ]] || [[ "$JAVA_VERSION" =~ ^[0-9]\. ]] && [[ ! "$JAVA_VERSION" =~ ^1[1-9]\. ]]; then
            print_warning "Java version is too old. Installing Java 11..."
            apt install -y openjdk-11-jdk
        fi
    else
        apt install -y openjdk-11-jdk
    fi
    
    # Set JAVA_HOME
    JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")
    echo "export JAVA_HOME=$JAVA_HOME" >> /etc/environment
    
    print_success "Java 11 installed successfully"
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
    
    # Set root password
    mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '$DB_PASSWORD';" 2>/dev/null || true
    
    # Download and import database schema
    print_status "Downloading database schema..."
    cd /tmp
    wget -q https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/its-projekt18.6.sql
    
    print_status "Importing database schema..."
    mysql -u root -p$DB_PASSWORD < its-projekt18.6.sql
    
    print_success "Database configured successfully"
}

# Function to install Git and other tools
install_tools() {
    print_status "Installing additional tools..."
    apt install -y git curl wget unzip net-tools
    print_success "Additional tools installed"
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
    ./mvnw clean package -DskipTests -q
    
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
spring.datasource.url=jdbc:mysql://localhost:3306/notizprojekt
spring.datasource.username=notizuser
spring.datasource.password=notizpassword
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
    JAR_FILE=$(find $APP_DIR/target -name "*.jar" | head -1)
    if [[ -z "$JAR_FILE" ]]; then
        print_error "Could not find JAR file"
        exit 1
    fi
    
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
        # Allow application port
        ufw allow $APP_PORT/tcp
        
        # Allow SSH (if not already allowed)
        ufw allow 22/tcp
        
        # Enable firewall (force yes)
        ufw --force enable
        
        print_success "Firewall configured (allowed port $APP_PORT)"
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
    print_success "ITS-Projekt Setup Complete!"
    echo "=============================================="
    echo
    print_status "Application Details:"
    echo "  • URL: http://$(hostname -I | awk '{print $1}'):$APP_PORT"
    echo "  • Port: $APP_PORT"
    echo "  • Application Directory: $APP_DIR"
    echo "  • Uploads Directory: $APP_DIR/uploads"
    echo
    print_status "Database Details:"
    echo "  • Database: notizprojekt"
    echo "  • Username: notizuser"
    echo "  • Password: notizpassword"
    echo "  • Root Password: $DB_PASSWORD"
    echo
    print_status "Default Login Credentials:"
    echo "  • Username: testuser123"
    echo "  • Password: password123"
    echo
    print_status "Useful Commands:"
    echo "  • Check status: sudo systemctl status notizprojekt"
    echo "  • View logs: sudo journalctl -u notizprojekt -f"
    echo "  • Restart app: sudo systemctl restart notizprojekt"
    echo "  • Stop app: sudo systemctl stop notizprojekt"
    echo
    if [[ -n "$DOMAIN" ]]; then
        print_status "Next Steps:"
        echo "  • Configure your domain ($DOMAIN) to point to this server"
        echo "  • Set up SSL certificate with Let's Encrypt"
        echo "  • Configure reverse proxy (Nginx/Apache) for production use"
    else
        print_status "For Production Use:"
        echo "  • Set up a domain name"
        echo "  • Configure SSL certificate"
        echo "  • Set up reverse proxy (Nginx/Apache)"
        echo "  • Change default passwords"
    fi
    echo
    print_success "Setup completed successfully!"
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
    echo "    ITS-Projekt Automated Setup Script"
    echo "=============================================="
    echo
    
    print_status "Starting setup process..."
    
    # Check if running as root
    check_root
    
    # Detect operating system
    detect_os
    
    # Show configuration
    print_status "Configuration:"
    echo "  • Database Password: $DB_PASSWORD"
    echo "  • Application Port: $APP_PORT"
    echo "  • Application Directory: $APP_DIR"
    if [[ -n "$DOMAIN" ]]; then
        echo "  • Domain: $DOMAIN"
    fi
    echo
    
    # Confirm before proceeding
    read -p "Do you want to continue with the setup? (Y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Nn]$ ]]; then
        print_status "Setup cancelled by user"
        exit 0
    fi
    
    # Execute setup steps
    update_system || handle_error "System Update"
    install_java || handle_error "Java Installation"
    install_mariadb || handle_error "MariaDB Installation"
    configure_database || handle_error "Database Configuration"
    install_tools || handle_error "Tools Installation"
    deploy_application || handle_error "Application Deployment"
    create_configuration || handle_error "Configuration Creation"
    create_service || handle_error "Service Creation"
    configure_firewall || handle_error "Firewall Configuration"
    start_application || handle_error "Application Startup"
    
    # Display final information
    display_info
}

# Run main function
main "$@"
#!/bin/bash

# Setup script for ITS-Projekt
# This script will:
# 1. Update the system
# 2. Install required dependencies
# 3. Clone the repository
# 4. Set up MySQL database
# 5. Build and deploy the application
# 6. Configure systemd service
# 7. Set up Nginx as a reverse proxy

# Exit on error
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration variables - MODIFY THESE
DB_NAME="notizprojekt"
DB_USER="notizuser"
DB_PASSWORD="password123" # Change this to a secure password
APP_PORT=8080
GIT_REPO="https://github.com/PythonTilk/ITS-Projekt.git"
GIT_BRANCH="html"
SERVER_IP="167.172.163.254"

# Function to print status messages
print_status() {
    echo -e "${GREEN}[+] $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}[!] $1${NC}"
}

print_error() {
    echo -e "${RED}[-] $1${NC}"
}

# Check if running as root
if [ "$EUID" -ne 0 ]; then
    print_error "Please run as root"
    exit 1
fi

# 1. Update the system
print_status "Updating system packages..."
apt update && apt upgrade -y

# 2. Install required dependencies
print_status "Installing dependencies..."
apt install -y openjdk-17-jdk maven git mysql-server nginx ufw

# 3. Create a dedicated user for the application
print_status "Creating application user..."
if id "appuser" &>/dev/null; then
    print_warning "User 'appuser' already exists"
else
    useradd -m -s /bin/bash appuser
    echo "appuser:appuser123" | chpasswd
fi

# 4. Set up MySQL
print_status "Setting up MySQL..."
# Start MySQL if not running
systemctl start mysql
systemctl enable mysql

# Secure MySQL installation non-interactively
print_status "Securing MySQL installation..."
mysql -e "DELETE FROM mysql.user WHERE User='';"
mysql -e "DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');"
mysql -e "DROP DATABASE IF EXISTS test;"
mysql -e "DELETE FROM mysql.db WHERE Db='test' OR Db='test\\_%';"
mysql -e "FLUSH PRIVILEGES;"

# Create database and user
print_status "Creating database and user..."
mysql -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -e "CREATE USER IF NOT EXISTS '$DB_USER'@'localhost' IDENTIFIED BY '$DB_PASSWORD';"
mysql -e "GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_USER'@'localhost';"
mysql -e "FLUSH PRIVILEGES;"

# 5. Clone the repository
print_status "Cloning repository..."
cd /home/appuser
if [ -d "ITS-Projekt" ]; then
    print_warning "Repository already exists, pulling latest changes..."
    cd ITS-Projekt
    git fetch
    git checkout $GIT_BRANCH
    git pull origin $GIT_BRANCH
else
    git clone $GIT_REPO
    cd ITS-Projekt
    git checkout $GIT_BRANCH
fi

# 6. Configure application properties
print_status "Configuring application properties..."
mkdir -p src/main/resources
cat > src/main/resources/application.properties << EOF
# Server configuration
server.port=$APP_PORT
server.address=0.0.0.0

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/$DB_NAME?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=$DB_USER
spring.datasource.password=$DB_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=false

# Logging
logging.level.root=INFO
logging.level.notizprojekt=INFO
logging.file.name=/var/log/notizprojekt.log

# Security
spring.security.user.name=admin
spring.security.user.password=admin
EOF

# 7. Build the application
print_status "Building application..."
chmod +x mvnw
./mvnw clean package -DskipTests

# 8. Configure systemd service
print_status "Configuring systemd service..."
cat > /etc/systemd/system/notizapp.service << EOF
[Unit]
Description=Notiz Project Application
After=network.target mysql.service

[Service]
User=appuser
WorkingDirectory=/home/appuser/ITS-Projekt
ExecStart=/usr/bin/java -jar target/notizprojekt-web-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

# 9. Set proper permissions
print_status "Setting permissions..."
chown -R appuser:appuser /home/appuser/ITS-Projekt
chmod +x /home/appuser/ITS-Projekt/mvnw

# 10. Configure Nginx as a reverse proxy
print_status "Configuring Nginx..."
cat > /etc/nginx/sites-available/notizapp << EOF
server {
    listen 80;
    server_name $SERVER_IP;

    location / {
        proxy_pass http://localhost:$APP_PORT;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

# Enable the Nginx site
ln -sf /etc/nginx/sites-available/notizapp /etc/nginx/sites-enabled/
rm -f /etc/nginx/sites-enabled/default
nginx -t
systemctl restart nginx

# 11. Configure firewall
print_status "Configuring firewall..."
ufw allow ssh
ufw allow 'Nginx Full'
ufw allow $APP_PORT/tcp
ufw --force enable

# 12. Start the application
print_status "Starting application..."
systemctl daemon-reload
systemctl enable notizapp.service
systemctl start notizapp.service

# 13. Create a log file and set permissions
touch /var/log/notizprojekt.log
chown appuser:appuser /var/log/notizprojekt.log

# 14. Print summary
print_status "Setup completed successfully!"
echo -e "${GREEN}----------------------------------------${NC}"
echo -e "${GREEN}Application deployed at: http://$SERVER_IP${NC}"
echo -e "${GREEN}Database: $DB_NAME${NC}"
echo -e "${GREEN}Database User: $DB_USER${NC}"
echo -e "${GREEN}Application logs: journalctl -u notizapp.service${NC}"
echo -e "${GREEN}Nginx logs: /var/log/nginx/access.log and /var/log/nginx/error.log${NC}"
echo -e "${GREEN}Application log: /var/log/notizprojekt.log${NC}"
echo -e "${GREEN}----------------------------------------${NC}"

# Check if the application is running
if systemctl is-active --quiet notizapp.service; then
    print_status "Application is running!"
else
    print_warning "Application is not running. Check logs with: journalctl -u notizapp.service"
fi
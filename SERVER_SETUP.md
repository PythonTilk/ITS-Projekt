# Server Setup Instructions

This document provides instructions for setting up the ITS-Projekt application on a server.

## Prerequisites

- A server with Ubuntu/Debian (tested on Ubuntu 20.04/22.04)
- Root access to the server
- Server IP: 167.172.163.254 (or your own server IP)
- GitHub authentication (Personal Access Token or SSH key)

## GitHub Authentication

GitHub no longer supports password authentication for Git operations. You need to use either a Personal Access Token or SSH key.

### Option 1: Personal Access Token (PAT)

1. Go to GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Click "Generate new token" → "Generate new token (classic)"
3. Give it a name like "Server Setup"
4. Select the "repo" scope (full control of repositories)
5. Click "Generate token"
6. Copy the token (you'll only see it once)
7. Edit the setup script and replace:
   ```
   GIT_REPO="https://YOUR_GITHUB_USERNAME:YOUR_PERSONAL_ACCESS_TOKEN@github.com/PythonTilk/ITS-Projekt.git"
   ```
   with your actual username and token.

### Option 2: SSH Key

1. On your server, generate an SSH key:
   ```bash
   ssh-keygen -t ed25519 -C "your_email@example.com"
   ```
2. Display the public key:
   ```bash
   cat ~/.ssh/id_ed25519.pub
   ```
3. Go to GitHub → Settings → SSH and GPG keys → New SSH key
4. Add the public key from your server
5. Edit the setup script and replace:
   ```
   GIT_REPO="git@github.com:PythonTilk/ITS-Projekt.git"
   ```

## Automatic Setup

We've provided a setup script that automates the entire installation process.

### Step 1: Copy the setup script to your server

```bash
scp setup_server.sh root@167.172.163.254:/root/
```

### Step 2: Connect to your server

```bash
ssh root@167.172.163.254
```

### Step 3: Make the script executable and run it

```bash
chmod +x /root/setup_server.sh
/root/setup_server.sh
```

The script will:
1. Update the system
2. Install required dependencies (Java, Maven, Git, MySQL, Nginx)
3. Create a dedicated user for the application
4. Set up MySQL database
5. Clone the repository
6. Configure application properties
7. Build the application
8. Configure systemd service
9. Set up Nginx as a reverse proxy
10. Configure firewall
11. Start the application

### Step 4: Access the application

After the script completes successfully, you can access the application at:
```
http://167.172.163.254
```

## Manual Setup

If you prefer to set up the server manually, follow these steps:

### 1. Update the system

```bash
apt update && apt upgrade -y
```

### 2. Install required dependencies

```bash
apt install -y openjdk-17-jdk maven git mysql-server nginx ufw
```

### 3. Set up MySQL

```bash
systemctl start mysql
systemctl enable mysql

# Create database and user
mysql -e "CREATE DATABASE notizprojekt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -e "CREATE USER 'notizuser'@'localhost' IDENTIFIED BY 'password123';"
mysql -e "GRANT ALL PRIVILEGES ON notizprojekt.* TO 'notizuser'@'localhost';"
mysql -e "FLUSH PRIVILEGES;"
```

### 4. Clone the repository

```bash
cd /home
git clone https://github.com/PythonTilk/ITS-Projekt.git
cd ITS-Projekt
git checkout html
```

### 5. Configure application properties

Create `src/main/resources/application.properties`:

```properties
# Server configuration
server.port=8080
server.address=0.0.0.0

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/notizprojekt?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=notizuser
spring.datasource.password=password123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=false
```

### 6. Build the application

```bash
chmod +x mvnw
./mvnw clean package -DskipTests
```

### 7. Configure systemd service

Create `/etc/systemd/system/notizapp.service`:

```
[Unit]
Description=Notiz Project Application
After=network.target mysql.service

[Service]
WorkingDirectory=/home/ITS-Projekt
ExecStart=/usr/bin/java -jar target/notizprojekt-web-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
```

### 8. Configure Nginx

Create `/etc/nginx/sites-available/notizapp`:

```
server {
    listen 80;
    server_name 167.172.163.254;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Enable the site:

```bash
ln -s /etc/nginx/sites-available/notizapp /etc/nginx/sites-enabled/
rm -f /etc/nginx/sites-enabled/default
nginx -t
systemctl restart nginx
```

### 9. Configure firewall

```bash
ufw allow ssh
ufw allow 'Nginx Full'
ufw --force enable
```

### 10. Start the application

```bash
systemctl daemon-reload
systemctl enable notizapp.service
systemctl start notizapp.service
```

## Troubleshooting

If you encounter issues:

1. Check application logs:
   ```
   journalctl -u notizapp.service
   ```

2. Check Nginx logs:
   ```
   tail -f /var/log/nginx/error.log
   ```

3. Check MySQL status:
   ```
   systemctl status mysql
   ```

4. Verify the application is running:
   ```
   systemctl status notizapp.service
   ```

5. Check if the port is in use:
   ```
   netstat -tulpn | grep 8080
   ```
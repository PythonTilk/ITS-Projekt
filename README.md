# ITS-Projekt - Note Management System

A modern, feature-rich note-taking application built with Spring Boot. Create, organize, and share notes with advanced features like rich text editing, image uploads, and collaborative sharing.

## ✨ Key Features

- **Rich Text Editing** - WYSIWYG editor with formatting, code syntax highlighting
- **User Profiles** - Custom avatars, display names, and biographies  
- **Note Sharing** - Private, shared, or public notes with user-specific permissions
- **Visual Organization** - Drag-and-drop interface with color-coded notes
- **Dark Mode** - Toggle between light and dark themes
- **Image Support** - Upload and embed images in notes
- **Search & Tags** - Find notes by content, tags, or author
- **Mobile Responsive** - Works on desktop, tablet, and mobile

## 🚀 Quick Start

### Production Deployment
Deploy to your server in one command:
```bash
curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/install.sh | sudo bash
```

### Local Development
```bash
# Clone and setup
git clone https://github.com/PythonTilk/ITS-Projekt.git
cd ITS-Projekt
git checkout html

# Setup database
mysql -u root -p < its-projekt18.6.sql

# Run application
./mvnw spring-boot:run
```

Access at: `http://localhost:12000`

**Test Account:** `testuser123` / `password123`

## 📚 Documentation

- **[Development Guide](DEVELOPMENT.md)** - Complete setup and development instructions
- **[Deployment Guide](DEPLOYMENT_GUIDE.md)** - Production deployment reference
- **[Server Setup](server-setup.md)** - Comprehensive server configuration

## 🛠️ Technology Stack

- **Backend:** Spring Boot 2.7, Java 17, Spring Security
- **Database:** MySQL/MariaDB with JPA/Hibernate
- **Frontend:** Thymeleaf, HTML5, CSS3, JavaScript
- **Build:** Maven with automated deployment scripts

## 📄 License

Licensed under the terms in the [LICENSE](LICENSE) file.

---

**Need help?** Check the [Development Guide](DEVELOPMENT.md) for detailed setup instructions or the [troubleshooting section](DEVELOPMENT.md#troubleshooting).

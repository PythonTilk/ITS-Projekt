# 🚀 ITS-Projekt Deployment Guide

Quick reference for deploying and managing your ITS-Projekt installation.

## 📦 Quick Installation

### One-Command Setup
```bash
curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/install.sh | sudo bash
```

### Automated Setup (No Prompts)
```bash
DOMAIN="your-domain.com" \
EMAIL="your-email@example.com" \
DB_PASSWORD="your-secure-password" \
SKIP_PROMPTS="true" \
curl -fsSL https://raw.githubusercontent.com/PythonTilk/ITS-Projekt/html/install.sh | sudo bash
```

### Manual Download & Install
```bash
git clone https://github.com/PythonTilk/ITS-Projekt.git
cd ITS-Projekt
sudo ./install.sh
```

## 🔄 Update Management

### Basic Update
```bash
# Switch to application user
sudo su - notizprojekt
cd ITS-Projekt

# Run update
./update.sh
```

### Update Commands
```bash
./update.sh          # Full update process
./update.sh --help    # Show help
./update.sh --status  # Check application status
./update.sh --start   # Start application
./update.sh --stop    # Stop application
./update.sh --logs    # View logs
```

### Automated Updates
```bash
# Schedule weekly updates (Sunday 3 AM)
sudo -u notizprojekt crontab -e
# Add: 0 3 * * 0 /opt/notizprojekt/ITS-Projekt/update.sh
```

## 📍 Important Locations

- **Application**: `/opt/notizprojekt/ITS-Projekt/`
- **Logs**: `/opt/notizprojekt/logs/application.log`
- **Backups**: `/opt/notizprojekt/backups/`
- **Config**: `/opt/notizprojekt/.env`
- **Update Script**: `/opt/notizprojekt/ITS-Projekt/update.sh`

## 🛠️ Management Commands

```bash
# Service management
sudo systemctl status notizprojekt
sudo systemctl restart notizprojekt
sudo systemctl stop notizprojekt
sudo systemctl start notizprojekt

# View logs
sudo journalctl -u notizprojekt -f
tail -f /opt/notizprojekt/logs/application.log

# Check application
curl -I https://your-domain.com
```

## 🆘 Quick Troubleshooting

### Application Won't Start
```bash
sudo systemctl status notizprojekt
sudo journalctl -u notizprojekt -n 50
```

### Update Fails
```bash
cd /opt/notizprojekt/ITS-Projekt
sudo -u notizprojekt git status
sudo -u notizprojekt mvn clean package
```

### Rollback Update
```bash
# Stop application
sudo -u notizprojekt /opt/notizprojekt/ITS-Projekt/update.sh --stop

# Restore from backup
cd /opt/notizprojekt/backups
ls -la  # Find backup
sudo -u notizprojekt cp backup_YYYYMMDD_HHMMSS/notizprojekt-0.0.1-SNAPSHOT.jar \
  /opt/notizprojekt/ITS-Projekt/target/

# Start application
sudo -u notizprojekt /opt/notizprojekt/ITS-Projekt/update.sh --start
```

## 📚 Full Documentation

- **Complete Setup Guide**: [server-setup.md](server-setup.md)
- **Installation Script**: [install.sh](install.sh)
- **Update Script**: [update.sh](update.sh)

## 🎯 Quick Start Checklist

1. **Install**: Run `sudo ./install.sh`
2. **Configure**: Provide domain, email, database password
3. **Access**: Visit `https://your-domain.com`
4. **Create Account**: Register first user
5. **Update**: Use `./update.sh` for updates

## 🔐 Security Notes

- Application runs as `notizprojekt` user (non-root)
- SSL certificate auto-renewal enabled
- Firewall configured for HTTP/HTTPS only
- Database secured with dedicated user
- Regular backups automated

## 📞 Support

For detailed documentation and troubleshooting, see [server-setup.md](server-setup.md).

---

**🎉 Your ITS-Projekt is ready for production use!**
# Database Setup Guide

## Quick Setup

1. **Install MySQL** (if not already installed)
2. **Run the SQL script** to create the database and user:
   ```bash
   mysql -u root -p < its-projekt18.6.sql
   ```

## What the SQL Script Creates

The `its-projekt18.6.sql` script automatically creates:

- **Database:** `notizprojekt`
- **User:** `notizuser` with password `notizpassword`
- **Tables:** All required tables for the application
- **Sample Data:** Test users and sample notes

### Created Database User
- **Username:** `notizuser`
- **Password:** `notizpassword`
- **Permissions:** Full access to `notizprojekt` database
- **Access:** Both localhost and remote connections

### Sample Users Created
The script creates these test users (all with password "password123"):
- `testuser123`
- `admin`
- `demo`

## Application Configuration

The application is automatically configured to use:
- **Database:** `notizprojekt`
- **Username:** `notizuser`
- **Password:** `notizpassword`

You can modify these settings in `src/main/resources/application.properties` if needed.

## Manual Database Setup (Alternative)

If you prefer to set up the database manually:

1. Create the database:
   ```sql
   CREATE DATABASE notizprojekt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. Create the user:
   ```sql
   CREATE USER 'notizuser'@'localhost' IDENTIFIED BY 'notizpassword';
   CREATE USER 'notizuser'@'%' IDENTIFIED BY 'notizpassword';
   ```

3. Grant permissions:
   ```sql
   GRANT ALL PRIVILEGES ON notizprojekt.* TO 'notizuser'@'localhost';
   GRANT ALL PRIVILEGES ON notizprojekt.* TO 'notizuser'@'%';
   FLUSH PRIVILEGES;
   ```

4. Run the rest of the SQL script to create tables and sample data.

## Troubleshooting

### Connection Issues
- Ensure MySQL is running
- Check that the `notizuser` was created successfully
- Verify the database `notizprojekt` exists
- Check firewall settings if connecting remotely

### Permission Issues
- Make sure the user has proper privileges on the database
- Try connecting manually: `mysql -u notizuser -p notizprojekt`

### Configuration Changes
- Modify `src/main/resources/application.properties` to change database settings
- The application will fall back to these defaults if the properties file is not found
-- ITS-Projekt Database Setup
-- Enhanced Note-Taking Application
-- Updated: June 2025
-- 
-- This script creates the complete database structure for the ITS-Projekt
-- note-taking application with all enhanced features including:
-- - User authentication system
-- - Enhanced note management with positioning, colors, and privacy
-- - Image upload support
-- - Note sharing capabilities
-- - Automatic user creation for the application

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Create Database and Application User
--

CREATE DATABASE IF NOT EXISTS `notizprojekt` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create application user with proper permissions
CREATE USER IF NOT EXISTS 'notizuser'@'localhost' IDENTIFIED BY 'notizpassword';
CREATE USER IF NOT EXISTS 'notizuser'@'%' IDENTIFIED BY 'notizpassword';

-- Grant all privileges on the notizprojekt database
GRANT ALL PRIVILEGES ON `notizprojekt`.* TO 'notizuser'@'localhost';
GRANT ALL PRIVILEGES ON `notizprojekt`.* TO 'notizuser'@'%';
FLUSH PRIVILEGES;

USE `notizprojekt`;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `geteilte_notizen`
--

CREATE TABLE IF NOT EXISTS `geteilte_notizen` (
  `GN_ID` int(11) NOT NULL,
  `Titel` varchar(60) NOT NULL,
  `Tag` varchar(30) NOT NULL,
  `Inhalt` mediumtext NOT NULL,
  `Datum` date NOT NULL,
  `Uhrzeit` time NOT NULL,
  `Ort` varchar(30) NOT NULL,
  `Mitbenutzer` varchar(20) NOT NULL,
  `B_ID` int(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Enhanced table structure for `notiz` with all new features
--

CREATE TABLE IF NOT EXISTS `notiz` (
  `n_id` int(11) NOT NULL AUTO_INCREMENT,
  `titel` varchar(255) NOT NULL,
  `tag` varchar(255) NOT NULL,
  `inhalt` mediumtext,
  `b_id` int(11) DEFAULT NULL,
  `position_x` int(11) DEFAULT 0,
  `position_y` int(11) DEFAULT 0,
  `color` varchar(255) DEFAULT '#FFFF88',
  `note_type` varchar(255) DEFAULT 'text',
  `privacy_level` varchar(255) DEFAULT 'private',
  `shared_with` text,
  `has_images` bit(1) DEFAULT b'0',
  `image_paths` text,
  `editing_permission` varchar(255) DEFAULT 'creator_only',
  PRIMARY KEY (`n_id`),
  KEY `fk_notiz_nutzer` (`b_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for `note_shares` - Better sharing management
--

CREATE TABLE IF NOT EXISTS `note_shares` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `note_id` int(11) NOT NULL,
  `shared_with_user_id` int(11) NOT NULL,
  `shared_by_user_id` int(11) NOT NULL,
  `permission_level` varchar(50) DEFAULT 'read',
  `shared_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_note_user_share` (`note_id`, `shared_with_user_id`),
  KEY `fk_note_shares_note` (`note_id`),
  KEY `fk_note_shares_shared_with` (`shared_with_user_id`),
  KEY `fk_note_shares_shared_by` (`shared_by_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Sample data for `notiz` table with enhanced features
--

INSERT INTO `notiz` (`titel`, `tag`, `inhalt`, `n_id`, `b_id`, `position_x`, `position_y`, `color`, `note_type`, `privacy_level`) VALUES
('Welcome Note', 'welcome', 'Welcome to the enhanced ITS-Projekt note-taking application! This note demonstrates the new features including positioning, colors, and rich text support.', 1, 1, 100, 100, '#FFFF88', 'text', 'private'),
('Sample Code Note', 'programming', '```javascript\nfunction hello() {\n    console.log("Hello, World!");\n}\n```', 2, 1, 300, 150, '#E1F5FE', 'code', 'private'),
('Rich Text Example', 'demo', '<p>This note supports <strong>rich text</strong> formatting including <em>italics</em>, <u>underlines</u>, and <span style="color: red;">colored text</span>!</p>', 3, 1, 500, 200, '#F3E5F5', 'rich', 'private'),
('Shared Note', 'collaboration', 'This is an example of a shared note that can be accessed by multiple users.', 4, 1, 200, 300, '#E8F5E8', 'text', 'shared');

-- --------------------------------------------------------

--
-- Enhanced table structure for `nutzer` (users)
--

CREATE TABLE IF NOT EXISTS `nutzer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `benutzername` varchar(255) NOT NULL UNIQUE,
  `passwort` varchar(255) NOT NULL,
  `email` varchar(255),
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `last_login` timestamp NULL DEFAULT NULL,
  `is_active` bit(1) DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_username` (`benutzername`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Sample users for testing (passwords are encoded using BCrypt in the application)
--

INSERT INTO `nutzer` (`benutzername`, `passwort`, `email`, `id`) VALUES
('testuser123', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EuHWDdaJE8jkjKoXEQmqAy', 'testuser@example.com', 1),
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EuHWDdaJE8jkjKoXEQmqAy', 'admin@example.com', 2),
('demo', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EuHWDdaJE8jkjKoXEQmqAy', 'demo@example.com', 3);

-- Note: All sample users have the password "password123" (BCrypt encoded)

--
-- Indexes and Constraints for Enhanced Tables
--

--
-- Indexes for table `geteilte_notizen`
--
ALTER TABLE `geteilte_notizen`
  ADD PRIMARY KEY (`GN_ID`),
  ADD KEY `fk_geteilte_nutzer` (`B_ID`);

--
-- Indexes for table `notiz` (already defined in CREATE TABLE)
--
-- PRIMARY KEY and foreign key already defined above

--
-- Indexes for table `nutzer` (already defined in CREATE TABLE)
--
-- PRIMARY KEY and unique constraints already defined above

--
-- AUTO_INCREMENT for exported tables
--

--
-- AUTO_INCREMENT for table `geteilte_notizen`
--
ALTER TABLE `geteilte_notizen`
  MODIFY `GN_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notiz`
--
ALTER TABLE `notiz`
  MODIFY `n_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `nutzer`
--
ALTER TABLE `nutzer`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Foreign Key Constraints
--

--
-- Foreign key for table `notiz`
--
ALTER TABLE `notiz`
  ADD CONSTRAINT `fk_notiz_nutzer` FOREIGN KEY (`b_id`) REFERENCES `nutzer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Foreign key for table `geteilte_notizen`
--
ALTER TABLE `geteilte_notizen`
  ADD CONSTRAINT `fk_geteilte_nutzer` FOREIGN KEY (`B_ID`) REFERENCES `nutzer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Foreign keys for table `note_shares`
--
ALTER TABLE `note_shares`
  ADD CONSTRAINT `fk_note_shares_note` FOREIGN KEY (`note_id`) REFERENCES `notiz` (`n_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_note_shares_shared_with` FOREIGN KEY (`shared_with_user_id`) REFERENCES `nutzer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_note_shares_shared_by` FOREIGN KEY (`shared_by_user_id`) REFERENCES `nutzer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

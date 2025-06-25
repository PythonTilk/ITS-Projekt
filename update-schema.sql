-- Add missing columns to the nutzer table
ALTER TABLE `nutzer` 
ADD COLUMN `display_name` varchar(255) DEFAULT NULL AFTER `email`,
ADD COLUMN `biography` text DEFAULT NULL AFTER `display_name`,
ADD COLUMN `profile_picture` varchar(255) DEFAULT NULL AFTER `biography`,
ADD COLUMN `b_id` int(11) DEFAULT 0 AFTER `id`;

-- Update existing users to have display_name same as username
UPDATE `nutzer` SET `display_name` = `benutzername` WHERE `display_name` IS NULL;
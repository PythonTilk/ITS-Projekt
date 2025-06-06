-- =====================================================
-- Entity Relationship Model - NotizProjekt
-- DDL (Data Definition Language) Script
-- =====================================================

-- Datenbank erstellen
CREATE DATABASE IF NOT EXISTS `notizprojekt` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `notizprojekt`;

-- =====================================================
-- ENTITÄT: NUTZER
-- Beschreibung: Speichert Benutzerinformationen
-- =====================================================
CREATE TABLE IF NOT EXISTS `nutzer` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `benutzername` VARCHAR(20) NOT NULL,
    `passwort` VARCHAR(60) NOT NULL,
    
    -- Constraints
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_nutzer_benutzername` (`benutzername`)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Benutzer-Entität für Authentifizierung und Notiz-Zuordnung';

-- =====================================================
-- ENTITÄT: NOTIZ
-- Beschreibung: Speichert private Notizen der Benutzer
-- =====================================================
CREATE TABLE IF NOT EXISTS `notiz` (
    `N_id` INT(11) NOT NULL AUTO_INCREMENT,
    `Titel` VARCHAR(30) NOT NULL,
    `Tag` VARCHAR(15) NOT NULL,
    `Inhalt` VARCHAR(2000) NOT NULL,
    `B_id` INT(11) NOT NULL,
    
    -- Constraints
    PRIMARY KEY (`N_id`),
    
    -- Foreign Key Constraints
    CONSTRAINT `fk_notiz_nutzer` 
        FOREIGN KEY (`B_id`) 
        REFERENCES `nutzer` (`id`) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    
    -- Indexes
    INDEX `idx_notiz_benutzer` (`B_id`),
    INDEX `idx_notiz_tag` (`Tag`)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Private Notizen der Benutzer';

-- =====================================================
-- ENTITÄT: GETEILTE_NOTIZEN
-- Beschreibung: Speichert geteilte Notizen mit erweiterten Metadaten
-- =====================================================
CREATE TABLE IF NOT EXISTS `geteilte_notizen` (
    `GN_ID` INT(11) NOT NULL AUTO_INCREMENT,
    `Titel` VARCHAR(60) NOT NULL,
    `Tag` VARCHAR(30) NOT NULL,
    `Inhalt` MEDIUMTEXT NOT NULL,
    `Datum` DATE NOT NULL,
    `Uhrzeit` TIME NOT NULL,
    `Ort` VARCHAR(30) NOT NULL,
    `Mitbenutzer` VARCHAR(20) NOT NULL,
    `B_ID` INT(11) NOT NULL,
    
    -- Constraints
    PRIMARY KEY (`GN_ID`),
    
    -- Foreign Key Constraints
    CONSTRAINT `fk_geteilte_notizen_nutzer` 
        FOREIGN KEY (`B_ID`) 
        REFERENCES `nutzer` (`id`) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    
    -- Indexes
    INDEX `idx_geteilte_notizen_benutzer` (`B_ID`),
    INDEX `idx_geteilte_notizen_tag` (`Tag`),
    INDEX `idx_geteilte_notizen_datum` (`Datum`),
    INDEX `idx_geteilte_notizen_mitbenutzer` (`Mitbenutzer`)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Geteilte Notizen mit erweiterten Metadaten für Kollaboration';

-- =====================================================
-- BEZIEHUNGEN UND GESCHÄFTSREGELN
-- =====================================================

-- Beziehung 1: NUTZER (1) ←→ (N) NOTIZ
-- Ein Nutzer kann mehrere private Notizen erstellen
-- Jede Notiz gehört genau einem Nutzer

-- Beziehung 2: NUTZER (1) ←→ (N) GETEILTE_NOTIZEN  
-- Ein Nutzer kann mehrere geteilte Notizen erstellen
-- Jede geteilte Notiz gehört genau einem Ersteller

-- =====================================================
-- BEISPIELDATEN (Optional)
-- =====================================================

-- Beispiel-Nutzer
INSERT INTO `nutzer` (`benutzername`, `passwort`) VALUES
('admin', '$2y$10$example_hash_for_admin_password'),
('max.mustermann', '$2y$10$example_hash_for_user_password'),
('anna.schmidt', '$2y$10$example_hash_for_user_password');

-- Beispiel-Notizen
INSERT INTO `notiz` (`Titel`, `Tag`, `Inhalt`, `B_id`) VALUES
('Einkaufsliste', 'privat', 'Milch, Brot, Eier, Käse', 1),
('Meeting Notizen', 'arbeit', 'Projektbesprechung um 14:00 Uhr', 2),
('Urlaubsplanung', 'freizeit', 'Reise nach Italien im Sommer', 3);

-- Beispiel-Geteilte Notizen
INSERT INTO `geteilte_notizen` (`Titel`, `Tag`, `Inhalt`, `Datum`, `Uhrzeit`, `Ort`, `Mitbenutzer`, `B_ID`) VALUES
('Projektplanung Q1', 'projekt', 'Ziele und Meilensteine für das erste Quartal', '2024-01-15', '10:00:00', 'Konferenzraum A', 'max.mustermann', 1),
('Team Building Event', 'team', 'Planung für das nächste Team-Event', '2024-02-01', '14:30:00', 'Büro', 'anna.schmidt', 2);

-- =====================================================
-- VIEWS FÜR HÄUFIGE ABFRAGEN
-- =====================================================

-- View: Alle Notizen eines Benutzers (private + geteilte)
CREATE OR REPLACE VIEW `v_benutzer_notizen` AS
SELECT 
    n.benutzername,
    'privat' as notiz_typ,
    no.N_id as notiz_id,
    no.Titel,
    no.Tag,
    no.Inhalt,
    NULL as Datum,
    NULL as Uhrzeit,
    NULL as Ort,
    NULL as Mitbenutzer
FROM nutzer n
JOIN notiz no ON n.id = no.B_id

UNION ALL

SELECT 
    n.benutzername,
    'geteilt' as notiz_typ,
    gn.GN_ID as notiz_id,
    gn.Titel,
    gn.Tag,
    gn.Inhalt,
    gn.Datum,
    gn.Uhrzeit,
    gn.Ort,
    gn.Mitbenutzer
FROM nutzer n
JOIN geteilte_notizen gn ON n.id = gn.B_ID;

-- =====================================================
-- STORED PROCEDURES FÜR HÄUFIGE OPERATIONEN
-- =====================================================

DELIMITER //

-- Procedure: Neue private Notiz erstellen
CREATE PROCEDURE `sp_create_private_note`(
    IN p_benutzer_id INT,
    IN p_titel VARCHAR(30),
    IN p_tag VARCHAR(15),
    IN p_inhalt VARCHAR(2000)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    INSERT INTO notiz (Titel, Tag, Inhalt, B_id)
    VALUES (p_titel, p_tag, p_inhalt, p_benutzer_id);
    
    COMMIT;
END //

-- Procedure: Neue geteilte Notiz erstellen
CREATE PROCEDURE `sp_create_shared_note`(
    IN p_benutzer_id INT,
    IN p_titel VARCHAR(60),
    IN p_tag VARCHAR(30),
    IN p_inhalt MEDIUMTEXT,
    IN p_ort VARCHAR(30),
    IN p_mitbenutzer VARCHAR(20)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    INSERT INTO geteilte_notizen (Titel, Tag, Inhalt, Datum, Uhrzeit, Ort, Mitbenutzer, B_ID)
    VALUES (p_titel, p_tag, p_inhalt, CURDATE(), CURTIME(), p_ort, p_mitbenutzer, p_benutzer_id);
    
    COMMIT;
END //

DELIMITER ;

-- =====================================================
-- TRIGGERS FÜR DATENINTEGRITÄT
-- =====================================================

-- Trigger: Automatische Zeitstempel für geteilte Notizen
DELIMITER //

CREATE TRIGGER `tr_geteilte_notizen_before_insert`
BEFORE INSERT ON `geteilte_notizen`
FOR EACH ROW
BEGIN
    IF NEW.Datum IS NULL THEN
        SET NEW.Datum = CURDATE();
    END IF;
    
    IF NEW.Uhrzeit IS NULL THEN
        SET NEW.Uhrzeit = CURTIME();
    END IF;
END //

DELIMITER ;

-- =====================================================
-- BERECHTIGUNGEN UND SICHERHEIT
-- =====================================================

-- Anwendungsbenutzer erstellen (für die Java-Anwendung)
CREATE USER IF NOT EXISTS 'notizapp'@'%' IDENTIFIED BY 'secure_app_password';

-- Berechtigungen vergeben
GRANT SELECT, INSERT, UPDATE, DELETE ON notizprojekt.nutzer TO 'notizapp'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON notizprojekt.notiz TO 'notizapp'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON notizprojekt.geteilte_notizen TO 'notizapp'@'%';
GRANT SELECT ON notizprojekt.v_benutzer_notizen TO 'notizapp'@'%';
GRANT EXECUTE ON PROCEDURE notizprojekt.sp_create_private_note TO 'notizapp'@'%';
GRANT EXECUTE ON PROCEDURE notizprojekt.sp_create_shared_note TO 'notizapp'@'%';

FLUSH PRIVILEGES;

-- =====================================================
-- ENDE DES DDL-SCRIPTS
-- =====================================================
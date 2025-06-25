-- Fügt das Feld Typ zur Tabelle notiz hinzu, falls es noch nicht existiert
ALTER TABLE `notiz` ADD COLUMN IF NOT EXISTS `Typ` varchar(20) DEFAULT 'PRIVAT';

-- Aktualisiert bestehende Einträge, um den Standardtyp zu setzen
UPDATE `notiz` SET `Typ` = 'PRIVAT' WHERE `Typ` IS NULL;
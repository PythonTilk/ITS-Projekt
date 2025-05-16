-- phpMyAdmin SQL Dump
-- version 5.1.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 18. Jun 2024 um 11:20
-- Server-Version: 10.4.18-MariaDB
-- PHP-Version: 8.0.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `its-projekt`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `geteilte_notizen`
--

CREATE TABLE `geteilte_notizen` (
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
-- Tabellenstruktur für Tabelle `notiz`
--

CREATE TABLE `notiz` (
  `Titel` varchar(30) NOT NULL,
  `Tag` varchar(15) NOT NULL,
  `Inhalt` varchar(2000) NOT NULL,
  `N_id` int(11) NOT NULL,
  `B_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Daten für Tabelle `notiz`
--

INSERT INTO `notiz` (`Titel`, `Tag`, `Inhalt`, `N_id`, `B_id`) VALUES
('Notiz1', 'test', 'Es tanzt ein Bi-Ba-Butzemann in unserem Haus herum Fideldum.', 1, 0),
('Du', 'Hs', 'geh jetzt', 3, 1),
('Du ', 'gehst', 'Jetzt endlich', 4, 2),
('Tiana', 'gute', 'Frage', 5, 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `nutzer`
--

CREATE TABLE `nutzer` (
  `Benutzername` varchar(20) NOT NULL,
  `Passwort` varchar(60) NOT NULL,
  `B_ID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Daten für Tabelle `nutzer`
--

INSERT INTO `nutzer` (`Benutzername`, `Passwort`, `B_ID`) VALUES
('root', '420', 1),
('Max', 'Baumstamm123', 2);

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `geteilte_notizen`
--
ALTER TABLE `geteilte_notizen`
  ADD PRIMARY KEY (`GN_ID`),
  ADD KEY `nutzer` (`B_ID`);

--
-- Indizes für die Tabelle `notiz`
--
ALTER TABLE `notiz`
  ADD PRIMARY KEY (`N_id`);

--
-- Indizes für die Tabelle `nutzer`
--
ALTER TABLE `nutzer`
  ADD PRIMARY KEY (`B_ID`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `geteilte_notizen`
--
ALTER TABLE `geteilte_notizen`
  MODIFY `GN_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `notiz`
--
ALTER TABLE `notiz`
  MODIFY `N_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT für Tabelle `nutzer`
--
ALTER TABLE `nutzer`
  MODIFY `B_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

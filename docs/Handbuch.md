# NoteGO - Handbuch und Bedienungsanleitung

## Inhaltsverzeichnis
1. [Einführung](#einführung)
2. [Systemanforderungen](#systemanforderungen)
3. [Installation und Setup](#installation-und-setup)
4. [Erste Schritte](#erste-schritte)
5. [Benutzeroberfläche](#benutzeroberfläche)
6. [Funktionen im Detail](#funktionen-im-detail)
7. [Fehlerbehebung](#fehlerbehebung)
8. [Häufig gestellte Fragen](#häufig-gestellte-fragen)

## Einführung

NoteGO ist eine Java-basierte Notizanwendung, die es Benutzern ermöglicht, persönliche Notizen zu erstellen, zu bearbeiten und zu verwalten. Die Anwendung bietet eine benutzerfreundliche Oberfläche und unterstützt verschiedene Notiztypen.

### Hauptfunktionen
- **Benutzeranmeldung**: Sicheres Anmelde- und Registrierungssystem
- **Notizverwaltung**: Erstellen, Bearbeiten und Löschen von Notizen
- **Kategorisierung**: Organisieren von Notizen mit Tags
- **Verschiedene Notiztypen**: Private, öffentliche und geteilte Notizen
- **Datenbankintegration**: Persistente Speicherung aller Daten

## Systemanforderungen

### Mindestanforderungen
- **Betriebssystem**: Windows 10, macOS 10.14, Linux (Ubuntu 18.04 oder äquivalent)
- **Java**: JDK 8 oder höher
- **Arbeitsspeicher**: Mindestens 512 MB RAM
- **Festplattenspeicher**: 100 MB freier Speicherplatz
- **Datenbank**: MySQL 5.7 oder höher (oder Docker für containerisierte Lösung)

### Empfohlene Anforderungen
- **Java**: JDK 11 oder höher
- **Arbeitsspeicher**: 1 GB RAM oder mehr
- **IDE**: NetBeans IDE (für Entwicklung)

## Installation und Setup

### 1. Projekt herunterladen
```bash
git clone https://github.com/PythonTilk/ITS-Projekt.git
cd ITS-Projekt
```

### 2. Datenbank einrichten

#### Option A: Lokale MySQL-Installation
```bash
# Datenbank-Schema importieren
mysql -u root -p < its-projekt18.6.sql
```

#### Option B: Docker-Container
```bash
# MySQL in Docker ausführen
docker run --name mysql -e MYSQL_ROOT_PASSWORD=420 -p 3306:3306 -d mysql:8.0

# Schema in Container importieren
docker exec -i mysql mysql -u root -p420 < its-projekt18.6.sql
```

### 3. Anwendung kompilieren und starten
```bash
# Projekt kompilieren
javac -d build/classes -cp lib/mysql-connector-java-8.0.28.jar src/NotizProjekt_All/*.java

# Anwendung starten
java -cp build/classes:lib/mysql-connector-java-8.0.28.jar NotizProjekt_All.NotizProjekt
```

## Erste Schritte

### 1. Anwendung starten
Nach dem Start der Anwendung erscheint das Anmeldefenster.

### 2. Anmeldung
- **Testbenutzer**: 
  - Benutzername: `root`
  - Passwort: `420`
- **Neuen Account erstellen**: Klicken Sie auf "Registrieren"

### 3. Registrierung (für neue Benutzer)
1. Klicken Sie auf "Registrieren" im Anmeldefenster
2. Geben Sie einen gewünschten Benutzernamen ein
3. Wählen Sie ein sicheres Passwort
4. Bestätigen Sie das Passwort
5. Klicken Sie auf "Registrieren"

## Benutzeroberfläche

### Anmeldefenster
- **Benutzername-Feld**: Eingabe des Benutzernamens
- **Passwort-Feld**: Eingabe des Passworts
- **Anmelden-Button**: Anmeldung durchführen
- **Registrieren-Button**: Wechsel zum Registrierungsfenster

### Hauptmenü (Notizübersicht)
- **Notizliste**: Übersicht aller eigenen Notizen
- **Neue Notiz**: Button zum Erstellen einer neuen Notiz
- **Bearbeiten**: Button zum Bearbeiten der ausgewählten Notiz
- **Löschen**: Button zum Löschen der ausgewählten Notiz
- **Abmelden**: Button zum Abmelden

### Notiz-Editor
- **Titel-Feld**: Eingabe des Notiztitels
- **Tag-Feld**: Eingabe von Tags zur Kategorisierung
- **Inhalt-Feld**: Haupttext der Notiz
- **Speichern**: Button zum Speichern der Notiz
- **Abbrechen**: Button zum Verwerfen der Änderungen

## Funktionen im Detail

### Notizen erstellen
1. Klicken Sie im Hauptmenü auf "Neue Notiz"
2. Geben Sie einen aussagekräftigen Titel ein
3. Fügen Sie relevante Tags hinzu (optional)
4. Schreiben Sie den Notizinhalt
5. Klicken Sie auf "Speichern"

### Notizen bearbeiten
1. Wählen Sie eine Notiz aus der Liste aus
2. Klicken Sie auf "Bearbeiten"
3. Nehmen Sie die gewünschten Änderungen vor
4. Klicken Sie auf "Speichern" um die Änderungen zu übernehmen

### Notizen löschen
1. Wählen Sie eine Notiz aus der Liste aus
2. Klicken Sie auf "Löschen"
3. Bestätigen Sie den Löschvorgang

### Notizen suchen und filtern
- Verwenden Sie Tags, um Notizen zu kategorisieren
- Die Notizliste zeigt alle Ihre Notizen chronologisch an
- Nutzen Sie aussagekräftige Titel für bessere Übersicht

### Notiztypen
- **Private Notizen**: Nur für Sie sichtbar (Standard)
- **Öffentliche Notizen**: Für alle Benutzer sichtbar
- **Geteilte Notizen**: Mit bestimmten Benutzern geteilt

## Fehlerbehebung

### Datenbankverbindungsprobleme
**Problem**: Anwendung kann keine Verbindung zur Datenbank herstellen

**Lösungsansätze**:
1. Überprüfen Sie, ob MySQL läuft:
   ```bash
   # Für lokale Installation
   sudo systemctl status mysql
   
   # Für Docker
   docker ps | grep mysql
   ```

2. Verbindungsparameter prüfen:
   - Host: `localhost` oder `127.0.0.1`
   - Datenbank: `notizprojekt`
   - Benutzer: `notizuser`
   - Passwort: `notizpassword`

3. Datenbankverbindung testen:
   ```bash
   mysql -u notizuser -p -h localhost notizprojekt
   ```

### Kompilierungsfehler
**Problem**: Java-Klassen können nicht kompiliert werden

**Lösungsansätze**:
1. Java-Version überprüfen: `java -version`
2. JDBC-Treiber im `lib`-Verzeichnis vorhanden?
3. Classpath korrekt gesetzt?

### Anmeldeprobleme
**Problem**: Anmeldung schlägt fehl

**Lösungsansätze**:
1. Testbenutzer verwenden: `root` / `420`
2. Neuen Benutzer registrieren
3. Datenbankverbindung überprüfen

### GUI-Probleme
**Problem**: Benutzeroberfläche wird nicht korrekt angezeigt

**Lösungsansätze**:
1. Java Swing-Unterstützung überprüfen
2. Bildschirmauflösung und Skalierung prüfen
3. Anwendung neu starten

## Häufig gestellte Fragen

### F: Kann ich meine Notizen exportieren?
A: Derzeit ist kein direkter Export implementiert. Die Notizen werden in der MySQL-Datenbank gespeichert und können über SQL-Abfragen exportiert werden.

### F: Wie kann ich mein Passwort ändern?
A: Eine Passwort-Änderungsfunktion ist derzeit nicht in der GUI implementiert. Änderungen müssen direkt in der Datenbank vorgenommen werden.

### F: Gibt es eine Suchfunktion?
A: Eine dedizierte Suchfunktion ist nicht implementiert. Nutzen Sie Tags zur Organisation und Filterung Ihrer Notizen.

### F: Kann ich Notizen mit anderen Benutzern teilen?
A: Das System unterstützt geteilte Notizen, jedoch ist diese Funktion in der aktuellen GUI-Version noch nicht vollständig implementiert.

### F: Wie sichere ich meine Daten?
A: Erstellen Sie regelmäßige Backups Ihrer MySQL-Datenbank:
```bash
mysqldump -u notizuser -p notizprojekt > backup.sql
```

### F: Kann ich die Anwendung auf mehreren Computern verwenden?
A: Ja, solange alle Computer Zugriff auf dieselbe MySQL-Datenbank haben. Konfigurieren Sie die Datenbankverbindung entsprechend.

### F: Gibt es Tastenkürzel?
A: Die Anwendung verwendet Standard-Java-Swing-Tastenkürzel (z.B. Tab zum Navigieren zwischen Feldern).

---

**Support**: Bei weiteren Fragen oder Problemen konsultieren Sie die technische Dokumentation oder wenden Sie sich an den Systemadministrator.

**Version**: 1.0 (Stand: Juni 2024)
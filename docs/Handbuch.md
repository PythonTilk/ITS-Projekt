# Notiz Desktop Anwendung - Benutzerhandbuch

## Inhaltsverzeichnis
1. [Einführung](#einführung)
2. [Installation](#installation)
3. [Erste Schritte](#erste-schritte)
4. [Notizen verwalten](#notizen-verwalten)
5. [Benutzereinstellungen](#benutzereinstellungen)
6. [Themen und Darstellung](#themen-und-darstellung)
7. [Fehlerbehebung](#fehlerbehebung)

## Einführung

Die Notiz Desktop Anwendung ist eine Java-basierte Software zur Verwaltung von Notizen mit Drag-and-Drop-Funktionalität, Rich-Text-Bearbeitung und umfassenden Notizenverwaltungsfunktionen. Die Anwendung bietet eine native Desktop-Erfahrung mit modernem UI-Design und robuster Datenbankintegration.

### Hauptfunktionen

- **Benutzerauthentifizierung**: Sicheres Login- und Registrierungssystem
- **Notizenverwaltung**: Erstellen, Bearbeiten, Löschen und Organisieren von Notizen
- **Drag & Drop-Oberfläche**: Intuitive Positionierung von Notizen auf einer virtuellen Tafel
- **Rich-Text-Unterstützung**: Sowohl Klartext als auch HTML-Rich-Text-Bearbeitung
- **Suchfunktion**: Notizen nach Titel, Inhalt oder Tags finden
- **Themenunterstützung**: Helle und dunkle Designthemen
- **Notizen teilen**: Teilen Sie Notizen mit anderen Benutzern

## Installation

### Systemvoraussetzungen
- Java 17 oder höher
- MySQL-Datenbankserver
- Apache Ant 1.10.0 oder höher (für die Erstellung aus dem Quellcode)

### Datenbank-Setup
```bash
# Führen Sie das SQL-Skript aus, um die Datenbank und Tabellen zu erstellen
mysql -u root -p < its-projekt18.6.sql
```

Weitere Details zur Datenbankkonfiguration finden Sie in der [DATABASE_SETUP.md](DATABASE_SETUP.md).

### Anwendung ausführen
```bash
# Mit Ant erstellen und ausführen
ant compile
ant run

# Oder ausführbare JAR erstellen und ausführen
ant fatjar
java -jar dist/NotizDesktop-with-dependencies.jar
```

## Erste Schritte

1. **Anwendung starten**: Führen Sie die Hauptklasse aus oder verwenden Sie den Ant-Befehl
2. **Anmelden/Registrieren**: Verwenden Sie bestehende Anmeldedaten oder erstellen Sie ein neues Konto
3. **Hauptoberfläche**: Zugriff auf die Notizentafel mit Kopfzeilennavigation

### Benutzeroberfläche

Die Hauptbenutzeroberfläche besteht aus:
- **Kopfzeile**: Enthält Navigationsschaltflächen, Suchleiste und Themenumschalter
- **Notizentafel**: Hauptbereich, in dem Notizen angezeigt und organisiert werden
- **Schwebende Schaltfläche**: Zum Erstellen neuer Notizen (unten rechts)

## Notizen verwalten

### Notizen erstellen
1. Klicken Sie auf die schwebende "+"-Schaltfläche (untere rechte Ecke)
2. Füllen Sie die Notizdetails aus:
   - Titel (erforderlich)
   - Inhalt (Klartext oder Rich-Text)
   - Tag zur Organisation
   - Notiztyp (Text, Code, Bild, Checkliste)
   - Datenschutzstufe (privat, geteilt, öffentlich)
   - Farbauswahl
3. Speichern Sie die Notiz

### Notizen bearbeiten
- **Bearbeiten**: Klicken Sie auf die Bearbeiten-Schaltfläche (✏) bei jeder Notiz, die Ihnen gehört
- **Löschen**: Klicken Sie auf die Löschen-Schaltfläche (🗑) mit Bestätigung
- **Verschieben**: Ziehen Sie Notizen auf der Tafel, um sie neu zu positionieren
- **Anzeigen**: Klicken Sie auf die Anzeigen-Schaltfläche (👁) bei geteilten Notizen

### Suche und Navigation
- Verwenden Sie die Suchleiste in der Kopfzeile, um Notizen zu finden
- Die Suche funktioniert über Titel, Inhalt und Tags
- Löschen Sie die Suche, um alle zugänglichen Notizen anzuzeigen

## Benutzereinstellungen

### Benutzerprofil
- Klicken Sie auf "Profil" in der Kopfzeile, um Ihr Konto zu verwalten
- Aktualisieren Sie Anzeigename, E-Mail und Passwort
- Änderungen werden mit der Webanwendung synchronisiert

## Themen und Darstellung

### Themenwechsel
- Klicken Sie auf die Themenumschaltschaltfläche (🌙/☀) in der Kopfzeile
- Wechseln Sie zwischen hellem und dunklem Modus
- Die Themeneinstellung wird lokal gespeichert

## Fehlerbehebung

### Häufige Probleme
1. **Datenbankverbindung**: Überprüfen Sie, ob MySQL läuft und die Anmeldedaten korrekt sind
2. **Java-Version**: Stellen Sie sicher, dass Java 17+ installiert und konfiguriert ist
3. **Themenprobleme**: Starten Sie die Anwendung neu, wenn der Themenwechsel nicht richtig funktioniert
4. **Notizpositionierung**: Wenn Drag & Drop nicht funktioniert, überprüfen Sie die Mausereignisbehandlung
5. **Ant-Build-Fehler**: Stellen Sie sicher, dass alle erforderlichen Bibliotheken im `lib`-Verzeichnis vorhanden sind

### Leistungstipps
- **Große Datensätze**: Die Anwendung verarbeitet Hunderte von Notizen effizient
- **Speichernutzung**: Starten Sie die Anwendung neu, wenn die Speichernutzung hoch wird
- **Datenbankleistung**: Stellen Sie eine ordnungsgemäße Datenbankindizierung für Suchvorgänge sicher
- **Build-Leistung**: Verwenden Sie `ant clean` vor dem Neuerstellen, wenn Sie auf seltsame Build-Probleme stoßen
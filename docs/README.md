# NoteGO - Projektdokumentation

## Übersicht

Willkommen zur umfassenden deutschen Dokumentation des NoteGO-Projekts (ITS-Projekt). Diese Dokumentation bietet eine vollständige Übersicht über die Anwendung, ihre Architektur und die Entwicklungsherausforderungen.

## Dokumentationsstruktur

### 📖 [Handbuch und Bedienungsanleitung](Handbuch.md)
Umfassende Anleitung zur Installation, Konfiguration und Nutzung der NoteGO-Anwendung.

**Inhalte:**
- Systemanforderungen und Installation
- Schritt-für-Schritt-Bedienungsanleitung
- Funktionen im Detail
- Fehlerbehebung und FAQ
- Tipps für optimale Nutzung

### 🏗️ [Klassendiagramm](Klassendiagramm.md)
Detaillierte Darstellung der Softwarearchitektur und Klassenbeziehungen.

**Inhalte:**
- UML-Klassendiagramm aller 5 Kernklassen
- Beziehungen und Abhängigkeiten
- Architekturprinzipien und Design Patterns
- Vereinfachung von 25 auf 5 Klassen

### 🗄️ [ER-Diagramm und Relationenmodell](ER-Diagramm-und-Relationenmodell.md)
Vollständige Beschreibung der Datenbankstruktur und -beziehungen.

**Inhalte:**
- Entity-Relationship-Diagramm
- Relationenmodell mit allen Tabellen
- Datenbankoperationen und -optimierung
- Sicherheitsüberlegungen und Backup-Strategien

### ⚠️ [Probleme und Besonderheiten](Probleme-und-Besonderheiten.md)
Dokumentation der Herausforderungen und Lösungsansätze während der Projektentwicklung.

**Inhalte:**
- Hauptprobleme bei der Entwicklung
- Architektonische Entscheidungen und deren Begründung
- Technische Besonderheiten und Workarounds
- Lessons Learned und zukünftige Verbesserungen

## Projektübersicht

### Was ist NoteGO?
NoteGO ist eine Java-basierte Notizanwendung mit MySQL-Datenbank, die es Benutzern ermöglicht:
- Persönliche Notizen zu erstellen und zu verwalten
- Notizen mit Tags zu kategorisieren
- Verschiedene Notiztypen zu nutzen (privat, öffentlich, geteilt)
- Sichere Benutzeranmeldung und -registrierung

### Technische Eckdaten
- **Programmiersprache**: Java (JDK 8+)
- **GUI-Framework**: Java Swing
- **Datenbank**: MySQL 5.7+
- **Architektur**: 5 Kernklassen (vereinfacht von ursprünglich 25)
- **Build-System**: Apache Ant

### Kernklassen
1. **NotizProjekt.java** - Haupteinstiegspunkt
2. **GUI.java** - Konsolidierte Benutzeroberfläche
3. **DBVerbindung.java** - Datenbankzugriff und -verwaltung
4. **User.java** - Benutzermodell
5. **Notiz.java** - Einheitliches Notizmodell

## Schnellstart

### Voraussetzungen
- Java JDK 8 oder höher
- MySQL 5.7 oder höher (oder Docker)
- Git für das Klonen des Repositories

### Installation
```bash
# Repository klonen
git clone https://github.com/PythonTilk/ITS-Projekt.git
cd ITS-Projekt

# Datenbank einrichten
mysql -u root -p < its-projekt18.6.sql

# Anwendung kompilieren und starten
javac -d build/classes -cp lib/mysql-connector-java-8.0.28.jar src/NotizProjekt_All/*.java
java -cp build/classes:lib/mysql-connector-java-8.0.28.jar NotizProjekt_All.NotizProjekt
```

### Erste Anmeldung
- **Benutzername**: `root`
- **Passwort**: `420`

## Projekthistorie

### Ursprünglicher Zustand
- 25 Klassen mit komplexer Abhängigkeitsstruktur
- 13 verschiedene GUI-Klassen
- 8 Testklassen ohne klaren Nutzen
- Redundanter Code und inkonsistente Implementierungen

### Vereinfachung
- Reduzierung auf 5 Kernklassen
- Konsolidierung aller GUI-Funktionalitäten
- Einheitliches Notizmodell mit Enum-Typisierung
- Entfernung redundanter und ungenutzter Klassen

### Ergebnis
- 80% weniger Klassen
- Deutlich verbesserte Wartbarkeit
- Einfachere Navigation und Verständlichkeit
- Reduzierte Komplexität bei gleichbleibender Funktionalität

## Dokumentationskonventionen

### Symbole und Kennzeichnungen
- ✅ **Erfüllt/Implementiert**
- ⚠️ **Bekannte Limitation/Problem**
- 🔄 **In Bearbeitung/Geplant**
- 💡 **Empfehlung/Tipp**
- 🚨 **Sicherheitshinweis**

### Code-Beispiele
Alle Code-Beispiele sind funktionsfähig und getestet. SQL-Beispiele verwenden die tatsächliche Datenbankstruktur des Projekts.

### Versionierung
Diese Dokumentation entspricht dem Stand des Projekts vom Juni 2024. Bei Änderungen am Code sollte die Dokumentation entsprechend aktualisiert werden.

## Beitrag zur Dokumentation

### Verbesserungsvorschläge
Verbesserungsvorschläge für die Dokumentation sind willkommen. Bitte beachten Sie:
- Klarheit und Verständlichkeit haben Priorität
- Beispiele sollten praxisnah und getestet sein
- Deutsche Sprache für Konsistenz verwenden

### Aktualisierung
Bei Änderungen am Code sollten folgende Dokumente überprüft werden:
- Klassendiagramm bei Strukturänderungen
- ER-Diagramm bei Datenbankänderungen
- Handbuch bei neuen Features
- Probleme-Dokument bei neuen Erkenntnissen

## Support und Kontakt

### Technische Fragen
Für technische Fragen konsultieren Sie zunächst:
1. Das [Handbuch](Handbuch.md) für Bedienungsfragen
2. [Probleme und Besonderheiten](Probleme-und-Besonderheiten.md) für bekannte Issues
3. Die Kommentare im Quellcode für Implementierungsdetails

### Weiterführende Ressourcen
- **Java Swing Tutorial**: [Oracle Swing Documentation](https://docs.oracle.com/javase/tutorial/uiswing/)
- **MySQL Dokumentation**: [MySQL 8.0 Reference Manual](https://dev.mysql.com/doc/refman/8.0/en/)
- **JDBC Tutorial**: [Oracle JDBC Documentation](https://docs.oracle.com/javase/tutorial/jdbc/)

---

**Letzte Aktualisierung**: Juni 2024  
**Dokumentationsversion**: 1.0  
**Projektversion**: Vereinfachte Architektur (5 Klassen)
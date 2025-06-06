# Entity Relationship Model - NotizProjekt
## Zusammenfassung und Dokumentation

### 📋 Projektübersicht
Das NotizProjekt ist eine Java-basierte Notiz-Anwendung, die es Benutzern ermöglicht, private und geteilte Notizen zu erstellen und zu verwalten. Das System verwendet eine MySQL-Datenbank zur Speicherung von Benutzerinformationen und Notizen.

### 🏗️ Datenmodell-Architektur

#### Entitäten (Entities)

1. **NUTZER** (User)
   - Zentrale Entität für Benutzerauthentifizierung
   - Eindeutige Identifikation über ID
   - Speichert Anmeldedaten (Benutzername, Passwort)

2. **NOTIZ** (Private Note)
   - Private Notizen einzelner Benutzer
   - Einfache Struktur mit Titel, Tag und Inhalt
   - Direkte Zuordnung zu einem Benutzer

3. **GETEILTE_NOTIZEN** (Shared Notes)
   - Erweiterte Notizen für Kollaboration
   - Zusätzliche Metadaten (Datum, Uhrzeit, Ort, Mitbenutzer)
   - Ermöglicht Zusammenarbeit zwischen Benutzern

#### Beziehungen (Relationships)

1. **NUTZER ←→ NOTIZ** (1:N)
   - Ein Benutzer kann mehrere private Notizen erstellen
   - Jede Notiz gehört genau einem Benutzer
   - Referentielle Integrität durch Foreign Key

2. **NUTZER ←→ GETEILTE_NOTIZEN** (1:N)
   - Ein Benutzer kann mehrere geteilte Notizen erstellen
   - Jede geteilte Notiz hat einen Ersteller
   - Zusätzliche Kollaborationsmöglichkeiten

### 🔑 Schlüssel und Constraints

#### Primary Keys
- `nutzer.id` - Eindeutige Benutzer-ID
- `notiz.N_id` - Eindeutige Notiz-ID
- `geteilte_notizen.GN_ID` - Eindeutige ID für geteilte Notizen

#### Foreign Keys
- `notiz.B_id` → `nutzer.id`
- `geteilte_notizen.B_ID` → `nutzer.id`

#### Geschäftsregeln
1. **Eindeutigkeit**: Jeder Benutzername muss eindeutig sein
2. **Referentielle Integrität**: Notizen müssen einem existierenden Benutzer zugeordnet sein
3. **Datenschutz**: Private Notizen sind nur für den Ersteller sichtbar
4. **Kollaboration**: Geteilte Notizen enthalten Informationen über Mitbenutzer
5. **Zeitstempel**: Geteilte Notizen haben automatische Datum/Uhrzeit-Erfassung

### 📊 Datentypen und Speicheranforderungen

| Entität | Attribut | Datentyp | Größe | Beschreibung |
|---------|----------|----------|-------|--------------|
| nutzer | id | INT | 11 | Auto-Increment Primary Key |
| nutzer | benutzername | VARCHAR | 20 | Eindeutiger Benutzername |
| nutzer | passwort | VARCHAR | 60 | Verschlüsseltes Passwort |
| notiz | N_id | INT | 11 | Auto-Increment Primary Key |
| notiz | Titel | VARCHAR | 30 | Notiz-Titel |
| notiz | Tag | VARCHAR | 15 | Kategorisierungs-Tag |
| notiz | Inhalt | VARCHAR | 2000 | Notiz-Inhalt |
| notiz | B_id | INT | 11 | Foreign Key zu nutzer |
| geteilte_notizen | GN_ID | INT | 11 | Auto-Increment Primary Key |
| geteilte_notizen | Titel | VARCHAR | 60 | Erweiterte Titel-Länge |
| geteilte_notizen | Tag | VARCHAR | 30 | Erweiterte Tag-Länge |
| geteilte_notizen | Inhalt | MEDIUMTEXT | ~16MB | Großer Inhalt möglich |
| geteilte_notizen | Datum | DATE | - | Erstellungsdatum |
| geteilte_notizen | Uhrzeit | TIME | - | Erstellungszeit |
| geteilte_notizen | Ort | VARCHAR | 30 | Standortinformation |
| geteilte_notizen | Mitbenutzer | VARCHAR | 20 | Kollaborationspartner |
| geteilte_notizen | B_ID | INT | 11 | Foreign Key zu nutzer |

### 🔍 Indizierung und Performance

#### Empfohlene Indizes
```sql
-- Primäre Indizes (automatisch)
PRIMARY KEY (id) ON nutzer
PRIMARY KEY (N_id) ON notiz  
PRIMARY KEY (GN_ID) ON geteilte_notizen

-- Foreign Key Indizes
INDEX idx_notiz_benutzer (B_id) ON notiz
INDEX idx_geteilte_notizen_benutzer (B_ID) ON geteilte_notizen

-- Funktionale Indizes
INDEX idx_nutzer_benutzername (benutzername) ON nutzer
INDEX idx_notiz_tag (Tag) ON notiz
INDEX idx_geteilte_notizen_tag (Tag) ON geteilte_notizen
INDEX idx_geteilte_notizen_datum (Datum) ON geteilte_notizen
```

### 🛡️ Sicherheitsaspekte

1. **Passwort-Sicherheit**
   - Passwörter sollten gehasht gespeichert werden
   - Empfehlung: bcrypt oder ähnliche sichere Hash-Algorithmen

2. **SQL-Injection-Schutz**
   - Verwendung von Prepared Statements
   - Input-Validierung auf Anwendungsebene

3. **Zugriffskontrolle**
   - Benutzer können nur ihre eigenen Notizen bearbeiten
   - Geteilte Notizen haben definierte Zugriffsberechtigung

### 📈 Erweiterungsmöglichkeiten

1. **Benutzergruppen**
   - Neue Entität für Gruppen-Management
   - Gruppen-basierte Notiz-Freigabe

2. **Notiz-Kategorien**
   - Hierarchische Tag-Struktur
   - Erweiterte Organisationsmöglichkeiten

3. **Versionierung**
   - Historische Versionen von Notizen
   - Änderungsverfolgung

4. **Anhänge**
   - Datei-Uploads zu Notizen
   - Multimedia-Unterstützung

5. **Berechtigungen**
   - Granulare Zugriffsrechte
   - Lese-/Schreibberechtigungen

### 📁 Generierte Dateien

1. **ER-Model.md** - Detaillierte Entitäts-Beschreibung
2. **ER-Diagram.png** - Visuelles ER-Diagramm
3. **UML-Class-Diagram.png** - UML-Klassendiagramm
4. **ER-Model-DDL.sql** - Vollständiges DDL-Script mit:
   - Tabellen-Definitionen
   - Constraints und Indizes
   - Views für häufige Abfragen
   - Stored Procedures
   - Trigger für Datenintegrität
   - Sicherheits-Konfiguration

### 🚀 Implementierungsempfehlungen

1. **Datenbank-Setup**
   ```bash
   mysql -u root -p < ER-Model-DDL.sql
   ```

2. **Java-Integration**
   - Verwendung von JPA/Hibernate für ORM
   - Connection Pooling für Performance
   - Transaktions-Management

3. **Testing**
   - Unit Tests für Datenbank-Operationen
   - Integration Tests für Geschäftslogik
   - Performance Tests für große Datenmengen

### 📞 Support und Wartung

- **Backup-Strategie**: Regelmäßige Datenbank-Backups
- **Monitoring**: Überwachung der Datenbank-Performance
- **Updates**: Versionierte Schema-Migrationen
- **Dokumentation**: Aktuelle Dokumentation der Datenstruktur

---

*Erstellt am: 2025-06-06*  
*Version: 1.0*  
*Autor: OpenHands AI Assistant*
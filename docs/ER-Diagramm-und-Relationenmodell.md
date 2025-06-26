# NoteGO - ER-Diagramm und Relationenmodell der Datenbank

## Übersicht

Die NoteGO-Anwendung verwendet eine MySQL-Datenbank namens `notizprojekt` zur persistenten Speicherung von Benutzerdaten und Notizen. Dieses Dokument beschreibt die Datenbankstruktur mittels Entity-Relationship-Diagramm und Relationenmodell.

## Entity-Relationship-Diagramm (ERD)

```
┌─────────────────────────────────────┐
│              NUTZER                 │
├─────────────────────────────────────┤
│ PK  id: INT(11) AUTO_INCREMENT      │
│     benutzername: VARCHAR(20)       │
│     passwort: VARCHAR(60)           │
└─────────────────────────────────────┘
                    │
                    │ 1
                    │
                    │ erstellt
                    │
                    │ n
                    ▼
┌─────────────────────────────────────┐
│              NOTIZ                  │
├─────────────────────────────────────┤
│ PK  N_id: INT(11) AUTO_INCREMENT    │
│ FK  B_id: INT(11)                   │
│     Titel: VARCHAR(30)              │
│     Tag: VARCHAR(15)                │
│     Inhalt: VARCHAR(2000)           │
│     Typ: VARCHAR(20) DEFAULT 'PRIVAT'│
└─────────────────────────────────────┘
                    │
                    │ 1
                    │
                    │ kann erweitert werden zu
                    │
                    │ 1
                    ▼
┌─────────────────────────────────────┐
│         GETEILTE_NOTIZEN            │
├─────────────────────────────────────┤
│ PK  GN_ID: INT(11) AUTO_INCREMENT   │
│ FK  B_ID: INT(20)                   │
│     Titel: VARCHAR(60)              │
│     Tag: VARCHAR(30)                │
│     Inhalt: MEDIUMTEXT              │
│     Datum: DATE                     │
│     Uhrzeit: TIME                   │
│     Ort: VARCHAR(30)                │
│     Mitbenutzer: VARCHAR(20)        │
└─────────────────────────────────────┘
                    │
                    │ n
                    │
                    │ gehört zu
                    │
                    │ 1
                    ▼
┌─────────────────────────────────────┐
│              NUTZER                 │
│         (Referenz zurück)           │
└─────────────────────────────────────┘
```

## Relationenmodell

### Tabelle: nutzer
```sql
nutzer (
    id INT(11) PRIMARY KEY AUTO_INCREMENT,
    benutzername VARCHAR(20) NOT NULL,
    passwort VARCHAR(60) NOT NULL
)
```

**Beschreibung**: Speichert alle registrierten Benutzer des Systems.

**Attribute**:
- `id`: Eindeutige Benutzer-ID (Primärschlüssel)
- `benutzername`: Eindeutiger Benutzername für die Anmeldung
- `passwort`: Passwort des Benutzers (Klartext - Sicherheitshinweis siehe unten)

### Tabelle: notiz
```sql
notiz (
    N_id INT(11) PRIMARY KEY AUTO_INCREMENT,
    B_id INT(11) FOREIGN KEY REFERENCES nutzer(id),
    Titel VARCHAR(30) NOT NULL,
    Tag VARCHAR(15) NOT NULL,
    Inhalt VARCHAR(2000) NOT NULL,
    Typ VARCHAR(20) DEFAULT 'PRIVAT'
)
```

**Beschreibung**: Speichert alle Notizen (private, öffentliche und geteilte).

**Attribute**:
- `N_id`: Eindeutige Notiz-ID (Primärschlüssel)
- `B_id`: Fremdschlüssel zur nutzer-Tabelle (Ersteller der Notiz)
- `Titel`: Titel der Notiz
- `Tag`: Kategorisierungs-Tag für die Notiz
- `Inhalt`: Hauptinhalt der Notiz
- `Typ`: Notiztyp ('PRIVAT', 'OEFFENTLICH', 'GETEILT')

### Tabelle: geteilte_notizen
```sql
geteilte_notizen (
    GN_ID INT(11) PRIMARY KEY AUTO_INCREMENT,
    B_ID INT(20) FOREIGN KEY REFERENCES nutzer(id),
    Titel VARCHAR(60) NOT NULL,
    Tag VARCHAR(30) NOT NULL,
    Inhalt MEDIUMTEXT NOT NULL,
    Datum DATE NOT NULL,
    Uhrzeit TIME NOT NULL,
    Ort VARCHAR(30) NOT NULL,
    Mitbenutzer VARCHAR(20) NOT NULL
)
```

**Beschreibung**: Erweiterte Informationen für geteilte Notizen (derzeit nicht vollständig implementiert).

**Attribute**:
- `GN_ID`: Eindeutige ID für geteilte Notizen (Primärschlüssel)
- `B_ID`: Fremdschlüssel zur nutzer-Tabelle (Ersteller)
- `Titel`: Titel der geteilten Notiz
- `Tag`: Kategorisierungs-Tag
- `Inhalt`: Hauptinhalt (größerer Speicherplatz als bei normalen Notizen)
- `Datum`: Erstellungsdatum
- `Uhrzeit`: Erstellungszeit
- `Ort`: Ortsangabe für die Notiz
- `Mitbenutzer`: Benutzer, mit denen die Notiz geteilt wird

## Beziehungen und Kardinalitäten

### 1. nutzer ↔ notiz
- **Beziehungstyp**: 1:n (Ein Benutzer kann viele Notizen haben)
- **Fremdschlüssel**: `notiz.B_id` → `nutzer.id`
- **Beschreibung**: Jede Notiz gehört zu genau einem Benutzer, ein Benutzer kann mehrere Notizen erstellen

### 2. nutzer ↔ geteilte_notizen
- **Beziehungstyp**: 1:n (Ein Benutzer kann viele geteilte Notizen haben)
- **Fremdschlüssel**: `geteilte_notizen.B_ID` → `nutzer.id`
- **Beschreibung**: Jede geteilte Notiz gehört zu genau einem Ersteller

### 3. notiz ↔ geteilte_notizen
- **Beziehungstyp**: 1:1 (Konzeptionell - derzeit nicht durch FK implementiert)
- **Beschreibung**: Eine geteilte Notiz kann als Erweiterung einer normalen Notiz betrachtet werden

## Datenbank-Constraints und Indizes

### Primary Keys
- `nutzer.id`: AUTO_INCREMENT, eindeutige Benutzeridentifikation
- `notiz.N_id`: AUTO_INCREMENT, eindeutige Notizidentifikation
- `geteilte_notizen.GN_ID`: AUTO_INCREMENT, eindeutige ID für geteilte Notizen

### Foreign Keys
- `notiz.B_id` → `nutzer.id`: Referentielle Integrität zwischen Notizen und Benutzern
- `geteilte_notizen.B_ID` → `nutzer.id`: Referentielle Integrität zwischen geteilten Notizen und Benutzern

### Indizes
```sql
-- Automatische Indizes durch Primary Keys
CREATE INDEX idx_nutzer_id ON nutzer(id);
CREATE INDEX idx_notiz_nid ON notiz(N_id);
CREATE INDEX idx_geteilte_notizen_gnid ON geteilte_notizen(GN_ID);

-- Zusätzliche Indizes für bessere Performance
CREATE INDEX idx_notiz_bid ON notiz(B_id);
CREATE INDEX idx_geteilte_notizen_bid ON geteilte_notizen(B_ID);
CREATE INDEX idx_nutzer_benutzername ON nutzer(benutzername);
```

## Normalisierung

### Erste Normalform (1NF)
✅ **Erfüllt**: Alle Attribute enthalten atomare Werte
- Keine mehrwertigen Attribute
- Jede Zelle enthält genau einen Wert

### Zweite Normalform (2NF)
✅ **Erfüllt**: Alle Nicht-Schlüssel-Attribute sind vollständig funktional abhängig vom Primärschlüssel
- `nutzer`: Benutzername und Passwort hängen vollständig von der ID ab
- `notiz`: Alle Attribute hängen vollständig von der N_id ab
- `geteilte_notizen`: Alle Attribute hängen vollständig von der GN_ID ab

### Dritte Normalform (3NF)
✅ **Erfüllt**: Keine transitiven Abhängigkeiten zwischen Nicht-Schlüssel-Attributen
- Alle Tabellen enthalten keine transitiven Abhängigkeiten

## Datenbankbenutzer und Berechtigungen

### Anwendungsbenutzer
```sql
-- Benutzer: notizuser
-- Passwort: notizpassword
-- Berechtigungen: Vollzugriff auf notizprojekt-Datenbank

CREATE USER 'notizuser'@'%' IDENTIFIED BY 'notizpassword';
GRANT ALL PRIVILEGES ON notizprojekt.* TO 'notizuser'@'%';
FLUSH PRIVILEGES;
```

## Beispieldaten

### Testbenutzer
```sql
INSERT INTO nutzer (benutzername, passwort, id) VALUES
('root', '420', 1),
('Max', 'Baumstamm123', 2);
```

### Beispielnotizen
```sql
INSERT INTO notiz (Titel, Tag, Inhalt, N_id, B_id, Typ) VALUES
('Notiz1', 'test', 'Es tanzt ein Bi-Ba-Butzemann in unserem Haus herum Fideldum.', 1, 0, 'PRIVAT'),
('Du', 'Hs', 'geh jetzt', 3, 1, 'PRIVAT'),
('Du ', 'gehst', 'Jetzt endlich', 4, 2, 'PRIVAT'),
('Tiana', 'gute', 'Frage', 5, 1, 'PRIVAT');
```

## Datenbankoperationen

### Häufige Abfragen

#### Benutzerauthentifizierung
```sql
SELECT id, benutzername FROM nutzer 
WHERE benutzername = ? AND passwort = ?;
```

#### Notizen eines Benutzers abrufen
```sql
SELECT N_id, Titel, Tag, Inhalt, Typ FROM notiz 
WHERE B_id = ? 
ORDER BY N_id DESC;
```

#### Neue Notiz erstellen
```sql
INSERT INTO notiz (Titel, Tag, Inhalt, B_id, Typ) 
VALUES (?, ?, ?, ?, ?);
```

#### Notiz aktualisieren
```sql
UPDATE notiz 
SET Titel = ?, Tag = ?, Inhalt = ?, Typ = ? 
WHERE N_id = ? AND B_id = ?;
```

#### Notiz löschen
```sql
DELETE FROM notiz 
WHERE N_id = ? AND B_id = ?;
```

## Sicherheitsüberlegungen

### Aktuelle Schwachstellen
1. **Passwort-Speicherung**: Passwörter werden im Klartext gespeichert
2. **SQL-Injection**: Abhängig von der Implementierung in der Anwendung
3. **Benutzerberechtigungen**: Anwendungsbenutzer hat Vollzugriff

### Empfohlene Verbesserungen
1. **Passwort-Hashing**: Verwendung von bcrypt oder ähnlichen Algorithmen
2. **Prepared Statements**: Schutz vor SQL-Injection
3. **Minimale Berechtigungen**: Eingeschränkte Datenbankberechtigungen
4. **Verschlüsselung**: Verschlüsselung sensibler Daten

## Performance-Optimierung

### Empfohlene Indizes
```sql
-- Für häufige Abfragen
CREATE INDEX idx_notiz_benutzer_typ ON notiz(B_id, Typ);
CREATE INDEX idx_notiz_tag ON notiz(Tag);
CREATE INDEX idx_geteilte_notizen_mitbenutzer ON geteilte_notizen(Mitbenutzer);
```

### Partitionierung
Für große Datenmengen könnte eine Partitionierung nach Benutzer-ID sinnvoll sein:
```sql
-- Beispiel für Partitionierung (MySQL 8.0+)
ALTER TABLE notiz PARTITION BY HASH(B_id) PARTITIONS 4;
```

## Backup und Wartung

### Backup-Strategie
```bash
# Vollständiges Backup
mysqldump -u notizuser -p notizprojekt > backup_$(date +%Y%m%d).sql

# Nur Daten (ohne Struktur)
mysqldump -u notizuser -p --no-create-info notizprojekt > data_backup_$(date +%Y%m%d).sql

# Nur Struktur (ohne Daten)
mysqldump -u notizuser -p --no-data notizprojekt > structure_backup_$(date +%Y%m%d).sql
```

### Wartungsaufgaben
```sql
-- Tabellen optimieren
OPTIMIZE TABLE nutzer, notiz, geteilte_notizen;

-- Statistiken aktualisieren
ANALYZE TABLE nutzer, notiz, geteilte_notizen;

-- Fragmentierung prüfen
SHOW TABLE STATUS FROM notizprojekt;
```

## Erweiterungsmöglichkeiten

### Zusätzliche Tabellen
1. **notiz_tags**: Many-to-Many-Beziehung für mehrere Tags pro Notiz
2. **benutzer_gruppen**: Gruppenverwaltung für geteilte Notizen
3. **notiz_versionen**: Versionierung von Notizen
4. **notiz_kommentare**: Kommentarsystem für geteilte Notizen

### Zusätzliche Attribute
1. **Zeitstempel**: created_at, updated_at für alle Tabellen
2. **Soft Delete**: deleted_at für logisches Löschen
3. **Priorität**: Prioritätsstufen für Notizen
4. **Kategorien**: Hierarchische Kategorisierung
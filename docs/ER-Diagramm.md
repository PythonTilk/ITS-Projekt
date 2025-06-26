# ER-Diagramm und Relationenmodell der Datenbank

## Übersicht

Dieses Dokument beschreibt die Datenbankstruktur der Notiz Desktop Anwendung. Die Anwendung verwendet eine MySQL-Datenbank mit mehreren Tabellen zur Speicherung von Benutzer- und Notizinformationen.

## ER-Diagramm

```
+----------------+       +----------------+       +----------------+
|     NUTZER     |       |     NOTIZ      |       |  NOTE_SHARES   |
+----------------+       +----------------+       +----------------+
| PK: id         |<----->| PK: n_id       |<----->| PK: id         |
|     b_id       |       |     titel      |       |     note_id    |
|     benutzername|       |     tag        |       |     shared_with_user_id |
|     passwort   |       |     inhalt     |       |     shared_by_user_id |
|     email      |       |     b_id       |       |     permission_level |
|     display_name|       |     position_x |       |     shared_at  |
|     biography  |       |     position_y |       +----------------+
|     profile_picture|    |     color      |
|     created_at |       |     note_type  |
|     last_login |       |     privacy_level|
|     is_active  |       |     shared_with|
+----------------+       |     has_images |
                         |     image_paths|
                         |     editing_permission|
                         +----------------+
```

## Relationenmodell

### Tabelle: nutzer

Diese Tabelle speichert Benutzerinformationen für die Authentifizierung und Profilverwaltung.

| Spalte           | Typ         | Beschreibung                                  |
|------------------|-------------|-----------------------------------------------|
| id               | int(11)     | Primärschlüssel, Auto-Increment               |
| b_id             | int(11)     | Business-ID für Integration mit anderen Systemen |
| benutzername     | varchar(255)| Eindeutiger Benutzername für die Anmeldung    |
| passwort         | varchar(255)| Verschlüsseltes Passwort (BCrypt)             |
| email            | varchar(255)| E-Mail-Adresse des Benutzers                  |
| display_name     | varchar(255)| Anzeigename (Standard: Benutzername)          |
| biography        | text        | Biografie-Text des Benutzers                  |
| profile_picture  | varchar(255)| Pfad zum Profilbild des Benutzers             |
| created_at       | timestamp   | Zeitpunkt der Kontoerstellung                 |
| last_login       | timestamp   | Zeitpunkt der letzten Anmeldung               |
| is_active        | bit(1)      | Gibt an, ob das Konto aktiv ist               |

**Primärschlüssel**: `id`
**Eindeutige Schlüssel**: `benutzername`

### Tabelle: notiz

Diese Tabelle speichert alle Notizinformationen einschließlich Inhalt, Position und Freigabeeinstellungen.

| Spalte             | Typ         | Beschreibung                                  |
|--------------------|-------------|-----------------------------------------------|
| n_id               | int(11)     | Primärschlüssel, Auto-Increment               |
| titel              | varchar(255)| Titel der Notiz                               |
| tag                | varchar(255)| Tag zur Kategorisierung der Notiz             |
| inhalt             | mediumtext  | Inhalt der Notiz (Text oder HTML)             |
| b_id               | int(11)     | Fremdschlüssel zur nutzer-Tabelle             |
| position_x         | int(11)     | X-Position auf der Notiztafel                 |
| position_y         | int(11)     | Y-Position auf der Notiztafel                 |
| color              | varchar(255)| Hintergrundfarbe der Notiz (HEX-Code)         |
| note_type          | varchar(255)| Notiztyp (text, code, image, checklist)       |
| privacy_level      | varchar(255)| Datenschutzstufe (private, shared, public)    |
| shared_with        | text        | Liste der Benutzer-IDs, mit denen geteilt wird|
| has_images         | bit(1)      | Gibt an, ob die Notiz Bilder enthält          |
| image_paths        | text        | Pfade zu Bildern in der Notiz                 |
| editing_permission | varchar(255)| Bearbeitungsberechtigungen für geteilte Notizen|

**Primärschlüssel**: `n_id`
**Fremdschlüssel**: `b_id` referenziert `nutzer(id)`

### Tabelle: note_shares

Diese Tabelle verwaltet die Freigabe von Notizen zwischen Benutzern.

| Spalte              | Typ         | Beschreibung                                  |
|---------------------|-------------|-----------------------------------------------|
| id                  | int(11)     | Primärschlüssel, Auto-Increment               |
| note_id             | int(11)     | Fremdschlüssel zur notiz-Tabelle              |
| shared_with_user_id | int(11)     | Benutzer-ID, mit dem die Notiz geteilt wird   |
| shared_by_user_id   | int(11)     | Benutzer-ID, der die Notiz geteilt hat        |
| permission_level    | varchar(50) | Berechtigungsstufe (read, edit)               |
| shared_at           | timestamp   | Zeitpunkt der Freigabe                        |

**Primärschlüssel**: `id`
**Eindeutige Schlüssel**: `(note_id, shared_with_user_id)`
**Fremdschlüssel**:
- `note_id` referenziert `notiz(n_id)`
- `shared_with_user_id` referenziert `nutzer(id)`
- `shared_by_user_id` referenziert `nutzer(id)`

### Tabelle: geteilte_notizen (Legacy)

Diese Tabelle ist eine ältere Version der Notizfreigabe, die aus Kompatibilitätsgründen beibehalten wird.

| Spalte       | Typ         | Beschreibung                                  |
|--------------|-------------|-----------------------------------------------|
| GN_ID        | int(11)     | Primärschlüssel, Auto-Increment               |
| Titel        | varchar(60) | Titel der geteilten Notiz                     |
| Tag          | varchar(30) | Tag zur Kategorisierung                       |
| Inhalt       | mediumtext  | Inhalt der geteilten Notiz                    |
| Datum        | date        | Erstellungsdatum                              |
| Uhrzeit      | time        | Erstellungszeit                               |
| Ort          | varchar(30) | Ort der Erstellung                            |
| Mitbenutzer  | varchar(20) | Benutzername des Mitbenutzers                 |
| B_ID         | int(20)     | Business-ID des Erstellers                    |

**Primärschlüssel**: `GN_ID`

## Beziehungen

1. **Nutzer zu Notiz**: Ein Nutzer kann viele Notizen erstellen (1:n)
   - Fremdschlüssel: `notiz.b_id` referenziert `nutzer.id`

2. **Notiz zu Note_Shares**: Eine Notiz kann mit vielen Benutzern geteilt werden (1:n)
   - Fremdschlüssel: `note_shares.note_id` referenziert `notiz.n_id`

3. **Nutzer zu Note_Shares**: Ein Nutzer kann viele Notizen teilen und viele geteilte Notizen haben (n:m)
   - Fremdschlüssel: 
     - `note_shares.shared_by_user_id` referenziert `nutzer.id`
     - `note_shares.shared_with_user_id` referenziert `nutzer.id`

## Datenbankschema-Updates

Das Datenbankschema wurde kürzlich aktualisiert, um folgende Funktionen zu unterstützen:

1. **Notiztypen**: Unterstützung für verschiedene Notiztypen (Text, Code, Bild, Checkliste)
2. **Datenschutzstufen**: Verbessertes Freigabesystem mit privaten, geteilten und öffentlichen Notizen
3. **Positionierung**: Speicherung der X- und Y-Koordinaten für Drag & Drop
4. **Farbkodierung**: Anpassbare Notizfarben
5. **Berechtigungsstufen**: Detaillierte Berechtigungen für geteilte Notizen

## Datenbankverbindung

Die Anwendung verbindet sich mit der Datenbank über die Konfiguration in `DatabaseConfig.java` oder über die Einstellungen in `application.properties`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/notizprojekt";
private static final String USERNAME = "notizuser";
private static final String PASSWORD = "notizpassword";
```

Für detaillierte Anweisungen zur Datenbankeinrichtung siehe [DATABASE_SETUP.md](DATABASE_SETUP.md).
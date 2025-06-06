# Entity Relationship Model - NotizProjekt

## Entitäten und Attribute

### 1. NUTZER (User)
- **id** (Primary Key, INT, AUTO_INCREMENT)
- **benutzername** (VARCHAR(20), NOT NULL)
- **passwort** (VARCHAR(60), NOT NULL)

### 2. NOTIZ (Note)
- **N_id** (Primary Key, INT, AUTO_INCREMENT)
- **Titel** (VARCHAR(30), NOT NULL)
- **Tag** (VARCHAR(15), NOT NULL)
- **Inhalt** (VARCHAR(2000), NOT NULL)
- **B_id** (Foreign Key → NUTZER.id, INT, NOT NULL)

### 3. GETEILTE_NOTIZEN (Shared Notes)
- **GN_ID** (Primary Key, INT, AUTO_INCREMENT)
- **Titel** (VARCHAR(60), NOT NULL)
- **Tag** (VARCHAR(30), NOT NULL)
- **Inhalt** (MEDIUMTEXT, NOT NULL)
- **Datum** (DATE, NOT NULL)
- **Uhrzeit** (TIME, NOT NULL)
- **Ort** (VARCHAR(30), NOT NULL)
- **Mitbenutzer** (VARCHAR(20), NOT NULL)
- **B_ID** (Foreign Key → NUTZER.id, INT, NOT NULL)

## Beziehungen

### 1. NUTZER ←→ NOTIZ
- **Beziehungstyp**: 1:N (Ein-zu-Viele)
- **Beschreibung**: Ein Nutzer kann mehrere private Notizen erstellen
- **Kardinalität**: Ein NUTZER kann 0 bis N NOTIZEN haben
- **Fremdschlüssel**: NOTIZ.B_id → NUTZER.id

### 2. NUTZER ←→ GETEILTE_NOTIZEN
- **Beziehungstyp**: 1:N (Ein-zu-Viele)
- **Beschreibung**: Ein Nutzer kann mehrere geteilte Notizen erstellen
- **Kardinalität**: Ein NUTZER kann 0 bis N GETEILTE_NOTIZEN haben
- **Fremdschlüssel**: GETEILTE_NOTIZEN.B_ID → NUTZER.id

## Geschäftsregeln

1. **Nutzer-Authentifizierung**: Jeder Nutzer muss einen eindeutigen Benutzernamen haben
2. **Notiz-Eigentümerschaft**: Jede Notiz muss einem Nutzer zugeordnet sein
3. **Geteilte Notizen**: Geteilte Notizen enthalten zusätzliche Metadaten (Datum, Uhrzeit, Ort, Mitbenutzer)
4. **Datenschutz**: Private Notizen sind nur für den Ersteller sichtbar
5. **Kollaboration**: Geteilte Notizen können mit anderen Nutzern geteilt werden

## Datentypen und Constraints

### NUTZER
- id: AUTO_INCREMENT, PRIMARY KEY
- benutzername: UNIQUE (implizit durch Geschäftslogik)
- passwort: Verschlüsselt gespeichert

### NOTIZ
- N_id: AUTO_INCREMENT, PRIMARY KEY
- B_id: FOREIGN KEY mit RESTRICT on DELETE

### GETEILTE_NOTIZEN
- GN_ID: AUTO_INCREMENT, PRIMARY KEY
- B_ID: FOREIGN KEY mit RESTRICT on DELETE
- Datum/Uhrzeit: Zeitstempel für Erstellung/Änderung
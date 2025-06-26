# Zusammenfassung der Änderungen

## Überblick
Das Notiz-System wurde überarbeitet, um nur noch zwischen "Privat" und "Öffentlich" zu unterscheiden. Der "Geteilt"-Typ wurde entfernt und durch ein einfacheres System ersetzt, bei dem öffentliche Notizen für alle Benutzer sichtbar und bearbeitbar sind.

## Änderungen in der Notiz-Klasse (Notiz.java)

### 1. Enum NotizTyp vereinfacht
- **Vorher**: `PRIVAT`, `OEFFENTLICH`, `GETEILT`
- **Nachher**: `PRIVAT`, `OEFFENTLICH`

### 2. Konstruktor für geteilte Notizen entfernt
- Der komplexe Konstruktor für geteilte Notizen mit Datum, Uhrzeit, Ort und Mitbenutzer wurde entfernt
- Neuer vereinfachter Konstruktor für Notizen mit Typ-Parameter hinzugefügt

## Änderungen in der GUI-Klasse (GUI.java)

### 1. ComboBox-Optionen reduziert
- **Vorher**: "Privat", "Öffentlich", "Geteilt"
- **Nachher**: "Privat", "Öffentlich"

### 2. Tabellendarstellung erweitert
- **Vorher**: Nur "Titel" und "Tag"
- **Nachher**: "Titel", "Tag", "Typ", "Ersteller"
- Zeigt jetzt den Typ der Notiz und den Ersteller an

### 3. Notizen-Ladelogik geändert
- **Private Notizen**: Nur für den Ersteller sichtbar
- **Öffentliche Notizen**: Für alle Benutzer sichtbar und bearbeitbar

#### Neue SQL-Abfrage für das Laden von Notizen:
```sql
SELECT N_id, Titel, Tag, Inhalt, Typ, B_id FROM notiz WHERE 
(B_id = [nutzerID] AND Typ = 'PRIVAT') OR Typ = 'OEFFENTLICH'
```

### 4. Suchfunktion angepasst
- Sucht sowohl in privaten Notizen des Benutzers als auch in allen öffentlichen Notizen
- Zeigt ebenfalls Typ und Ersteller in den Suchergebnissen an

### 5. Bearbeitungsberechtigungen implementiert
- **Private Notizen**: Nur der Ersteller kann sie bearbeiten
- **Öffentliche Notizen**: Alle Benutzer können sie bearbeiten
- Berechtigungsprüfung beim Laden einer Notiz zur Bearbeitung

## Funktionalität

### Neue Notiz erstellen
1. Benutzer wählt zwischen "Privat" und "Öffentlich"
2. Private Notizen sind nur für den Ersteller sichtbar
3. Öffentliche Notizen sind für alle Benutzer sichtbar und bearbeitbar

### Notizen anzeigen
- Die Tabelle zeigt alle privaten Notizen des Benutzers und alle öffentlichen Notizen
- Zusätzliche Spalten zeigen den Typ und den Ersteller der Notiz
- Benutzer können erkennen, wer eine öffentliche Notiz erstellt hat

### Notizen bearbeiten
- Private Notizen: Nur der Ersteller kann sie bearbeiten
- Öffentliche Notizen: Alle Benutzer können sie bearbeiten
- Automatische Berechtigungsprüfung verhindert unbefugte Bearbeitung

### Notizen suchen
- Suche funktioniert sowohl in privaten als auch in öffentlichen Notizen
- Suchergebnisse zeigen ebenfalls Typ und Ersteller

## Datenbank-Kompatibilität
- Das System prüft automatisch, ob die "Typ"-Spalte in der Datenbank existiert
- Falls nicht vorhanden, wird sie automatisch hinzugefügt
- Rückwärtskompatibilität mit bestehenden Notizen ohne Typ-Information

## Benutzerfreundlichkeit
- Einfachere Auswahl zwischen nur zwei Optionen
- Klare Kennzeichnung von öffentlichen vs. privaten Notizen
- Transparenz über den Ersteller öffentlicher Notizen
- Automatische Berechtigungsprüfung verhindert Verwirrung

## Technische Details
- Keine neuen Klassen hinzugefügt (wie gefordert)
- Bestehender Code überarbeitet und vereinfacht
- Robuste Fehlerbehandlung bei Datenbankoperationen
- Effiziente SQL-Abfragen für bessere Performance
# NoteGO - Klassendiagramm

## Übersicht

Das NoteGO-Projekt wurde von ursprünglich 25 Klassen auf 5 Kernklassen reduziert. Dieses Dokument beschreibt die Klassenstruktur und die Beziehungen zwischen den Klassen.

## UML-Klassendiagramm

```
┌─────────────────────────────────────┐
│            NotizProjekt             │
├─────────────────────────────────────┤
│ + main(String[] args): void         │
└─────────────────────────────────────┘
                    │
                    │ creates
                    ▼
┌─────────────────────────────────────┐
│                GUI                  │
├─────────────────────────────────────┤
│ + ANMELDEN: int                     │
│ + REGISTRIEREN: int                 │
│ + MENU: int                         │
│ + NEUE_NOTIZ: int                   │
│ + BEARBEITEN: int                   │
├─────────────────────────────────────┤
│ - guiTyp: int                       │
│ - currentUser: User                 │
│ - dbVerbindung: DBVerbindung        │
│ - notizListe: List<Notiz>           │
├─────────────────────────────────────┤
│ + GUI(int guiTyp)                   │
│ + setVisible(boolean visible): void │
│ + anmeldenGUI(): void               │
│ + registrierenGUI(): void           │
│ + menuGUI(): void                   │
│ + neueNotizGUI(): void              │
│ + bearbeitenGUI(): void             │
│ - initializeComponents(): void      │
│ - setupEventHandlers(): void        │
└─────────────────────────────────────┘
                    │
                    │ uses
                    ▼
┌─────────────────────────────────────┐
│           DBVerbindung              │
├─────────────────────────────────────┤
│ - host: String                      │
│ - database: String                  │
│ - username: String                  │
│ - password: String                  │
│ - connection: Connection            │
├─────────────────────────────────────┤
│ + DBVerbindung(String host,         │
│   String database, String username, │
│   String password)                  │
│ + open(): boolean                   │
│ + close(): void                     │
│ + executeQuery(String sql):         │
│   ResultSet                         │
│ + executeUpdate(String sql): int    │
│ + authenticateUser(String username, │
│   String password): User            │
│ + createUser(String username,       │
│   String password): boolean         │
│ + getNotesForUser(int userId):      │
│   List<Notiz>                       │
│ + saveNotiz(Notiz notiz): boolean   │
│ + updateNotiz(Notiz notiz): boolean │
│ + deleteNotiz(int notizId): boolean │
└─────────────────────────────────────┘
                    │
                    │ manages
                    ▼
┌─────────────────────────────────────┐
│               User                  │
├─────────────────────────────────────┤
│ - UName: String                     │
│ - UPasswort: String                 │
│ - B_id: int                         │
├─────────────────────────────────────┤
│ + User(String UName,                │
│   String UPasswort, int B_id)       │
│ + getB_id(): int                    │
│ + getUName(): String                │
│ + getUPasswort(): String            │
│ + setUName(String UName): void      │
│ + setUPasswort(String UPasswort):   │
│   void                              │
│ + setB_id(int B_id): void           │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│               Notiz                 │
├─────────────────────────────────────┤
│ - titel: String                     │
│ - tag: String                       │
│ - inhalt: String                    │
│ - id: int                           │
│ - userId: int                       │
│ - datum: String                     │
│ - uhrzeit: String                   │
│ - ort: String                       │
│ - mitbenutzer: String               │
│ - typ: NotizTyp                     │
├─────────────────────────────────────┤
│ + Notiz(int id, String titel,       │
│   String tag, String inhalt,        │
│   int userId)                       │
│ + Notiz(int id, String titel,       │
│   String tag, String inhalt,        │
│   int userId, NotizTyp typ)         │
│ + Notiz(int id, String titel,       │
│   String tag, String inhalt,        │
│   String datum, String uhrzeit,     │
│   String ort, String mitbenutzer,   │
│   int userId)                       │
│ + getTitel(): String                │
│ + setTitel(String titel): void      │
│ + getTag(): String                  │
│ + setTag(String tag): void          │
│ + getInhalt(): String               │
│ + setInhalt(String inhalt): void    │
│ + getId(): int                      │
│ + setId(int id): void               │
│ + getUserId(): int                  │
│ + setUserId(int userId): void       │
│ + getTyp(): NotizTyp                │
│ + setTyp(NotizTyp typ): void        │
│ + getDatum(): String                │
│ + setDatum(String datum): void      │
│ + getUhrzeit(): String              │
│ + setUhrzeit(String uhrzeit): void  │
│ + getOrt(): String                  │
│ + setOrt(String ort): void          │
│ + getMitbenutzer(): String          │
│ + setMitbenutzer(String             │
│   mitbenutzer): void                │
│ + toString(): String                │
└─────────────────────────────────────┘
                    │
                    │ contains
                    ▼
┌─────────────────────────────────────┐
│            NotizTyp                 │
│            <<enumeration>>          │
├─────────────────────────────────────┤
│ PRIVAT                              │
│ OEFFENTLICH                         │
│ GETEILT                             │
└─────────────────────────────────────┘
```

## Klassenbeziehungen

### Abhängigkeiten und Assoziationen

1. **NotizProjekt → GUI**
   - Beziehung: Erstellt und startet
   - Kardinalität: 1:1
   - Beschreibung: Die Hauptklasse erstellt eine GUI-Instanz

2. **GUI → DBVerbindung**
   - Beziehung: Verwendet
   - Kardinalität: 1:1
   - Beschreibung: GUI nutzt DBVerbindung für alle Datenbankoperationen

3. **GUI → User**
   - Beziehung: Verwaltet aktuellen Benutzer
   - Kardinalität: 1:0..1
   - Beschreibung: GUI hält Referenz auf den angemeldeten Benutzer

4. **GUI → Notiz**
   - Beziehung: Verwaltet Notizliste
   - Kardinalität: 1:0..*
   - Beschreibung: GUI verwaltet eine Liste von Notizen

5. **DBVerbindung → User**
   - Beziehung: Erstellt und authentifiziert
   - Kardinalität: 1:0..*
   - Beschreibung: DBVerbindung verwaltet Benutzer in der Datenbank

6. **DBVerbindung → Notiz**
   - Beziehung: Persistiert
   - Kardinalität: 1:0..*
   - Beschreibung: DBVerbindung speichert und lädt Notizen

7. **Notiz → NotizTyp**
   - Beziehung: Komposition
   - Kardinalität: 1:1
   - Beschreibung: Jede Notiz hat einen Typ

## Detaillierte Klassenbeschreibungen

### NotizProjekt
**Zweck**: Haupteinstiegspunkt der Anwendung
- Enthält die `main`-Methode
- Startet die GUI-Anwendung
- Minimale Klasse mit nur einer Methode

### GUI
**Zweck**: Zentrale Benutzeroberfläche
- Konsolidiert alle GUI-Funktionalitäten
- Verwaltet verschiedene Ansichten (Anmeldung, Registrierung, Hauptmenü, etc.)
- Behandelt Benutzereingaben und -interaktionen
- Kommuniziert mit der Datenbank über DBVerbindung

**Wichtige Konstanten**:
- `ANMELDEN`: Anmeldeansicht
- `REGISTRIEREN`: Registrierungsansicht  
- `MENU`: Hauptmenü mit Notizübersicht
- `NEUE_NOTIZ`: Neue Notiz erstellen
- `BEARBEITEN`: Notiz bearbeiten

### DBVerbindung
**Zweck**: Datenbankzugriff und -verwaltung
- Verwaltet MySQL-Datenbankverbindung
- Stellt CRUD-Operationen für Benutzer und Notizen bereit
- Behandelt Authentifizierung
- Abstrahiert SQL-Operationen

### User
**Zweck**: Benutzermodell
- Repräsentiert einen Systembenutzer
- Speichert Benutzername, Passwort und ID
- Einfache Datenklasse mit Gettern und Settern

### Notiz
**Zweck**: Einheitliches Notizmodell
- Unterstützt alle Notiztypen (privat, öffentlich, geteilt)
- Flexible Konstruktoren für verschiedene Anwendungsfälle
- Enthält alle notwendigen Felder für verschiedene Notiztypen
- Verwendet Enum für Typisierung

### NotizTyp (Enumeration)
**Zweck**: Typisierung von Notizen
- `PRIVAT`: Nur für den Ersteller sichtbar
- `OEFFENTLICH`: Für alle Benutzer sichtbar
- `GETEILT`: Mit bestimmten Benutzern geteilt

## Architekturprinzipien

### Vereinfachung
Das ursprüngliche Design mit 25 Klassen wurde auf 5 Kernklassen reduziert:
- Entfernung aller Testklassen
- Konsolidierung der GUI-Klassen
- Vereinheitlichung der Notizmodelle

### Separation of Concerns
- **Präsentation**: GUI-Klasse
- **Geschäftslogik**: In GUI und DBVerbindung aufgeteilt
- **Datenzugriff**: DBVerbindung
- **Datenmodell**: User und Notiz

### Erweiterbarkeit
- Neue Notiztypen können einfach über das NotizTyp-Enum hinzugefügt werden
- GUI kann um neue Ansichten erweitert werden
- Datenbankoperationen sind zentralisiert und erweiterbar

## Design Patterns

### Singleton-ähnliches Verhalten
- DBVerbindung wird typischerweise einmal pro GUI-Instanz erstellt

### Model-View-Controller (vereinfacht)
- **Model**: User, Notiz, NotizTyp
- **View**: GUI (Swing-Komponenten)
- **Controller**: GUI (Event-Handler) und DBVerbindung

### Factory-Pattern (implizit)
- DBVerbindung erstellt User- und Notiz-Objekte aus Datenbankdaten

## Vorteile der aktuellen Architektur

1. **Einfachheit**: Weniger Klassen, einfachere Navigation
2. **Wartbarkeit**: Zentralisierte Funktionalitäten
3. **Verständlichkeit**: Klare Verantwortlichkeiten
4. **Erweiterbarkeit**: Modularer Aufbau ermöglicht einfache Erweiterungen

## Mögliche Verbesserungen

1. **Trennung von GUI und Controller**: GUI-Klasse könnte in View und Controller aufgeteilt werden
2. **Interface-basierte Programmierung**: Abstrakte Interfaces für bessere Testbarkeit
3. **Dependency Injection**: Lose Kopplung zwischen Komponenten
4. **Observer Pattern**: Für automatische GUI-Updates bei Datenänderungen
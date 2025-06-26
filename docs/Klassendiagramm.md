# Klassendiagramm der Notiz Desktop Anwendung

## Übersicht

Dieses Dokument beschreibt die Klassenstruktur der Notiz Desktop Anwendung. Die Anwendung verwendet eine vereinfachte Struktur mit allen Klassen in einem einzigen `notizapp`-Paket.

## Hauptklassenstruktur

```
notizapp
├── NotizDesktopApplication.java  # Haupteinstiegspunkt der Anwendung
├── DatabaseConfig.java           # Datenbankverbindungskonfiguration
├── AuthFrame.java                # Kombinierte Anmelde- und Registrierungsklasse
├── MainFrame.java                # Hauptanwendungsfenster
├── Note.java                     # Kombiniertes Notizmodell und -service
├── User.java                     # Kombiniertes Benutzermodell und -service
├── NoteBoardPanel.java           # Notiztafel mit Drag-and-Drop
├── NoteDialog.java               # Kombinierte Notizbearbeitung/-anzeige
├── ProfileDialog.java            # Benutzerprofilverwaltung
└── ThemeManager.java             # Themenverwaltungssystem
```

## Klassenbeziehungen

### Hauptklassen

#### NotizDesktopApplication
- **Beschreibung**: Haupteinstiegspunkt der Anwendung
- **Verantwortlichkeiten**: Initialisierung der Anwendung, Erstellung des Hauptfensters
- **Beziehungen**:
  - Erstellt `AuthFrame` für die Benutzerauthentifizierung
  - Erstellt `MainFrame` nach erfolgreicher Anmeldung

#### DatabaseConfig
- **Beschreibung**: Verwaltet Datenbankverbindungen
- **Verantwortlichkeiten**: Verbindungspooling, SQL-Abfragen, Transaktionsverwaltung
- **Beziehungen**:
  - Wird von `Note` und `User` für Datenbankoperationen verwendet

### Modell- und Serviceklassen

#### Note
- **Beschreibung**: Kombiniertes Notizmodell und -service
- **Verantwortlichkeiten**: Notizendaten speichern, CRUD-Operationen für Notizen
- **Beziehungen**:
  - Verwendet `DatabaseConfig` für Datenbankoperationen
  - Wird von `MainFrame` und `NoteDialog` verwendet

#### User
- **Beschreibung**: Kombiniertes Benutzermodell und -service
- **Verantwortlichkeiten**: Benutzerdaten speichern, Authentifizierung, Profiloperationen
- **Beziehungen**:
  - Verwendet `DatabaseConfig` für Datenbankoperationen
  - Wird von `AuthFrame` und `ProfileDialog` verwendet

### UI-Klassen

#### AuthFrame
- **Beschreibung**: Kombinierte Anmelde- und Registrierungsklasse
- **Verantwortlichkeiten**: Benutzerauthentifizierung, Registrierung neuer Benutzer
- **Beziehungen**:
  - Verwendet `User` für Authentifizierung und Registrierung
  - Erstellt `MainFrame` nach erfolgreicher Anmeldung

#### MainFrame
- **Beschreibung**: Hauptanwendungsfenster
- **Verantwortlichkeiten**: Hauptbenutzeroberfläche, Navigation, Notizenverwaltung
- **Beziehungen**:
  - Enthält `NoteBoardPanel` für die Notizenanzeige
  - Verwendet `Note` für Notizoperationen
  - Erstellt `NoteDialog` für Notizbearbeitung
  - Erstellt `ProfileDialog` für Profilverwaltung

#### NoteBoardPanel
- **Beschreibung**: Notiztafel mit Drag-and-Drop
- **Verantwortlichkeiten**: Notizenanzeige, Drag-and-Drop-Funktionalität
- **Beziehungen**:
  - Verwendet `Note` für Notizendaten
  - Wird von `MainFrame` enthalten

#### NoteDialog
- **Beschreibung**: Kombinierte Notizbearbeitung/-anzeige
- **Verantwortlichkeiten**: Notizen erstellen, bearbeiten und anzeigen
- **Beziehungen**:
  - Verwendet `Note` für Notizoperationen
  - Wird von `MainFrame` erstellt

#### ProfileDialog
- **Beschreibung**: Benutzerprofilverwaltung
- **Verantwortlichkeiten**: Profilinformationen anzeigen und aktualisieren
- **Beziehungen**:
  - Verwendet `User` für Profiloperationen
  - Wird von `MainFrame` erstellt

#### ThemeManager
- **Beschreibung**: Themenverwaltungssystem
- **Verantwortlichkeiten**: Themenumschaltung, UI-Styling
- **Beziehungen**:
  - Wird von allen UI-Klassen verwendet

## Vereinfachte Klassenstruktur

Die Anwendung wurde kürzlich umstrukturiert, um die Anzahl der Klassen zu reduzieren und die Wartbarkeit zu verbessern:

1. **Modell- und Serviceklassen wurden vereinheitlicht**:
   - `Note` kombiniert das frühere `DesktopNote`-Modell und den `NoteService`
   - `User` kombiniert das frühere `DesktopUser`-Modell und den `UserService`

2. **UI-Komponenten mit ähnlicher Funktionalität wurden zusammengeführt**:
   - `AuthFrame` kombiniert die früheren separaten Login- und Registrierungsklassen
   - `NoteDialog` kombiniert die früheren separaten Bearbeitungs- und Anzeigedialoge

3. **Vorteile dieser Vereinfachung**:
   - Einfachere Navigation in NetBeans
   - Vereinfachte Import-Anweisungen
   - Reduzierte Komplexität beim Finden der Hauptklasse
   - Beseitigung paketbezogener Stack-Overflow-Probleme
   - Weniger Klassen zu warten und zu verstehen
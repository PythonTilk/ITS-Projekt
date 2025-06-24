# Änderungen am Projekt

## Übersicht der Änderungen

Das Projekt wurde von ursprünglich 25 Klassen auf nur noch 5 Klassen reduziert. Dies wurde durch folgende Maßnahmen erreicht:

1. **Entfernung aller Testklassen**
   - Alle Testklassen wurden entfernt, da sie für die Funktionalität der Anwendung nicht notwendig sind
   - Entfernte Klassen: TestDBConnection, TestGUI, TestGUINavigation, TestGUI_NeueNotizSharing, TestHeadless, TestLogin, TestNotizFunctionality, TestSharedNoteCreation

2. **Konsolidierung der GUI-Klassen**
   - Alle GUI-Klassen wurden in eine einzige GUI-Klasse konsolidiert
   - Entfernte Klassen: GUI_Anmelden, GUI_CreateSharedNotiz, GUI_MenuTabelle, GUI_NeuHier, GUI_NeueNotiz, GUI_NeueSharedNotiz, GUI_NotizBearbeiten, GUI_ShareNotiz, SimpleGUI_BearbeitenNotiz, SimpleGUI_MenuTabelle, SimpleGUI_NeuHier, SimpleGUI_NeueNotiz, SimpleShareNoteGUI

3. **Konsolidierung der Modellklassen**
   - Die Modellklassen (Notiz, OeffentlichNotiz, SharedNotiz) wurden in eine einzige Notiz-Klasse mit einem Typ-Attribut konsolidiert
   - Entfernte Klassen: OeffentlichNotiz, SharedNotiz

## Verbleibende Klassen

1. **NotizProjekt.java**
   - Hauptklasse zum Starten der Anwendung
   - Verwendet nun die konsolidierte GUI-Klasse

2. **GUI.java**
   - Konsolidierte GUI-Klasse, die alle GUI-Funktionalitäten enthält
   - Enthält die Funktionalitäten für Anmeldung, Registrierung, Notizübersicht, Neue Notiz erstellen und Notiz bearbeiten
   - Verwendet Konstanten für die verschiedenen GUI-Typen (ANMELDEN, REGISTRIEREN, MENU, NEUE_NOTIZ, BEARBEITEN)

3. **Notiz.java**
   - Modellklasse für Notizen
   - Enthält ein Typ-Attribut (NotizTyp enum) für die verschiedenen Notiztypen (PRIVAT, OEFFENTLICH, GETEILT)

4. **User.java**
   - Modellklasse für Benutzer
   - Unverändert

5. **DBVerbindung.java**
   - Klasse für die Datenbankverbindung
   - Unverändert

## Datenbankänderungen

Die Datenbank wurde vereinfacht:
- Die Tabelle `geteilte_notizen` wird nicht mehr benötigt, da alle Notiztypen in der Tabelle `notiz` gespeichert werden
- Die Tabelle `notiz` enthält nun ein zusätzliches Feld `Typ`, das den Typ der Notiz angibt (PRIVAT, OEFFENTLICH, GETEILT)

## Vorteile der Änderungen

1. **Einfachere Wartung**
   - Weniger Klassen bedeuten weniger Code, der gewartet werden muss
   - Einfachere Struktur macht das Projekt leichter verständlich

2. **Bessere Erweiterbarkeit**
   - Neue Notiztypen können einfach durch Hinzufügen eines neuen Enum-Werts in der Notiz-Klasse hinzugefügt werden
   - Neue GUI-Funktionalitäten können einfach in die bestehende GUI-Klasse integriert werden

3. **Reduzierte Redundanz**
   - Kein duplizierter Code mehr für ähnliche Funktionalitäten
   - Einheitliche Implementierung für alle Notiztypen

4. **Verbesserte Lesbarkeit**
   - Klare Struktur mit wenigen, gut dokumentierten Klassen
   - Einfachere Navigation im Code
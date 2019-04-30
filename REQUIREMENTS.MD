# Rechnerarchitektur GP 2019
#### Modularer Grafischer Schaltkreis Simulator (für die MI-Maschine)

*Ihr könnt hier gerne nach belieben Dinge ändern, haltet euch nicht zurück^^*

## Anforderungen

### Pflichtfeatures
1.  Emulation der MI-Maschine (siehe Am2900ME)
2.  Visualisierung der MI-Maschine sehr ähnlich dem Bild der VL
3.  sehr gute Website, Dokumentation, Hilfe aus dem Kontextmenü heraus
4.  Hilfreiche Fehlermeldungen, Warnungen bei gefährlichen Operationen  
    für uns/Entwickler: gutes Loggen, Absturzberichte
5.  Immer hilfreiche Tooltips zu fast allem.
    

### Sollte möglich sein
1.  Export der Ansicht
2.  Sicht des Assemblerprogrammierers, spezifizieren einer ISA und Nutzung
3.  Farben, Schriftgröße & Co. einstellbar (für Präsentation, Hell/Dunkel, Farbenblind/…)
4.  Entwurf eines Schaltkreises mit einem Editor
5.  Sprache ändern (für nicht Muttersprachler, internat. Potenzial, Erweiterungsmögl.)
6.  Sehr einfache Installation (Eclipse Marketplace)
7.  Editor sollte tooltip Einstellung erlauben
8.  Rückgängig
    

### Wunschfeatures
1.  Debuggen im Sinne von Breakpoints & Co.
2.  interaktives Tutorial in der GUI
3.  Zurückspulen der Simulation / Zeitleiste
4.  Wiederherstellungspunkt (Status)

### Weiterführende Ideen
1.  x86 Modell
2.  ARM Modell
3.  Signalgraphen

### Szenarios
<details><summary><strong>Alle Szenarios anzeigen</strong></summary><p>

#### 01. Installation
Das Programm lässt sich entweder über den Eclipse Marketplace einfach installieren (oder als Eclipse Projekt) oder **als eigenes Eclipse RPC Projekt direkt herunterladen (ggf. entpacken) und starten.**<sup>[A]</sup> Das Programm / Plugin lässt sich auf Windows (7+), MacOS und Linux ausführen; alle verbreitete Hardware nach 2010 mit 64 Bit OS sollte funktionieren.<sup>[B]</sup> Es muss auf den Rechnern in den TUM Rechnerhallen funktionieren.<sup>[C]</sup>

#### 02. Deinstallation
Das Programm muss sich ohne nennenswerten Aufwand oder Kentnisse vom Benutzer rückstandslos entfernen lassen.<sup>[A]</sup>

#### 03. Programmstart
Das Programm bzw. Plugin startet schnell (unter 10 sec.; besser unter 5 sec).<sup>[A]</sup> Bei dem ersten Programmstart ist ein Willkommensfenster zu sehen, das auf erklärende Resourcen verweist, den Nutzer grüßt und ggf. auf die (Sprach-)Einstellungen hinweist.<sup>[B]</sup> Es wird bestenfalls auf ein Beispielprojekt verwiesen (kann erstellt werden?) um dem Nutzer zu ermöglichen, sich damit vertraut zu machen.<sup>[C]</sup> Ein schnell Link zum Erstellen eines neuen Projekts/Simulation/... wird angezeigt.<sup>[D]</sup>

#### 04. Programmende
Sollten bei Beenden des Programms oder Programmabsturz nicht persistierte Daten vorliegen, muss dafür gesorgt werden, dass diese nicht verloren gehen.<sup>[A]</sup> Enweder durch einen Dialog<sup>[B]</sup>, oder durch erzeugte Recovery-Dateien<sup>[C]</sup>. Bei Absturz des Programms wird eine passende und aufschlussreiche Fehlermeldung angezeigt, bestenfalls mit Link zu einer Report-Möglichkeit.<sup>[D]</sup>

#### 05. Einstellungen
Die Hauptextfarbe und Schriftgröße muss einstellbar sein<sup>[A]</sup>, besser noch Hintergrund- und Markierungsfarben und Schriftstil.<sup>[B]</sup> Die Einstellung der Sprache muss möglich sein.<sup>[C]</sup>

#### 06. Einstieg
Das Einstiegsprojekt zeigt zu Beginn die passende Ansicht an, die Mikroprogrammierung ist wie bei dem Am2900ME ungehindert möglich.<sup>[A]</sup> Bestenfalls ist dieses beim (erstmaligen) Start direkt geöffnet.<sup>[B]</sup> Wichtige Fenster (v.a. die grafische Darstellung) sind direkt sichtbar, oder zumindest in einem Tab geöffnet (müssen nicht gesucht werden).<sup>[C]</sup>

#### 07. Nutzung der Visualisierung
Die Visualisierung soll möglichst der Vorlesung entsprechen.<sup>[A]</sup> Sie lässt sich transformieren (z.B. Zoom mit dem Mausrad, Verschieben mit Mausklick oder Mausklick und Alt) und wieder zurücksetzen.<sup>[B]</sup>

#### 08. Hilfestellung
Mit dem Produkt wird eine offline-Dokumentation mitgeliefert. (bestenfalls DE und EN)<sup>[A]</sup> Mit Rechtsklick lässt sich über ein Kontextmenü der Teil der Dokumentation öffnen, der das angeklickte und seine Funktion beschreibt. Dies sollte mit fast allem möglich sein.<sup>[B]</sup> Bleibt man mit der Maus länger über einem GUI-Bestandteil, wird eine kurze Beschreibung/Hilfe dazu angezeigt (Tooltip).<sup>[C]</sup>

#### Fügt hier eure eigenen dazu, fortlaufend nummeriert und ggf. mit <sup>[A,B,C,..]</sup> genauer spezifiziert.

</p></details>

## Technologie-Stack
*   Git (anscheinend über Gitlab)
*   Eclipse RCP
    -  (noch zu testen)
*   Java
    -   Version 8?
    -   Version 10?
    -   Version 12?
*   Bibliotheken
    -   Lombok? [https://projectlombok.org/features/all](https://projectlombok.org/features/all)
	Sauberer und sicherer Code; aber zusätzliche Dep.
    -   JUnit 5.4.x ? AssertJ? [http://joel-costigliola.github.io/assertj/index.html](http://joel-costigliola.github.io/assertj/index.html)
    - (Apache Commons?)
    Sehr weit verbreitete und gut designte & getestete Lib;  
    aber zusätzliche Dep.
    - Sonstiges? Logging? Log4j
-   Automatischer Build mit Maven? Wohl eher nicht, wegen ERPC Build

## Meilensteine

## Außenwirkung
### Name, Abkürzung
### Signet, Logo
### Beschreibung

## Sonstiges
### Nützliche Links
- [https://wiki.eclipse.org/Rich_Client_Platform](https://wiki.eclipse.org/Rich_Client_Platform) -Links
- [https://wiki.eclipse.org/User_Interface_Guidelines](https://wiki.eclipse.org/User_Interface_Guidelines) - etwas alt, aber nützlich
- [https://download.eclipse.org/eclipse/downloads/](https://download.eclipse.org/eclipse/downloads/) - Eclipse Project
- [https://www.vogella.com/tutorials/EclipseRCP/article.html](https://www.vogella.com/tutorials/EclipseRCP/article.html) - **Eclipse RPC Tutorial**
- [https://github.com/eclipse/gef/wiki](https://github.com/eclipse/gef/wiki) - **Wiki für das Grafikframework**
- [https://github.com/eclipse/MaisiKoleni/Am2900ME](https://github.com/eclipse/MaisiKoleni/Am2900ME) - der geistige Vorgänger
- [https://github.com/Haspamelodica/Am2900ME_swt](https://github.com/Haspamelodica/Am2900ME_swt) -  ... auch mit SWT

### Rechtliches
<details><summary>MIT Lizenz? - [https://choosealicense.com/licenses/mit/](https://choosealicense.com/licenses/mit/)</summary><p>

>MIT License
>
>Copyright (c) 2019 Christian Femers, Daniel Kirschten, Fabian Stemmler
>
> 
>
>Permission is hereby granted, free of charge, to any person obtaining a copy
>of this software and associated documentation files (the "Software"), to deal
>in the Software without restriction, including without limitation the rights
>to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
>copies of the Software, and to permit persons to whom the Software is
>furnished to do so, subject to the following conditions:
>
>The above copyright notice and this permission notice shall be included in all
>copies or substantial portions of the Software.
>
>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
>IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
>FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
>AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
>LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
>OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
>SOFTWARE.

</p></details>
Muss ggf. Eclipse Public License verwendet werden wegen Copyleft?

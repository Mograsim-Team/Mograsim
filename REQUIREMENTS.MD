# Rechnerarchitektur GP 2019
#### Modularer Grafischer Schaltkreis Simulator (für die MI-Maschine)

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
    
## Technologie-Stack
*   Git (anscheinend über Gitlab)
*   Eclipse RCP
	-  (noch zu testen)
*   Java
	- Version 8?
	-  Version 10?
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
[https://wiki.eclipse.org/Rich_Client_Platform](https://wiki.eclipse.org/Rich_Client_Platform) -Links
[https://wiki.eclipse.org/User_Interface_Guidelines](https://wiki.eclipse.org/User_Interface_Guidelines) - etwas alt, aber nützlich
[https://download.eclipse.org/eclipse/downloads/](https://download.eclipse.org/eclipse/downloads/) - Eclipse Project
[https://www.vogella.com/tutorials/EclipseRCP/article.html](https://www.vogella.com/tutorials/EclipseRCP/article.html) - Eclipse RPC Tutorial
[https://github.com/eclipse/gef/wiki](https://github.com/eclipse/gef/wiki) - Wiki für das Grafikframework

### Rechtliches
MIT Lizenz? - [https://choosealicense.com/licenses/mit/](https://choosealicense.com/licenses/mit/)

  

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

Muss ggf. Eclipse Public License verwendet werden wegen Copyleft?

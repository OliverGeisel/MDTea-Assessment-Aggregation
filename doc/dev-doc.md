# Entwicklerdokumentation für den MDTea Prototypen

## Inhaltsverzeichnis

<!-- TOC -->
* [Entwicklerdokumentation für den MDTea Prototypen](#entwicklerdokumentation-für-den-mdtea-prototypen)
  * [Inhaltsverzeichnis](#inhaltsverzeichnis)
  * [1. Einleitung](#1-einleitung)
  * [2. Architektur](#2-architektur)
    * [Allgemein](#allgemein)
    * [Frontend](#frontend)
    * [Backend](#backend)
      * [Aggregation](#aggregation)
      * [Generation](#generation)
      * [Finalization](#finalization)
  * [3. Installation](#3-installation)
  * [4. Konfiguration](#4-konfiguration)
    * [Modelle für GPT4All](#modelle-für-gpt4all)
    * [Neo4j](#neo4j)
    * [H2](#h2)
  * [5. Bauen und Starten](#5-bauen-und-starten)
  * [6. Bekannte Probleme](#6-bekannte-probleme)
    * [GPT4All](#gpt4all)
    * [Webanwendung](#webanwendung)
  * [Anhang](#anhang)
<!-- TOC -->

## 1. Einleitung

Dieses Dokument beschreibt die Entwicklerdokumentation für den MDTea Prototypen.
Der Prototyp setzt einen Teil von MDTea um.
Für Informationen zu MDTea siehe [MDTea](https://nbn-resolving.org/urn:nbn:de:bsz:14-qucosa2-894079).

Dieser Prototyp setzt die Aggregation von Wissen rudimentär um.
Es wird mithilfe eines LLM (Large Language Model) von GPT4All ein Textfragment analysiert und in Wissen umgewandelt.
Jedoch werden nicht alle Arten von Wissenselementen gefunden.
Auch die Relationen werden nur teilweise durch das LLM gefunden.
Für die Speicherung des Wissensmodells kommt Neo4j zum Einsatz.

Die Generation kann nun komplexe Materialien (Übersichten, Zusammenfassungen und Tests) generieren.
Zudem kamen die Assessment-Materialien hinzu.

In der Finalization werden die Materialien zu einem Kurs zusammengefasst.
Hier ist es möglich, neben HTML, auch das OPAL-Format für den Export zu wählen.
Die dabei entstandene Zip kann dann in OPAL importiert werden.
<hr>

## 2. Architektur

Dieser Prototyp benötigt für die vollständige Funktionalität drei Komponenten: Die compilierte **MDTea-Software**, ein
**GPT4All-Backend** und eine **Neo4j-Datenbank**.
Die folgende Abbildung zeigt die Architektur des Systems.
![Das MDTea System](images/system-overview-c4.png)
Die H2-Datenbank wird automatisch beim Start der MDTea-Software erstellt.
<span style="color:darkred">**Achtung:**</span> Die Datenbank wird bei jedem Start der MDTea-Software neu erstellt.
Das bedeutet, dass alle Daten verloren gehen.
Zum Behalten der Datenbank muss dies in den application.properties geändert werden.
([siehe hier]( #4-konfiguration))

Die MDTea-Software ist in Java geschrieben und wird als JAR-Datei bereitgestellt. Das GPT4All-Backend ist in Python
geschrieben.

### Allgemein

### Frontend

Das Frontend wird mit Spring Boot und Thymeleaf umgesetzt.
Alle HTML-Dateien befinden sich im Ordner `src/main/resources/templates/`.
Es gibt auch vereinzelte JavaScript-Dateien, die sich im Ordner `src/main/resources/static/js` befinden.
Das generelle Design wird mit Bootstrap umgesetzt.
An sich sind alle Requests synchron.
Nur wenige Requests werden asynchron ausgeführt.
Deshalb kann es zu längeren Ladezeiten kommen.
**Besonders** wenn die Anfrage an das GPT4All-Backend gestellt wird.
Auch die generierung von Materialien kann sehr lange dauern.
Hier ist der Grund eine schlechte Nutzung der Neo4j-Datenbank.
Es werden zu viele Anfragen gestellt, die nicht effizient sind.

### Backend

![C4 Ansicht zum Backend](images/backend-c4.png)
Das Bild zeigt die grobe Architektur des Backends.
Es gibt 3 Hauptkomponenten:

1. Aggregation
2. Generation
3. Finalization

Zudem gibt es noch ein Package `core`, welches von den anderen Packages benutzt wird.
Es enthält unter anderem die Klassen für die Struktur des Kurses und einen Kursplan.
Dieses Package ist aber veraltet und sollte in die anderen Packages überführt werden.

Wichtig ist der Aufbau eines Kurses.
Die Struktur eines Kurses ist allgemein wie folgt aufgebaut:

* Kurs
  * Kapitel 1
    * Gruppe 1
      * Task 1
        * Material 1-1
        * Material 1-2
      * Task 2
        * Material 2-1
      * Komplexes Material (Test)
    * Gruppe 2
      * Task 1
        * Material 1-1
  * Kapitel 2
  * ...

#### Aggregation

Die Abbildung zeigt die Packages des Aggregations-Teils.
![Ansicht zum Aggregations-Teil](images/aggregation-packages.png)

Dieses besteht aus den Teilen:

1. knowledgemodel
2. extraction
3. source

Das Knowledgemodel ist dabei Aufbau

`knowledgemodel` ist für das Wissensmodell zuständig.
Es enthält alle Klassen, die für das Wissensmodell benötigt werden.
`forms` ist enthält Formulare für die Controller.
`model` ist der Kern des Packages.
Hier sind alle Klassen enthalten, die für das Wissensmodell benötigt werden.
Alle Klassen des Wissensmodells haben eine `@Node`-Annotation.
Damit sind sie in der Neo4j-Datenbank speicherbar.
Es gibt noch ein `old_version`-Package, dies enthält die Version des Wissensmodells, welche mit jGraphT umgesetzt
wurde.
Diese hält das gesamte Wissensmodell in einem Objekt im Speicher.
Das Modell kann noch gut für das Laden von Wissensmodellen in Form von json-Dateien benutzt werden.
Ansonsten sollte aber das neue Modell mit dem Zugriff auf die Datenbank benutzt werden.

`extraction` ist für die Extraktion des Wissens aus einem Textfragment zuständig.

`source` ist für die Quellen des Wissens zuständig. Dieses Package ist noch nicht vollständig implementiert und wird
auch nicht wirklich benutzt.

#### Generation

Die Abbildung zeigt die Packages des Generations-Teils.
![Ansicht zum Generations-Teil](images/generation-packages.png)
Es gibt 4 Packages:

1. configuration
2. generator
3. material
4. templates

In `configuration` befinden sich alle Funktionen, die für das Laden bzw. Speichern von Konfigurationen zuständig sind.
Das sind konkret die Konfigurationen für Tests und Items (Fragen).
Es sind aber auch die entsprechenden Klassen für das Parsen der Konfigurationen enthalten.

In `generator` sind die namensgebenden Generatoren.
Das sind die Klassen, die die Materialien generieren.
Die Abbildung zeigt das Klassendiagramm für das Package.
![Klassendiagramm für das generator Package](images/generator-class-diagram.png)
Neben den _großen_ Generatoren gibt es noch Subgeneratoren.
Diese generieren nur ein bestimmtes Material.
Diese lassen sich entweder in ein Extractor oder Assembler zuordnen.
**Assembler** bauen Materialien aus anderen Materialien zusammen.
**Extractor** gehen in das Wissensmodell und versuchen Material aus Relationen, Struktur bzw. Schlussfolgerungen
daraus generieren.

`material` hat alle internen Repräsentationen der Materialien, die generiert werden können.
Sie sind nach den beiden Gruppen `transfer` und `assessment` aufgeteilt.

`templates` hat die Verwaltung für die Templates bzw. _TemplateSets_, die für die Generierung der Materialien
benutzt.
Die konkreten TemplateSets sind in `resources/templateSets` zu finden. Dort ist jedes TemplateSet in einem eigenen
Ordner.
Der Name des Ordners ist der Name des TemplateSets.
Jeder Ordner enthält die Templates für die Materialien.
Die Namen der Templates sind dabei die Namen der Materialien, die generiert werden können.
Sie werden immer komplett in Großbuchstaben geschrieben.
Ein Beispiel ist für ein Definition-Template: `DEFINITION.html`.
In einem `include`-Ordner können Dateien abgelegt werden, die im Kurs später eingebunden werden sollen.
Das können Bilder, Videos, CSS, JavaScript oder andere Dateien sein.
Im `exclude`-Ordner können Dateien abgelegt werden, die nicht in den Kurs eingebunden werden sollen.
Diese werden lediglich für das Erstellen des Kurses benutzt.
Dies können z.B. HTML-Vorlagen sein, die von Thymeleaf in die HTML-Datei eingebunden werden.

#### Finalization

In der Finalization werden zum einen die Materialien aus der Generation zu einem Kurs zusammengefasst.
Dafür sind die packages material_assign und parts zuständig (siehe Abbildung).
![Ansicht zum Finalization Package](images/finalization-packages.png)
Folgende Packages sind enthalten;

1. structure
2. material_assign
3. export

`strcture` ist dabei die Repräsentation des Kurses. Hier sind alle Klassen enthalten, die einen Rohkurs (RawCourse)
zusammenbauen.
Besonders ist dabei, dass alle Klassen aus den `core/courseplan`-Package überführbar sind.
Also fast jede Klasse besitzt einen Konstruktor, der aus der entsprechenden Klasse aus dem `core/courseplan`-Package
erstellt werden kann.

`material_assign` ist für die Zuweisung der Materialien zu den Kursen zuständig.
Hier wird entschieden, welches Material in welchem Kurs verwendet wird.
Das wird initial bei der Erstellung des Kurses gemacht.
Es werden zurzeit nur die Materialien für Aufgaben (Task) und Gruppen zugewiesen.
Es gibt momentan auch nur einen **BasicCriteriaSelector**, der die Materialien zuweist.
Dieser prüft nur, ob die Namen bzw. Aliase der Gruppe/Task mit dem Material übereinstimmen.

`export` ist für den Export des Kurses zuständig.
Hier werden die Materialien in die entsprechenden Formate umgewandelt.
Jedes Format hat einen eigenen Exporter.
Es gibt momentan nur den Exporter für HTML und OPAL.
Zudem wird jeder Export als ZIP-Datei erstellt und kann heruntergeladen werden.

<hr>

## 3. Installation

Es werden 2 Entwicklerumgebungen benötigt: Java und Python.
Die Installation von Java und Python wird hier nicht erklärt.
Dazu siehe die offizielle Dokumentation von [Java](https://jdk.java.net/21/)
und [Python](https://www.python.org/downloads/).
Wichtig ist die Java Version **muss exakt** die **21** sein.
Der Grund sind die benutzten Preview-Features.
Für Python wird mindestens die Version 3.10 benötigt.

Alle Abhängigkeiten für Java werden automatisch über den Build-Manager [Maven](https://maven.apache.org/) installiert.

Für python müssen folgende Bibliotheken installiert werden:

* argparse
* openai 0.28.1
* pathlib
* requests
* gpt4all

Für die Installation der Bibliotheken kann pip benutzt werden.

```bash
pip install argparse openai==0.28.1 pathlib requests gpt4all
```

Die Neo4j-Datenbank kann entweder lokal installiert werden oder über Docker.
Im Wurzelverzeichnis des Projektes befindet sich eine `docker-compose.yml`-Datei.
Mit dem Befehl

```bash
docker-compose up
```

wird die Datenbank erstellt und gestartet.
Die Datenbank ist dann unter `http://localhost:7474` erreichbar.
Standardmäßig ist kein Benutzername und Passwort gesetzt.
Die Datenbank wird langfristig gespeichert.
Die Daten sind in `/neo4j` gespeichert.
<hr>

## 4. Konfiguration

### Modelle für GPT4All

In der Datei `gpt-connection/model.json` befindet sich die Konfiguration für das Modell, das von GPT4All benutzt wird.
Wenn ein weiteres Modell benutzt werden soll, muss die Datei angepasst werden.
Dazu den Anzeigenamen und den konkreten Dateinamen hinzufügen.

Jedes Modell besitzt in dem GPT4All-Client eine gewisse Default-Konfiguration.
Diese kann im Client angepasst werden.
Dazu in den Einstellungen des Clients gehen und die Konfiguration für das gewünschte Modell anpassen.
Es sollte unbedingt die Länge des Kontexts angepasst werden.
Ist der Kontext zu kurz, kann es zu Abstürzen des Modells kommen.

### Neo4j

In der Datei `application.properties` befinden sich die Konfigurationen für die Neo4j-Datenbank.
Hier kann der Benutzername und das Passwort für die Datenbank geändert werden.
Im Normalfall ist kein Benutzername und Passwort gesetzt.
Auch im Docker-Compose-File ist kein Benutzername und Passwort gesetzt.
Für die Änderung der Server-Adresse muss in `application.properties` der Wert von `spring.data.neo4j.uri` geändert
werden.

### H2

Im Normalfall wird die H2-Datenbank bei jedem Start der MDTea-Software neu erstellt.
Das bedeutet, dass alle Daten verloren gehen.
Zum Behalten der Datenbank muss dies in den `application.properties` geändert werden.

```properties
# für eine lokale Datenbank im Dateisystem
spring.datasource.url=jdbc:h2:file:./data/mdtea
```

<hr>

## 5. Bauen und Starten

Im Wurzelverzeichnis des Projektes kann das Projekt mit Maven gebaut werden.
Hierzu kann entweder das System-Maven oder der mitgelieferte Wrapper benutzt werden.

Folgender Befehl baut das Projekt (mit dem Wrapper):

```bash
./mvnw clean package
```

Das erstellt eine JAR-Datei im Ordner `target/`.
Die JAR-Datei kann dann mit dem Befehl

```bash
java -jar target/MDTea-Assessment-Aggregation-1.1.0-SNAPSHOT.jar
```

Für die genaue Nutzung der Anwendung im Betrieb sollte das [Handbuch](./manual.md) konsultiert werden.
<hr>

## 6. Bekannte Probleme

### GPT4All

GPT4All ist eine Software, die ständig weiterentwickelt wird. Es kann sein, dass die Version, die in diesem Projekt
benutzt wurde (2.7.2) nicht mehr aktuell ist.
Es kann daher auch sein, dass die Probleme, die hier beschrieben sind, nicht mehr aktuell sind.

* Absturz von GPT4All, wenn der Prompt zu lang ist (mit Mistral instruct mehrfach aufgetreten)
* Ausführen des Modells auf der CPU, obwohl es auf der GPU ausgeführt werden sollte -> Liegt an der Architektur des
  genutzten
  Modells. Es kann sein, dass das Modell nicht auf der GPU ausgeführt werden kann.
* Fehlerhafte Ausgabe des Modells -> Das Modell gibt nicht immer die gewünschte Ausgabe -> Oft hilft ein erneutes
  Ausführen des Modells.

### Webanwendung

* Stackoverflow bei anfrage der Elemente der Struktur -> in `/knowledge-model` kommt es vermehrt zu einem Stackoverflow.
  Das liegt an der Verlinkung der Wissenselemente. Durch die Anfrage, die Elemente der Struktur zu laden, kann es sein,
  dass die Elemente zu sehr verlinkt sind und dadurch ein Stackoverflow entsteht.
* Lange Ladezeiten -> Die Anwendung ist nicht für den produktiven Einsatz gedacht. Es kann sein, dass die Anwendung
  lange lädt, wenn das GPT4All arbeitet. Daher sind mehrere Anfragen möglich und es kann zu ungewollten Ergebnissen
  führen.
* Unerwartetes ende noch vor der Analyse -> Es kann sein, dass bei einem Fragment, das analysiert werden soll,
  sofort eine Fehlermeldung kommt.
  Hier ist die Ursache nicht bekannt.
  Es ist vermutlich ein Sonderzeichen (wie µ), das im Hintergrund nicht escaped wird und dadurch den String falsch
  interpretiert und somit zum Absturz führt.
  Der Fehler wurde aber bereits auf innerhalb des Python-Programms bzw den Aufruf jenes Programms eingegrenzt.
* Verlinkung der Struktur bzw. Bearbeitung -> Bei der Verlinkung bzw. verschiebung in der Struktur des
  Wissensmodells kommt es vereinzelt zu Fehlern.
  Dann kann es sein, dass die Baumstruktur plötzlich nicht mehr stimmt und ein Knoten mehrfach vorkommt oder mehrere
  Eltern hat.

<hr>

## Anhang

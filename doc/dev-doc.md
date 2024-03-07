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
    * [5. Bauen und Starten](#5-bauen-und-starten)
    * [6. Bekannte Probleme](#6-bekannte-probleme)
    * [Anhang](#anhang)

<!-- TOC -->

## 1. Einleitung

Dieses Dokument beschreibt die Entwicklerdokumentation für den MDTea Prototypen. Der Prototyp setzt einen Teil von
MDTea um. Für Informationen zu MDTea siehe [MDTea](https://nbn-resolving.org/urn:nbn:de:bsz:14-qucosa2-894079).

## 2. Architektur

Dieser Prototyp benötigt für die vollständige Funktionalität drei Komponenten: Die compilierte MDTea-Software, ein
GPT4All-Backend und eine Neo4j-Datenbank. Die folgende Abbildung zeigt die Architektur des Systems.
![Das MDTea System](images/system-overview-c4.png)
Die H2-Datenbank wird automatisch beim Start der MDTea-Software erstellt.
<span style="color:darkred">**Achtung:**</span> Die Datenbank wird bei jedem Start der MDTea-Software neu erstellt. Das
bedeutet, dass alle Daten verloren gehen.
Zum Behalten der Datenbank muss dies in den application.properties geändert werden.
([siehe hier]( #4-konfiguration))

Die MDTea-Software ist in Java geschrieben und wird als JAR-Datei bereitgestellt. Das GPT4All-Backend ist in Python
geschrieben.

### Allgemein

### Frontend

Das Frontend wird mit Spring Boot und Thymeleaf umgesetzt.
alle HTML-Dateien befinden sich im Ordner `src/main/resources/templates/`.
Es gibt auch vereinzelte JavaScript-Dateien, die sich im Ordner `src/main/resources/static/js` befinden.
Das generelle Design wird mit Bootstrap umgesetzt.
An sich alle Requests synchron. Nur wenige Requests werden asynchron ausgeführt.
Deshalb kann es zu längeren Ladezeiten kommen.
**Besonders** wenn die Anfrage an das GPT4All-Backend gestellt wird.

### Backend

![C4 Ansicht zum Backend](images/backend-c4.png)
Das Bild zeigt die grobe Architektur des Backends.
Es gibt 3 Hauptkomponenten:

1. Aggregation
2. Generation
3. Finalization

#### Aggregation

Die Abbildung zeigt die Packages des Aggregations-Teils.
![Ansicht zum Aggregations-Teil](images/aggregation-packages.png)

#### Generation

Die Abbildung zeigt die Packages des Generations-Teils.
![Ansicht zum Generations-Teil](images/generation-packages.png)
Es gibt 4 Packages:

1. configuration
2. generator
3. material
4. templates

In `configuration` befinden sich alle Funktionen, die für das Laden bzw. Speichern von Konfigurationen zuständig sind.

In `generator` sind die namensgebenden Generatoren.
Das sind die Klassen, die die Materialien generieren.
Die Abbildung zeigt das Klassendiagramm für das Package.
![Klassendiagramm für das Generator-Package](images/generator-class-diagram.png)

#### Finalization

In der Finalization werden zum einen die Materialien aus der Generation zu einem Kurs zusammengefasst.
Dafür sind die packages material_assign und parts zuständig (siehe Abbildung).
![Ansicht zum Finalization-Teil](images/finalization-packages.png)

## 3. Installation

Es werden 2 Entwicklerumgebungen benötigt: Java und Python. Die Installation von Java und Python wird hier nicht
erklärt.
Dazu siehe die offizielle Dokumentation von [Java](https://jdk.java.net/21/)
und [Python](https://www.python.org/downloads/).
Wichtig ist die Java Version **muss exakt** die 21 sein. Der Grund sind die benutzten Preview-Features.
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

die Neo4j-Datenbank kann entweder lokal installiert werden oder über Docker.
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

## 4. Konfiguration

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

## 6. Bekannte Probleme

## Anhang

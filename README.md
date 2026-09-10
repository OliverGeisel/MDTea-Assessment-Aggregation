# MDTea Assessment and Aggregation

<span style="color:red">**This project is a prototype for MDTea. It's still in development.**</span> <br>
<span style="color:green">**For the full documentation look into _[doc/manual.md](doc/manual.md)_ 
and _[doc/dev-doc_.md](doc/dev-doc.md)_**</span>

Example implementation for Model-Driven Teaching (MDTea).
Based on the [Material-Generator](https://github.com/OliverGeisel/Material-generator) project.
Add the features:

* Assessment-Generator
* Simple Aggregation-Phase

<hr>

## Installation and Requirements

### Java

This project is a maven project. Use the wrapper to build the project. You need exact java 21. This project use
preview-feature of java 21.

### Python

This project use a python program to connect to a gpt-model (or other LLM) to find knowledge elements. 
Please check if you have python3 and the following packages installed:

- requests
- openai **version:0.28.1**
- argparse
- gpt4all

use the `requirements.txt` file to install the packages with the following command:

```shell
pip install -r requirements.txt
```

### docker

You need a Neo4j-Database. You can use the docker-compose file in the root directory to start a neo4j-database.
You can start the database with the following command:

```shell
# current wd is the root directory of the project
docker-compose up -d
```

### GPT4All

There are two ways to use GPT4All. You can use the compiled version for your OS or you use the backend-version. For
both please visit the [GPT4All](https://gpt4all.io/index.html) project. 

<hr>

## Run

### CLI
use **maven** to build the project. You can use the following command to build the project:

```shell
./mvnw clean package
```
or if you skip the tests:

```shell
./mvnw clean package -DskipTests
```

then you find the jar-file in the `target` directory. You can run the jar-file with the following command:

```shell
java -jar target/MDTea-Assessment-Aggregation-1.1.0-SNAPSHOT.jar
```
Then the application is available under `http://localhost:8080/`

### IntelliJ
The Project contains an `.run` folder with a run-configuration for IntelliJ. You can use this configuration to run the project in IntelliJ.

- MDTea start: normal start of the application (includes the docker-compose up command)
- MDTea start-load: Will load the given Graph of `src/resources/data/knowledge/knowledgedata.json` into the database. This is a small example graph for testing.
- All tests: Will run all tests in the project. This includes the integration tests and the unit tests.
- All Unit tests: Will run all unit tests in the project.
- docker-compose start: Will start the docker-compose file in the root directory. This will only start the 
  neo4j-database.


## Change Log

### 1.2.0
- Update dependencies
  - update to Spring Boot 4.1.1 
  - Update to Neo4j to 2026-community-trixie
  - Other dependencies are updated to the latest version (Sept. 2026)
- Add selenide and webdriver-manager for testing
- Remove deprecated version in docker-compose
- Add requirements.txt for python dependencies
- Add Testcontainers for future development
- Switch Structure AliasMapping to a better structure in Database

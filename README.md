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

### java

This project is a maven project. Use the wrapper to build the project. You need exact java 21. This project use
preview-feature of java 21.

### python

This project use a python program to connect to a gpt-model (or other LLM) to find knowledge elements. Please check
if you have python3 and the following packages installed:

- requests
- openai **version:0.28.1**
- argparse
- gpt4all

### docker

You need a Neo4j-Database. You can use the docker-compose file in the root directory to start a neo4j-database.

### GPT4All

There are two ways to use GPT4All. You can use the compiled version for your OS or you use the backend-version. For
both please visit the [GPT4All](https://gpt4all.io/index.html) project. 

<hr>

## Run
use maven to build the project. You can use the following command to build the project:

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



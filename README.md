# MDTea Assessment and Aggregation

Example implementation for Model-Driven Teaching (MDTea).
Based on the [Material-Generator](https://github.com/OliverGeisel/Material-generator) project.
Add the features:

* Assessment-Generator
* Simple Aggregation-Phase

## Installation

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


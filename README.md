
ixa-pipe-sst
=============
[![Build Status](https://travis-ci.org/ixa-ehu/ixa-pipe-sst.svg?branch=master)](https://travis-ci.org/ixa-ehu/ixa-pipe-sst)
[![GitHub license](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/apache/opennlp/master/LICENSE)

ixa-pipe-sst is a multilingual Supersense tagger (or Word class disambiguation tagger).

ixa-pipe-sst is part of IXA pipes, a multilingual set of NLP tools developed
by the IXA NLP Group [http://ixa2.si.ehu.es/ixa-pipes].

Please go to [http://ixa2.si.ehu.es/ixa-pipes] for general information about the IXA
pipes tools but also for **official releases, including source code and binary
packages for all the tools in the IXA pipes toolkit**.

This document is intended to be the **usage guide of ixa-pipe-sst**. If you really need to clone
and install this repository instead of using the releases provided in
[http://ixa2.si.ehu.es/ixa-pipes], please scroll down to the end of the document for
the [installation instructions](#installation).

**NOTICE!!**: ixa-pipe-nerc is now in [Maven Central](http://search.maven.org/)
for easy access to its API.

## TABLE OF CONTENTS

1. [Overview of ixa-pipe-nerc](#overview)
  + [Available features](#features)
  + [SST distributed models](#sst-models)
  + [Epinonyms distributed models](#epinonyms-models)
2. [Usage of ixa-pipe-nerc](#cli-usage)
  + [Tagging](#tagging)
  + [Server mode](#server)
3. [API via Maven Dependency](#api)
4. [Git installation](#installation)

## OVERVIEW

### Features

**A description of every feature is provided in the sequenceTrainer.properties properties
file** distributed with ixa-pipe-nerc. As the training functionality is configured in
properties files, please do check this document. For each model distributed,
there is a prop file which describes the training of the model, as well as a
log file which provides details about the evaluation and training process.

### SST-Models

## CLI-USAGE

ixa-pipe-nerc provides a runable jar with the following command-line basic functionalities:

1. **server**: starts a TCP service loading the model and required resources.
2. **client**: sends a NAF document to a running TCP server.
2. **tag**: reads a NAF document containing *wf* and *term* elements and tags named
   entities.

Each of these functionalities are accessible by adding (server|client|tag) as a
subcommand to ixa-pipe-sst-${version}-exec.jar. Please read below and check the -help
parameter:

````shell
java -jar target/ixa-pipe-sst-${version}-exec.jar tag -help
````

### Tagging

If you are in hurry, just execute:

````shell
cat file.txt | ixa-pipe-tok | ixa-pipe-pos | java -jar ixa-pipe-sst-${version}-exec.jar tag -m model.bin
````

If you want to know more, please follow reading.

ixa-pipe-nerc reads NAF documents (with *wf* and *term* elements) via standard input and outputs NAF
through standard output. The NAF format specification is here:

(http://wordpress.let.vupr.nl/naf/)

You can get the necessary input for ixa-pipe-nerc by piping
[ixa-pipe-tok](https://github.com/ixa-ehu/ixa-pipe-tok) and
[ixa-pipe-pos](https://github.com/ixa-ehu/ixa-pipe-pos) as shown in the
example.

There are several options to tag with ixa-pipe-sst:

+ **model**: pass the model as a parameter.
+ **language**: pass the language as a parameter.
+ **outputFormat**: Output annotation in a format: available CoNLL03, CoNLL02,
  OpenNLP native format and NAF. It defaults to NAF.

**Example**:

````shell
cat file.txt | ixa-pipe-tok | ixa-pipe-pos | java -jar ixa-pipe-sst-${version}-exec.jar tag -m en-model.bin
````

### Server

We can start the TCP server as follows:

````shell
java -jar target/ixa-pipe-sst-${version}-exec.jar server -l en --port 2060 -m model.bin
````
Once the server is running we can send NAF documents containing (at least) the term layer like this:

````shell
 cat file.pos.naf | java -jar target/ixa-pipe-sst-${version}-exec.jar client -p 2060
````

## API

The easiest way to use ixa-pipe-nerc programatically is via Apache Maven. Add
this dependency to your pom.xml:

````shell
<dependency>
    <groupId>eus.ixa</groupId>
    <artifactId>ixa-pipe-sst</artifactId>
    <version>1.0.0</version>
</dependency>
````

## JAVADOC

The javadoc of the module is located here:

````shell
ixa-pipe-nerc/target/ixa-pipe-sst-$version-javadoc.jar
````

## Module contents

The contents of the module are the following:

    + formatter.xml           Apache OpenNLP code formatter for Eclipse SDK
    + pom.xml                 maven pom file which deals with everything related to compilation and execution of the module
    + src/                    java source code of the module and required resources
    + Furthermore, the installation process, as described in the README.md, will generate another directory:
    target/                 it contains binary executable and other directories
    + trainParams.properties      A template properties file containing documention
    for every available option


## INSTALLATION

Installing the ixa-pipe-nerc requires the following steps:

If you already have installed in your machine the Java 1.7+ and MAVEN 3, please go to step 3
directly. Otherwise, follow these steps:

### 1. Install JDK 1.7 or JDK 1.8

If you do not install JDK 1.7+ in a default location, you will probably need to configure the PATH in .bashrc or .bash_profile:

````shell
export JAVA_HOME=/yourpath/local/java7
export PATH=${JAVA_HOME}/bin:${PATH}
````

If you use tcsh you will need to specify it in your .login as follows:

````shell
setenv JAVA_HOME /usr/java/java17
setenv PATH ${JAVA_HOME}/bin:${PATH}
````

If you re-login into your shell and run the command

````shell
java -version
````

You should now see that your JDK is 1.7 or 1.8.

### 2. Install MAVEN 3

Download MAVEN 3 from

````shell
wget http://apache.rediris.es/maven/maven-3/3.0.5/binaries/apache-maven-3.0.5-bin.tar.gz
````
Now you need to configure the PATH. For Bash Shell:

````shell
export MAVEN_HOME=/home/ragerri/local/apache-maven-3.0.5
export PATH=${MAVEN_HOME}/bin:${PATH}
````

For tcsh shell:

````shell
setenv MAVEN3_HOME ~/local/apache-maven-3.0.5
setenv PATH ${MAVEN3}/bin:{PATH}
````

If you re-login into your shell and run the command

````shell
mvn -version
````

You should see reference to the MAVEN version you have just installed plus the JDK that is using.

### 3. Get module source code

If you must get the module source code from here do this:

````shell
git clone https://github.com/ixa-ehu/ixa-pipe-sst
````

### 4. Compile

Execute this command to compile ixa-pipe-sst:

````shell
cd ixa-pipe-sst
mvn clean package
````
This step will create a directory called target/ which contains various directories and files.
Most importantly, there you will find the module executable:

ixa-pipe-sst-${version}-exec.jar

This executable contains every dependency the module needs, so it is completely portable as long
as you have a JVM 1.8 installed.

To install the module in the local maven repository, usually located in ~/.m2/, execute:

````shell
mvn clean install
````

## Contact information

````shell
Rodrigo Agerri
IXA NLP Group
University of the Basque Country (UPV/EHU)
E-20018 Donostia-San Sebastián
rodrigo.agerri@ehu.eus
````

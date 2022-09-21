# README #

This is basic guide to start with springboot kafka learning.

## What is this repository for? ###

* Quick summary
  * springboot-kafka learning project
  * kafka-template
* Version 
  * Kafka-learning - 0.0.1-SNAPSHOT
  * Java - 8
  * Kafka - 2.13-3.2.1
  * maven - 3.8.1
* [Learn Kafka](https://bitbucket.org/patiludayk/kafka-basics/)

##How do I get set up? ###

* Prerequisite 
  * [Java](https://www.oracle.com/uk/java/technologies/javase/javase8-archive-downloads.html)
  * [Maven](https://maven.apache.org/download.cgi)
  * [Springboot](https://spring.io)
    * create basic springboot project from [here](https://start.spring.io) with below dependencies 
  * [Kafka](https://kafka.apache.org/downloads)
  * [Rest API tool](https://www.postman.com/downloads/)
    * Good to have
      * [Offset Explorer](https://www.kafkatool.com/download.html) tool
      * [Conduktor](https://www.conduktor.io)
  * update [input.txt](src/main/resources/scripts/input.txt) file for zookeeper and kafka configuration.
* Configuration
  * set kafka home bin directory to 
    * **kafka.bin.directory**=path to kafka bin directory
  * set kafka config directory to 
    * **kafka.config.directory**=path to kafka config directory
* Dependencies
  * spring-boot-starter-web
  * spring-kafka
  * lombok
* Database configuration
  * NA
* How to run tests
  * under development
* Once checkout build using below command from project root folder.
  * mvn clean install
* Deployment instructions
  * build deployable artifact using jar/war option and deploy to server.
  * No special requirement except path to kafka directories.

##How to Run/start 
* Run as normal spring boot project from IDE.
* If not using STS or any springboot supported plugin run as below maven goal for class _KafkaLearningProjectApplication.java_
  * spring-boot:run
  * from any API tool, hit API - /kafka/send with POST method to send event to kagfka broker
  * once received 200 successful response, connect kafka broker using Offset Explore to check your event at Kafka against topic name you provided.

##REST API
The REST API to the example app is described below.

###1. Push message to kafka
#### Request
`POST /kafka/produce`

    curl -i -H 'Accept: application/json' http://localhost:8080/kafka/produce

#### Response

    HTTP/1.1 200 OK
    Date: Thu, 22 Sept 2022 00:32:30 GMT
    Status: 200 OK
    Connection: close
    Content-Type: application/json
    Content-Length: 2

    [
    {
        "partition": 0,
        "offset": 2,
        "msg": "msg delivered.",
        "exception": null
    },
    {
        "partition": 0,
        "offset": 3,
        "msg": "msg delivered.",
        "exception": null
    }
    ]

### 2. new endpoint.. - coming soon...

#### Request

`POST /thing/`

    curl -i -H 'Accept: application/json' -d 'name=Foo&status=new' http://localhost:8080/thing

#### Response

    HTTP/1.1 201 Created
    Date: Thu, 24 Feb 2011 12:36:30 GMT
    Status: 201 Created
    Connection: close
    Content-Type: application/json
    Location: /thing/1
    Content-Length: 36

    {"id":1,"name":"Foo","status":"new"}


### Contribution guidelines ###

* Writing tests
  * under development
* Code review
  * under development
* Other guidelines
  * under development

### Who do I talk to? ###

* Repo [owner](https://www.linkedin.com/in/patiludayk/) or [admin](https://www.linkedin.com/in/patiludayk/)

### Misc
* How to connect to zk server
  * _sh zookeeper-shell.sh localhost:2181_ [more detail](https://zookeeper.apache.org/doc/r3.3.3/zookeeperStarted.html)

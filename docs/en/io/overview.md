# HStream IO Overview

HStream IO is an internal data integration framework for HStreamDB, composed of connectors and IO runtime.
It allows interconnection with various external systems,
facilitating the efficient flow of data across the enterprise data stack and thereby unleashing the value of real-time-ness.

## Motivation

HStreamDB is a streaming database,
we want to build a reliable data integration framework to connect HStreamDB with external systems easily,
we also want to use HStreamDB to build a real-time data synchronization service (e.g. synchronizes data from MySQL to PostgreSQL).

Here are our goals for HStream IO:

* easy to use
* scalability
* fault-tolerance
* extensibility
* streaming and batch
* delivery semantics

HStream IO is highly inspired by Kafka Connect, Pulsar IO, Airbyte, etc. frameworks,
we will introduce the architecture and workflow of HStream IO,
and compare it with other frameworks to describe how HStream IO achieves the goals listed above.

## Architect and Workflow

HStream IO consists of two components:

* IO Runtime: IO Runtime is a part of HStreamDB managing and empowering scalability, fault-tolerance, and load-balancing for connectors.
* Connectors: Connectors are used to synchronize data between HStreamDB and external systems.

HStream IO provides two types of connectors:
* Source Connector - A source connector subscribes to data from other systems such as MySQL, and PostgreSQL, making the data available for data processing in HStreamDB.
* Sink Connector - A sink connector writes data to other systems from HStreamDB streams.

For a clear understanding,
we would name a running connector process to be a task and the Docker image for the connector is a connector plugin.

Here is a summary workflow of creating a source connector:

1. Users can send a CREATE SOURCE CONNECTOR SQL to HStreamDB to create a connector
2. HStreamDB dispatches the request to a correct node
3. HStream IO Runtime handles the request to launch a connector task
4. the connector task will fetch data from source systems and store them in HStreamDB.

## Design and Implement

### Easy to use

HStream IO is a part of HStreamDB,
so if you want to create a connector,
do not need to deploy an HStream IO cluster like Kafka Connect,
just send a SQL to HStreamDB, e.g.:

```
create source connector source01 from mysql with
    ( "host" = "mysql-s1"
    , "port" = 3306
    , "user" = "root"
    , "password" = "password"
    , "database" = "d1"
    , "table" = "person"
    , "stream" = "stream01"
    );
```

### Scalability, Availability, and Delivery Semantics

Connectors are resources for HStreamDB Cluster,
HStreamDB Cluster provides high scalability and fault-tolerance for HStream IO,
for more details, please check HStreamDB docs.

Users can manually create multiple connectors for sources or streams to use parallel synchronization to achieve better performance,
we will support a connector scheduler for dynamical parallel synchronization like Kafka Connect and Pulsar IO soon.

When a connector is running, the offsets of the connector will be recorded in HStreamDB,
so if the connector failed unexpectedly,
HStream IO Runtime will detect the failure and recover it by recent offsets,
even if the node crashed,
HStreamDB cluster will rebalance the connectors on the node to other nodes and recover them.

HStream IO supported at-least-once delivery semantics now,
we will support more delivery semantics(e.g. exactly-once delivery semantic) for some connectors later.

### Streaming and Batch

Many ELT frameworks like Airbyte are designed for batch systems,
they can not handle streaming data efficiently,
HStreamDB is a streaming database,
and a lot of streaming data need to be loaded into HStreamDB,
so HStream IO is designed to support both streaming and batch data,
and users can use it to build a real-time streaming data synchronization service.

### Extensibility

We want to establish a great ecosystem like Kafka Connect and Airbyte,
so an accessible connector API for deploying new connectors is necessary.

Kafka Connect design a java connector API,
you can not develop connectors in other languages easily,
Airbyte and Pulsar IO inspired us to build a connector plugin as a Docker image to support multiple languages
and design a protocol between HStream IO Runtime and connectors,
but it brings more challenges to simplify the connector API,
you can not implement a couple of Java interfaces to build a connector easily like Kafka Connect,
you have to care about how to build a Docker image, 
handle command line arguments,
implement the protocol interfaces correctly, etc.

So to avoid that we split the connector API into two parts:

* HStream IO Protocol
* Connector Toolkit

Compared with Airbyte's heavy protocol,
HStream IO Protocol is designed as simple as possible,
it provides basic management interfaces for launching and stopping connectors,
does not need to exchange record messages(it will bring more latencies),
the Connector Toolkit is designed to handle heaviest jobs(e.g. fetch data from source systems, write data into HStreamDB, recorded offsets,  etc.)
to provide the simplest connector API,
so developers can use Connector Toolkit to implement new connectors easily like Kafka Connect.

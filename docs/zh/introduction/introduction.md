# Introduction to HStreamDB

## Overview

**HStreamDB is a streaming database designed for streaming data, with complete
lifecycle management for accessing, storing, processing, and distributing
large-scale real-time data streams**. It uses standard SQL (and its stream
extensions) as the primary interface language, with real-time as the main
feature, and aims to simplify the operation and management of data streams and
the development of real-time applications.

## Why HStreamDB?

Nowadays, data is continuously being generated from various sources, e.g. sensor
data from the IoT, user-clicking events on the Internet, etc.. We want to build
low-latency applications that respond quickly to these incoming streaming data
to provide a better user experience, real-time data insights and timely business
decisions.

However, currently, it is not easy to build such stream processing applications.
To construct a basic stream processing architecture, we always need to combine
multiple independent components. For example, you would need at least a
streaming data capture subsystem, a message/event storage component, a stream
processing engine, and multiple derived data systems for different queries.

None of these should be so complicated, and this is where HStreamDB comes into
play. Just as you can easily build a simple CRUD application based on a
traditional database, with HStreamDB, you can easily build a basic streaming
application without any other dependencies.

## Key Features 

### Reliable, low-latency streaming data storage

With an optimized storage engine design, HStreamDB provides low latency persistent storage of streaming data and replicates written data to multiple storage nodes to ensure data reliability.

It also supports hierarchical data storage and can automatically dump historical data to lower-cost storage services such as object storage, distributed file storage, etc. The storage capacity is infinitely scalable, enabling permanent storage of data.

### Easy support and management of large scale data streams 

HStreamDB uses a stream-native design where data is organized and accessed as streams, supporting creating and managing large data streams. Stream creation is a very lightweight operation in HStreamDB, maintaining stable read and write latency despite large numbers of streams being read and written concurrently.

The performance of HStreamDB streams is excellent thanks to its native design, supporting millions of streams in a single cluster.

### Real-time, orderly data subscription delivery

HStreamDB is based on the classic publish-subscribe model, providing low-latency data subscription delivery for data consumption and the ability to deliver data subscriptions in the event of cluster failures and errors.

It also guarantees the orderly delivery of machines in the event of cluster failures and errors.

### Powerful stream processing support built-in 

HStreamDB has designed a complete processing solution based on event time. It supports basic filtering and conversion operations, aggregations by key, calculations based on various time windows, joining between data streams, and processing disordered and late messages to ensure the accuracy of calculation results. Simultaneously, the stream processing solution of HStream is highly extensible, and users can extend the interface according to their own needs.

### Real-time analysis based on materialized views
  
HStreamDB will offer materialized view to support complex query and analysis operations on continuously updated data streams. The incremental computing engine updates the materialized view instantly according to the changes of data streams, and users can query the materialized view through SQL statements to get real-time data insights.

### Easy integration with multiple external systems
 
The stream-native design of HStreamDB and the powerful stream processing capabilities built-in make it ideally suited as a data hub for the enterprise, responsible for all data access and flow, connecting multiple upstream and downstream services and data systems.

For this reason, HStreamDB also provides Connector components for interfacing with various external systems, such as MySQL, ClickHouse, etc., making it easy to integrate with external data systems.

### Cloud-native architecture, unlimited horizontal scaling
 
HStreamDB is built with a Cloud-Native architecture, where the compute and storage layers are separated and can be horizontally scaled independently.

It also supports online cluster scaling, dynamic expansion and contraction, and is efficient in scaling without data repartitioning, mass copying, etc.

### Fault tolerance and high availability

HStreamDB has built-in automatic node failure detection and error recovery mechanisms to ensure high availability while using an optimized consistency model based on Paxos.

Data is always securely replicated to multiple nodes, ensuring consistency and orderly delivery even in errors and failures.

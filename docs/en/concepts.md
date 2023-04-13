# Concepts

This page explains key concepts in HStream, which we recommend you to understand before you start.

## Record 

In HStream, a record is a unit of data that may contain arbitrary user data and is immutable. Each record is assigned a unique recordID in a stream. Additionally, a partition key is included in every record, represented as a string, and used to determine the stream shard where the record is stored.

## Stream

All records live in streams. A stream is essentially an unbound, append-only dataset. A stream can contain multiple shards and each shard can be located in different nodes. There are some attributes of a stream, such as:
- Replicas: how many replicas the data in the stream has
- Retention period: how long the data in the stream is retained

## Subscription

Clients can obtain the latest data in streams in real time by subscriptions. A subscription can automatically track and save the progress of clients processing records: clients indicate that a record has been successfully received and processed by replying to the subscription with a corresponding ACK, and the subscription will not continue to deliver records to clients that have already been acked. If the subscription has not received ACKs from clients after the specified time, it will redeliver last records.

A subscription is immutable, which means you cannot reset its internal delivery progress. Multiple subscriptions can be created on the same stream, and they are independent of each other.

Multiple clients can join the same subscription, and the system will distribute records accross clients based on different subscription modes. Currently, the default subscription mode is shard-based, which means that records in a shard will be delivered to the same client, and different shards can be assigned to different clients.

## Query

Unlike queries in traditional databases that operate on static datasets, return limited results, and immediately terminate execution, queries in HStream operate on unbound data streams, continuously update results as the source streams changes, and keep running until the user stops it explicitly. This kind of  long-running queries against unbound streams is also known as the streaming query.

By default,  a query will write its computing results to a new stream continuously. Clients can subscribe to the result stream to obtain real-time updates of the computing results.

## Materialized View

Queries are also usually used to create materialized views. Unlike streams that store records in an append-only way, materialized views are more similar to tables in relational databases that hold results in a compacted form, which means that you can query them directly and get the latest results immediately.

Traditional databases also have materialized views, but the results are often stale and they have a significant impact on database performance, so they are generally rarely used. However, in HStream, the results saved in materialized views are automatically updated in real-time with changes in the source streams, which is very useful in many scenarios, such as building real-time dashboards.

## Connector

The connectors are responsible for the streaming data integration between HStream and external systems and can be divided into two categories according to the direction of integration: source connectors and sink connectors.

Source connectors are used for continuously ingesting data from other systems into HStream, and sink connectors are responsible for continuously distributing data from  HStream to other  systems.There are also different types of connectors for different systems，such as PostgreSQL connector, MongoDB connector…

The running of connectors are supervised, managed and scheduled by HStream itself, without relying on any other systems.

# HStream IO Overview

**HStream IO** is a built-in framework of HStreamDB, which provides a real-time
synchronization service between HStreamDB and external systems and enables you
to an easy migration of data from/to external systems.

## Concept

**HStream IO** consists of two components:

- **IO Runtime**: IO Runtime is a part of HStreamDB managing and empowering
  scalability, fault-tolerance and load-balancing for connectors.
- [**Connectors**](https://github.com/hstreamdb/hstream-connectors): Connectors
  are used to synchronize data between HStreamDB and external systems.

**HStream IO** provides two types of connectors: source and sink.

- **Source Connector** - A source connector subscribes to data from other
  systems such as MySQL, and PostgreSQL, making the data available for data
  processing in HStreamDB.
- **Sink Connector** - A sink connector writes data to other systems from
  HStreamDB streams.

For a clear understanding, we would name a running **connector** process to be a task
and the docker image for the connector is a **connector plugin**.

**HStream IO Protocol** is the lightweight protocol between HStream IO Runtime
and connectors. Following the protocol, users can use any language to implement
their own connector plugins that HStream IO has not provided.

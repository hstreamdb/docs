# HStream IO Overview

**HStream IO** is a platform based on HStreamDB to provide a real-time synchronization service between HStreamDB and external systems,
enable you to easily feed data from external systems into HStreamDB or feed data from HStreamDB into external systems.

## Concept

**HStream IO** includes two components:

- **IO Runtime**: IO Runtime is a part of HStreamDB to provide scalability, fault-tolerence, load-balancing for connectors.
- [**Connectors**](https://github.com/hstreamdb/hstream-connectors): Connectors are used to synchronize data between HStreamDB and external systems.

**HStream IO** includes two types of connectors: source and sink.

- **Source Connector** - A source connector subscribes data from other systems like mysql or postgresql, making the data availlable for hstream data processing.
- **Sink Connector** - A sink connector write data to other systems like mysql or postgresql from HStreamDB streams.

For understanding clearly, we call a **connector** as a task and a **connector plugin** as a docker image for the connector.

**HStream IO Protocol** is the lightweight protocol between HStream IO Runtime and connectors,
based on the protocol, you can use any languege implement your connector plugins that we have not implemented yet.

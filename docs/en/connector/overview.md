# HStream Connector Overview

Stream Database should work with other external systems like messaging systems and other database easily.

**HStream Connector** enable you to easily feed data from external systems into **HStreamDB** or feed data from **HStreamDB** into external systems.

## Concept

HStream connectors should include two types of connectors: source and sink.

- **Source Connector** - A source connector subscribes data from other systems like mysql or clickhouse, making the data availlable for hstream data processing.

- **Sink Connector** - A sink connector write data to other systems like mysql or clickhouse. It often subscribes data from **stream** in HStreamDB.

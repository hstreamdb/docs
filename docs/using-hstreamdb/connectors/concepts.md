# Connector Related Concepts

HStreamDB provides connectors to integrate with external systems.

## Connector

A **Connector** works as the data transport center for different systems.
In HStreamDB, the connector is charge of import and export data.
Since data stored in HStreamDB in streams, the connectors focuses only on streaming data.
There are two kinds of connectors in HStreamDB, **Source Connectors** and **Sink Connectors**.

### Source Connector

A source connector, ingests [TODO]. [TODO], stream processing with low latency.

### Sink Connector

A sink connector delivers data from HStream streams into different branches of external system, including SQL, Clickhouse and etc.

Graph explanation:

```
                     +-----------+                 +-----------------+
        data         | HStreamDB |    connector    | external system |
                ->   |           |        ->       |                 |
        (x:y)        |   (x:y)   |                 |     (x:y)       |
                     +-----------+                 +-----------------+
```

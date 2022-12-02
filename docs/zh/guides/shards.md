# Manage Shards of the Stream

## Sharding in HStreamDB

A stream is a logical concept for producer and consumer, and under the hood,
these data passing through are stored in the shards of the stream in an
append-only fashion.

A shard is essentially the primary storage unit which contains all the
corresponding records with some partition keys. Every stream will contain
multiple shards spread across multiple server nodes. Since we believe that
stream on itself is a sufficiently concise and powerful abstraction, the
sharding logic is minimally visible to the user. For example, during writing or
consumption, each stream appears to be managed as an entity as far as the user
is concerned.

However, for the cases where the user needs more fine-grained control and better
flexibility, we offer interfaces to get into the details of shards of the stream
and other interfaces to work with shards like Reader.

## Specify the Number of Shards When Creating a Stream

To decide the number of shards which a stream should have, an attribute
shardCount is provided when creating a
[stream](./stream.md#attributes-of-a-stream).

## List Shards

To list all the shards of one stream.

:::: tabs

::: tab Java

```java
// ListShardsExample.java
```

:::

::: tab Go

```go
// ExampleListShards.go
```

:::

::: tab Python3
@snippet examples/py/snippets/guides.py common list-shards
:::

::::

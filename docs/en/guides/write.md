# Write Records to Streams

This document provides information about how to write data to streams in
HStreamDB using hstreamdb-java or clients implemented in other languages.

You can also read the following pages to get a more thorough understanding:

- How to [create and manage Streams](./stream.md).
- How to [consume the data written to a Stream](./consume.md).

To write data to HStreamDB, we need to pack messages as HStream Records and a
producer that creates and sends messages to servers.

## HStream Record

All data in streams are in the form of an HStream Record. There are two kinds of
HStream Record:

- **HRecord**: You can think of an hrecord as a piece of JSON data, just like
  the document in some NoSQL databases.
- **Raw Record**: Arbitrary binary data.

## End-to-End Compression

To reduce transfer overhead and maximize bandwidth utilization, HStreamDB
supports the compression of written HStream records. Users can set the
compression algorithm when creating a `BufferedProducer`. Currently, HStreamDB
supports both `gzip` and `zstd` compression algorithms. Compressed records are
automatically decompressed by the client when they are consumed from HStreamDB.

## Write HStream Records

There are two ways to write records to servers. For simplicity, you can use
`Producer` from `client.newProducer()` to start with. `Producer`s do not provide
any configure options, it simply sends records to servers as soon as possible,
and all these records are sent in parallel, which means they are unordered. In
practice, `BufferedProducer` from the `client.newBufferedProducer()` would
always be better. `BufferedProducer` will buffer records in order as a batch and
send the batch to servers. When a record is written to the stream, HStream
Server will generate a corresponding record id for the record and send it back
to the client. The record id is unique in the stream.

## Write Records Using a Producer

:::: tabs

::: tab Java

```java
// WriteDataSimpleExample.java
```

:::

::: tab Go

```go
// ExampleWriteProducer.go
```

:::

::: tab Python3
@snippet examples/py/snippets/guides.py common append-records
:::

::::

## Write Records Using a Buffered Producer

In almost all scenarios, we would recommend using `BufferedProducer` whenever
possible because it offers higher throughput and provides a very flexible
configuration that allows you to adjust between throughput and latency as
needed. You can configure the following two settings of BufferedProducer to
control and set the trigger and the buffer size. With `BatchSetting`, you can
determine when to send the batch based on the maximum number of records, byte
size in the batch and the maximum age of the batch. By configuring
`FlowControlSetting`, you can set the buffer for all records. The following code
example shows how you can use `BatchSetting` to set responding triggers to
notify when the producer should flush and `FlowControlSetting` to limit maximum
bytes in a BufferedProducer.

:::: tabs

::: tab Java

```java
// WriteDataBufferedExample.java
```

:::

::: tab Go

```go
// ExampleWriteBatchProducer.go
```

:::

::: tab Python3
@snippet examples/py/snippets/guides.py common buffered-append-records
:::

::::

## Write Records with Partition Keys

Partition keys are optional, and if not given, the server will automatically
assign a default key. Records with the same partition key can be guaranteed to
be written orderly in BufferedProducer.

Another important feature of HStreamDB, sharding, uses these partition keys to
decide which shards the record will be allocated to and improve write/read
performance. See [Manage Shards of a Stream](./shards.md) for a more detailed
explanation.

You can easily write records with keys using the following example:

:::: tabs

::: tab Java

```java
// WriteDataWithKeyExample.java
```

:::

::: tab Go

```go
// ExampleWriteBatchProducerMultiKey.go
```

:::

::::

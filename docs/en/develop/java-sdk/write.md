# Write data

This page shows how to write data into HStreamDB using Java SDK.

## Prerequisites

Make sure you have HStreamDB running and accessible.

## Concepts

You can write two types of data to streams in HStreamDB:

- raw record
- hstream record(HRecord)

### Raw Record

Raw record represents arbitray binary data. You can save binary data to a
stream, but **please note that currently stream processing via sql ignores
binary data, it now only processes `HRecord` type data.** Of course, you can
always get the binary data from the stream and process it yourself.

### HRecord

You can think of an `HRecord` as a piece of JSON data， just like a document in
some nosql databases. You can process hrecords directly in real time via sql
statements.

## Producer

Before you can write data, you first need to create a `Producer` object using
the `HStreamClient.newProducer()` method:

```java

Producer producer = client.newProducer().stream("test_stream").build();

```

A producer has some options, for now, let's just ignore them and use the default
settings.

## Write Data

You can write binary data using the `Producer.write()` method:

```java

Random random = new Random();
byte[] rawRecord = new byte[100];
random.nextBytes(rawRecord);
CompletableFuture<RecordId> future = producer.write(rawRecord);

```

Similarly, you can use the same method to write hrecords:

```java

HRecord hRecord = HRecord.newBuilder()
        .put("key1", 10)
        .put("key2", "hello")
        .put("key3", true)
        .build();

CompletableFuture<RecordId> future = producer.write(hRecord);

```

## Buffered Writes (Preferred)

When writing to HStreamDB, sending many small records limits throughput. To
achieve higher thoughput, you can enable batch mode of `Producer`.

```java

BufferedProducer producer = client.newBufferedProducer()
        .stream("test_stream")
        // optional, default: 100, the value must be greater than 0
        .recordCountLimit(100)
        // optional, default: 100(ms), disabled if the value <= 0
        .flushIntervalMs(100)
        // optional, default: 4096(Bytes), disabled if the value <= 0
        .maxBytesSize(4096)
        .build();

Random random = new Random();
for(int i = 0; i < 1000; ++i) {
    byte[] rawRecord = new byte[100];
    random.nextBytes(rawRecord);
    CompletableFuture<RecordId> future = producer.write(rawRecord);
}
producer.close();
```

::: warning Please do not write both binary data and hrecord in one stream! :::

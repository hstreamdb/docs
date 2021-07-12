# Write data

This page shows how to write data into HStreamDB using Java SDK.

## Prerequisites 

Make sure you have HStreamDB running and accessible.

## Concepts

You can write two types of data to streams in HStreamDB:

- raw record
- hstream record(HRecord)

### Raw Record

Raw record represents arbitray binary data. 
You can save binary data to a stream, 
but **please note that currently stream processing via sql ignores binary data, 
it now only processes ``HRecord`` type data.**
Of course, you can always get the binary data from the stream and process it yourself.

### HRecord

You can think of an ``HRecord`` as a piece of JSON data，
just like a document in some nosql databases.
You can process hrecords directly in real time via sql statements.

## Producer

Before you can write data, you first need to create a ``Producer`` object
using the ``HStreamClient.newProducer()`` method:

```java

Producer producer = client.newProducer().stream("test_stream").build();

```

A producer has some options, for now, 
let's just ignore them and use the default settings.

## Write Binary Data 

### Write Binary Data Synchronously 

You can write binary data synchronously using the ``Producer.write()``
method:

```java

Random random = new Random();
byte[] rawRecord = new byte[100];
random.nextBytes(rawRecord);
RecordId recordId = producer.write(rawRecord);

```

### Write Binary Data Asynchronously 

You can write binary data asynchronously using the ``Producer.writeAsync()``
method:

```java

Random random = new Random();
byte[] rawRecord = new byte[100];
random.nextBytes(rawRecord);
CompletableFuture<RecordId> future = producer.writeAsync(rawRecord);

```

## Write HRecord 

### Write HRecord Synchronously 

You can write hrecords synchronously using the ``Producer.write()``
method:

```java

HRecord hRecord = HRecord.newBuilder()
        .put("key1", 10)
        .put("key2", "hello")
        .put("key3", true)
        .build();

RecordId recordId = producer.write(hRecord);

```

### Write HRecord Asynchronously 

You can write hrecords asynchronously using the ``Producer.writeAsync()``
method:

```java

HRecord hRecord = HRecord.newBuilder()
        .put("key1", 10)
        .put("key2", "hello")
        .put("key3", true)
        .build();

CompletableFuture<RecordId> future = producer.write(hRecord);

```

## Buffered Writes for higher throughput (Preferred)

TODO

## Warnings 

- ** Please do not write both binary data and hrecord in one stream.**



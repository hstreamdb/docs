# 写入 data

如何通过 Java SDK 向 HStreamDB 写入数据。

## 前提条件

确保有一个运行中并可用的 HStreamDB

## 概念

你可以往 stream 写入以下两种类型的数据：

- raw record
- hstream record(HRecord)

### Raw Record

Raw record 代表任意二进制数据。当前确实支持二进制数据的写入，但是**注意现在实现
的通过 sql 的数据处理会直接忽略二进制数据，支持处理 `HRecord`。**当然，你可以选择
从 stream 中读出二进制数据然后手动处理。

### HRecord

你可以把 `HRecord` 看作是 JSON data，就像是一些 nosql 数据库的 document in 。
Query 可以通过 sql statement 直接实时的处理 hrecords。

## 生产者（Producer）

在往 stream 中写入数据之前，你需要先通过 `HStreamClient.newProducer()`
创建一个 `Producer` 对象。

```java

Producer producer = client.newProducer().stream("test_stream").build();

```

创建 Producer 时有多个配置项，在之后会详细介绍，当前，我们将使用默认配置：

## 写入数据

通过 `Producer.write()` 可以写入二进制数据：

```java

Random random = new Random();
byte[] rawRecord = new byte[100];
random.nextBytes(rawRecord);
CompletableFuture<RecordId> future = producer.write(rawRecord);

```

类似的，你也可以写入 hrecords:

```java

HRecord hRecord = HRecord.newBuilder()
        .put("key1", 10)
        .put("key2", "hello")
        .put("key3", true)
        .build();

CompletableFuture<RecordId> future = producer.write(hRecord);

```

## Buffered Writes (推荐)

当写入数据是，发送很多很小的 records 会限制吞吐。为了更大的吞吐，
你可以开启 `Producer` 的 batch mode 。

```java

Producer producer = client.newProducer()
        .stream("test_stream")
        .enableBatch()
        .recordCountLimit(100)
        .build();

Random random = new Random();
for(int i = 0; i < 1000; ++i) {
    byte[] rawRecord = new byte[100];
    random.nextBytes(rawRecord);
    CompletableFuture<RecordId> future = producer.write(rawRecord);
}

```

::: warning
请不要往同一个 stream 里同时写入二进制数据 和 hrecord ！
:::

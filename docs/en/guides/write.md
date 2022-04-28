# Writing records to streams

This document provides information about how to write data to streams in HStreamDB using [hstreamdb-java](). 

To write data to HStreamDB, we need to pack messages as HStream Records and a producer that creates and sends messages to servers.

## HStream Record 

All data in streams are in the form of an HStream Record. There are two kinds of HStream Record:

HRecord: You can think of an hrecord as a piece of JSON data, just like the document in some NoSQL databases.  HRecords can be directly processed in real-time via SQL statements.

Raw Record: Arbitrary binary data. You can write binary data to a stream, but please note that stream processing via SQL currently ignores binary data. It now only processes HRecord type data. Of course, you can always get the binary data from the stream and process it yourself.

## Write HStream Records

There are two ways to write records to servers. For simplicity, you can use ``Producer`` from ``client.newProducer()`` to start with. ``Producer`` does not provice any configure options, it simply sends records to servers as soon as possible and all these records are sent in parallel, which means they are unordered. In practice, ``BufferedProducer`` from ``client.newBufferedProducer()`` would always be the better choice. ``BufferedProducer`` will buffer records in order as a batch and send the batch to servers. When a record is written to the stream, HStream Server will generate a corresponding record id for the record and send it back to the client. The record id is unique in the stream. 

### Using Producer

```java
import io.hstream.*;
import io.hstream.Record;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WriteDataSimpleExample {
  public static void main(String[] args) throws Exception {
    // TODO (developers): Replace these variables for your own use cases.
    String serviceUrl = "127.0.0.1:6570";
    if (System.getenv("serviceUrl") != null) {
      serviceUrl = System.getenv("serviceUrl");
    }

    // We do not recommend write both raw data and HRecord data into the same stream.
    String streamName1 = "stream_h_records";
    String streamName2 = "stream_raw_records";

    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();

    writeHRecordData(client, streamName1);
    writeRawData(client, streamName2);
    client.close();
  }

  public static void writeHRecordData(HStreamClient client, String streamName) {
    // Create a basic producer for low latency scenarios
    // For high throughput scenarios, please see the next section "Using `BufferedProducer`s"
    Producer producer = client.newProducer().stream(streamName).build();

    HRecord hRecord =
        HRecord.newBuilder()
            .put("id", 10)                                                    // number
            .put("isReady", true)                                             // Boolean
            .put("targets", HArray.newBuilder().add(1).add(2).add(3).build()) // List
            .put("name", "h".repeat(100))                                     // String
            .build();

    for (int i = 0; i <= 3000; i++) {
      Record record = Record.newBuilder().hRecord(hRecord).build();
      // If the data is written successfully, returns a server-assigned record id
      CompletableFuture<String> recordId = producer.write(record);
      System.out.println("Wrote message ID: " + recordId.join());
    }
  }

  private static void writeRawData(HStreamClient client, String streamName) {
    Producer producer = client.newProducer().stream(streamName).build();
    List<String> messages = Arrays.asList("first", "second");
    for (final String message : messages) {
      Record record =
          Record.newBuilder().rawRecord(message.getBytes(StandardCharsets.UTF_8)).build();
      CompletableFuture<String> recordId = producer.write(record);
      System.out.println("Published message ID: " + recordId.join());
    }
  }
}
```

### Using Buffered Producer

In almost all scenarios, we would recommend using BufferedProducer when possible, not only because it offers higher throughput, but also it provides a very flexible configuration that allows you to adjust between throughput and latency as needed.  You can configure the following two settings of BufferedProducer to control and set the trigger and the buffer size. With BatchSetting, you can determine when to send the batch based on the maximum number of records,  byte size in the batch and the maximum age of the batch. By configuring FlowControlSetting, you can set the buffer for all records. The following code example shows how you can use BatchSetting to set responding triggers to notify when the producer should flush and FlowControlSetting to limit maximum bytes in a BufferedProducer.

```java
import io.hstream.*;
import io.hstream.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class WriteDataBufferedExample {
  public static void main(String[] args) throws Exception {
    // TODO (developers): Replace these variables for your own use cases.
    String serviceUrl = "127.0.0.1:6570";
    if (System.getenv("serviceUrl") != null) {
      serviceUrl = System.getenv("serviceUrl");
    }

    String streamName = "stream_h_records";
    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    writeHRecordDataWithBufferedProducers(client, streamName);
    client.close();
  }

  public static void writeHRecordDataWithBufferedProducers(
      HStreamClient client, String streamName) {
    BatchSetting batchSetting =
        BatchSetting.newBuilder()
            // optional, default: 100, the max records count of a batch,
            // disable the trigger if the value <= 0.
            .recordCountLimit(100)

            // optional, default: 4096(4KB), the max bytes size of a batch,
            // disable the trigger if the value <= 0.
            .bytesLimit(4096)

            // optional, default: 100(ms), the max age of a buffering batch,
            // disable the trigger if the value <= 0.
            .ageLimit(100)
            .build();

    // FlowControlSetting is to control total records,
    // including buffered batch records and sending records.
    FlowControlSetting flowControlSetting =
        FlowControlSetting.newBuilder()
            // Optional, the default: 104857600(100MB), total bytes limit,
            // including buffered batch records and sending records,
            // the value must be greater than batchSetting.bytesLimit
            .bytesLimit(40960)
            .build();
    BufferedProducer producer =
        client.newBufferedProducer().stream(streamName).batchSetting(batchSetting).build();

    List<CompletableFuture<String>> recordIds = new ArrayList<>();
    Random random = new Random();

    for (int i = 0; i < 100; i++) {
      double temp = random.nextInt(100) / 10.0 + 15;
      HRecord hRecord = HRecord.newBuilder().put("temperature", temp).build();
      Record record = Record.newBuilder().hRecord(hRecord).build();
      CompletableFuture<String> recordId = producer.write(record);
      recordIds.add(recordId);
    }

    // close a producer, it will call flush() first
    producer.close();
    System.out.println("Wrote message IDs: " + recordIds.stream().map(CompletableFuture::join));
  }
}
```

### Using Ordering Key

Ordering keys are optional, and if not given, the server will automatically assign a default key, see transparent sharding[doc link]() for a more detailed explanation.

Records with the same ordering key can be guaranteed to be written orderly in BufferedProducer.  Another important feature of HStreamDB, transparent sharding, uses these ordering keys to decide which shards the record will be allocated and improve write/read performance. 

You can easily write records with keys using the following example:

```java
import io.hstream.*;
import io.hstream.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class WriteDataWithKeyExample {
  public static void main(String[] args) throws Exception {
    // TODO (developers): Replace these variables for your own use cases.
    String serviceUrl = "127.0.0.1:6570";
    if (System.getenv("serviceUrl") != null) {
      serviceUrl = System.getenv("serviceUrl");
    }

    String streamName = "stream_h_records";

    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    writeHRecordDataWithKey(client, streamName);
    client.close();
  }

  public static void writeHRecordDataWithKey(HStreamClient client, String streamName) {
    // For demonstrations, we would use the following as our ordering keys for the records.
    // As the documentations mentioned, if we do not give any ordering key, it will get a default
    // key and be mapped to some default shard.
    String key1 = "South";
    String key2 = "North";

    // Create a buffered producer with default BatchSetting and FlowControlSetting.
    BufferedProducer producer = client.newBufferedProducer().stream(streamName).build();

    List<CompletableFuture<String>> recordIds = new ArrayList<>();
    Random random = new Random();

    for (int i = 0; i < 100; i++) {
      double temp = random.nextInt(100) / 10.0 + 15;
      Record record;
      if ((i % 3) == 0) {
        HRecord hRecord = HRecord.newBuilder().put("temperature", temp).put("withKey", 1).build();
        record = Record.newBuilder().hRecord(hRecord).orderingKey(key1).build();
      } else {
        HRecord hRecord = HRecord.newBuilder().put("temperature", temp).put("withKey", 2).build();
        record = Record.newBuilder().hRecord(hRecord).orderingKey(key2).build();
      }

      CompletableFuture<String> recordId = producer.write(record);
      recordIds.add(recordId);
    }
    producer.close();
    System.out.println("Wrote message IDs: " + recordIds.stream().map(CompletableFuture::join));
  }
}
```

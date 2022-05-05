# 向 HStreamDB 中的 Stream 写入 Records

本文档提供了关于如何通过 hstreamdb-java 向 HStreamDB 中的 Stream 写入数据的相关
教程。

同时还可参考其他的相关教程：

- 如何[创建和管理 Stream](./stream.md).
- 如何[通过 Subscription 消费写入 Stream 中的 Records](./read.md).

为了向 HStreamDB 写数据，我们需要将消息打包成 HStream Record，以及一个创建和发送
消息到服务器的 Producer。

## HStream Record

Stream 中的所有数据都是以 HStream Record 的形式存在，HStreamDB 支持以下两种
HStream Record：

- **HRecord**: 可以看作是一段 JSON 数据，就像一些 NoSQL 数据库中的 document。

- **Raw Record**: 二进制数据。

## 写入 HStream Record

有两种方法可以把 records 写入 HStreamDB。从简单易用的角度，你可以从
`client.newProducer()` 的`Producer` 入手。这个 `Producer` 没有提供任何配置项，它
只会即刻将收到的每个 record 并行发送到 HServer，这意味着它并不能保证这些 records
的顺序。在生产环境中， `client.newBufferedProducer()` 中的 `BufferedProducer` 将
是更好的选择，`BufferedProducer` 将按顺序缓存打包 records 成一个 batch，并将该
batch 发送到服务器。每一条 record 被写入 stream 时，HServer 将为该 record 生成一
个相应的 record ID，并将其发回给客户端。这个 record ID 在 stream 中是唯一的。

## 使用 Producer

```Java
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

## 使用 BufferedProducer

在几乎所有情况下，我们更推荐使用 `BufferedProducer`。不仅因为它能提供更大的吞吐
量，它还提供了更加灵活的配置去调整，用户可以根据需求去在吞吐量和时延之间做出调整
。你可以配置 `BufferedProducer` 的以下两个设置来控制和设置触发器和缓存区大小。通
过 `BatchSetting`，你可以根据 batch 的最大 record 数、batch 的总字节数和 batch
存在的最大时限来决定何时发送。通过配置 `FlowControlSetting`，你可以为所有的缓存
的 records 设置缓存大小和策略。下面的代码示例展示了如何使用 BatchSetting 来设置
响应的 trigger，以通知 producers 何时应该刷新，以及 `FlowControlSetting` 来限制
`BufferedProducer` 中的 buffer 的最大字节数。

```Java
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
        client.newBufferedProducer().stream(streamName)
            .batchSetting(batchSetting)
            .flowControlSetting(flowControlSetting)
            .build();

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

## 使用排序键（Ordering Key）

是否附加一个排序键是可选的，如果没有给出，HServer 会自动分配一个默认的 key。

具有相同排序键的 records 可以在 BufferedProducer 中被保证能有序地写入。HStreamDB
的另一个重要功能，透明分区，也使用这些排序键来决定 records 将被分配到哪个分区，
以此提高写/读性能。更详细的解释请看[透明分区](../concepts/transparent-sharding.md)。

参考下面的例子，你可以很容易地写入带有排序键的 records。

```Java
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

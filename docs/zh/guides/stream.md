# 创建和管理 Stream

## Stream 的属性

- Replication factor

  为了容错性和更高的可用性，每个 Stream 都可以在集群中的节点之间进行复制。一个常
  用的生产环境 Replication factor 配置是为 3，也就是说，你的数据总是有三个副本，
  这在出错或你想对 Server 进行维护时将会很有帮助。这种复制是以 Stream 为单位上进
  行的。

- Backlog Retention

  该配置控制 HStreamDB 的 Stream 中的 records 被写入后保留的时间。当超过
  retention 保留的时间后，HStreamDB 将会清理这些 records，不管它是否被消费过。

  + 默认值=7 天
  + 最小值=1 秒
  + 最大值=21 天

## 创建一个 stream

在你写入 records 或者 创建一个订阅之前先创建一个 stream。

```Java
import io.hstream.HStreamClient;

public class CreateStreamExample {
  public static void main(String[] args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String serviceUrl = "your_service_url_address";
    String streamName = "your_stream_name";

    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    createStreamExample(client, streamName);
  }

  public static void createStreamExample(HStreamClient client, String streamName) {
    client.createStream(streamName);
  }
}
```

## 删除一个 Stream

只有当一个 Stream 没有所属的订阅时才允许被删除，除非传一个强制标删除的 flag 。

## 强制删除一个 Stream

如果你需要删除一个有订阅的 stream 时，请启用强制删除。在强制删除一个 stream 后，
原来 stream 的订阅仍然可以从 backlog 中读取数据。这些订阅的 stream 名字会变成
`__deleted_stream__`。同时，我们并不允许在被删除的 stream 上创建新的订阅，也不允
许向该 stream 写入新的 record。

```Java
import io.hstream.HStreamClient;

public class DeleteStreamExample {
  public static void main(String[] args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String serviceUrl = "your_service_url_address";
    String streamName = "your_stream_name";

    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    deleteStreamExample(client, streamName);
    deleteStreamForceExample(client, streamName);
  }

  public static void deleteStreamExample(HStreamClient client, String streamName) {
    client.deleteStream(streamName);
  }

  public static void deleteStreamForceExample(HStreamClient client, String streamName) {
    client.deleteStream(streamName, true);
  }
}
```

## 列出所有 stream 信息

可以如下拿到所有 HStream 中的 stream:

```Java
import io.hstream.HStreamClient;
import io.hstream.Stream;
import java.util.List;

public class ListStreamsExample {
  public static void main(String[] args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String serviceUrl = "your-service-url-address";

    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    listStreamExample(client);
  }

  public static void listStreamExample(HStreamClient client) {
    List<Stream> streams = client.listStreams();
    for (Stream stream : streams) {
      System.out.println(stream.getStreamName());
    }
  }
}
```

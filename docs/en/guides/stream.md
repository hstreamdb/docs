# Create and manage streams

## Attributes of a Stream

- Replication factor

  For fault tolerance and higher availability, every stream can be replicated
  across nodes in the cluster. A typical production setting is a replication
  factor of 3, i.e., there will always be three copies of your data, which is
  helpful just in case things go wrong or you want to do maintenance on the
  brokers. This replication is performed at the level of the stream.

- Backlog retention

  The configuration controls how long streams of HStreamDB retain records after
  being appended. HStreamDB will discard the message regardless of whether it is
  consumed when it exceeds the backlog retention duration.

  + Default = 7 days

  + Minimum value = 1 seconds

  + Maximum value = 21 days

- Maximum record size

  The largest size of a record batch allowed by HStreamDB, the default value is
  1MB.

## Create a stream

Create a stream before you write records or create a subscription.

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

## Delete a Stream

Deletion is only allowed when a stream has no subsequent subscriptions unless
the force flag is set.

### Delete a stream with the force flag

If you need to delete a stream with subscriptions, enable force deletion.
Existing stream subscriptions can still read from the backlog after deleting a
stream with the force flag enabled. However, these subscriptions will have
stream name **deleted_stream**, no new subscription creation on the deleted
stream would be allowed, nor new records would be allowed to be written to the
stream.

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

## List streams

To get all streams in HStream:

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

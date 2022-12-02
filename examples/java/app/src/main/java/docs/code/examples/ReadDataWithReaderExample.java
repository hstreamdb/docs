package docs.code.examples;

import static io.hstream.StreamShardOffset.SpecialOffset.EARLIEST;

import io.hstream.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReadDataWithReaderExample {
  public static void main(String[] args) throws Exception {
    // TODO (developers): Replace these variables for your own use cases.
    String serviceUrl = "127.0.0.1:6570";
    if (System.getenv("serviceUrl") != null) {
      serviceUrl = System.getenv("serviceUrl");
    }
    String streamName = "your_h_records_stream_name";
    // Please change the value of shardId to the ones you can get from listShards
    long shardId = 0;
    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    readTheFirstRecordInShard(client, streamName, shardId);
    client.close();
  }

  public static void readTheFirstRecordInShard(
      HStreamClient client, String streamName, long shardId) {
    StreamShardOffset offset = new StreamShardOffset(EARLIEST);
    Reader reader =
        client
            .newReader()
            .readerId("your_reader_id")
            .streamName(streamName)
            .shardId(shardId)
            .shardOffset(offset) // default: EARLIEST
            .timeoutMs(1000) // default: 0
            .build();
    CompletableFuture<List<ReceivedRecord>> records =
        reader.read(10); // Specify the maximum available records a reader will get for one read
    System.out.println("Read records: " + records.join());
  }
}

package docs.code.examples;

import io.hstream.HStreamClient;
import io.hstream.Shard;
import java.util.List;

public class ListShardsExample {
  public static void main(String[] args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String serviceUrl = "127.0.0.1:6570";
    if (System.getenv("serviceUrl") != null) {
      serviceUrl = System.getenv("serviceUrl");
    }
    String streamName = "your_h_records_stream_name";

    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    listShardsExample(client, streamName);
    client.close();
  }

  public static void listShardsExample(HStreamClient client, String streamName) {
    List<Shard> shards = client.listShards(streamName);
    for (Shard shard : shards) {
      System.out.println(shard.getStreamName());
    }
  }
}

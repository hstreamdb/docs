package docs.code.examples;

import io.hstream.HStreamClient;

public class CreateStreamExample {
  public static void main(String[] args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String serviceUrl = "127.0.0.1:6570";
    if (System.getenv("serviceUrl") != null) {
      serviceUrl = System.getenv("serviceUrl");
    }

    String streamName1 = "your_h_records_stream_name";
    String streamName2 = "your_raw_records_stream_name";

    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    createStreamExample(client, streamName1);
    createStreamWithAttrsExample(client, streamName2);
    client.close();
  }

  public static void createStreamExample(HStreamClient client, String streamName) {
    client.createStream(streamName);
  }

  public static void createStreamWithAttrsExample(HStreamClient client, String streamName) {
    client.createStream(
        streamName,
        (short) 1 // replication factor
        ,
        10 // Number of shards
        ,
        7 * 24 * 3600 // backlog retention time in seconds
        );
  }
}

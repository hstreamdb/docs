package docs.code.examples;

import io.hstream.HStreamClient;

public class DeleteStreamExample {
  public static void main(String[] args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    // String serviceUrl = "your-service-url-address";
    String serviceUrl = "hstream://127.0.0.1:6570";
    if (System.getenv("serviceUrl") != null) {
      serviceUrl = System.getenv("serviceUrl");
    }

    String streamName1 = "your_h_records_stream_name";
    String streamName2 = "your_raw_records_stream_name";

    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    deleteStreamExample(client, streamName1);
    deleteStreamForceExample(client, streamName2);
    client.close();
  }

  public static void deleteStreamExample(HStreamClient client, String streamName) {
    client.deleteStream(streamName);
  }

  public static void deleteStreamForceExample(HStreamClient client, String streamName) {
    client.deleteStream(streamName, true);
  }
}

package docs.code.examples;

import io.hstream.HStreamClient;
import io.hstream.Stream;
import java.util.List;

public class ListStreamsExample {
  public static void main(String[] args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String serviceUrl = "127.0.0.1:6570";
    if (System.getenv("serviceUrl") != null) {
      serviceUrl = System.getenv("serviceUrl");
    }

    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    listStreamExample(client);
    client.close();
  }

  public static void listStreamExample(HStreamClient client) {
    List<Stream> streams = client.listStreams();
    for (Stream stream : streams) {
      System.out.println(stream.getStreamName());
    }
  }
}

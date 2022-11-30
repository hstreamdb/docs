package docs.code.examples;

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

    String streamName = "your_h_records_stream_name";

    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    writeHRecordDataWithKey(client, streamName);
    client.close();
  }

  public static void writeHRecordDataWithKey(HStreamClient client, String streamName) {
    // For demonstrations, we would use the following as our partition keys for the records.
    // As the documentations mentioned, if we do not give any partition key, it will get a default
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
        record = Record.newBuilder().hRecord(hRecord).partitionKey(key1).build();
      } else {
        HRecord hRecord = HRecord.newBuilder().put("temperature", temp).put("withKey", 2).build();
        record = Record.newBuilder().hRecord(hRecord).partitionKey(key2).build();
      }

      CompletableFuture<String> recordId = producer.write(record);
      recordIds.add(recordId);
    }
    System.out.println("Wrote message IDs: " + recordIds.stream().map(CompletableFuture::join));
    producer.close();
  }
}

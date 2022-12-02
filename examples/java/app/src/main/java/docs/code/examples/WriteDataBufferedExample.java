package docs.code.examples;

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

    String streamName = "your_h_records_stream_name";
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
    // including buffered batch records and sending records
    FlowControlSetting flowControlSetting =
        FlowControlSetting.newBuilder()
            // Optional, the default: 104857600(100MB), total bytes limit, including buffered batch
            // records and
            // sending records, the value must be greater than batchSetting.bytesLimit
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

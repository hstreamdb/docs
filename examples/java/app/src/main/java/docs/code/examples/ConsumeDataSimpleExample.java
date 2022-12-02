package docs.code.examples;

import static java.util.concurrent.TimeUnit.SECONDS;

import io.hstream.Consumer;
import io.hstream.HRecordReceiver;
import io.hstream.HStreamClient;
import java.util.concurrent.TimeoutException;

public class ConsumeDataSimpleExample {
  public static void main(String[] args) throws Exception {
    String serviceUrl = "127.0.0.1:6570";
    if (System.getenv("serviceUrl") != null) {
      serviceUrl = System.getenv("serviceUrl");
    }

    String subscriptionId = "your_subscription_id";
    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    consumeDataFromSubscriptionExample(client, subscriptionId);
    client.close();
  }

  public static void consumeDataFromSubscriptionExample(
      HStreamClient client, String subscriptionId) {
    HRecordReceiver receiver =
        ((hRecord, responder) -> {
          System.out.println("Received a record :" + hRecord.getHRecord());
          responder.ack();
        });
    // Consumer is a Service(ref:
    // https://guava.dev/releases/19.0/api/docs/com/google/common/util/concurrent/Service.html)
    Consumer consumer =
        client
            .newConsumer()
            .subscription(subscriptionId)
            // optional, if it is not set, client will generate a unique id.
            .name("consumer_1")
            .hRecordReceiver(receiver)
            .build();
    // start Consumer as a background service and return
    consumer.startAsync().awaitRunning();
    try {
      // sleep 5s for consuming records
      consumer.awaitTerminated(5, SECONDS);
    } catch (TimeoutException e) {
      // stop consumer
      consumer.stopAsync().awaitTerminated();
    }
  }
}

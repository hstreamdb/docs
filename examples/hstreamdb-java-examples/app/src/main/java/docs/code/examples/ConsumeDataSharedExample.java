package docs.code.examples;

import static java.util.concurrent.TimeUnit.SECONDS;

import io.hstream.Consumer;
import io.hstream.HRecordReceiver;
import io.hstream.HStreamClient;
import java.util.concurrent.TimeoutException;

public class ConsumeDataSharedExample {
  public static void main(String[] args) throws Exception {
    String serviceUrl = "127.0.0.1:6570";
    if (System.getenv("serviceUrl") != null) {
      serviceUrl = System.getenv("serviceUrl");
    }

    String subscription = "your_subscription_id";
    String consumer1 = "your_consumer1_name";
    String consumer2 = "your_consumer2-name";
    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();

    // create two consumers to consume records with several partition keys.
    Thread t1 =
        new Thread(() -> consumeDataFromSubscriptionSharedExample(client, subscription, consumer1));
    Thread t2 =
        new Thread(() -> consumeDataFromSubscriptionSharedExample(client, subscription, consumer2));
    t1.start();
    t2.start();
    t1.join();
    t2.join();
    client.close();
  }

  public static void consumeDataFromSubscriptionSharedExample(
      HStreamClient client, String subscription, String consumerName) {
    HRecordReceiver receiver =
        ((hRecord, responder) -> {
          System.out.println("Received a record :" + hRecord.getHRecord());
          responder.ack();
        });
    Consumer consumer =
        client
            .newConsumer()
            .subscription(subscription)
            .name(consumerName)
            .hRecordReceiver(receiver)
            .build();
    try {
      // sleep 5s for consuming records
      consumer.startAsync().awaitRunning();
      consumer.awaitTerminated(5, SECONDS);
    } catch (TimeoutException e) {
      // stop consumer
      consumer.stopAsync().awaitTerminated();
    }
  }
}

package docs.code.examples;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.google.common.util.concurrent.Service;
import io.hstream.HRecordReceiver;
import io.hstream.HStreamClient;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

public class ConsumeDataWithErrorListenerExample {
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

  public static void consumeDataFromSubscriptionExample(HStreamClient client, String subscription) {
    HRecordReceiver receiver =
        ((hRecord, responder) -> {
          System.out.println("Received a record :" + hRecord.getHRecord());
          responder.ack();
        });
    var consumer =
        client
            .newConsumer()
            .subscription(subscription)
            .name("consumer_1")
            .hRecordReceiver(receiver)
            .build();
    // add Listener for handling failed consumer
    var threadPool = new ScheduledThreadPoolExecutor(1);
    consumer.addListener(
        new Service.Listener() {
          public void failed(Service.State from, Throwable failure) {
            System.out.println("consumer failed, with error: " + failure.getMessage());
          }
        },
        threadPool);
    try {
      consumer.startAsync().awaitRunning();
      consumer.awaitTerminated(5, SECONDS);
    } catch (TimeoutException e) {
      consumer.stopAsync().awaitTerminated();
      threadPool.shutdown();
    }
  }
}

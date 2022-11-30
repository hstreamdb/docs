package docs.code.examples;

import io.hstream.HStreamClient;
import io.hstream.Subscription;

public class CreateSubscriptionExample {
  public static void main(String[] args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String serviceUrl = "127.0.0.1:6570";
    if (System.getenv("serviceUrl") != null) {
      serviceUrl = System.getenv("serviceUrl");
    }
    String streamName = "your_h_records_stream_name";
    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    createSubscriptionExample(client, streamName);
    client.close();
  }

  public static void createSubscriptionExample(HStreamClient client, String streamName) {
    // TODO(developer): Specify the options while creating the subscription
    String subscriptionId = "your_subscription_id";
    Subscription subscription =
        Subscription.newBuilder().subscription(subscriptionId).stream(streamName)
            .ackTimeoutSeconds(600) // The default setting is 600 seconds
            .maxUnackedRecords(10000) // The default setting is 10000 records
            .build();
    client.createSubscription(subscription);
  }
}

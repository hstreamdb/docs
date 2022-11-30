package docs.code.examples;

import io.hstream.HStreamClient;

public class DeleteSubscriptionExample {
  public static void main(String[] args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    // String serviceUrl = "your-service-url-address";
    String serviceUrl = "127.0.0.1:6570";
    if (System.getenv("serviceUrl") != null) {
      serviceUrl = System.getenv("serviceUrl");
    }

    String subscriptionId = "your_subscription_id";
    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    deleteSubscriptionExample(client, subscriptionId);
    client.close();
  }

  public static void deleteSubscriptionExample(HStreamClient client, String subscriptionId) {
    client.deleteSubscription(subscriptionId);
  }

  public static void deleteSubscriptionForceExample(HStreamClient client, String subscriptionId) {
    client.deleteSubscription(subscriptionId, true);
  }
}

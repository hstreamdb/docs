package docs.code.examples;

import io.hstream.HStreamClient;
import io.hstream.Subscription;
import java.util.List;

public class ListSubscriptionsExample {

  public static void main(String[] args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String serviceUrl = "127.0.0.1:6570";
    if (System.getenv("serviceUrl") != null) {
      serviceUrl = System.getenv("serviceUrl");
    }

    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    listSubscriptionExample(client);
    client.close();
  }

  public static void listSubscriptionExample(HStreamClient client) {
    List<Subscription> subscriptions = client.listSubscriptions();
    for (Subscription subscription : subscriptions) {
      System.out.println(subscription.getSubscriptionId());
    }
  }
}

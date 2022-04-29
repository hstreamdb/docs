# Create and Manage Subscriptions

## Attributes of a Subscription

- ackTimeoutSeconds.

  Specifies the max amount of time for the server to mark the record as
  unacknowledged, after which the record will be sent again.

- maxUnackedRecords.

  The maximum amount of unacknowledged records allowed. After exceeding the size
  set, the server will stop sending records to corresponding consumers.

## Create a subscription

Every subscription has to specify which stream to subscribe to, which means you
have to make sure the stream to be subscribed has already been created.

When creating a subscription, you can provide the attributes mentioned like
this:

```Java
import io.hstream.HStreamClient;
import io.hstream.Subscription;

public class CreateSubscriptionExample {
  public static void main(String[] args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String serviceUrl = "127.0.0.1:6570";
    if (System.getenv("serviceUrl") != null) {
      serviceUrl = System.getenv("serviceUrl");
    }
    String streamName = "your-stream-name";

    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    createSubscriptionExample(client, streamName);
    client.close();
  }

  public static void createSubscriptionExample(HStreamClient client, String streamName) {
    // TODO: Specify the options while creating the subscription
    String subscriptionId = "your-subscription-id";
    Subscription subscription =
        Subscription.newBuilder().subscription(subscriptionId).stream(streamName)
            .ackTimeoutSeconds(600)   // The default setting is 600 seconds
            .maxUnackedRecords(10000) // The default setting is 10000 records
            .build();
    client.createSubscription(subscription);
  }
}
```

## Delete a subscription

To delete a subscription without the force flag, you need to make sure that
there is no active subscription consumer.

### Delete a subscription with the force flag

If you do want to delete a subscription with running consumers, enable force
deletion. While force deleting a subscription, the subscription will be in
deleting state and closing running consumers, which means you will not be able
to join, delete or create a subscription with the same name. After the deletion
completes, you can create a subscription with the same name. However, this new
subscription will be a brand new subscription. Even if they subscribe to the
same stream, this new subscription will not share the consumption progress with
the deleted subscription.

```Java
import io.hstream.HStreamClient;

public class DeleteSubscriptionExample {
  public static void main(String[] args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String serviceUrl = "127.0.0.1:6570";
    if (System.getenv("serviceUrl") != null) {
      serviceUrl = System.getenv("serviceUrl");
    }
    String subscriptionId = "your-subscription-id";
    HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
    deleteSubscriptionForceExample(client, subscriptionId);
    client.close();
  }

  public static void deleteSubscriptionForceExample(HStreamClient client, String streamName) {
    client.deleteStream(streamName, true);
  }
}
```

## List subscriptions

To list all subscriptions in HStream

```Java
import io.hstream.HStreamClient;
import io.hstream.Subscription;
import java.util.List;

public class ListSubscriptionExample {

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
```

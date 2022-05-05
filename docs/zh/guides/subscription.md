# 创建和管理 Subscription

Subscription 的属性

- ackTimeoutSeconds

  指定 HServer 将 records 标记为 unacked 的最大等待时间，之后该记录将被再次发送。

- maxUnackedRecords。

  允许的未 acked record 的最大数量。超过设定的大小后，服务器将停止向相应的消费者
  发送 records。

## 创建一个 Subscription

每个 subscription 都必须指定要订阅哪个 stream，这意味着你必须确保要订阅的 stream
已经被创建。

当创建一个 subscription 时，你可以像这样提供提到的属性：

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

## 删除一个订阅

要删除一个的订阅，你需要确保没有活跃的订阅消费者，除非启用强制删除。

## 强制删除一个 Subscription

如果你确实想删除一个 subscription，并且有消费者正在运行，请启用强制删除。当强制
删除一个 subscription 时，该订阅将处于删除中的状态，并关闭正在运行的消费者，这意
味着你将无法加入、删除或创建一个同名的 subscription 。在删除完成后，你可以用同样
的名字创建一个订阅，这个订阅将是一个全新的订阅。即使他们订阅的是同一个流，这个新
的订阅也不会与被删除的订阅共享消费进度。

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

## 列出 HStream 中的 subscription 信息

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

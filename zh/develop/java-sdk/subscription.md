# 订阅

在 HStreamDB 中，订阅也被看作是一种持久的资源，它使得消费者和 stream 解耦。
用户可以创建、删除和列出订阅。

## 概念

HStreamDB 用订阅来管理消费的进度信息 (e.g. checkpoint, offset)。
每一个消费者开始消费前，**必须**加入一个**已经存在**的订阅

## 前提条件

确保有一个运行中并可用的 HStreamDB

## 创建一个新的订阅

通过 `HStreamClient.createSubscription(Subscription)` 可以创建一个新的订阅:


```java

Subscription subscription =
    Subscription
        .newBuilder()
        .subscription("my_subscription")
        .stream("my_stream")
        .offset(new SubscriptionOffset(SubscriptionOffset.SpecialOffset.LATEST))
        .ackTimeoutSeconds(600)
        .build();
client.createSubscription(subscription);

```

其中可以给 `SubscriptionOffset` 以下三种值：

```java

// consume from the start of the stream
SubscriptionOffset.SpecialOffset offset = SubscriptionOffset.SpecialOffset.EARLIST;

// consume from the tail of the stream
SubscriptionOffset.SpecialOffset offset = SubscriptionOffset.SpecialOffset.LATEST;

// consume from RecordId with specified LSN and offset
RecordId rid = new RecordId(1, 2);

```

## 列出所有存在的订阅

通过 `HStreamClient.listSubscriptions()` 可以拿到所有存在的订阅列表:

```java

List<Subscription> subscriptions = client.listSubscriptions();
for (Subscription subscription : subscriptions) {
  System.out.println(subscription);
  System.out.println(subscription.getSubscriptionId());
}

```

## 删除一个订阅

通过 `HStreamClient.deleteSubscription()` 可以删除一个订阅:

```java

client.deleteSubscription("test_subscription");

```

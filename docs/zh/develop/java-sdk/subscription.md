# Subscription

In HStramDB, subscription is also seen as a persistent resource which enabling
decoupling between the consumer and the stream. User can create、delete and list
subscriptions.

## Concepts

HStreamDB uses subscription to manage the progress information of consumption
(e.g. checkpoint, offset). For consumer to consume the data, it **must join** a
subscription that **already exists**.

!!! Tips

    Currently, only one consumer is allowed to join the same subscription at a time.

## Prerequisites

Make sure you have HStreamDB running and accessible.

## Subscription Object

A subscription object is defined in protobuf:

```protobuf

message Subscription {
  string subscriptionId = 1; // the unique identifier of the subscription
  string streamName = 2; // streams to be consumed
  SubscriptionOffset offset = 3; // the starting position of consumption
}

message SubscriptionOffset {
  enum SpecialOffset {
    EARLIST = 0; // consume from the start of the stream
    LATEST = 1; // consume from the tail of the stream
  }

  oneof offset {
    SpecialOffset specialOffset = 1;
    RecordId recordOffset = 2; // consume from a customer specified position
  }
}

```

## Create a New Subscription

You can create a new subscription using the
`HStreamClient.createSubscription(Subscription)` method:

```java

Subscription subscription = Subscription.newBuilder()
    .setSubscriptionId("test_subscription")
    .setStreamName("test_stream")
    .setOffset(
        SubscriptionOffset.newBuilder()
            .setSpecialOffset(SubscriptionOffset.SpecialOffset.LATEST)
            .build())
    .build();

client.createSubscription(subscription);

```

## Get a List of Subscriptions

You can get a list of the subscriptions using the
`HStreamClient.listSubscriptions()` method:

```java

List<Subscription> subscriptions = client.listSubscriptions();
for (Subscription subscription : subscriptions) {
  System.out.println(subscription);
  System.out.println(subscription.getSubscriptionId());
}

```

## Delete a Subscription

You can delete a subscription using the `HStreamClient.deleteSubscription()`
method:

```java

client.deleteSubscription("test_subscription");

```

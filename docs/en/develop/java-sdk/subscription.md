# Subscription

In HStramDB, subscription is also seen as a persistent resource which enabling
decoupling between the consumer and the stream. User can create、delete and list
subscriptions.

## Concepts

HStreamDB uses subscription to manage the progress information of consumption.
For consumer to consume the data, it **must join** a
subscription that **already exists**.

## Prerequisites

Make sure you have HStreamDB running and accessible.

## Create a New Subscription

You can create a new subscription using the
`HStreamClient.createSubscription(Subscription)` method:

```java

Subscription subscription =
    Subscription
        .newBuilder()
        .subscription("my_subscription")
        .stream("my_stream")
        .ackTimeoutSeconds(600)
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

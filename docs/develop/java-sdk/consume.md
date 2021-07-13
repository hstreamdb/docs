# Consume data

This page shows how to consume data from HStreamDB using Java SDK.

## Prerequisites 

Make sure you have HStreamDB running and accessible.

## Consumer

Before you can consume data, you first need to create a ``Consumer`` object
using the ``HStreamClient.newConsumer()`` method:

```java

Consumer consumer = client.newConsumer()
        .subscription("test_subscription")
        .stream("test_stream")
        .maxPollRecords(100)
        .pollTimeoutMs(1000)
        .build();

```

A consumer must be associated with a subscription, and a subscription contains a
stream.

Once the consumer is created successfully, it can be used to continuously
receive data from the subscribed stream.

## Receive Raw Records 

You can receive receive raw records using the ``Consumer.pollRawRecords()`` method:

```java

while(true) {
    List<ReceivedRawRecord> receivedRawRecords = consumer.pollRawRecords();
    for(ReceivedRawRecord receivedRawRecord: receivedRawRecords) {
        System.out.println(receivedRawRecord.getRecordId());
    }
}

```

## Receive HRecords 

You can receive receive hrecords using the ``Consumer.pollHRecords()`` method:

```java

while(true) {
    List<ReceivedHRecord> receivedHRecords = consumer.pollHRecords();
    for(ReceivedHRecord receivedHRecord: receivedHRecords) {
        System.out.println(receivedHRecord.getRecordId());
    }
}


```

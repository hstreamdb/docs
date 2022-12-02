# hstreamdb-java-examples

## Prerequisites

- hstreamdb server v0.10.0
- hstreamdb-java v0.10.0

## Examples

### Stream
- [CreateStreamExample.java](app/src/main/java/docs/code/examples/CreateStreamExample.java) - How to create a stream
- [ListStreamsExample.java](app/src/main/java/docs/code/examples/ListStreamsExample.java) - How to lists streams
- [DeleteStreamExample.java](app/src/main/java/docs/code/examples/DeleteStreamExample.java) - How to delete a stream

### Subscription
- [CreateSubscriptionExample.java](app/src/main/java/docs/code/examples/CreateSubscriptionExample.java) - How to create a subscription
- [ListSubscriptionsExample.java](app/src/main/java/docs/code/examples/ListSubscriptionsExample.java) - How to lists subscriptions
- [DeleteSubscriptionExample.java](app/src/main/java/docs/code/examples/DeleteSubscriptionExample.java) - How to delete create a subscription


### Producer
- [WriteDataSimpleExample.java](app/src/main/java/docs/code/examples/WriteDataSimpleExample.java) - How to write RawRecord and HRecord messages
- [WriteDataBufferedExample.java](app/src/main/java/docs/code/examples/WriteDataBufferedExample.java) - How to write records with BufferedProducer
- [WriteDataWithKeyExample.java](app/src/main/java/docs/code/examples/WriteDataWithKeyExample.java) - How to write records including multiple partition keys


### Consumer
- [ConsumeDataSimpleExample.java](app/src/main/java/docs/code/examples/ConsumeDataSimpleExample.java) - How to consume records
- [ConsumeDataSharedExample.java](app/src/main/java/docs/code/examples/ConsumeDataSharedExample.java) - How to consume records with multiple consumers
- [ConsumeDataWithErrorListenerExample](app/src/main/java/docs/code/examples/ConsumeDataWithErrorListenerExample.java) - How to handle a failed consumer

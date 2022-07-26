# Create and Manage Streams

## Attributes of a Stream

- Replication factor

  For fault tolerance and higher availability, every stream can be replicated
  across nodes in the cluster. A typical production setting is a replication
  factor of 3, i.e., there will always be three copies of your data, which is
  helpful just in case things go wrong or you want to do maintenance on the
  brokers. This replication is performed at the level of the stream.

- Backlog retention

  The configuration controls how long streams of HStreamDB retain records after
  being appended. HStreamDB will discard the message regardless of whether it is
  consumed when it exceeds the backlog retention duration.

  + Default = 7 days
  + Minimum value = 1 seconds
  + Maximum value = 21 days

## Create a Stream

Create a stream before you write records or create a subscription.

:::: tabs

::: tab Java

```java
// CreateStreamExample.java
```

:::

::: tab Go

```go
// ExampleCreateStream.go
```

:::

::::

## Delete a Stream

Deletion is only allowed when a stream has no subsequent subscriptions unless
the force flag is set.

### Delete a stream with the force flag

If you need to delete a stream with subscriptions, enable force deletion.
Existing stream subscriptions can still read from the backlog after deleting a
stream with the force flag enabled. However, these subscriptions will have
stream name `__deleted_stream__`, no new subscription creation on the deleted
stream would be allowed, nor new records would be allowed to be written to the
stream.

:::: tabs

::: tab Java

```java
// DeleteStreamExample.java
```

:::

::: tab Go

```go
// ExampleDeleteStream.go
```

:::

::::

## List Streams

To get all streams in HStream:

:::: tabs

::: tab Java

```java
// ListStreamsExample.java
```

:::

::: tab Go

```go
// ExampleListStreams.go
```

:::

::::

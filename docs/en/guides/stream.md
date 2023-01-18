# Create and Manage Streams

## Guidelines to name a resource

An HStream resource name uniquely identifies an HStream resource, such as a
stream, a subscription or a reader. The resource name must fit the following
requirements:

- Start with a letter
- Length must be no longer than 255 characters
- Contain only the following characters: Letters `[A-Za-z]`, numbers `[0-9]`,
  dashes `-`, underscores `_`

\*For the cases where the resource name is used as a part of a SQL statement,
  such as in [HStream SQL Shell](../reference/cli.md#hstream-sql-shell), there
  will be situations where the resource name cannot be parsed properly (such as
  conflicts with Keywords etc.), enclose the resource name with backticks `` ` ``.
  With the enhancements of the SQL parser, the constriction may be removed in the future.

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

- Shard Count

  The number of shards that a stream will have.

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

::: tab Python3
@snippet examples/py/snippets/guides.py common create-stream
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

::: tab Python3
@snippet examples/py/snippets/guides.py common delete-stream
:::

::::

## List Streams

To get all streams in HStreamDB:

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

::: tab Python3
@snippet examples/py/snippets/guides.py common list-streams
:::

::::

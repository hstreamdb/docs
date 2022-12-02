# Get Records from Shards of the Stream with Reader

## What is a Reader

To allow users to retrieve data from any stream shard, HStreamDB provides
readers for applications to manually manage the exact position of the record to
read from. Unlike subscription and consumption, a reader can be seen as a
lower-level API for getting records from streams. It gives users direct access
to any records in the stream, more precisely, any records from a specific shard
in the stream, and it does not require or rely on subscriptions and will not
send any acknowledgement back to the server. Therefore, the reader is helpful
for the case that requires better flexibility or rewinding of data reading.

When a user creates a reader instance, it is required that the user needs to
specify which record and which shard the reader begins from. A reader provides
starting position with the following options:

- The earliest available record in the shard
- The latest available record in the shard
- User-specified record location in the shard

## Reader Example

To read from the shards, users are required to get the desired shard id with
[`listShards`](./shards.md#listshards).

The name of a reader should also follow the format specified by the [guidelines](./stream.md#guidelines-to-name-a-resource)

:::: tabs

::: tab Java

```java
// ReadDataWithReaderExample.java
```

:::

::: tab Go

```go
// ExampleReadDataWithReader.go
```

:::

::: tab Python3
@snippet examples/py/snippets/guides.py common read-reader
:::

::::

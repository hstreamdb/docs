# Transparent Sharding

## Transparent Sharding in HStreamDB

Transparent sharding in HStreamDB means that every stream will contain multiple
implicit partitions that spread among multiple server nodes. We believe that
stream as itself is a concise and powerful enough abstraction. Thus partitions
should only be the implementation details and not be exposed to users. Since
these partitions are invisible to users, it will appear to users that every
stream is managed as one.

## Make Use of Transparent Sharding in HStreamDB

The transparent sharding feature does not require users to deal with any
partition logic like the number of partitions or partition mapping. As a user,
all they need to do is to specify the ordering key when they try to write a
record to a stream. Every key corresponds to a virtual partition, and HServer
will map thess virtual partitions to physical partitions in the storage.

If the user does not specify the ordering key, all the records with no ordering
key will be assigned to a default partition of the stream. So if no ordering
keys are provided at all, the system will have the same behaviour as there is no
sharding. However, this will not be noticeable to the user regardless because
there is no explicit sharding logic during any user interactions.

## Benefits of Transparent Sharding

Sharding is an effective solution for relieving the single-node performance
bottleneck and increasing the horizontal scalability of the system. However, if
the partition logic were directly exposed to users, the higher-level abstraction
such as stream would be fragmented and increase the cost of learning and use.
Hiding the partition from the user will significantly reduce the complexity of
using HStreamDB but still take advantage of sharding.

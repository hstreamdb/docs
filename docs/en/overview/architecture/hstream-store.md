# HStream Storage (HStore)

HStream Storage (HStore), the core storage component of HStreamDB, is a low-latency
storage component explicitly designed for streaming data.
It can store large-scale real-time data in a distributed and persistent manner
and seamlessly interface with large-capacity secondary storage such as S3 through
the Auto-Tiering mechanism to achieve unified storage of historical and real-time data.

The core storage model of HStore is a logging model that fits with streaming data.
Regard data stream as an infinitely growing log, the typical operations supported
include appending and reading by batches.
Also, since the data stream is immutable, it generally does not support update operations.

## HStream Storage (HStore) consists of following layer

### Streaming Data API layer

This layer provides the core data stream management and read/write operations,
including stream creation/deletion and writing to/consuming data in the stream.
In the design of HStore, data streams are not stored as actual streams.
Therefore, the creation of a stream is a very light-weight operation.
There is no limit to the number of streams to be created in HStore.
Besides, it supports concurrent writes to numerous data streams and still maintains a stable low latency.
For the characteristics of data streams, HStore provides append operation to support fast data writing.
While reading from stream data, it gives a subscription-based operation
and pushes any new data written to the stream to the data consumer in real time.

### Replicator Layer

This layer implements the strongly consistent replication based on an optimized
Flexible Paxos consensus mechanism,
ensuring the fault tolerance and high availability to data,
and maximizes cluster availability through a non-deterministic data distribution policy.
Moreover, it supports replication groups reconfiguration online to achieve seamless
cluster data balancing and horizontal scaling.

### Tier1 Local Storage Layer

The layer fulfilled local persistent storage needs of data based on the optimized RocksDB storage engine,
which encapsulates the access interface of streaming data
and can support low-latency writing and reading a large amount of data.

### Tier2 Offloader Layer

This layer provides a unified interface encapsulation for various long-term storage systems,
such as HDFS, AWS S3, etc.
It supports automatic offloading of historical data to these secondary storage systems
and can also be accessed through a unified streaming data interface.

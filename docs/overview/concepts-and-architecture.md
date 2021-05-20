# Concepts and Architecture

HStreamDB - a cloud-native and unified solution for streaming data

## Concepts

### Stream

What is a **Stream** in HStreamDB? A stream is a flow of endless data with a unique name. These endless data are known as streaming data, which is generated continuously. Typically, streaming data are in small sizes and the form of data records. Therefore, in an HStream stream, the default form of a single data is JSON.

### Query/Stream Processing

Stream Processing is done by **Queries**. A query put, in short, is a streaming version of the `SELECT` SQL task. A running query will manipulate/filter stream and return continuous results in real-time. With queries, real-time monitoring and response can be easily implemented.

### Connenctor

Connectors are the ways how HStreamDB can interact with other data systems. They are in charge of import(export)ing data.

## HStream Streaming Database Overview

**HStreamDB is a streaming database designed for streaming data, with complete lifecycle management for accessing, storing, processing, and distributing large-scale real-time data streams**. It uses standard SQL (and its stream extensions) as the primary interface language, with real-time as the main feature, and aims to simplify the operation and management of data streams and the development of real-time applications.

## Architecture

The figure below shows the overall architecture of HStreamDB. A single HStreamDB node consists of two core components, HStream Server (HSQL) and HStream Storage (HStorage). And an HStream cluster consists of several peer-to-peer HStreamDB nodes. Clients can connect to any HStreamDB node in the cluster and perform stream processing and analysis through your familiar SQL language.

![](https://static.emqx.net/images/faab4a8b1d02f14bc5a4153fe37f21ca.png)

<center>HStreamDB Structure Overview</center>

HStream Server (HSQL), the core computation component of HStreamDB, is designed to be stateless. The primary responsibility of HSQL is to support client connection management, security authentication, SQL parsing and optimization, and operations for stream computation such as task creation, scheduling, execution, management, etc.

### HStream Server

#### HStream Server (HSQL) top-down layered structures

1. Access Layer

   It is in charge of protocol processing, connection management, security authentication, and access control for client requests.

2. SQL layer

   To perform most stream processing and real-time analysis tasks, clients interact with HStreamDB through SQL statements. This layer is mainly responsible for compiling these SQL statements into logical data flow diagrams. Like the classic database system model, it contains two core sub-components: SQL parser and SQL optimizer. The SQL parser deals with the lexical and syntactic analysis and the compilation from SQL statements to relational algebraic expressions; the SQL optimizer will optimize the generated execution plan based on various rules and contexts.

3. Stream Layer

   Stream layer includes the implementation of various stream processing operators, the data structures and DSL to express data flow diagrams, and the support for user-defined functions as processing operators. So, it is responsible for selecting the corresponding operator and optimization to generate the executable data flow diagram.

4. Runtime Layer

   It is the layer responsible for executing the computation task of data flow diagrams and returning the results. The main components of the layer include task scheduler, state manager, and execution optimizer. The schedule takes care of the tasks scheduling between available computation resources, such as multiple threads of a single process, multiple processors of a single machine, and multiple machines or containers of a distributed cluster.

HStream Storage (HStore), the core storage component of HStreamDB, is a low-latency storage component explicitly designed for streaming data. It can store large-scale real-time data in a distributed and persistent manner and seamlessly interface with large-capacity secondary storage such as S3 through the Auto-Tiering mechanism to achieve unified storage of historical and real-time data.

The core storage model of HStore is a logging model that fits with streaming data. Regard data stream as an infinitely growing log, the typical operations supported include appending and reading by batches. Also, since the data stream is immutable, it generally does not support update operations.

### HStream Storage (HStore)

#### HStream Storage (HStore) consists of following layers

1. Streaming Data API layer

   This layer provides the core data stream management and read/write operations, including stream creation/deletion and writing to/consuming data in the stream. In the design of HStore, data streams are not stored as actual streams. Therefore, the creation of a stream is a very light-weight operation. There is no limit to the number of streams to be created in HStore. Besides, it supports concurrent writes to numerous data streams and still maintains a stable low latency. For the characteristics of data streams, HStore provides append operation to support fast data writing. While reading from stream data, it gives a subscription-based operation and pushes any new data written to the stream to the data consumer in real time.

2. Replicator Layer

   This layer implements the strongly consistent replication based on an optimized Flexible Paxos consensus mechanism, ensuring the fault tolerance and high availability to data, and maximizes cluster availability through a non-deterministic data distribution policy. Moreover, it supports replication groups reconfiguration online to achieve seamless cluster data balancing and horizontal scaling.

3. Tier1 Local Storage Layer

   The layer fulfilled local persistent storage needs of data based on the optimized RocksDB storage engine, which encapsulates the access interface of streaming data and can support low-latency writing and reading a large amount of data.

4. Tier2 Offloader Layer

   This layer provides a unified interface encapsulation for various long-term storage systems, such as HDFS, AWS S3, etc. It supports automatic offloading of historical data to these secondary storage systems and can also be accessed through a unified streaming data interface.

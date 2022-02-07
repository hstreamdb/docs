# HStreamDB release notes

## v0.7.0 [2022-01-28]

### Features

#### Add transparent sharding support

HStreamDB has already supported the storage and management of large-scale data
streams. With the newly added cluster support in the last release, we decided to
improve a single stream's scalability and reading/writing performance with a
transparent sharding strategy. In HStreamDB v0.7, every stream is spread across
multiple server nodes, but it it appears to user that a stream with partitions
is managed as an entity. Therefore, users do not need to specify the number of
shards or any sharding logic in advance.

In the current implementation, each record in a stream should contain an
ordering key to specify a logical partition, and the HStream server will be
responsible for mapping these logical partitions to physical partitions when
storing data.

#### Redesign load balancing with the consistent hashing algorithm

We have adapted our load balancing with a consistent hashing algorithm in this
new release. Both write and read requests are currently allocated by the
ordering key of the record carried in the request.

In the previous release, our load balancing was based on the hardware usage of
the nodes. The main problem with this was that it relied heavily on a leader
node to collect it. At the same time, this policy requires the node to
communicate with the leader to obtain the allocation results. Overall the past
implementation was too complex and inefficient. Therefore, we have
re-implemented the load balancer, which simplifies the core algorithm and copes
well with redistribution when cluster members change.

#### Add HStream admin tool

We have provided a new admin tool to facilitate the maintenance and management
of HStreamDB. HAdmin can be used to monitor and manage the various resources of
HStreamDB, including Stream, Subscription and Server nodes. The HStream Metrics,
previously embedded in the HStream SQL Shell, have been migrated to the new
HAdmin. In short, HAdmin is for HStreamDB operators, and SQL Shell is for
HStreamDB end-users.

#### Deployment and usage

- Support quick deployment via the script, see:
  [Manual Deployment with Docker](../deployment/deploy-docker.md)
- Support config HStreamDB with a configuration file, see:
  [HStreamDB Configuration](../reference/config.md)
- Support one-step docker-compose for quick-start:
  [Quick Start With Docker Compose](../start/quickstart-with-docker.md)

**To make use of HStreamDB v0.7, please use [hstreamdb-java v0.7.0](https://github.com/hstreamdb/hstreamdb-java) and above**

## v0.6.0 [2021-11-04]

### Features

#### Add HServer cluster support

As a cloud-native distributed streaming database, HStreamDB has adopted a
separate architecture for computing and storage from the beginning of design, to
support the independent horizontal expansion of the computing layer and storage
layer. In the previous version of HStreamDB, the storage layer HStore already
has the ability to scale horizontally. In this release, the computing layer
HServer will also support the cluster mode so that the HServer node of the
computing layer can be expanded according to the client request and the scale of
the computing task.

HStreamDB's computing node HServer is designed to be stateless as a whole, so it
is very suitable for rapid horizontal expansion. The HServer cluster mode of
v0.6 mainly includes the following features:

- Automatic node health detection and failure recovery
- Scheduling and balancing client requests or computing tasks according to the
  node load conditions
- Support dynamic joining and exiting of nodes

#### Add shared-subscription mode

In the previous version, one subscription only allowed one client to consume
simultaneously, which limited the client's consumption capacity in the scenarios
with a large amount of data. Therefore, in order to support the expansion of the
client's consumption capacity, HStreamDB v0.6 adds a shared-subscription mode,
which allows multiple clients to consume in parallel on one subscription.

All consumers included in the same subscription form a Consumer Group, and
HServer will distribute data to multiple consumers in the consumer group through
a round-robin manner. The consumer group members can be dynamically changed at
any time, and the client can join or exit the current consumer group at any
time.

HStreamDB currently supports the "at least once" consumption semantics. After
the client consumes each data, it needs to reply to the ACK. If the Ack of a
certain piece of data is not received within the timeout, HServer will
automatically re-deliver the data to the available consumers.

Members in the same consumer group share the consumption progress. HStream will
maintain the consumption progress according to the condition of the client's
Ack. The client can resume consumption from the previous location at any time.

It should be noted that the order of data is not maintained in the shared
subscription mode of v0.6. Subsequent shared subscriptions will support a
key-based distribution mode, which can support the orderly delivery of data with
the same key.

#### Add statistical function

HStreamDB v0.6 also adds a basic data statistics function to support the
statistics of key indicators such as stream write rate and consumption rate.
Users can view the corresponding statistical indicators through HStream CLI, as
shown in the figure below.

![](./statistics.png)

#### Add REST API for data writing

HStreamDB v0.6 adds a REST API for writing data to HStreamDB.

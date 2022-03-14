# Streaming Data, Stream and HStream Record

## Streaming Data

Streaming data are continuously generated and typically sent in data records
with small sizes (order of Kilobytes).

## What is a **Stream** in HStreamDB?

Conceptually, in HStreamDB, a stream is a flow of endless persisted data
assigned with a unique name. A flow indicates that the stream in HStreamDB is
append-only.

Programmatically, streams are the fundamental storage unit of HStreamDB for
organizing and processing streaming data. Producers and Consumers are provided
to write and read data to/from streams.

## What is the data like in a stream?

Data in streams are in the form of HStream Records. An HStream Record is
composed of a header and payloads. The following table lists the components
of an HStream Record Header.

| Key          | Description                                                                         |
| ------------ | ----------------------------------------------------------------------------------- |
| Flag         | Flag for payloads, whether it is in the form of `JSON` or `Raw` binary.             |
| Key          | Optional key with the message, would be useful for things like
[transparent sharding](./transparent-sharding.md) |
| Attributes   | Customized attributes                                                               |
| Publish_Time | The timestamp of the written, will be modified at server                            |

```proto
message HStreamRecord {
  HStreamRecordHeader header = 1;     // Required.
  bytes payload = 2;                  // Optional, payload can be empty.
}
```

### Appendix: Examples of Streaming Data

_reference: [streaming-data](https://aws.amazon.com/streaming-data/)_

> - Sensors in transportation vehicles, industrial equipment, and farm machinery
>   send data to a streaming application. The application monitors performance,
>   detects any potential defects in advance, and places a spare part order
>   automatically preventing equipment down time.
> - A financial institution tracks changes in the stock market in real time,
>   computes value-at-risk, and automatically rebalances portfolios based on
>   stock price movements.
> - A real-estate website tracks a subset of data from consumers’ mobile devices
>   and makes real-time property recommendations of properties to visit based on
>   their geo-location.
> - A solar power company has to maintain power throughput for its customers, or
>   pay penalties. It implemented a streaming data application that monitors of
>   all of panels in the field, and schedules service in real time, thereby
>   minimizing the periods of low throughput from each panel and the associated
>   penalty payouts.
> - A media publisher streams billions of clickstream records from its online
>   properties, aggregates and enriches the data with demographic information
>   about users, and optimizes content placement on its site, delivering
>   relevancy and better experience to its audience.
> - An online gaming company collects streaming data about player-game
>   interactions, and feeds the data into its gaming platform. It then analyzes
>   the data in real-time, offers incentives and dynamic experiences to engage
>   its players.

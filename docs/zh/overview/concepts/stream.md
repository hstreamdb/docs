# Stream and Streaming Data

## Streaming Data

Streaming data are data that are generated continuously and typically sent in data records with small sizes (order of Kilobytes).

## What is a **Stream** in HStreamDB?

A stream is a flow of endless data assigned with a unique name. In a stream from HStreamDB, the default form of a single data is JSON. A flow indicates that the stream in HStreamDB is append-only.

### Examples of Streaming Data

*reference: [streaming-data](https://aws.amazon.com/streaming-data/)*

> - Sensors in transportation vehicles, industrial equipment, and farm machinery send data to a streaming application. The application monitors performance, detects any potential defects in advance, and places a spare part order automatically preventing equipment down time.
> - A financial institution tracks changes in the stock market in real time, computes value-at-risk, and automatically rebalances portfolios based on stock price movements.
> - A real-estate website tracks a subset of data from consumers’ mobile devices and makes real-time property recommendations of properties to visit based on their geo-location.
> - A solar power company has to maintain power throughput for its customers, or pay penalties. It implemented a streaming data application that monitors of all of panels in the field, and schedules service in real time, thereby minimizing the periods of low throughput from each panel and the associated penalty payouts.
> - A media publisher streams billions of clickstream records from its online properties, aggregates and enriches the data with demographic information about users, and optimizes content placement on its site, delivering relevancy and better experience to its audience.
> - An online gaming company collects streaming data about player-game interactions, and feeds the data into its gaming platform. It then analyzes the data in real-time, offers incentives and dynamic experiences to engage its players.

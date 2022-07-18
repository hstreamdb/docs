# HStream Admin CLI

We can run the following to use HStream Admin CLI:

```sh
docker run -it --rm --name some-hstream-admin --network host hstreamdb/hstream:v0.8.0 hadmin --help
======= HStream Admin CLI =======

Usage: hadmin COMMAND

Available options:
  -h,--help                Show this help text

Available commands:
  server                   Admin command
  store                    Internal store admin command
```

For the ease of illustration, we execute an interactive bash shell in the
hstream container to use hstream admin,

The following example usage is based on the cluster started in
[quick start](../start/quickstart-with-docker.md), please adjust
correspondingly.

```sh
docker exec -it docker_hserver0_1 bash
$ hadmin server --help
```

```
Usage: hadmin server [--host SERVER-HOST] [--port INT]
                     [--log-level [critical|fatal|warning|info|debug]]
                     (COMMAND | COMMAND)
  Admin command

Available options:
  --host SERVER-HOST       server host admin value (default: "127.0.0.1")
  --port INT               server admin port value (default: 6570)
  --log-level [critical|fatal|warning|info|debug]
                           log level (default: info)
  -h,--help                Show this help text

Available commands:
  sql                      Start an interactive SQL shell
  stats                    Get the stats of an operation on a stream
  stream                   Stream command
  sub                      Subscription command
  view                     View command
  status                   Get the status of the HServer cluster
```

## Check Cluster status

```sh
> hadmin server status
+---------+---------+-------------------+
| node_id |  state  |      address      |
+---------+---------+-------------------+
| 100     | Running | 192.168.64.4:6570 |
| 101     | Running | 192.168.64.5:6572 |
+---------+---------+-------------------+
```

## Resource Management

### Streams

```sh
> hadmin server stream create --name s1
OK
> hadmin server stream list
+------+----------------------+
| name | replication_property |
+------+----------------------+
| s1   | node:3               |
+------+----------------------+
```

### Subscriptions

```sh
> hadmin server sub create --name sub1 --stream s1
OK
> hadmin server sub list
+------+-------------+---------+
| id   | stream_name | timeout |
+------+-------------+---------+
| sub1 | s1          | 60      |
+------+-------------+---------+
```

## HStream Stats

```
hadmin server stats <stats_category> <stats_name>
```

- stream_counter
  + `append_total`
  + `append_failed`
- stream
  + `appends` or `append_in_bytes`
  + `append_in_record`
  + `append_in_requests`
  + `append_failed_requests`
- subscription_counter
  + `resend_records`
- subscription
  + `sends` or `send_out_bytes`
  + `send_out_records`
  + `acks` or `acknowledgements`
  + `request_messages`
  + `response_messages`

For the full list of stats and explanations, please read [hstream metrics](../reference/metrics.md)

### Examples

```sh
hadmin server stats stream appends
```

```
+-------------+--------------+--------------+---------------+
| stream_name | appends_1min | appends_5min | appends_10min |
+-------------+--------------+--------------+---------------+
| s1          | 3570393      | 3570572      | 3570604       |
+-------------+--------------+--------------+---------------+
```

## Interactive SQL shell

HAdmin has an interactive SQL shell, in which the users can get statistics of
server collects.

```sh
hadmin server sql
```

Use `show tables;` to get all the value tables collected from the server. It
explains all the available data.

```
sql> show tables;
+-------------------------------+------------------------------------------+
|             Table             |               Description                |
+-------------------------------+------------------------------------------+
| streams                       | A  table that  lists the streams created |
|                               | in the cluster.                          |
+-------------------------------+------------------------------------------+
|                               | For   each  server   node,  reports  the |
| server_append_request_latency | estimated  percentiles latency of server |
|                               | append request                           |
+-------------------------------+------------------------------------------+
|                               | For   each  server   node,  reports  the |
| server_append_latency         | estimated  percentiles latency of server |
|                               | appends                                  |
+-------------------------------+------------------------------------------+
| append_total_counter          | Total append requests server received.   |
+-------------------------------+------------------------------------------+
|                               | For   each  server   node,  reports  the |
| append_throughput             | estimated  per-stream append  throughput |
|                               | over various time periods.               |
+-------------------------------+------------------------------------------+
|                               | For   each  server   node,  reports  the |
| subscription_throughput       | estimated  per-stream append  throughput |
|                               | over various time periods.               |
+-------------------------------+------------------------------------------+
|                               | For   each  server   node,  reports  the |
| read_throughput               | estimated   per-stream  read  throughput |
|                               | over various time periods.               |
+-------------------------------+------------------------------------------+
| append_failed_counter         | Failed append requests.                  |
+-------------------------------+------------------------------------------+
```

```
sql> describe streams;
+----------------------+--------+------------------------------------------+
|        Column        |  Type  |               Description                |
+----------------------+--------+------------------------------------------+
| node_id              | int    | Node ID this row is for.                 |
+----------------------+--------+------------------------------------------+
| name                 | string | The name of the stream.                  |
+----------------------+--------+------------------------------------------+
| replication_property | string | Replication property configured for this |
|                      |        | stream.                                  |
+----------------------+--------+------------------------------------------+
```

You can use `select` intuitively to get and calculate the data you want.

```
sql> select * from streams;
+---------+------+----------------------+
| node_id | name | replication_property |
+---------+------+----------------------+
| 1       | demo | node:3               |
+---------+------+----------------------+
```

### Example

Find the top 5 streams that have had the highest throughput in the last 10
minutes.

```sql
SELECT streams.name, sum(append_throughput.throughput_10min) AS total_throughput
FROM append_throughput
LEFT JOIN streams ON streams.name = append_throughput.stream_name
GROUP BY stream_name
ORDER BY total_throughput DESC
LIMIT 0, 5;
```

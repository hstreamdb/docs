# HStream Admin

We can run the following to use hstream admin:

```sh
docker run -it --rm --name some-hstream-admin --network host hstreamdb/hstream:v0.7.1 hadmin --help
======= HStream Admin CLI =======

Usage: hadmin COMMAND

Available options:
  -h,--help                Show this help text

Available commands:
  server                   Admin command
  store                    Internal store admin command
```

For the ease of illustration, we execute an interactive bash shell in the hstream container to use hstream admin,

The following example usage is used on the cluster you started in quick start, please adjust correspondingly.

```sh
docker exec -it docker_hserver0_1 bash
> hadmin --help
======= HStream Admin CLI =======

Usage: hadmin COMMAND

Available options:
  -h,--help                Show this help text

Available commands:
  server                   Admin command
  store                    Internal store admin command
```

## Server Command

```sh
> hadmin server --help
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
  hserver-sql              Start an interactive SQL shell
  stats                    Get the stats of an operation on a stream
  stream                   Stream command
  sub                      Subscription command
  view                     View command
  status                   Get the status of the HServer cluster
```

### Check Cluster status

```sh
> hadmin server status
+---------+---------+-------------------+
| node_id |  state  |      address      |
+---------+---------+-------------------+
| 100     | Running | 192.168.64.4:6570 |
| 101     | Running | 192.168.64.5:6572 |
+---------+---------+-------------------+
```

### Resource Management

#### Streams

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

#### Subscriptions

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

### HSteam Metrics

```sh
> hadmin server stats --sql "select * from append_throughput"
+-------------+-----------------+-----------------+------------------+
| stream_name | throughput_1min | throughput_5min | throughput_10min |
+-------------+-----------------+-----------------+------------------+
| s1          | 38              | 11              | 6                |
+-------------+-----------------+-----------------+------------------+
```

## Admin interactive SQL shell

HAdmin has an interactive SQL shell, in which you can get statistics of streams.

### SQL Shell

Use `show tables;` to get all the value tables collected from server.
It explains all the available data.

```sh
ADMIN> show tables;
+-------------------+------------------------------------------+
|       Table       |               Description                |
+-------------------+------------------------------------------+
| streams           | A  table that  lists the streams created |
|                   | in the cluster.                          |
+-------------------+------------------------------------------+
|                   | For   each  server   node,  reports  the |
| read_throughput   | estimated   per-stream  read  throughput |
|                   | over various time periods.               |
+-------------------+------------------------------------------+
|                   | For   each  server   node,  reports  the |
| append_throughput | estimated  per-stream append  throughput |
|                   | over various time periods.               |
+-------------------+------------------------------------------+
```

```sh
describe streams;
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

```sh
ADMIN> select * from streams;
+---------+------+----------------------+
| node_id | name | replication_property |
+---------+------+----------------------+
| 1       | demo | node:3               |
+---------+------+----------------------+
```

#### example

Find the top 5 streams that have had the highest throughput in the last 10 minutes.

```sql
SELECT streams.name, sum(append_throughput.throughput_10min) AS total_throughput
FROM append_throughput
LEFT JOIN streams ON streams.name = append_throughput.stream_name
GROUP BY stream_name
ORDER BY total_throughput DESC
LIMIT 0, 5;
```

# HStream Admin

我们可以运行以下命令来使用 hstream admin：

```sh
docker run -it --rm --name some-hstream-admin --network host hstreamdb/hstream:latest hadmin --help
======= HStream Admin CLI =======

Usage: hadmin COMMAND

Available options:
  -h,--help                Show this help text

Available commands:
  server                   Admin command
  store                    Internal store admin command
```

为了便于演示说明，我们将在 hstream 容器中执行一个交互式 bash shell 来使用 hstream admin，

下面的示例用法是在你在[快速启动](../start/quickstart-with-docker.md)中启动的集群上演示的，请相应调整。

```sh
docker exec -it docker_hserver_1 bash
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

## 检查集群状态

```sh
> hadmin server status
+---------+---------+-------------------+
| node_id |  state  |      address      |
+---------+---------+-------------------+
| 100     | Running | 192.168.64.4:6570 |
| 101     | Running | 192.168.64.5:6572 |
+---------+---------+-------------------+
```

## 资源管理

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

## 数据统计 (HStream Stats)

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

更详细的解释请阅读 [hstream metrics](../reference/metrics.md)。

### 示例

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

## 交互式 SQL shell

HAdmin 有一个交互式的 SQL shell，你可以在其中获得流的统计数据。

```sh
hadmin server sql
sql>
```

### SQL Shell

使用 `show tables;` 来获得从服务器上收集的所有 value tables 。它解释了所有存在的数据。

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

你可以直观地使用 `select` 来获取和计算你想要的数据。

```sh
sql> select * from streams;
+---------+------+----------------------+
| node_id | name | replication_property |
+---------+------+----------------------+
| 1       | demo | node:3               |
+---------+------+----------------------+
```

#### 示例

找到过去10分钟内吞吐量最高的前 5 个 stream。

```sql
SELECT streams.name, sum(append_throughput.throughput_10min) AS total_throughput
FROM append_throughput
LEFT JOIN streams ON streams.name = append_throughput.stream_name
GROUP BY stream_name
ORDER BY total_throughput DESC
LIMIT 0, 5;
```

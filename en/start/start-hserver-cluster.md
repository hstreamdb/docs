# Start Local HServer Cluster with Docker

## Requirement

### Running HStream Storage

Please refer to [quickstart with docker](./quickstart-with-docker.md) and make sure you have a running HStore (HStream Storage).

### Start ZooKeeper Server

A running zookeeper is required to start a HServer cluster.

```sh
docker run --rm -d --network host --name some-zookeeper-demo zookeeper
```

## Start a local HStream-Server Cluster in Docker

::: warning
Do NOT use this configuration in your production environment!
:::

You can manually start 3 standalone server and pass the same zkuri to create a hserver cluster.
Note that every server needs the following options to be unique to work properly:

- **server-id     : the id has to be an integer. This is the identifier of every server.**
- **port          : the port number that client connects to.**
- **internal-port : the internal channel for server communication.**

For example, run the following commands to start a cluster with 3 nodes:

```sh
docker run -it --rm --name some-hstream-server -v /dbdata:/data/store --network host hstreamdb/hstream hstream-server --store-config /data/store/logdevice.conf --zkuri 127.0.0.1:2181 --port 6570 --internal-port 6571 --server-id 1
```

```sh
docker run -it --rm --name some-hstream-server -v /dbdata:/data/store --network host hstreamdb/hstream hstream-server --store-config /data/store/logdevice.conf --zkuri 127.0.0.1:2181 --port 6572 --internal-port 6573 --server-id 2
```

```sh
docker run -it --rm --name some-hstream-server -v /dbdata:/data/store --network host hstreamdb/hstream hstream-server --store-config /data/store/logdevice.conf --zkuri 127.0.0.1:2181 --port 6574 --internal-port 6575 --server-id 3
```

### Start HStreamDB's interactive SQL CLI

[Start a cli session](./quickstart-with-docker.md#start-hstreamdb-s-interactive-sql-cli) in the similar way as when you have a standalone server.
An HStream Server Cluster does not affect how you use CLI.

```sh
docker run -it --rm --name some-hstream-cli -v /dbdata:/data/store --network host hstreamdb/hstream hstream-client --port 6570 --client-id 1
```

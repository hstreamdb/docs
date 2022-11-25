# Manual Deployment with Docker

This document describes how to run HStreamDB cluster with docker.

::: warning

This tutorial only shows the main process of starting HStreamDB cluster with
docker, the parameters are not configured with any security in mind, so please
do not use them directly when deploying!

:::

## Set up a ZooKeeper ensemble

`HServer` and `HStore` require ZooKeeper in order to store some metadata. We
need to set up a ZooKeeper ensemble first.

You can find a tutorial online on how to build a proper ZooKeeper ensemble. As
an example, here we just quickly start a single-node ZooKeeper via docker.

```shell
docker run --rm -d --name zookeeper --network host zookeeper
```

## Create data folders on storage nodes

Storage nodes store data in shards. Typically each shard maps to a different
physical disk. Assume your data disk is mounted on `/mnt/data0`

```shell
# creates the root folder for data
sudo mkdir -p /data/logdevice/

# writes the number of shards that this box will have
echo 1 | sudo tee /data/logdevice/NSHARDS

# creates symlink for shard 0
sudo ln -s /mnt/data0 /data/logdevice/shard0

# adds the user for the logdevice daemon
sudo useradd logdevice

# changes ownership for the data directory and the disk
sudo chown -R logdevice /data/logdevice/
sudo chown -R logdevice /mnt/data0/
```

- See
  [Create data folders](https://logdevice.io/docs/FirstCluster.html#4-create-data-folders-on-storage-nodes)
  for details

## Create a configuration file

Here is a minimal configuration file example. Before using it, please modify it
to suit your situation.

```json
{
  "server_settings": {
    "enable-nodes-configuration-manager": "true",
    "use-nodes-configuration-manager-nodes-configuration": "true",
    "enable-node-self-registration": "true",
    "enable-cluster-maintenance-state-machine": "true"
  },
  "client_settings": {
    "enable-nodes-configuration-manager": "true",
    "use-nodes-configuration-manager-nodes-configuration": "true",
    "admin-client-capabilities": "true"
  },
  "cluster": "logdevice",
  "internal_logs": {
    "config_log_deltas": {
      "replicate_across": {
        "node": 3
      }
    },
    "config_log_snapshots": {
      "replicate_across": {
        "node": 3
      }
    },
    "event_log_deltas": {
      "replicate_across": {
        "node": 3
      }
    },
    "event_log_snapshots": {
      "replicate_across": {
        "node": 3
      }
    },
    "maintenance_log_deltas": {
      "replicate_across": {
        "node": 3
      }
    },
    "maintenance_log_snapshots": {
      "replicate_across": {
        "node": 3
      }
    }
  },
  "metadata_logs": {
    "nodeset": [],
    "replicate_across": {
      "node": 3
    }
  },
  "zookeeper": {
    "zookeeper_uri": "ip://10.100.2.11:2181",
    "timeout": "30s"
  }
}
```

- If you have a multi-node ZooKeeper ensemble, use the list of ZooKeeper
  ensemble nodes and ports to modify `zookeeper_uri` in the `zookeeper` section:

  ```json
      "zookeeper": {
          "zookeeper_uri": "ip://10.100.2.11:2181,10.100.2.12:2181,10.100.2.13:2181",
          "timeout": "30s"
      }
  ```

- Detailed explanations of all the attributes can be found in the
  [Cluster configuration](https://logdevice.io/docs/Config.html) docs.

## Store the configuration file

You can the store configuration file in ZooKeeper, or store it on each storage
nodes.

### Store configuration file in ZooKeeper

Suppose you have a configuration file on one of your ZooKeeper nodes with the
path `~/logdevice.conf`. Save the configuration file to the ZooKeeper by running
the following command.

```shell
docker exec zookeeper zkCli.sh create /logdevice.conf "`cat ~/logdevice.conf`"
```

You can verify the create operation by:

```shell
docker exec zookeeper zkCli.sh get /logdevice.conf
```

## Set up HStore cluster

For the configuration file stored in ZooKeeper, assume that the value of the
`zookeeper_uri` field in the configuration file is `"ip:/10.100.2.11:2181"` and
the path to the configuration file in ZooKeeper is `/logdevice.conf`.

For the configuration file stored on each node, assume that your file path is
`/data/logdevice/logdevice.conf`.

### Start admin server on a single node

- Configuration file stored in ZooKeeper：

  ```shell
  docker run --rm -d --name storeAdmin --network host -v /data/logdevice:/data/logdevice \
          hstreamdb/hstream:latest /usr/local/bin/ld-admin-server \
          --config-path zk:10.100.2.11:2181/logdevice.conf \
          --enable-maintenance-manager \
          --maintenance-log-snapshotting \
          --enable-safety-check-periodic-metadata-update
  ```

  - If you have a multi-node ZooKeeper ensemble, Replace `--config-path`
    parameter to:
    `--config-path zk:10.100.2.11:2181,10.100.2.12:2181,10.100.2.13:2181/logdevice.conf`

- Configuration file stored in each node：

  Replace `--config-path` parameter to
  `--config-path /data/logdevice/logdevice.conf`

### Start logdeviced on every node

- Configuration file stored in ZooKeeper：

  ```shell
  docker run --rm -d --name hstore --network host -v /data/logdevice:/data/logdevice \
          hstreamdb/hstream:latest /usr/local/bin/logdeviced \
          --config-path zk:10.100.2.11:2181/logdevice.conf \
          --name store-0 \
          --address 192.168.0.3 \
          --local-log-store-path /data/logdevice
  ```

  - For each node, you should update the `--name` to a **different value** and
    `--address` to the host IP address of that node.

- Configuration file stored in each node：

  Replace `--config-path` parameter to
  `--config-path /data/logdevice/logdevice.conf`

### Bootstrap the cluster

After starting the admin server and logdeviced for each storage node, now we can
bootstrap our cluster.

On the admin server node, run:

```shell
docker exec storeAdmin hadmin store nodes-config bootstrap --metadata-replicate-across 'node:3'
```

And you should see something like this:

```shell
Successfully bootstrapped the cluster, new nodes configuration version: 7
Took 0.019s
```

You can check the cluster status by run:

```shell
docker exec storeAdmin hadmin store status
```

And the result should be:

```shell
+----+---------+----------+-------+-----------+---------+---------------+
| ID |  NAME   | PACKAGE  | STATE |  UPTIME   |  SEQ.   | HEALTH STATUS |
+----+---------+----------+-------+-----------+---------+---------------+
| 0  | store-0 | 99.99.99 | ALIVE | 2 min ago | ENABLED | HEALTHY       |
| 1  | store-2 | 99.99.99 | ALIVE | 2 min ago | ENABLED | HEALTHY       |
| 2  | store-1 | 99.99.99 | ALIVE | 2 min ago | ENABLED | HEALTHY       |
+----+---------+----------+-------+-----------+---------+---------------+
Took 7.745s
```

Now we finish setting up the `HStore` cluster.

## Set up HServer cluster

To start a single `HServer` instance, you can modify the start command to fit
your situation:

```shell
docker run -d --name hstream-server --network host \
        hstreamdb/hstream:latest /usr/local/bin/hstream-server \
        --bind-address $SERVER_HOST \
        --advertised-address $SERVER_HOST \
        --seed-nodes $SERVER_HOST \
        --metastore-uri zk://$ZK_ADDRESS \
        --store-config zk:$ZK_ADDRESS/logdevice.conf \
        --store-admin-host $ADMIN_HOST \
        --server-id 1
```

- `$SERVER_HOST` ：The host IP address of your server node, e.g `192.168.0.1`
- `metastore-uri`: The address of HMeta, it currently support `zk://$ZK_ADDRESS` for zookeeper and `rq://$RQ_ADDRESS` for rqlite (experimental).
- `$ZK_ADDRESS` ：Your ZooKeeper ensemble address list, e.g
  `10.100.2.11:2181,10.100.2.12:2181,10.100.2.13:2181`
- `--store-config` ：The path to your `HStore` configuration file. Should match
  the value of the `--config-path` parameter when starting the `HStore` cluster
- `--store-admin-host`： The IP address of the `HStore Admin Server` node
- `--server-id` ：You should set a **unique identifier** for each server
  instance

You can start multiple server instances on different nodes in the same way.

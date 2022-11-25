# 用 Docker 手动部署

本文描述了如何用 Docker 运行 HStreamDB 集群。

::: warning

本教程只展示了用 Docker 启动 HStreamDB 集群的主要过程。参数的配置没有考虑到任何安全问题，所以请
请不要在部署时直接使用它们

:::

## 设置一个 ZooKeeper 集群

`HServer` 和 `HStore` 需要 ZooKeeper 来存储一些元数据，所以首先我们需要配置一个 ZooKeeper 集群。

你可以在网上找到关于如何建立一个合适的 ZooKeeper 集群的教程。

这里我们只是通过 docker 快速启动一个单节点的 ZooKeeper 为例。

```shell
docker run --rm -d --name zookeeper --network host zookeeper
```

## 在存储节点上创建数据文件夹

存储节点会把数据存储分片（Shard）中。通常情况下，每个分片映射到不同的物理磁盘。
假设你的数据盘被挂载（mount）在`/mnt/data0` 上

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

## 创建配置文件

这里是一个配置文件的最小示例。

在使用它之前，请根据你的情况进行修改。

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

- 如果你有一个多节点的 ZooKeeper，修改 `zookeeper_uri`部分为 ZooKeeper 集群的节点和端口列表：

  ```json
      "zookeeper": {
          "zookeeper_uri": "ip://10.100.2.11:2181,10.100.2.12:2181,10.100.2.13:2181",
          "timeout": "30s"
      }
  ```

- 所有属性的详细解释可以在[集群配置](https://logdevice.io/docs/Config.html) 文档中找到。

## 存储配置文件

你可以将配置文件存储在 ZooKeeper 中，或存储在每个存储节点上。

### 在 ZooKeeper 中存储配置文件

假设你的一个 ZooKeeper 节点上有一个路径为 `~/logdevice.conf` 的配置文件。

通过运行以下命令将配置文件保存到 ZooKeeper 中：

```shell
docker exec zookeeper zkCli.sh create /logdevice.conf "`cat ~/logdevice.conf`"
```

通过以下命令验证创建是否成功：

```shell
docker exec zookeeper zkCli.sh get /logdevice.conf
```

## 配置 HStore 集群

对于存储在 ZooKeeper 中的配置文件，假设配置文件中 `zookeeper_uri` 字段的值是 `"ip:/10.100.2.11:2181"` ，ZooKeeper中配置文件的路径是 `/logdevice.conf` 。

对于存储在每个节点上的配置文件，假设你的文件路径是 `/data/logdevice/logdevice.conf'。

### 在单个节点上启动 admin 服务器

- 配置文件存储在 ZooKeeper 中：

  ```shell
  docker run --rm -d --name storeAdmin --network host -v /data/logdevice:/data/logdevice \
          hstreamdb/hstream:latest /usr/local/bin/ld-admin-server \
          --config-path zk:10.100.2.11:2181/logdevice.conf \
          --enable-maintenance-manager \
          --maintenance-log-snapshotting \
          --enable-safety-check-periodic-metadata-update
  ```

  + 如果你有一个多节点的 ZooKeeper，请将`--config-path`替换为：
    `--config-path zk:10.100.2.11:2181,10.100.2.12:2181,10.100.2.13:2181/logdevice.conf`

- 存储在每个节点的配置文件：

  更改 `--config-path` 参数为 `--config-path /data/logdevice/logdevice.conf`

### 在每个节点上启动 logdeviced

- 存储在 ZooKeeper 中的配置文件：

  ```shell
  docker run --rm -d --name hstore --network host -v /data/logdevice:/data/logdevice \
          hstreamdb/hstream:latest /usr/local/bin/logdeviced \
          --config-path zk:10.100.2.11:2181/logdevice.conf \
          --name store-0 \
          --address 192.168.0.3 \
          --local-log-store-path /data/logdevice
  ```

  + 对于每个节点，你应该将`--name`更新为一个不同的值，并将`--address`更新为该节点的 IP 地址。

- 存储在每个节点的配置文件：

  更改 `--config-path` 参数为 `--config-path /data/logdevice/logdevice.conf`

### Bootstrap 集群

在启动管理服务器和每个存储节点的 logdeviced 之后，现在我们可以 bootstrap 我们的集群。

在管理服务器节点上，运行。

```shell
docker exec storeAdmin hadmin store nodes-config bootstrap --metadata-replicate-across 'node:3'
```

你应该看到像这样的信息：

```shell
Successfully bootstrapped the cluster, new nodes configuration version: 7
Took 0.019s
```

你可以通过运行以下命令来检查集群的状态：

```shell
docker exec storeAdmin hadmin store status
```

而结果应该是：

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

现在我们完成了对 `HStore` 集群的设置。

## 配置 HServer 集群

要启动一个单一的 `HServer` 实例，你可以修改启动命令以适应你的情况。

```shell
docker run -d --name hstream-server --network host \
        hstreamdb/hstream:latest /usr/local/bin/hstream-server \
        --bind-address $SERVER_HOST \
        --advertised-address $SERVER_HOST \
        --seed-nodes $SERVER_HOST \
        --metastore-uri zk://$ZK_ADDRESS \
        --store-config zk:$ZK_ADDRESS/logdevice.conf \
        --store-admin-host $ADMIN_HOST \
        --replicate-factor 3 \
        --server-id 1
```

- `$SERVER_HOST`：你的服务器节点的主机 IP 地址，例如 `192.168.0.1`。
- `metastore-uri`: 你的元信息存储 HMeta 地址，例如使用 `zk://$ZK_ADDRESS` 指定 zookeeper 存储元数据。同时实现性支持使用 rqlite `rq://$RQ_ADDRESS`。
- `$ZK_ADDRESS` ：你的 ZooKeeper 集群地址列表，例如 `10.100.2.11:2181,10.100.2.12:2181,10.100.2.13:2181`。
- `--store-config`：你的 `HStore` 配置文件的路径。应该与启动 `HStore` 集群 `--config-path` 参数的值一致。
- `--store-admin-host`：`HStore Admin Server` 节点的 IP 地址。
- `--server-id`：你应该为每个服务器实例设置一个的**唯一标识符**

你可以以同样的方式在不同的节点上启动多个服务器实例。

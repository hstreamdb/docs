# 用 docker 启动一个本地 HServer 集群

## 前提条件

### 运行中的 HStream 存储单位

阅读并按照 [用 Docker 快速上手章节](./quickstart-with-docker.md) 启动，
并确保你的 HStore (HStream Storage) 正确运行.

### 启动 ZooKeeper 服务

一个运行中的 zookeeper 是启动集群的必要条件。

```sh
docker run --rm -d --network host --name some-zookeeper-demo zookeeper
```

## 用 docker 启动一个本地的 HStream-Server 集群

::: warning
请勿在您的生产环境中使用该配置！
:::

你可以手动以[用 Docker 快速上手章节](./quickstart-with-docker.md)中提到的方式启三个 server， 只要确保 zkuri 这个参数是一样的，就可以启动一个具有三个节点的集群。

注意各个节点的以下几个参数需要保证是独语无二的：

- **server-id     : server 的整数标识符。**
- **port          : 客户端连接的端口号。**
- **internal-port : 服务端互相通信的内部端口号。**

比如，分别运行以下几个命令来启动一个具有三个节点的集群：

```sh
docker run -it --rm --name some-hstream-server-1 -v /dbdata:/data/store --network host hstreamdb/hstream hstream-server --store-config /data/store/logdevice.conf --zkuri 127.0.0.1:2181 --port 6570 --internal-port 6571 --server-id 1
```

```sh
docker run -it --rm --name some-hstream-server-2 -v /dbdata:/data/store --network host hstreamdb/hstream hstream-server --store-config /data/store/logdevice.conf --zkuri 127.0.0.1:2181 --port 6572 --internal-port 6573 --server-id 2
```

```sh
docker run -it --rm --name some-hstream-server-3 -v /dbdata:/data/store --network host hstreamdb/hstream hstream-server --store-config /data/store/logdevice.conf --zkuri 127.0.0.1:2181 --port 6574 --internal-port 6575 --server-id 3
```

### 启动一个 HStreamDB 的命令行界面

和当你只有一个单机版 HStream 一样来[启动一个命令行界面窗口](./quickstart-with-docker.md#start-hstreamdb-s-interactive-sql-cli)：

```sh
docker run -it --rm --name some-hstream-cli -v /dbdata:/data/store --network host hstreamdb/hstream hstream-client --port 6570 --client-id 1
```

集群模式下的 HStream 并不会影响 CLI 的使用方式

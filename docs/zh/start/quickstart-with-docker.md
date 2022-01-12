# 使用 Docker 快速上手

## 前提条件

启动 HStream 需要一个内核版本不小于 Linux 4.14 的操作系统。

## 安装

### 安装 docker

::: tip
如果您已经有一安装好的 Docker，可以跳过这一步
:::

浏览查阅 [Install Docker Engine](https://docs.docker.com/engine/install/)，然后安装到您的操作系统上。安装时，请注意检查您的设备是否满足所有的前置条件。

确认 Docker daemon 正在运行：

```sh
docker version
```

::: tip
在 Linux，Docker 需要 root 权限。
当然，你也可以以非 root 用户的方式运行 Docker，详情可以参考 [Post-installation steps for Linux][non-root-docker]。
:::

### 安装 docker-compose

::: tip
如果您已经有一安装好的 Docker，可以跳过这一步
:::

浏览查阅 [Install Docker Compose](https://docs.docker.com/compose/install/)，然后安装到您的操作系统上。安装时，请注意检查您的设备是否满足所有的前置条件。

```sh
docker compose
```

## 在 Docker 容器里，启动一个本地独立的 HStreamDB

::: warning
请不要在生产环境中使用以下配置
:::

### 创建一个存储 db 数据的文件目录

```sh
mkdir /data/store
```

如果您是非 root 用户，您将无法在根（root）路径下创建文件夹，
那么您可以在任意位置创建该文件夹

```sh
mkdir $HOME/data/store

# 确保设置了环境变量 DATA_DIR
export DATA_DIR=$HOME/data/store
```

## 启动 HStreamDB 服务和存储模块

创建一个 quick-start.yaml,
可以直接[下载][quick-start.yaml]或者复制以下内容:

```yaml
## quick-start.yaml
```

在同一个文件夹中运行：

```sh
docker-compose -f quick-start.yaml up
```

如果出现如下信息，表明现在已经有了一个运行中的 HServer：

```
hserver_1    | [INFO][2021-11-22T09:15:18+0000][app/server.hs:137:3][thread#67]************************
hserver_1    | [INFO][2021-11-22T09:15:18+0000][app/server.hs:145:3][thread#67]Server started on port 6570
hserver_1    | [INFO][2021-11-22T09:15:18+0000][app/server.hs:146:3][thread#67]*************************
```

::: tip
当然，你也可以选择在后台启动
:::

```sh
docker-compose -f quick-start.yaml up -d
```

并且可以通过以下命令展示 logs ：

```
docker-compose -f quick-start.yaml logs -f hserver
```

## 启动 HStreamDB 的 SQL 命令行界面

```sh
docker run -it --rm --name some-hstream-cli --network host hstreamdb/hstream:v0.6.1 hstream-client --port 6570 --client-id 1
```

如果所有的步骤都正确运行，您将会进入到命令行界面，并且能看见一下帮助信息：

```
      __  _________________  _________    __  ___
     / / / / ___/_  __/ __ \/ ____/   |  /  |/  /
    / /_/ /\__ \ / / / /_/ / __/ / /| | / /|_/ /
   / __  /___/ // / / _, _/ /___/ ___ |/ /  / /
  /_/ /_//____//_/ /_/ |_/_____/_/  |_/_/  /_/

Command
  :h                           To show these help info
  :q                           To exit command line interface
  :help [sql_operation]        To show full usage of sql statement

SQL STATEMENTS:
  To create a simplest stream:
    CREATE STREAM stream_name;

  To create a query select all fields from a stream:
    SELECT * FROM stream_name EMIT CHANGES;

  To insert values to a stream:
    INSERT INTO stream_name (field1, field2) VALUES (1, 2);

>
```

## 创建一个 stream

首先，我们可以用 `CREATE STREAM` 语句创建一个名为 demo 的 stream.

```sql
CREATE STREAM demo;
```

## 对这个 stream 执行一个持久的查询操作

现在，我们可以通过 `SELECT` 在这个 stream 上执行一个持久的查询。

这个查询的结果将被直接展现在 CLI 中。

以下查询任务会输出所有 `demo` stream 中具有 humidity 大于 70 的数据。

```sql
SELECT * FROM demo WHERE humidity > 70 EMIT CHANGES;
```

现在看起来好像无事发生。这是因为从这个任务执行开始，还没有数据被写入到 demo 中。
接下来，我们会写入一些数据，然后符合条件的数据就会被以上任务输出。

## 启动另一个 CLI 窗口

我们可以利用这个新的 CLI 来插入数据：

```sh
docker exec -it some-hstream-cli hstream-client --port 6570 --client-id 2
```

## 向 stream 中写入数据

输入并运行以下所有 `INSERT` 语句，然后关注我们之创建的 CLI 窗口。

```sql
INSERT INTO demo (temperature, humidity) VALUES (22, 80);
INSERT INTO demo (temperature, humidity) VALUES (15, 20);
INSERT INTO demo (temperature, humidity) VALUES (31, 76);
INSERT INTO demo (temperature, humidity) VALUES ( 5, 45);
INSERT INTO demo (temperature, humidity) VALUES (27, 82);
INSERT INTO demo (temperature, humidity) VALUES (28, 86);
```

不出意外的话，你将看到以下的结果。

```
{"temperature":22,"humidity":80}
{"temperature":31,"humidity":76}
{"temperature":27,"humidity":82}
{"temperature":28,"humidity":86}
```

## 用 docker 启动一个本地的 HStream-Server 3节点集群

::: warning
请勿在您的生产环境中使用该配置！
:::

如果你跳过了上面启动单机的教程，可以直接跳到这一部分，[用 docker-compose 拉起一个本地集群](#用-docker-compose-拉起一个本地集群) instead.

### 假设已经有了一个根据以上步骤启动的单机节点

你可以手动启动剩余的两个节点，只要连上同一个 Zookeeper 服务，就能组成一个集群。
注意以下几个配置需要保证不要撞：

- **server-id     : the id has to be an integer. This is the identifier of every server.**
- **port          : the port number that client connects to.**
- **internal-port : the internal channel for server communication.**

然后你可以运行如下的指令，用 docker 来启动剩余的两个节点。
如果你更改了[这个文件][quick-start.yaml]，请确保在以下指令中，
也作出相应的修改。

```sh
docker run -it --rm --name some-hstream-server-1 -v $DATA_DIR:/data/store --network hstream-quickstart hstreamdb/hstream:v0.6.1 hstream-server --store-config /data/store/logdevice.conf --zkuri 127.0.0.1:2181 --port 6580 --internal-port 6581 --server-id 101
```

```sh
docker run -it --rm --name some-hstream-server-2 -v $DATA_DIR:/data/store --network hstream-quickstart hstreamdb/hstream:v0.6.1 hstream-server --store-config /data/store/logdevice.conf --zkuri 127.0.0.1:2181 --port 6590 --internal-port 6591 --server-id 102
```

### 用 docker-compose 拉起一个本地集群

下载 [quick-start.yaml]，然后 **原模原样的将以下配置粘贴在 service 一栏**:

```yaml
  hserver0:
    image: hstreamdb/hstream
    depends_on:
      - zookeeper
      - hstore
    ports:
      - "127.0.0.1:6580:6580"
    expose:
      - 6580
    networks:
      - hstream-quickstart
    volumes:
      - ${DATA_DIR:-/data/store}:/data/store
    command:
      - bash
      - "-c"
      - |
        set -e
        /usr/local/script/wait-for-storage.sh hstore 6440 zookeeper 2181 600 \
        /usr/local/bin/hstream-server \
        --host 0.0.0.0 --port 6580 \
        --internal-port 6581 \
        --address $$(hostname -I | awk '{print $$1}') \
        --server-id 101 \
        --zkuri zookeeper:2181 \
        --store-config /data/store/logdevice.conf \
        --store-admin-host hstore --store-admin-port 6440 \
        --replicate-factor 3 \

  hserver1:
    image: hstreamdb/hstream
    depends_on:
      - zookeeper
      - hstore
    ports:
      - "127.0.0.1:6590:6590"
    expose:
      - 6590
    networks:
      - hstream-quickstart
    volumes:
      - ${DATA_DIR:-/data/store}:/data/store
    command:
      - bash
      - "-c"
      - |
        set -e
        /usr/local/script/wait-for-storage.sh hstore 6440 zookeeper 2181 600 \
        /usr/local/bin/hstream-server \
        --host 0.0.0.0 --port 6590 \
        --internal-port 6591 \
        --address $$(hostname -I | awk '{print $$1}') \
        --server-id 102 \
        --zkuri zookeeper:2181 \
        --store-config /data/store/logdevice.conf \
        --store-admin-host hstore --store-admin-port 6440 \
        --replicate-factor 3 \
```

这样，就能很轻松地用 docker-compose 拉起来了:

```sh
docker-compose -f quick-start.yaml up -d
docker-compose -f quick-start.yaml logs -f hserver hserver0 hserver1
```

## 运用 HStreamDB's interactive SQL CLI

[启动命令行界面](#启动-HStreamDB-的-SQL-命令行界面)的方式和原来相同。
HStreamDB 服务是集群还是单机并不会影响 CLI 的使用方式

```sh
docker run -it --rm --name some-hstream-cli --network host hstreamdb/hstream:v0.6.1 hstream-client --port 6570 --client-id 1
```

[non-root-docker]: https://docs.docker.com/engine/install/linux-postinstall/#manage-docker-as-a-non-root-user
[quick-start.yaml]: https://github.com/hstreamdb/hstream/raw/main/docker/quick-start.yaml

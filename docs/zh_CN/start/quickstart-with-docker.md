HStreamDB 快速上手（基于docker）
============================

## 安装

### 安装 docker

!!! note
    如果您已经安装了 docker ，可以跳过这一步。

查阅 [Install Docker Engine](https://docs.docker.com/engine/install/) ，然后安装到您的操作系统。请仔细对照，确保您已安装所有的依赖。

确认 Docker 是否已经启动：

```sh
docker version
```

!!! Tips
    在 Linux 上， Docker 需要 root 权限。 您可以选择以非 root 用户启动 docker，查阅 see [Post-installation steps for Linux][non-root-docker].

###    拉取 docker 镜像

```bash
docker pull hstreamdb/logdevicedocker pull hstreamdb/hstream
```

##    在 docker 中启动一个本地的 HStream Server

!!! warning
    请勿在实际生产环境下使用此配置！

### 创建一个目录用于存储数据

```bash
mkdir ./dbdata
```

### 启动 HStream Storage

```bash
docker run -td --rm --name some-hstream-store -v dbdata:/data/store --network host hstreamdb/logdevice ld-dev-cluster --root /data/store --use-tcp
```

### 启动 HStreamDB Server

```bash
docker run -it --rm --name some-hstream-server -v dbdata:/data/store --network host hstreamdb/hstream hstream-server --port 6570 -l /data/store/logdevice.conf
```

## 启动 HStreamDB CLI

```bash
docker run -it --rm --name some-hstream-cli -v dbdata:/data/store --network host hstreamdb/hstream hstream-client --port 6570
```

如果一切正常的话，进入 CLI 后你会看到类似下面的信息：

```
     / / / / ___/_  __/ __ \/ ____/   |  /  |/  /
    / /_/ /\__ \ / / / /_/ / __/ / /| | / /|_/ /
   / __  /___/ // / / _, _/ /___/ ___ |/ /  / /
  /_/ /_//____//_/ /_/ |_/_____/_/  |_/_/  /_/

Command
  :h                        help command
  :q                        quit cli
  <sql>                     run sql
>
```

   **创建数据流**

下面我们将用 `CREATE STREAM` 语句创建一个新的数据流，

```sql
CREATE STREAM demo WITH (FORMAT = "JSON");
```



在 CLI 中执行上述语句后，你会看到类似下面的信息，表示执行成功。

```json
Right
    ( CreateTopic
        { taskid = 0
        , tasksql = "CREATE STREAM demo WITH (FORMAT = "JSON");"
        , taskStream = "demo"
        , taskState = Finished
        , createTime = 2021 - 02 - 04 09 : 07 : 25.639197201 UTC
        }
    )
```

   **执行一个持续查询**

我们使用 `SELECT` 语句来对数据流进行实时处理和分析。在 CLI 中执行以下语句，

```sql
SELECT * FROM demo WHERE humidity > 70 EMIT CHANGES;
```

执行完成后会发现并没有产生任何结果，这是正常的， 因为现在数据流中还没有任何数据， 接下来我们将向数据流中写入一些数据并观察结果。另外，请注意这个 `SELECT` 语句不同于普通数据库的`SELECT` 在一次执行完毕后返回， 相反它会一直执行下去， 除非你显式的终止它。

###    开启一个新的 CLI 会话

```bash
docker exec -it some-hstream-cli hstream-client --port 6570
```

###    向数据流中插入数据

执行以下 `INSERT` 语句向数据流中写入数据，

```sql
INSERT INTO demo (temperature, humidity) VALUES (22, 80);
INSERT INTO demo (temperature, humidity) VALUES (15, 20);
INSERT INTO demo (temperature, humidity) VALUES (31, 76);
INSERT INTO demo (temperature, humidity) VALUES ( 5, 45);
INSERT INTO demo (temperature, humidity) VALUES (27, 82);
INSERT INTO demo (temperature, humidity) VALUES (28, 86);
```

如果一切运行正常的话， 你将会在刚才的 CLI 窗口看到以下实时的输出：

```
{"temperature":22,"humidity":80}
{"temperature":31,"humidity":76}
{"temperature":27,"humidity":82}
{"temperature":28,"humidity":86}
```








[non-root-docker]: https://docs.docker.com/engine/install/linux-postinstall/#manage-docker-as-a-non-root-user

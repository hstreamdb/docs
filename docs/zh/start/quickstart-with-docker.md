# 使用 Docker-Compose 快速上手

## 前提条件

启动 HStream 需要一个内核版本不小于 Linux 4.14 的操作系统。

::: tip
如果遇到无法使用 4.14 或以上版本 Linux 内核的情况，
可以给 HStore 添加一个 `--enable-dscp-reflection=false` 选项。
:::

## 安装

### 安装 docker

::: tip
如果您已经有一安装好的 Docker，可以跳过这一步
:::

浏览查阅 [Install Docker Engine](https://docs.docker.com/engine/install/)，然后
安装到您的操作系统上。安装时，请注意检查您的设备是否满足所有的前置条件。

确认 Docker daemon 正在运行：

```sh
docker version
```

::: tip
在 Linux，Docker 需要 root 权限。当然，你也可以以非 root 用户的方式运行
Docker，详情可以参考 [Post-installation steps for Linux][non-root-docker]。
:::

### 安装 docker-compose

::: tip
如果您已经有一安装好的 Docker Compose，可以跳过这一步
:::

浏览查阅 [Install Docker Compose](https://docs.docker.com/compose/install/)，然
后安装到您的操作系统上。安装时，请注意检查您的设备是否满足所有的前置条件。

```sh
docker-compose -v
```

## 启动 HStreamDB 服务

::: warning
请不要在生产环境中使用以下配置
:::

创建一个 quick-start.yaml, 可以直接[下载][quick-start.yaml]或者复制以下内容:

```yaml
# quick-start.yaml
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
docker run -it --rm --name some-hstream-cli --network host hstreamdb/hstream:latest hstream --port 6570 sql
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
docker exec -it some-hstream-cli hstream --port 6570 sql
```

## 向 stream 中写入数据

输入并运行以下所有 `INSERT` 语句，然后关注我们之前创建的 CLI 窗口。

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

[non-root-docker]:
  https://docs.docker.com/engine/install/linux-postinstall/#manage-docker-as-a-non-root-user
[quick-start.yaml]:
  https://raw.githubusercontent.com/hstreamdb/docs/main/assets/quick-start.yaml

# Quickstart with Docker

## Installation

### Install docker

!!! note
If you have already installed docker, you can skip this step.

See [Install Docker Engine](https://docs.docker.com/engine/install/),
and install it for your operating system. Please carefully check that you
meet all prerequisites.

Confirm that the Docker daemon is running:

```sh
docker version
```

!!! Tips
On Linux, Docker needs root privileges. You can also run Docker as
a non-root user, see [Post-installation steps for Linux][non-root-docker].

### Pull docker images

```sh
docker pull hstreamdb/logdevice
docker pull hstreamdb/hstream
```

## Start a local standalone HStream-Server in Docker

!!! warning
Do NOT use this configuration in your production environment!

### Create a directory for storing db datas

```sh
mkdir ./dbdata
```

### Start local logdevice cluster

```sh
docker run -td --rm --name some-hstream-store -v dbdata:/data/store --network host hstreamdb/logdevice ld-dev-cluster --root /data/store --use-tcp
```

### Start HStreamDB Server

```sh
docker run -it --rm --name some-hstream-server -v dbdata:/data/store --network host hstreamdb/hstream hstream-server --port 6570 -l /data/store/logdevice.conf
```

## Start HStreamDB's interactive SQL CLI

```sh
docker run -it --rm --name some-hstream-cli -v dbdata:/data/store --network host hstreamdb/hstream hstream-client --port 6570
```

If everything works fine, you will enter an interactive CLI and see help information like

```
Start HStream-Cli!
Command
  :h                        help command
  :q                        quit cli
  show queries              list all queries
  terminate query <taskid>  terminate query by id
  terminate query all       terminate all queries
  <sql>                     run sql

>
```

## Create a stream

What we are going to do first is create a stream by `CREATE STREAM` query.

The `FORMAT` parameter after `WITH` specifies the format of data in the stream. Note that only `"JSON"` format is supported now.

```sql
CREATE STREAM demo WITH (FORMAT = "JSON");
```

Copy and paste this query into the interactive CLI session, and press enter to execute the statement. If everything works fine, you will get something like

```Haskell
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

which means the query is successfully executed.

## Run a continuous query over the stream

Now we can run a continuous query over the stream we just created by `SELECT` query.

The query will output all records from the `demo` stream whose humidity is above 70 percent.

```sql
SELECT * FROM demo WHERE humidity > 70 EMIT CHANGES;
```

It seems that nothing happened. But do not worry because there is no data in the stream now. Next, we will fill the stream with some data so the query can produce output we want.

## Start another CLI session

Start another CLI session, this CLI will be used for inserting data into the stream.

```sh
docker exec -it some-hstream-cli hstream-client --port 6570
```

## Insert data into the stream

Run each of the given `INSERT` query in the new CLI session and keep an eye on the CLI session created in (2).

```sql
INSERT INTO demo (temperature, humidity) VALUES (22, 80);
INSERT INTO demo (temperature, humidity) VALUES (15, 20);
INSERT INTO demo (temperature, humidity) VALUES (31, 76);
INSERT INTO demo (temperature, humidity) VALUES ( 5, 45);
INSERT INTO demo (temperature, humidity) VALUES (27, 82);
INSERT INTO demo (temperature, humidity) VALUES (28, 86);
```

If everything works fine, the continuous query will output matching records in real time:

```
{"temperature":22,"humidity":80}
{"temperature":31,"humidity":76}
{"temperature":27,"humidity":82}
{"temperature":28,"humidity":86}
```

[non-root-docker]: https://docs.docker.com/engine/install/linux-postinstall/#manage-docker-as-a-non-root-user

# Quickstart with Docker

## Requirement

Start HStream requires an operating system kernel version greater than at least Linux 4.14

## Installation

### Install docker

::: tip
If you have already installed docker, you can skip this step.
:::

See [Install Docker Engine](https://docs.docker.com/engine/install/), and
install it for your operating system. Please carefully check that you have met all
prerequisites.

Confirm that the Docker daemon is running:

```sh
docker version
```

::: tip
On Linux, Docker needs root privileges. You can also run Docker as a
non-root user, see [Post-installation steps for Linux][non-root-docker].
:::

### Install docker compose

::: tip
If you have already installed docker compose, you can skip this step.
:::

See [Install Docker Compose](https://docs.docker.com/compose/install/), and
install it for your operating system. Please carefully check that you met all
prerequisites.

```sh
docker-compose version
```

## Start a local standalone HStream-Server in Docker

::: warning
Do NOT use this configuration in your production environment!
:::

## Create a directory for storing db datas

```sh
mkdir /data/store
```

::: tip
If you are a non-root user, that you can not create directory under the
root.

You can create it anywhere as you can.
:::

```sh
mkdir $HOME/data/store

# make sure that you have set the environment variable DATA_DIR
export DATA_DIR=$HOME/data/store
```

## Start HStreamDB Server and Store

Create a docker-compose.yaml file for docker compose,
you can [download][quick-start.yaml] or paste the following contents:

```yaml
## docker-compose.yaml
```

then run:

```sh
docker-compose -f quick-start.yaml up
```

If you see some thing like this, then you have a running hstream:

```
hserver_1    | [INFO][2021-11-22T09:15:18+0000][app/server.hs:137:3][thread#67]************************
hserver_1    | [INFO][2021-11-22T09:15:18+0000][app/server.hs:145:3][thread#67]Server started on port 6570
hserver_1    | [INFO][2021-11-22T09:15:18+0000][app/server.hs:146:3][thread#67]*************************
```

::: tip
You can also run in background.
:::

```sh
docker-compose -f quick-start.yaml up -d
```

And if you want to show logs of server, run:

```sh
docker-compose -f quick-start.yaml logs -f hserver
```

## Start HStreamDB's interactive SQL CLI

```sh
docker run -it --rm --name some-hstream-cli --network host hstreamdb/hstream hstream-client --port 6570 --client-id 1
```

If everything works fine, you will enter an interactive CLI and see help
information like

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

## Create a stream

What we are going to do first is create a stream by `CREATE STREAM` statement.

```sql
CREATE STREAM demo;
```

## Run a continuous query over the stream

Now we can run a continuous query over the stream we just created by `SELECT`
query.

The query will output all records from the `demo` stream whose humidity is above
70 percent.

```sql
SELECT * FROM demo WHERE humidity > 70 EMIT CHANGES;
```

It seems that nothing happened. But do not worry because there is no data in the
stream now. Next, we will fill the stream with some data so the query can
produce output we want.

## Start another CLI session

Start another CLI session, this CLI will be used for inserting data into the
stream.

```sh
docker exec -it some-hstream-cli hstream-client --port 6570 --client-id 2
```

## Insert data into the stream

Run each of the given `INSERT` statement in the new CLI session and keep an eye on
the CLI session created in (2).

```sql
INSERT INTO demo (temperature, humidity) VALUES (22, 80);
INSERT INTO demo (temperature, humidity) VALUES (15, 20);
INSERT INTO demo (temperature, humidity) VALUES (31, 76);
INSERT INTO demo (temperature, humidity) VALUES ( 5, 45);
INSERT INTO demo (temperature, humidity) VALUES (27, 82);
INSERT INTO demo (temperature, humidity) VALUES (28, 86);
```

If everything works fine, the continuous query will output matching records in
real time:

```json
{"temperature":22,"humidity":80}
{"temperature":31,"humidity":76}
{"temperature":27,"humidity":82}
{"temperature":28,"humidity":86}
```

## Start a 3-node local HStream-Server Cluster in Docker

::: warning
Do NOT use this configuration in your production environment!
:::

If you did not follow quick start and have not yet got a running local HStream-Server,
read this [section](#start-with-docker-compose) instead.

### If you already have a running standalone HStream-Server

You can manually start the other 2 servers and pass the same zkuri as the running server
to create a hserver cluster.
Note that every server needs the following options to be unique to work properly:

- **server-id     : the id has to be an integer. This is the identifier of every server.**
- **port          : the port number that client connects to.**
- **internal-port : the internal channel for server communication.**

For example, run the following commands to start a cluster with 3 nodes,
only if you followed quick start and did not change the [config][quick-start.yaml] :

```sh
docker run -it --rm --name some-hstream-server-1 -v $DATA_DIR:/data/store --network hstream-quickstart hstreamdb/hstream hstream-server --store-config /data/store/logdevice.conf --zkuri 127.0.0.1:2181 --port 6580 --internal-port 6581 --server-id 101
```

```sh
docker run -it --rm --name some-hstream-server-2 -v $DATA_DIR:/data/store --network hstream-quickstart hstreamdb/hstream hstream-server --store-config /data/store/logdevice.conf --zkuri 127.0.0.1:2181 --port 6590 --internal-port 6591 --server-id 102
```

### Start with docker compose

Download [quick-start.yaml] and **copy the exact following contents under services section**:

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

You can easily get a cluster with:

```sh
docker-compose -f quick-start.yaml up -d
docker-compose -f quick-start.yaml logs -f hserver hserver0 hserver1
```

## Use HStreamDB's interactive SQL CLI

[Start a cli session](#start-hstreamdb-s-interactive-sql-cli) in the similar way as when you have a standalone server.
An HStream Server Cluster does not affect how you use CLI.

```sh
docker run -it --rm --name some-hstream-cli --network host hstreamdb/hstream hstream-client --port 6570 --client-id 1
```

[non-root-docker]: https://docs.docker.com/engine/install/linux-postinstall/#manage-docker-as-a-non-root-user
[quick-start.yaml]: https://github.com/hstreamdb/hstream/raw/main/docker/quick-start.yaml

Quickstart with Docker
======================

## Installation

### 1. Install docker

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

### 2. Pull image

Pull the latest released image of HStreamDB from Docker Hub:

```sh
docker pull hstreamdb/hstream
```


## Start HStreamDB Server

### 1. Create a bridge network

The bridge network will enable the containers to communicate as a single
cluster while keeping them isolated from external networks.

```sh
docker network create -d bridge some-hstreamdb-net
```

### 2. Start the server

```sh
docker run -d --network some-hstreamdb-net --name some-hstreamdb hstreamdb/hstream
```

Datas are stored in the `VOLUME /data`, which can be used with
`-v /your/host/dir:/data` (see [use volumes](https://docs.docker.com/storage/volumes/)).

Default lisenling port is `6560`, you can expose the port outside of your host (e.g., via `-p` on `docker run`).

!!! warning
    If you expose the port outside of your host, it will be open to anyone.


## Connecting via redis-cli

```sh
docker run -it --rm --network some-hstreamdb-net redis redis-cli -h some-hstreamdb -p 6560
```

```
some-hstreamdb:6560> xadd users * name alice age 20
"1599444243554-0"
some-hstreamdb:6560> xadd users * name bob age 20
"1599444249940-0"
some-hstreamdb:6560> xrange users - +
1) 1) "1599444243554-0"
   2) 1) "name"
      2) "alice"
      3) "age"
      4) "20"
2) 1) "1599444249940-0"
   2) 1) "name"
      2) "bob"
      3) "age"
      4) "20"
```


## Building from source

See [this document](../development/build-from-source.md) for more details.




[non-root-docker]: https://docs.docker.com/engine/install/linux-postinstall/#manage-docker-as-a-non-root-user



--------------------------------------------------------------------------------
## Quickstart

### 1. Start HStreamDB's Server

[TODO]

### 2. Start HStreamDB's CLI

[TODO]

If everything works fine, you will enter an interactive CLI and see help information like

```
Start HStream-Cli!
Command
  :h                     help command
  :q                     quit cli
  show tasks             list all tasks
  delete query <taskid>  delete query by id
  deletet query all      delete all queries
  <sql>                  run sql

>
```

### 3. Create a stream

What we are going to do first is create a stream by `CREATE STREAM` query.

The `FORMAT` parameter after `WITH` specifies the format of data in the stream. Note that only `"JSON"` format is supported now.

```sql
CREATE STREAM weather WITH (FORMAT = "JSON");
```

Copy and paste this query into the interactive CLI session, and press enter to execute the statement. If everything works fine, you will get something like

```Haskell
Right
    ( CreateTopic
        { taskid = 0
        , tasksql = "CREATE STREAM weather WITH (FORMAT = "JSON");"
        , taskTopic = "weather"
        , taskState = Finished
        , createTime = 2021 - 02 - 04 09 : 07 : 25.639197201 UTC
        }
    )
```
which means the query is successfully executed.


### 4. Run a continuous query over the stream

Now we can run a continuous query over the stream we just created by `SELECT` query.

The query will output all records from the `weather` stream whose humidity is above 70 percent.

```sql
SELECT * FROM weather WHERE humidity > 70;
```

It seems that nothing happened. But do not worry because there is no data in the stream now. Next, we will fill the stream with some data so the query can produce output we want.

### 5. Start another CLI session

Start another CLI session like what we did in (2). This CLI will be used for inserting data into the stream.

### 6. Insert data into the stream

Run each of the given `INSERT` query in the new CLI session and keep an eye on the CLI session created in (2).

```sql
INSERT INTO weather (cityId, temperature, humidity) VALUES (1, 22, 80);
INSERT INTO weather (cityId, temperature, humidity) VALUES (2, 15, 20);
INSERT INTO weather (cityId, temperature, humidity) VALUES (3, 31, 76);
INSERT INTO weather (cityId, temperature, humidity) VALUES (4,  5, 45);
INSERT INTO weather (cityId, temperature, humidity) VALUES (5, 27, 82);
INSERT INTO weather (cityId, temperature, humidity) VALUES (6, 28, 86);
```

If everything works fine, the continuous query will output matching records in real time.

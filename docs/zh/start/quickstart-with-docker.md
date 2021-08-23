# Quickstart with Docker

## Requirement

Start HStream requires an operating system kernel version greater than at least Linux 4.14

## Installation

### Install docker

!!! note

    If you have already installed docker, you can skip this step.

See [Install Docker Engine](https://docs.docker.com/engine/install/), and
install it for your operating system. Please carefully check that you meet all
prerequisites.

Confirm that the Docker daemon is running:

```sh
docker version
```

!!! Tips

    On Linux, Docker needs root privileges. You can also run Docker as a
    non-root user, see [Post-installation steps for Linux][non-root-docker].

### Pull docker images

```sh
docker pull hstreamdb/hstream
```

## Start a local standalone HStream-Server in Docker

!!! Warning 

	Do NOT use this configuration in your production environment!

### Create a directory for storing db datas

```sh
mkdir /dbdata
```

!!! Tips

    If you are a non-root user, that you can not create directory under the
    root, you can also create it anywhere as you can, but you need to pass the
    absolute data path to docker volume arguments.

### Start HStream Storage

```sh
docker run -td --rm --name some-hstream-store -v /dbdata:/data/store --network host hstreamdb/hstream ld-dev-cluster --root /data/store --use-tcp
```

### Start HStreamDB Server

```sh
docker run -it --rm --name some-hstream-server -v /dbdata:/data/store --network host hstreamdb/hstream hstream-server --port 6570 --store-config /data/store/logdevice.conf
```

## Start HStreamDB's interactive SQL CLI

```sh
docker run -it --rm --name some-hstream-cli -v /dbdata:/data/store --network host hstreamdb/hstream hstream-client --port 6570
```

If everything works fine, you will enter an interactive CLI and see help
information like

```
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

What we are going to do first is create a stream by `CREATE STREAM` query.

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
docker exec -it some-hstream-cli hstream-client --port 6570
```

## Insert data into the stream

Run each of the given `INSERT` query in the new CLI session and keep an eye on
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

```
{"temperature":22,"humidity":80}
{"temperature":31,"humidity":76}
{"temperature":27,"humidity":82}
{"temperature":28,"humidity":86}
```

[non-root-docker]:
  https://docs.docker.com/engine/install/linux-postinstall/#manage-docker-as-a-non-root-user

# HStream IO User Guides

This document will show you how to use HStream IO through a case that synchronizes records from a mysql table to a postgresql table,
you will learn:

- How to create a source connector that synchronizes records from a mysql table to a HStream stream.
- How to create a sink connector that synchronizes records from the HStream stream to a postgresql table.
- How to use HStream IO SQLs to manage the connectors.

## Setup a HStream Cluster

You can check [quick-start](https://hstream.io/docs/en/latest/start/quickstart-with-docker.html) to find how to setup a HStream cluster and connect to it.

## Setup a Mysql

Setup a mysql instance by docker:

```shell
docker run --network=hstream-quickstart --name mysql-s1 -e MYSQL_ROOT_PASSWORD=password -d mysql
```

Here we use the ``hstream-quickstart`` network if you setup your HStream cluster based on [quick-start](https://hstream.io/docs/en/latest/start/quickstart-with-docker.html) .

Connect to the mysql instance:

```shell
docker exec -it mysql-s1 mysql -uroot -h127.0.0.1 -P3306 -ppassword
```

Create a database ``d1``, a table ``person`` and insert some records:

```sql
create database d1;
use d1;
create table person (id int primary key, name varchar(256), age int);
insert into person values (1, "John", 20), (2, "Jack", 21), (3, "Ken", 33);
```

the table ``person`` must include a primary key, or ``DELETE`` statement may not be replicated correctly.

## Setup a Postgresql

Setup a postgresql instance by docker:

```shell
docker run --network=hstream-quickstart --name pg-s1 -e POSTGRES_PASSWORD=postgres -d postgres
```

Connect to the postgresql instance:

```shell
docker exec -it pg-s1 psql -h 127.0.0.1 -U postgres
```

``sink-postgresql`` doesn't support creating a table automatically yet,
so you need to create the database ``d1`` and the table ``person``:

```sql
create database d1;
\c d1;
create table person (id int primary key, name varchar(256), age int);
```

The table ``person`` must include an primary key.

## Download Connector Plugins

A connector plugin is a docker image,
so before you use the connectors,
you should download and update their plugins by ``docker pull``:

```shell
docker pull hstreamdb/connector:source-mysql
docker pull hstreamdb/connector:sink-postgresql
```

If your want to find more connectors, please check [Connectors](https://hstream.io/docs/en/latest/io/connectors.html) .

## Create Connectors

After connecting a HStream Server, you can use create source/sink connector SQLs to create connectors.

Connect to the HStream server:

```shell
docker run -it --rm --network host hstreamdb/hstream:v0.9.0 hstream sql --port 6570
```

Create a source connector:

```sql
create source connector source01 from mysql with ("host" = "mysql-s1", "port" = 3306, "user" = "root", "password" = "password", "database" = "d1", "table" = "person", "stream" = "stream01");
```

The source connector will run as a HStream IO task,
the task will continually synchronize data from mysql table ``d1.person`` to stream ``stream01`` as a synchronization service,
whenever you update records in mysql and the change events will be recorded in stream ``stream01`` if the connector is running,
you can use ``show connectors`` to check connectors and their status and use ``pause`` and ``resume`` to start/stop connector:

```sql
pause connector source01;
resume connector source01;
```

After restarting the connector task, the task will not fetch data from begin, instead, it will resume from the point that it paused.

Then you can create a sink connector that consumes the records from the stream ``stream01`` to postgresql table ``d1.public.person``:

```sql
create sink connector sink01 to postgresql with ("host" = "pg-s1", "port" = 5432, "user" = "postgres", "password" = "postgres", "database" = "d1", "table" = "person", "stream" = "stream01");
```

After running source01 and sink01 connectors, you get a synchronization service from mysql to postgresql.

You can use drop connector statement to delete the connectors:

```sql
drop connector source01;
drop connector sink01;
```

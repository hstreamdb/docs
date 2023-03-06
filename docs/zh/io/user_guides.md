# HStream IO User Guides

Data synchronization service is used to synchronize data between systems(e.g. databases) in real time,
which is useful for many cases, for example, MySQL is a widely-used database,
if your application is running on MySQL, and:

* You found its query performance is not enough.
    + You want to migrate your data to another database (e.g. PostgreSQL), but you need to switch your application seamlessly.
    + Your applications highly depended on MySQL, migrating is difficult, so you have to migrate gradually.
    + You don't need to migrate the whole MySQL data, instead, just copy some data from MySQL to other databases(e.g. HStreamDB) for data analysis.
* You need to upgrade your MySQL version for some new features seamlessly.
* You need to back up your MySQL data in multiple regions in real time.

In those cases, you will find a data synchronization service is helpful,
HStream IO is an internal data integration framework for HStreamDB,
and it can be used as a data synchronization service,
this document will show you how to use HStream IO to build a data synchronization service from a MySQL table to a PostgreSQL table,
you will learn:

* How to create a source connector that synchronizes records from a MySQL table to an HStreamDB stream.
* How to create a sink connector that synchronizes records from an HStreamDB stream to a PostgreSQL table.
* How to use HStream SQLs to manage the connectors.

## Set up an HStreamDB Cluster

You can check
[quick-start](https://hstream.io/docs/en/latest/start/quickstart-with-docker.html)
to find how to set up an HStreamDB cluster and connect to it.

## Set up a MySQL

Set up a MySQL instance with docker:

```shell
docker run --network=hstream-quickstart --name mysql-s1 -e MYSQL_ROOT_PASSWORD=password -d mysql
```

Here we use the `hstream-quickstart` network if you set up your HStreamDB
cluster based on
[quick-start](https://hstream.io/docs/en/latest/start/quickstart-with-docker.html).

Connect to the MySQL instance:

```shell
docker exec -it mysql-s1 mysql -uroot -h127.0.0.1 -P3306 -ppassword
```

Create a database `d1`, a table `person` and insert some records:

```sql
create database d1;
use d1;
create table person (id int primary key, name varchar(256), age int);
insert into person values (1, "John", 20), (2, "Jack", 21), (3, "Ken", 33);
```

the table `person` must include a primary key, or the `DELETE` statement may not
be synced correctly.

## Set up a PostgreSQL

Set up a PostgreSQL instance with docker:

```shell
docker run --network=hstream-quickstart --name pg-s1 -e POSTGRES_PASSWORD=postgres -d postgres
```

Connect to the PostgreSQL instance:

```shell
docker exec -it pg-s1 psql -h 127.0.0.1 -U postgres
```

`sink-postgresql` doesn't support the automatic creation of a table yet, so you
need to create the database `d1` and the table `person` first:

```sql
create database d1;
\c d1;
create table person (id int primary key, name varchar(256), age int);
```

The table `person` must include a primary key.

## Download Connector Plugins

A connector plugin is a docker image, so before you can set up the connectors,
you should download and update their plugins with `docker pull`:

```shell
docker pull hstreamdb/source-mysql:v0.2.3
docker pull hstreamdb/sink-postgresql:v0.2.3
```

[Here](https://hstream.io/docs/en/latest/io/connectors.html) is a table of all
available connectors.

## Create Connectors

After connecting an HStream Server, you can use create source/sink connector
SQLs to create connectors.

Connect to the HStream server:

```shell
docker run -it --rm --network host hstreamdb/hstream:latest hstream sql --port 6570
```

Create a source connector:

```sql
create source connector source01 from mysql with ("host" = "mysql-s1", "port" = 3306, "user" = "root", "password" = "password", "database" = "d1", "table" = "person", "stream" = "stream01");
```

The source connector will run an HStream IO task, which continually synchronizes
data from MySQL table `d1.person` to stream `stream01`. Whenever you update
records in MySQL, the change events will be recorded in stream `stream01` if the
connector is running.

You can use `SHOW CONNECTORS` to check connectors and their status and use
`PAUSE` and `RESUME` to stop/restart connectors:

```sql
PAUSE connector source01;
RESUME connector source01;
```

If resume the connector task, the task will not fetch data from the beginning,
instead, it will continue from the point when it was paused.

Then you can create a sink connector that consumes the records from the stream
`stream01` to PostgreSQL table `d1.public.person`:

```sql
create sink connector sink01 to postgresql with ("host" = "pg-s1", "port" = 5432, "user" = "postgres", "password" = "postgres", "database" = "d1", "table" = "person", "stream" = "stream01");
```

With both `source01` and `sink01` connectors running, you get a synchronization
service from MySQL to PostgreSQL.

You can use the `DROP CONNECTOR` statement to delete the connectors:

```sql
drop connector source01;
drop connector sink01;
```

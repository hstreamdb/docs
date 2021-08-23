# How to use HStream connectors with MySQL

This tutorial describes how to use HStream connectors.

!!! Note

    Up to the present, we only provide sink connectors writing to MySQL or ClickHouse. **The recommended verion of MySQL is 5.7** and MySQL versions after 8.0 are not supported yet.

## Prerequisites

Make sure you have started an HStreamDB server and a client connected to the
server. You also need MySQL or ClickHouse service available in order to dump
data to the databases. In this tutorial, I will use HStream CLI client and MySQL
database as the example. To better manipulate data in MySQL, I will use MyCLI as
the interface but any alternatives should work similarly. Please replace the
host address and port number with your own configuration.

```
mycli --host 127.0.0.1 --port 3306 --user root
```

I will be using a database called `hstreamdb` in MySQL.

```sql
> CREATE DATABASE hstreamdb;
> USE hstreamdb;
```

## Create Built-In Connectors

We first create a source stream called `hstreamsrc` in our server via an HStream client.

```sql
CREATE STREAM hstreamsrc;
```

!!! Note

    Before we create a sink connector to subscribe to the source stream, **make sure that there is a table in the database with the same name as the source stream**. 

[^1]: We will no longer have this restriction in the next release.

Otherwise, we need to create one with the schema of our interest.[^1]

```sql
> CREATE TABLE IF NOT EXISTS hstreamsrc (temperature INT, humidity INT);
```

Then, let's take a look at the usage of the command to create a connector.

### Synopsis

```sql
CREATE SINK CONNECTOR connector_name [IF NOT EXIST] WITH (connector_options [...]);
```

Connector options include:

| Option   | Type       | Description or default value |
|----------|------------|------------------------------|
| type*    | Identifier | [`mysql` \| `clickhouse`]    |
| stream*  | Identifier | Name of the source stream    |
| username | String     | "root"                       |
| password | String     | "password"                   |
| host     | String     | "127.0.0.1"                  |
| port*    | Int        | Port number for connection   |
| database | String     | "mysql"                      |

Options with a * symbol are required and others are optional. The `type` option has to be either `mysql` or `clickhouse` for now and the default value listed above are specific to the MySQL option.

Back to our example, we will use the following command to create a sink connector called `mysql_conn` that subsribes to the `hstreamsrc` stream.

```sql
CREATE SINK CONNECTOR mysql_conn WITH (type = mysql, stream = hstreamsrc, username = "root", password = "", host = "127.0.0.1", port = 3306, database = "hstreamdb");
```

You can use the following command to check the status of a connector.

```sql
SHOW CONNECTORS;
```

One of the following states is assigned to the connectors:

| state          | description                                                                    |
|----------------|--------------------------------------------------------------------------------|
| Creating       | The server has started to process the request                                  |
| Created        | The connection has been established but it has not started to process the data |
| CreationAbort  | The process of creating the connection failed and it is frozon                 |
| Running        | The connector is ready to process requests                                     |
| ExecutionAbort | The connector failed to execute a SQL statement and it is frozen               |
| Terminate      | The connector is frozen by a user request                                      |

Please wait for it to finish setting up if the state of the connector is `Creating` or `Created`. After the connector has been successfully initialized, its state will be set to `Running` until it is killed per client request or a MySQL error occurs. A connector is not working if its state is `CreationAbort`, `ExecutionAbort`, or `Terminate`. You can restart an aborted or terminated connector (in the future). You may also abandon connectors by using

```sql
DROP CONNECTOR connector_name;
```

### Check the result

Once the connector has been set up, the data inserted into the source stream thereafter will be written into the connected external system in a very short time gap.

For example, we can insert some data to the source stream by

```sql
INSERT INTO hstreamsrc (temperature, humidity) VALUES (12, 84);
INSERT INTO hstreamsrc (temperature, humidity) VALUES (13, 83);
INSERT INTO hstreamsrc (temperature, humidity) VALUES (14, 82);
```

Please make sure that the data inserted into the source stream follow the schema of the table in the database. Otherwise, a MySQL error will happen and the connection is broken subsequently.

After inserting the data into the source stream, you should be able to view the data on MySQL end,

```sql
> SELECT * FROM hstreamsrc;
```

### Troubleshooting

* What happened if the status of the connector is `CreationAbort`?

This is caused by an error occured when the server tried to connect to the MySQL service. Please double check that you have passed the correct configuration options, especially the port number, and that the database has been created. Please drop the connector before you try again.

* What happened if the status of the connector is `ExecutionAbort`?

This is caused by an error occured in the execution of a MySQL command, e.g. the table with the same name as the source stream does not exist or the data fed into the source stream do not follow the table schema. You could restart the connection (in the future) or drop it.

* What happened if the status of the connector is `Terminate`?

It means a client has requested that the connector be terminated. You could restart the connection (in the future) or drop it in this circumstance.

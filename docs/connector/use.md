# How to use HStream connectors
This tutorial describes how to use HStream connectors. 

!!! Note 

	Up to the present, we only provide two built-in sink connectors which can subscribe data from 	streams and write them into mysql and clickhouse.

## Prerequisites 
Make sure you have HStreamDB running and accessible. Also available mysql and clickhouse service are required if you want to use related connectors.

## Create Built-In Connectors
Currently, we provide two built-in connectors, mysql sink connector and clickhouse sink connector.

### Synopsis

```sql
CREATE SINK CONNECTOR connector_name [IF NOT EXIST] WITH (connector_options [...]);
```
There are some connector options provided to create connector such as database password, database name, username, database host and port.
`Type` is the only required connector option. Until now, the only supported type values are `mysql` and `clickhouse`.


### Mysql Connector

connector options and default value for mysql includes:
```
username = "root"
password = "password"
host = "127.0.0.1"
port = 3306
database = "mysql"
```

An example to create a mysql connector which subscribes from stream `source` and write data to mysql looks like:
```sql
CREATE SINK CONNECTOR mysql WITH (type = mysql, host = "127.0.0.1", stream = source);
```
The mysql table name will be the same as the stream name.

### Clickhouse Connector

connector options and default value for clickhouse includes:
```
username = "default"
password = ""
host = "127.0.0.1"
port = "9000"
database = "default"
```

An example to create a clickhouse connector which subscribes from stream `source` and write data to clickhouse looks like:
```sql
CREATE SINK CONNECTOR mysql WITH (type = clickhouse, host = "127.0.0.1", stream = source);
```
The clickhouse table name will be the same as the stream name.

### Check the result
Once you create a connector which subscribe a particular stream and write data to external systems, each time there are new data insert into the stream, the data will be write into connected external system in a very short time gap. 

For example, if you create mysql connector like this: 
```sql
CREATE SINK CONNECTOR mysql WITH (type = mysql, host = "127.0.0.1", stream = source);
```

Since the stream data shema is always known advanced, we assume the mysql table named `source` is created advanced. The table schema should looks as the same of stream. In this example, we will insert data looks like `(temperature, humidity) VALUES (12, 84)`. Both `temperature` and `humidity` are numbers.

So if the mysql table is not exist in the default database `mysql` yet, you might create the mysql table in this way:

```sql
use mysql;
CREATE TABLE IF NOT EXISTS source (temperature INT, humidity INT) CHARACTER SET utf8;
```

After you insert some data in streams by:

```sql
INSERT INTO source (temperature, humidity) VALUES (12, 84);
INSERT INTO source (temperature, humidity) VALUES (13, 83);
INSERT INTO source (temperature, humidity) VALUES (14, 82);
```

You should found your data in mysql table very soon after the insertion are done.

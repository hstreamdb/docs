# Perform Stream Processing by SQL

This part provides a demo of performing real-time stream processing by SQL. You
will be introduced to some basic concepts such as **streams**, **queries** and
**materialized views** with some examples to demonstrate the power of our
processing engine, such as the ease to use and dealing with complex queries.

## Overview

One of the most important applications of stream processing is real-time
business information analysis. Imagine that we are managing a supermarket and
would like to analyze the sales information to adjust our marketing strategies.

Suppose we have two **streams** of data:

```sql
info(product, category)      // represents the category a product belongs to
visit(product, user, length) // represents the length of time when a customer looks at a product
```

Unlike tables in traditional relational databases, a stream is an endless series
of data which comes with time. Next, we will run some analysis on the two
streams to get some useful information.

## Requirements

Ensure you have deployed HStreamDB successfully. The easiest way is to follow
[quickstart](../start/quickstart-with-docker.md) to start a local cluster. Of
course, you can also try other methods mentioned in the Deployment part.

## Step 1: Create related streams

We have mentioned that we have two streams, `info` and `visit` in the
[overview](#overview). Now let's create them. Start an HStream SQL shell and run
the following statements:

```sql
> CREATE STREAM info;
info
> CREATE STREAM visit;
visit
```

We have successfully created two streams.

## Step 2: Create streaming queries

We can now create streaming **queries** on the streams. A query is a running
task that fetches data from the stream(s) and produces results continuously.
Let's create a trivial query that fetches data from stream `info` and outputs
them:

```sql
> SELECT * FROM info EMIT CHANGES;
```

The query will keep running until you interrupt it. Next, we can just leave it
there and start another query. It fetches data from the stream `visit` and
outputs the maximum length of time of each product. Start a new SQL shell and
run

```sql
> SELECT product, MAX(length) AS max_len FROM visit GROUP BY product EMIT CHANGES;
```

Neither of the queries will print any results since we have not inserted any
data yet. So let's do that.

## Step 3: Insert data into streams

There are multiple ways to insert data into the streams, such as client
libraries and HStream IO, and the data inserted will all be cheated the same
while processing. You can refer to [guides](./write.md) for client usage or the
[overview](../io/overview.md) of HStream IO.

For consistency and ease of demonstration, we would use SQL statements.

Start a new SQL shell and run:

```sql
> INSERT INTO info (product, category) VALUES ("Apple", "Fruit");
Done.
> INSERT INTO visit (product, user, length) VALUES ("Apple", "Alice", 10);
Done.
> INSERT INTO visit (product, user, length) VALUES ("Apple", "Bob", 20);
Done.
> INSERT INTO visit (product, user, length) VALUES ("Apple", "Caleb", 10);
Done.
```

Switch to the shells with running queries You should be able to see the expected
outputs as follows:

```sql
> SELECT * FROM info EMIT CHANGES;
{"product":"Apple","category":"Fruit"}
```

```sql
> SELECT product, MAX(length) AS max_len FROM visit GROUP BY product EMIT CHANGES;
{"product":"Apple","max_len":10.0}
{"product":"Apple","max_len":20.0}
{"product":"Apple","max_len":20.0}
```

Note that `max_len` changes from `10` to `20`, which is expected.

## Step 4: Create materialized views

Now let's do some more complex analysis. If we want to know the longest visit
time of each category **any time we need it**, the best way is to create
**materialized views**.

A materialized view is an object which contains the result of a query. In
HStreamDB, the view is maintained and continuously updated in memory, which
means we can read the results directly from the view right when needed without
any extra computation. Thus getting results from a view is very fast.

Here we can create a view like

```sql
> CREATE VIEW result AS SELECT info.category, MAX(visit.length) as max_length FROM info JOIN visit ON info.product = visit.product WITHIN (INTERVAL '1' HOUR) GROUP BY info.category;
Done. Query ID: 1362152824401458
```

Note the query ID will be different from the one shown above. Now let's try to
get something from the view:

```sql
> SELECT * FROM result;
Done.
```

It outputs no data because we have not inserted any data into the streams since
**after** the view is created. Let's do it now:

```sql
> INSERT INTO info (product, category) VALUES ("Apple", "Fruit");
Done.
> INSERT INTO info (product, category) VALUES ("Banana", "Fruit");
Done.
> INSERT INTO info (product, category) VALUES ("Carrot", "Vegetable");
Done.
> INSERT INTO info (product, category) VALUES ("Potato", "Vegetable");
Done.
> INSERT INTO visit (product, user, length) VALUES ("Apple", "Alice", 10);
Done.
> INSERT INTO visit (product, user, length) VALUES ("Apple", "Bob", 20);
Done.
> INSERT INTO visit (product, user, length) VALUES ("Carrot", "Bob", 50);
Done.
```

## Step 5: Get results from views

Now let's find out what is in our view:

```sql
> SELECT * FROM result;
{"max_length":20.0,"info.category":"Fruit"}
{"max_length":50.0,"info.category":"Vegetable"}
```

It works. Now insert more data and repeat the inspection:

```sql
> INSERT INTO visit (product, user, length) VALUES ("Banana", "Alice", 40);
Done.
> INSERT INTO visit (product, user, length) VALUES ("Potato", "Eve", 60);
Done.
> SELECT * FROM result;
{"max_length":40.0,"info.category":"Fruit"}
{"max_length":60.0,"info.category":"Vegetable"}
```

The result is updated right away.

## Related Pages

For a detailed introduction to the SQL, see
[HStream SQL](../reference/sql/sql-overview.md).

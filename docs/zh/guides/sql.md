Perform Stream Processing by SQL
===================================

This part provides a demo on performing real-time stream processing by SQL. You can get first understand on basic concepts such as **streams**, **queries** and **materialized views**. It also shows some powerful features of our system, such as easy-to-use and low-lantency.

## Overview

One of the most important applications of stream processing is real-time bussiness information analisis. Imagine that we manage a supermarket and we want to analyze the selling information of products to adjust management strategies. To be brief, suppose we have two **stream**s:

```
info(product, category)      // represents the category a product belongs to
visit(product, user, length) // represents the length of time when a customer looks at a product
```

Unlike tables in traditional relational databases, a stream is an endless series of data which comes with time. Now we want to do some analisis on the two streams to get some useful information.

## Prerequirements

Ensure you have deployed the HStream system successfully. The most easiest way is at [quickstart](../start/quickstart-with-docker.md). Of course you can also try other methods mentioned in the Deployment part.

## Step 1: Create related streams

In the overview part we have mentioned that we have two streams `info` and `visit`. Now let's create them. Just open a CLI session and run the following statements:

```
> CREATE STREAM info;
info
> CREATE STREAM visit;
visit
```

We have successfully created two streams.

## Step 2: Create streaming queries

Now we can create streaming **queries** on the streams. A query is a running task which fetch data from stream(s) and produce results continuously. Let's create a trivial query which fetches data from stream `info` and outputs them identically:

```
> SELECT * FROM info EMIT CHANGES;
```

The query will start running until you interrupt it. Now just let it there and create another query. It fetches data from stream `visit` and outputs the maximum length of time of each product. Open a new CLI session and run

```
> SELECT product, MAX(length) AS max_len FROM visit GROUP BY product EMIT CHANGES;
```

Both two queries output no result now because we have not inserted any data into the two streams. We will do it then.

## Step 3: Insert data into streams

To insert data into streams, we can use many methods such as interactive CLI, client libraries and HStream IO. You can refer to [guides](../write.md) for client usage and [overview](../io/overview.md) for HStream IO. Here we use CLI to insert data to streams.

Open a new CLI session and run

```
> INSERT INTO info (product, category) VALUES ("Apple", "Fruit");
Done.
> INSERT INTO visit (product, user, length) VALUES ("Apple", "Alice", 10);
Done.
> INSERT INTO visit (product, user, length) VALUES ("Apple", "Bob", 20);
Done.
```

Now switch to CLI sessions of the queries, we can get outputs as expected:

```
> SELECT * FROM info EMIT CHANGES;
{"product":"Apple","category":"Fruit"}
```

```
> SELECT product, MAX(length) AS max_len FROM visit GROUP BY product EMIT CHANGES;
{"product":"Apple","max_len":10.0}
{"product":"Apple","max_len":20.0}
```

Note that `max_len` changes from `10` to `20`, which is expected.

## Step 3: Create materialized views

Now let's do some more complex analisis. We want to know the maximum length of visit time of each category **at any time we need it**. The best way to solve the problem is by **materialized views**.

A materialized view is a physical object which is continuously maintained in the memory. We can get the results directly from the view once we need it without any extra computation. Thus getting results from a view is very fast.

Here we can create a view like

```
> CREATE VIEW result AS SELECT info.category, MAX(visit.length) as max_length FROM info, visit WHERE info.product = visit.product GROUP BY info.category EMIT CHANGES;
Done. Query ID: 1362152824401458
```

Note the query ID may be different. Now let's get something from the view:

```
> SELECT * FROM result;
Done.
```

We have got nothing! It is because we have not inserted any data into the streams **after** the view is created. Let's insert some data:

```
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

## Step 4: Get results from views

Now let's find out what is in our view again:

```
> SELECT * FROM result;
{"max_length":20.0,"info.category":"Fruit"}
{"max_length":50.0,"info.category":"Vegetable"}
```

It is correct! Now insert more data and repeat the inspection:

```
> INSERT INTO visit (product, user, length) VALUES ("Banana", "Alice", 40);
Done.
> INSERT INTO visit (product, user, length) VALUES ("Potato", "Eve", 60);
Done.
> SELECT * FROM result;
{"max_length":40.0,"info.category":"Fruit"}
{"max_length":60.0,"info.category":"Vegetable"}
```

The result is updated at once.

## Related Pages

For detailed introduction of the SQL, see [the reference](../reference/sql/sql-overview.md).

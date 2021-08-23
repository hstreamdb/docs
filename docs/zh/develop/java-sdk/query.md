# Stream Processing with SQL

This page shows how to processing stream data in HStreamDB with SQL using Java
SDK.

## Prerequisites

Make sure you have HStreamDB running and accessible.

## Execute Real-time Query on Stream

Here is an example:

```java

Observer<HRecord> observer =
    new Observer<HRecord>() {
      @Override
      public void onNext(HRecord value) {
        System.out.println("get hrecord: {}" + value);
      }

      @Override
      public void onError(Throwable t) {
        System.out.println("error!");
      }

      @Override
      public void onCompleted() {}
    };

Queryer queryer =
    client
        .newQueryer()
        .sql("select * from test_stream where temperature > 30 emit changes;")
        .resultObserver(observer)
        .build();

// queryer will fetch real-time data at background
queryer.startAsync().awaitRunning();

// ... execute query for some time ...

// finally, you can stop the queryer using queryer.stopAsync()
queryer.stopAsync().awaitTerminated();

```

You can use a `Queryer` object to create a real-time SQL statement, also you
need to provide an `Observer` object which contains your logic for processing
the results returned by the query. The `Queryer` will process data in a
background thread.

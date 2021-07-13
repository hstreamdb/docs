# Stream Processing with SQL 

This page shows how to processing stream data in HStreamDB with SQL using Java SDK.

## Prerequisites 

Make sure you have HStreamDB running and accessible.


## Execute Real-time Query on Stream 

You can execute a real-time query on stream using the ``HStreamClient.streamQuery()`` method:

```java

final String TEST_STREAM = "test_stream";

Publisher<HRecord> publisher = client.streamQuery(
        "select * from " + TEST_STREAM +
        " where temperature > 30 emit changes;"
);

Observer<HRecord> observer = new Observer<HRecord>() {
    @Override
    public void onNext(HRecord hrecord) {
        System.out.println(hrecord);
    }

    @Override
    public void onError(Throwable t) {
        throw new RuntimeException(t);
    }

    @Override
    public void onCompleted() {

    }
};

publisher.subscribe(observer);

```

The ``HStreamClient.streamQuery()`` method return a ``Publisher`` object,
and you need to provide an ``Observer`` object 
that contains your logic for processing the results returned by the query.


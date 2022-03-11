# Stream Processing with SQL

This page shows how to processing stream data in HStreamDB with SQL using Java
SDK.

## 前提条件

确保有一个运行中并可用的 HStreamDB

## 在 Stream 上执行实时查询任务（Query）

举个例子：

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

可以用一个 `Queryer` 对象来创建一个实时的 SQL 语句，同时你也可以提供一个具备处理返回数据逻辑的
`Observer`。这个 `Queryer` 会在后台处理数据。

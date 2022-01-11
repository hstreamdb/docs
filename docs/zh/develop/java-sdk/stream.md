# 流（Streams）

HStreamDB 以 streams 的形式存储数据, 那么如何通过 Java SDK 来对管理 HStreamDB 中的 streams。

## 前提条件

- 确保有一个运行中并可用的 HStreamDB
- 包含下列 import 语句：

```java

import io.hstream.HStreamClient;
import io.hstream.Stream;

```

## 连接到一个 HStreamDB 示例

首先你需要连接到一个 HStreamDB 示例，然后得到一个 `HStreamClient` 对象。

```java

HStreamClient client = HStreamClient.builder().serviceUrl("SERVER_HOST:SERVER_PORT").build();

```

## 拿到当前存在的 stream 列表

通过 `HStreamClient.listStreams()` 可以拿到当前存在的 stream 列表


```java

for(Stream stream: client.listStreams()) {
  System.out.println(stream.getStreamName());
}

```

## 创建一个新的 stream

通过 `HStreamClient.createStream()` 可以创建一个新的 stream with 3（默认值） replicates：


```java

client.createStream("test_stream");

```

你也可以给定 replicate 的个数:

```java

client.createStream("test_stream", 5);

```

## 删除一个 stream

通过 `HStreamClient.deleteStream()` 可以删除一个stream:

```java

client.deleteStream("test_stream");

```

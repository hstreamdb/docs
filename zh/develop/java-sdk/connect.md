# 连接

如何通过 Java SDK 连接 HStreamDB

## 前提条件

确保有一个运行中并可用的 HStreamDB

## 例子

```java

package io.hstream.example;

import io.hstream.*;

public class ConnectExample {
    public static void main(String[] args) throws Exception {
        // need to replace the serviceUrl to "<Your HStreamDB server host>:<Your HStreamDB server port>",
        // you can also connect to multiple HStreamDB servers, as follows
        final String serviceUrl = "localhost:6570,localhost:6571,localhost:6572";
        HStreamClient client = HStreamClient.builder().serviceUrl(serviceUrl).build();
        System.out.println("Connected");
        client.close();
    }
}

```

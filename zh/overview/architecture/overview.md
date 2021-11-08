# Architecture

HStreamDB 的整体架构如下图所示，单个 HStreamDB 节点主要由 HStream Server (HSQL) 和 HStream Storage (HStore) 两个核心部件组成，一个 HStream 集群由若干个对等的 HStreamDB 节点组成， 客户端可连接至集群中任意一个 HStreamDB 节点， 并通过熟悉的 SQL 语言来完成各种从简单到复杂的流处理和分析任务。

![](https://static.emqx.net/images/faab4a8b1d02f14bc5a4153fe37f21ca.png)

<center>HStreamDB 整体架构</center>

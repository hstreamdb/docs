# HStreamDB release notes

## v0.8.0 [2022-04-29]

### Server

#### New features

- 新增 [mutual TLS 支持](../security/overview.md)
- 新增 Subscription 的配置项 `maxUnackedRecords`：该配置项用于控制每个订阅上已派
  发但尚未 acked 的最大的 record 的数量，当某个订阅上的未 acked 的数据达到这个配
  置的最大值后，便会停止该订阅上的数据交付，防止极端情况下造成大量 unacked
  records 堆积，影响 HServer 和对应 Consumers 的性能表现。建议用户根据自身实际应
  用场景下的数据消费能力对这一参数进行合理配置。
- 新增 Stream 的配置项 `backlogDuration`：这个配置决定了当前 Stream 的数据可以在
  HStreamDB 中驻留多久，超过这个期限的数据将不可访问并被清理。
- 新增 Stream 的配置项 `maxRecordSize`：在创建 stream 时，用户可以通过该配置来控
  制当前 stream 支持的最大的数据大小，超过这一阈值的数据会返回写入失败。
- 新增[多项监控指标](../admin/admin.md#数据统计-hstream-stats)
- 新增 server 端的[压缩配置](../reference/config.md)

#### Enhancements

- [breaking change] 简化了订阅的协议，重构并优化了订阅的性能
- 优化了数据消费超时后的重传的实现，提升了数据重传的效率
- 优化了 hstore client 读数据的性能
- 改进了对订阅上重复的 ack 消息的处理
- 改进了订阅删除的实现
- 改进了 stream 删除的实现
- 改进了集群使用的一致性哈希算法
- 优化了 server 内部的异常处理
- 优化了 server 启动流程
- 改进了 stats 模块的实现

#### Bug fixes

- 修复了若干由于 grpc-haskell 引起的内存泄漏问题
- 修复了若干 zk-client 的问题
- 修复了 server 启动过程中 checkpoint store 已经存在报错的问题
- 修复了 `lookupStream` 过程对 `defaultKey` 的处理不一致的问题
- 修复了 hstore 的 loggroup 初始化完成之前 stream 写入错误的问题
- 修复了 hstore client 写入数据不正确的问题
- 修复了在订阅上向空闲 consumer 分配出错的问题
- 修复了 hstore client 的 `appendBatchBS` 函数的内存分配问题
- 修复了订阅数据重传过程中由于原 consumer 不可用造成的重传数据丢失的问题
- 修复了订阅数据派发过程中因为 `Workload` 排序错误引起数据分发的问题

### Java Client

#### New features

- 新增 TLS 支持
- 新增 `BufferedProducer` 配置项 `FlowControlSetting`
- 新增 `Subscription` 配置项 `maxUnackedRecords`
- 新增 `createStream` 配置项 `backlogDuration`
- 新增 `Subscription` 的强制删除支持
- 新增 `Stream` 的强制删除支持

#### Enhancements

- [Breaking change] 改进了 RecordId，新的 RecordID 是一个 opaque string
- 提高了 `BufferedProducer` 的性能
- 改进了 `Responder，用分批的` ACKs 提升效率
- 改进了 record id，用于保证多个 ordering keys 场景下的唯一性
- 改进了 `BufferedProducerBuilder`，用 `BatchSetting` 统一了 `recordCountLimit`,
  `recordCountLimit`, `ageLimit` 等配置项
- 改进了 javadoc 中相关接口的介绍和说明

#### Bug fixes

- 修复了 Consumer 关闭时，没有取消 `streamingFetch` 的问题
- 修复了 Consumer 对 gRPC 异常处理的问题
- 修复了 `BufferedProducer` 对累积的 record size 的错误的计算方法

### Go Client

hstreamdb-go v0.1.0 现已正式发布，更多详细介绍以及如何使用请参考
[Github 仓库](https://github.com/hstreamdb/hstreamdb-go)

### Admin Server

负责为多种 CLI 工具提供服务并开放 REST API 的 Admin Server 现已发布，可以通过
[Github 仓库](https://github.com/hstreamdb/hstreamdb-go) 查看并部署体验。

### Deployment and Benchmark

- 新增了 benchmark 工具
- [dev-deploy] 新增了限制指定容器资源的支持
- [dev-deploy] 新增了重启容器的配置
- [dev-deploy] 新增了在部署时上传所有配置文件的支持
- [dev-deploy] 新增了与 Prometheus 集成的部署支持

## v0.7.0 [2022-01-28]

### 特性

#### 添加透明分区支持

HStreamDB 已经支持大规模数据流的存储和管理。随着上个版本新增加的集群支持，我们决
定用透明分区来改善单个 stream 的可扩展性和读写性能。在 HStreamDB v0.7 中，每个
stream 都分布在多个服务器节点上，但是在用户看来，一个有分区的 stream 是作为一个
整体来管理的。因此，用户不需要事先指定分区的数量或任何其他分区逻辑。

在目前的实现中，stream 的每条 record 都应该包含一个分区键来指定一个逻辑分区，
HStream Server 将负责在存储数据时将这些逻辑分区映射到物理分区。

#### 用一致的散列算法重新设计负载平衡

我们在这个版本中用一致性哈希算法调整了我们的负载均衡策略。当前，不管是写请求还是
读请求，都是以读写请求中的所带分区键来分配的。

在上一个版本中，我们的负载平衡是基于节点的硬件使用情况进行分配的。而这样做的主要
的问题是，它在很大程度上依赖于一个 leader 节点去收集这些信息。同时，这种策略需要
与节点与 leader 进行通信，以获得分配结果。总体来看过去的实现过于复杂，效率太低。
因此我们重新实现了负载均衡器，不仅核心算法更加简洁，也能很好应对集群成员变更的时
候的重分配问题。

#### 添加 HStream Admin 管理工具

我们提供了一个新的管理工具，以方便用户对 HStreamDB 的维护和管理。HAdmin 可以用于
监控和管理 HStreamDB 的各种资源，包括 stream、订阅和 server 节点。以前嵌入在
HStream SQL Shell 中的 HStream Metrics，现已迁移到了新的 HAdmin 中。简而言之
，HAdmin 是为 HStreamDB 操作人员准备的，而 SQL Shell 是为 HStreamDB 终端用户准备
的。

#### 部署和使用

- 支持通过脚本快速部署，见：
  [使用 Docker 手动部署](../deployment/deploy-docker.md)
- 支持用配置文件来配置 HStreamDB，见：
  [HStreamDB Configuration](../reference/config.md)
- 支持一步到位的 docker-compose 的快速上手，见：
  [使用 Docker Compose 快速上手](../start/quickstart-with-docker.md)

**为了使用 HStreamDB v0.7，请使用
[hstreamdb-java v0.7.0](https://github.com/hstreamdb/hstreamdb-java) 及以上版
本**

## v0.6.0 [2021-11-04]

### 特性

#### 新增 HServer 集群支持

作为一款[云原生分布式流数据库](https://hstream.io/zh)，HStreamDB 从设计之初就采
用了计算和存储分离的架构，目标是支持计算层和存储层的独立水平扩展。在 HStreamDB
之前的版本中，存储层 HStore 已经具备了水平扩展的能力。在即将发布的 v0.6 版本中，
计算层 HServer 也将支持集群模式，从而可以实现随客户端请求和计算任务的规模对计算
层的 HServer 节点进行扩展。

HStreamDB 的计算节点 HServer 整体上被设计成无状态的，因此非常适合进行快速的水平
扩展。v0.6 的 HServer 集群模式主要包含以下特性：

- 自动节点健康检测和失败恢复
- 按照节点负载情况对客户端请求或者计算任务进行调度和均衡
- 支持节点的动态加入和退出

#### 新增共享订阅功能

在之前的版本中，一个 Subscription 同时只允许一个客户端进行消费，这在较大数据量的
场景下限制了客户端的消费能力。因此，为了支持扩展客户端的消费能力，v0.6 版本新增
了共享订阅功能，它允许多个客户端在一个订阅上并行消费。

同一个订阅里包含的所有的消费者组成一个消费者组(Consumer Group)，HServer 会通过
round-robin 的方式向消费者组中的多个消费者派发数据。消费者组中的成员支持随时动态
变更，客户端可以在任何时候加入或退出当前的消费者组。

HStreamDB 目前支持 at least once 的消费语义，每条数据在客户端消费完之后需要回复
Ack，如果超时未接收到某条数据的 Ack，HServer 会自动重新向可用的消费者投递该条数
据。

同一个消费者组中的成员共享消费进度，HStream 会根据客户端 Ack 的情况维护消费进度
，客户端任何时候都可以从上一次的位置恢复消费。

需要注意的是，v0.6 的共享订阅模式下不保持数据的顺序，后续共享订阅将支持按 key 派
发的模式，可以支持相同 key 的数据有序交付。

#### 新增统计功能

v0.6 还增加了基本的数据统计功能，支持对诸如 stream 的写入速率，消费速率等关键指
标进行统计。用户可以通过 HStream CLI 来查看相应的统计指标，如下图所示。

![](./statistics.png)

#### 新增数据写入 Rest API

v0.6 版本增加了向 HStreamDB 写入数据的 Rest API，通过此 API 并结合 EMQ X 开源版
的 web hook 功能，可以实现 EMQ X 和 HStreamDB 的快速集成。

#### HStreamDB Java SDK 更新

HStreamDB-Java 是目前主要支持的 HStreamDB 客户端（后续将会有更多语言的客户端支持
），用户主要通过该客户端来使用 HStreamDB 的大多数功能。

即将发布的 HStreamDB Java SDK v0.6 主要包含以下特性：

- 新增对 HStreamDB 集群的支持
- 新增对共享订阅的支持
- 重构部分 API
- 修复了一些已知的问题

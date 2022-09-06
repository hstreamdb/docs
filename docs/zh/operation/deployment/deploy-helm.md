# 在 Kubernetes 上通过 helm 部署

本文档描述了如何使用我们提供的 helm chart 来运行 HStreamDB kubernetes。该文档假设读者
有基本的 kubernetes 知识。在本节结束时，你将拥有一个完全运行在 kubernetes 上的
HStreamDB 集群，它已经准备就绪，可以接收读/写，处理数据，等等。

## 建立你的 Kubernetes 集群

第一步是要有一个正在运行的 kubernetes 集群。你可以使用一个托管的集群（由你的云提
供商提供），一个自我托管的集群或一个本地的 kubernetes 集群，比如 minikube。请确
保 kubectl 指向你计划使用的任何集群。

另外，你需要一个存储类，你可以通过 "kubectl "创建。或者通过你的云服务提供商的网页来创建，如果它有的话。
minikube 默认提供一个名为 `standard` 的存储类，helm chart 默认使用此存储类。

## 通过 Helm 部署 HStreamDB

### Clone 代码并获取 helm 依赖

```sh
git clone https://github.com/hstreamdb/hstream.git
cd hstream/deploy/chart/hstream/
helm dependency build .
```

### 通过 Helm 部署 HStreamDB

```sh
helm install my-hstream .
```

Helm chart 还提供了 `value.yaml` 文件，你可以在这个文件中修改你的配置,比如当你希望使用其他的存储类来部署集群时，你可以修改 `value.yaml` 中的 `logdevice.persistence.storageClass` 和 `zookeeper.persistence.storageClass`，并使用 `helm install my-hstream -f values.yaml .` 来署。

### 检查集群状态

`helm install` 命令会部署 zookeeper 集群、logdevice 集群和 hstream 集群，这可能会花费一定的时间，你可以通过 `kubectl get pods` 来检查集群的状况，在集群部署的过程中，会有一些 `Error` 和 `CrashLoopBackOff` 的状态，这些状态会在一定时间后消失，最终你会看到类似如下的内容：

```
NAME                                                 READY   STATUS    RESTARTS      AGE
my-hstream-0                                         1/1     Running   3 (16h ago)   16h
my-hstream-1                                         1/1     Running   2 (16h ago)   16h
my-hstream-2                                         1/1     Running   0             16h
my-hstream-logdevice-0                               1/1     Running   3 (16h ago)   16h
my-hstream-logdevice-1                               1/1     Running   3 (16h ago)   16h
my-hstream-logdevice-2                               1/1     Running   0             16h
my-hstream-logdevice-3                               1/1     Running   0             16h
my-hstream-logdevice-admin-server-6867fd9494-bk5mf   1/1     Running   3 (16h ago)   16h
my-hstream-zookeeper-0                               1/1     Running   0             16h
my-hstream-zookeeper-1                               1/1     Running   0             16h
my-hstream-zookeeper-2                               1/1     Running   0             16h
```

你可以通过 `hadmin server` 命令来检查 HStreamDB 集群的状态。

```
kubectl exec -it hstream-1 -- bash -c "hadmin server status"
+---------+---------+------------------+
| node_id |  state  |     address      |
+---------+---------+------------------+
| 100     | Running | 172.17.0.4:6570  |
| 101     | Running | 172.17.0.10:6570 |
| 102     | Running | 172.17.0.12:6570 |
+---------+---------+------------------+
```

## 管理存储集群

现在你可以运行 `hadmin store` **来管理这个集群**：

```
kubectl exec -it my-hstream-0 -- bash -c "hadmin store --help"
```

要检查 store 集群的状态，你可以运行：

```sh
kubectl exec -it my-hstream-0 -- bash -c "hadmin store --host my-hstream-logdevice-admin-server status"
+----+------------------------+----------+-------+--------------+----------+
| ID |        NAME            | PACKAGE  | STATE |    UPTIME    | LOCATION |
+----+------------------------+----------+-------+--------------+----------+
| 0  | my-hstream-logdevice-0 | 99.99.99 | ALIVE | 16 hours ago |          |
| 1  | my-hstream-logdevice-1 | 99.99.99 | DEAD  | 16 hours ago |          |
| 2  | my-hstream-logdevice-2 | 99.99.99 | DEAD  | 16 hours ago |          |
| 3  | my-hstream-logdevice-3 | 99.99.99 | DEAD  | 16 hours ago |          |
+----+------------------------+----------+-------+--------------+----------+
+---------+-------------+---------------+------------+---------------+
|  SEQ.   | DATA HEALTH | STORAGE STATE | SHARD OP.  | HEALTH STATUS |
+---------+-------------+---------------+------------+---------------+
| ENABLED | HEALTHY(1)  | READ_WRITE(1) | ENABLED(1) | HEALTHY       |
| ENABLED | HEALTHY(1)  | READ_WRITE(1) | ENABLED(1) | HEALTHY       |
| ENABLED | HEALTHY(1)  | READ_WRITE(1) | ENABLED(1) | HEALTHY       |
| ENABLED | HEALTHY(1)  | READ_WRITE(1) | ENABLED(1) | HEALTHY       |
+---------+-------------+---------------+------------+---------------+
Took 16.727s
```

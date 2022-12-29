# 在 Kubernetes 上运行

本文档描述了如何使用我们提供的 specs 来运行 HStreamDB kubernetes。该文档假设读者
有基本的 kubernetes 知识。在本节结束时，你将拥有一个完全运行在 kubernetes 上的
HStreamDB 集群，它已经准备就绪，可以接收读/写，处理数据，等等。

## 建立你的 Kubernetes 集群

第一步是要有一个正在运行的 kubernetes 集群。你可以使用一个托管的集群（由你的云提
供商提供），一个自我托管的集群或一个本地的 kubernetes 集群，比如 minikube。请确
保 kubectl 指向你计划使用的任何集群。

另外，你需要一个名为 "hstream-store "的存储类，你可以通过 "kubectl "创建。或者通
过你的云服务提供商的网页来创建，如果它有的话。

::: tip

对于使用 minikube 的用户, 你可以用默认的存储类 `standard`.

:::

## 安装 Zookeeper

HStreamDB 依赖于 Zookeeper 来存储查询信息和一些内部的存储配置，所以我们需要提供
一个 Zookeeper 集群，以便 HStreamDB 能够访问。

在这个演示中，我们将使用[helm](https://helm.sh/)（一个用于 kubernetes 的软件包管
理器）来安装 zookeeper。安装完 helm 后，运行：

```sh
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

helm install zookeeper bitnami/zookeeper \
  --set image.tag=3.6 \
  --set replicaCount=3 \
  --set persistence.storageClass=hstream-store \
  --set persistence.size=20Gi
```

```
NAME: zookeeper
LAST DEPLOYED: Tue Jul  6 10:51:37 2021
NAMESPACE: test
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
** Please be patient while the chart is being deployed **

ZooKeeper can be accessed via port 2181 on the following DNS name from within your cluster:

    zookeeper.svc.cluster.local

To connect to your ZooKeeper server run the following commands:

    export POD_NAME=$(kubectl get pods -l "app.kubernetes.io/name=zookeeper,app.kubernetes.io/instance=zookeeper,app.kubernetes.io/component=zookeeper" -o jsonpath="{.items[0].metadata.name}")
    kubectl exec -it $POD_NAME -- zkCli.sh

To connect to your ZooKeeper server from outside the cluster execute the following commands:

    kubectl port-forward svc/zookeeper 2181:2181 &
    zkCli.sh 127.0.0.1:2181
WARNING: Rolling tag detected (bitnami/zookeeper:3.6), please note that it is strongly recommended to avoid using rolling tags in a production environment.
+info https://docs.bitnami.com/containers/how-to/understand-rolling-tags-containers/
```

这将默认安装一个 3 个节点的 zookeeper 集合。等到所有的节点标记为 Ready。

```sh
kubectl get pods
```

```
NAME         READY   STATUS    RESTARTS   AGE
zookeeper-0  1/1     Running   0          22h
zookeeper-1  1/1     Running   0          4d22h
zookeeper-2  1/1     Running   0          16m
```

## 配置和启动 HStreamDB

一旦所有的 zookeeper pods 都准备好了，我们就可以开始安装 HStreamDB 集群。

### 拿到 k8s spec

```sh
git clone git@github.com:hstreamdb/hstream.git
cd hstream/deploy/k8s
```

### 更新配置

如果你使用了不同的方式来安装 zookeeper，请确保更新存储配置文件 `config.json` 中
的 zookeeper 连接字符串和服务器配置文件`hstream-server.yaml`。

它应该看起来像这样：

```sh
$ cat config.json | grep -A 2 zookeeper
  "zookeeper": {
    "zookeeper_uri": "ip://zookeeper-0.zookeeper-headless:2181,zookeeper-1.zookeeper-headless:2181,zookeeper-2.zookeeper-headless:2181",
    "timeout": "30s"
  }

$ cat hstream-server.yaml | grep -A 1 metastore-uri
            - "--metastore-uri"
            - "zk://zookeeper-0.zookeeper-headless:2181,zookeeper-1.zookeeper-headless:2181,zookeeper-2.zookeeper-headless:2181"
```

::: tip

Storage 配置文件和服务文件中的 zookeeper 连接字符串可以是不同的。但对于正常情况
下，它们是一样的。

:::

在默认情况下。本规范安装了一个 3 个节点的 HStream 服务器集群和 4 个节点的存储集
群。如果你想要一个更大的集群，修改 `hstream-server.yaml` 和
`logdevice-statefulset.yaml` 文件，并将复制的数量增加到你想要的集群中的节点数。
另外，默认情况下，我们给节点附加一个 40GB 的持久性存储，如果你想要更多，你可以在
volumeClaimTemplates 部分进行修改。

### 启动集群

```sh
kubectl apply -k .
```

当你运行`kubectl get pods`时，你应该看到类似如下：

```
NAME                                                 READY   STATUS    RESTARTS   AGE
hstream-server-0                                     1/1     Running   0          6d18h
hstream-server-1                                     1/1     Running   0          6d18h
hstream-server-2                                     1/1     Running   0          6d18h
logdevice-0                                          1/1     Running   0          6d18h
logdevice-1                                          1/1     Running   0          6d18h
logdevice-2                                          1/1     Running   0          6d18h
logdevice-3                                          1/1     Running   0          6d18h
logdevice-admin-server-deployment-5c5fb9f8fb-27jlk   1/1     Running   0          6d18h
zookeeper-0                                          1/1     Running   0          6d22h
zookeeper-1                                          1/1     Running   0          10d
zookeeper-2                                          1/1     Running   0          6d
```

### Bootstrap 集群

一旦所有的 logdevice pods 运行并准备就绪，你将需要 Bootstrap 集群以启用所有的存
储节点。要做到这一点，请运行：

```sh
kubectl run hstream-admin -it --rm --restart=Never --image=hstreamdb/hstream:latest -- \
    hadmin store --host logdevice-admin-server-service \
    nodes-config \
    bootstrap --metadata-replicate-across 'node:3'
```

这将启动一个 hstream-admin pod，它连接到管理服务器并调用
`nodes-config bootstrap` hadmin store 命令，并将集群的元数据复制属性设置为跨三个
不同的节点进行复制。

成功后，你应该看到类似如下：

```
Successfully bootstrapped the cluster
pod "hstream-admin" deleted
```

现在，你可以 bootstrap server 节点：

```sh
kubectl run hstream-admin -it --rm --restart=Never --image=hstreamdb/hstream:latest -- \
    hadmin server --host hstream-server-0.hstream-server init
```

成功后，你应该看到类似如下：

```
Cluster is ready!
pod "hstream-admin" deleted
```

注意：取决于硬件条件，存储节点可能没有及时准备就绪，所以运行 `hadmin init` 可能
会返回失败。这时需要等待几秒，再次运行即可。

## 管理存储集群

```sh
kubectl run hstream-admin -it --rm --restart=Never --image=hstreamdb/hstream:latest -- bash
```

现在你可以运行 `hadmin store` 来管理这个集群：

```
hadmin store --help
```

要检查集群的状态，你可以运行：

```sh
hadmin store --host logdevice-admin-server-service status
```

```
+----+-------------+-------+---------------+
| ID |    NAME     | STATE | HEALTH STATUS |
+----+-------------+-------+---------------+
| 0  | logdevice-0 | ALIVE | HEALTHY       |
| 1  | logdevice-1 | ALIVE | HEALTHY       |
| 2  | logdevice-2 | ALIVE | HEALTHY       |
| 3  | logdevice-3 | ALIVE | HEALTHY       |
+----+-------------+-------+---------------+
Took 2.567s
```

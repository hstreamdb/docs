# Running on Kubernetes

This document describes how to run HStreamDB kubernetes using the specs that we
provide. The document assumes basic previous kubernetes knowledge. By the end of
this section, you'll have a fully running HStreamDB cluster on kubernetes that's
ready to receive reads/writes, process datas, etc.

## Building your Kubernetes Cluster

The first step is to have a running kubernetes cluster. You can use a managed
cluster (provided by your cloud provider), a self-hosted cluster or a local
kubernetes cluster using a tool like minikube. Make sure that kubectl points to
whatever cluster you're planning to use.

Also, you need a storageClass named `hstream-store`, you can create by `kubectl`
or by your cloud provider web page if it has.

## Install Zookeeper

HStreamDB depends on Zookeeper for storing queries information and some internal
storage configuration. So we will need to provision a zookeeper ensemble that
HStreamDB will be able to access. For this demo, we will use
[helm](https://helm.sh/) (A package manager for kubernetes) to install
zookeeper. After installing helm run:

```sh
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

helm install zookeeper bitnami/zookeeper \
  --set image.tag=3.6.3 \
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
WARNING: Rolling tag detected (bitnami/zookeeper:3.6.3), please note that it is strongly recommended to avoid using rolling tags in a production environment.
+info https://docs.bitnami.com/containers/how-to/understand-rolling-tags-containers/
```

This will by default install a 3 nodes zookeeper ensemble. Wait until all the
three pods are marked as ready:

```sh
kubectl get pods
```

```
NAME         READY   STATUS    RESTARTS   AGE
zookeeper-0  1/1     Running   0          22h
zookeeper-1  1/1     Running   0          4d22h
zookeeper-2  1/1     Running   0          16m
```

## Configuring and Starting HStreamDB

Once all the zookeeper pods are ready, we're ready to start installing the
HStreamDB cluster.

### Fetching The K8s Specs

```sh
git clone git@github.com:hstreamdb/hstream.git
cd hstream/k8s
```

### Update Configuration

If you used a different way to install zookeeper, make sure to update the
zookeeper connection string in storage config file `config.json` and server
service file `hstream-server.yaml`.

It should look something like this:

```sh
$ cat config.json | grep -A 2 zookeeper
  "zookeeper": {
    "zookeeper_uri": "ip://zookeeper-0.zookeeper-headless:2181,zookeeper-1.zookeeper-headless:2181,zookeeper-2.zookeeper-headless:2181",
    "timeout": "30s"
  }

$ cat hstream-server.yaml | grep -A 1 zkuri
            - "--zkuri"
            - "zookeeper-0.zookeeper-headless:2181,zookeeper-1.zookeeper-headless:2181,zookeeper-2.zookeeper-headless:2181"
```

::: tip
The zookeeper connection string in stotage config file and the service file
can be different. But for normal scenario, they are the same.
:::

By default, this spec installs a 3 nodes HStream server cluster and 4 nodes
storage cluster. If you want a bigger cluster, modify the `hstream-server.yaml`
and `logdevice-statefulset.yaml` file, and increase the number of replicas to
the number of nodes you want in the cluster. Also by default, we attach a 40GB
persistent storage to the nodes, if you want more you can change that under the
volumeClaimTemplates section.

### Starting the Cluster

```sh
kubectl apply -k .
```

When you run `kubectl get pods`, you should see something like this:

```
NAME                                                 READY   STATUS    RESTARTS   AGE
hstream-server-deployment-765c84c489-94nqd           1/1     Running   0          6d18h
hstream-server-deployment-765c84c489-jrm5p           1/1     Running   0          6d18h
hstream-server-deployment-765c84c489-jxsjd           1/1     Running   0          6d18h
logdevice-0                                          1/1     Running   0          6d18h
logdevice-1                                          1/1     Running   0          6d18h
logdevice-2                                          1/1     Running   0          6d18h
logdevice-3                                          1/1     Running   0          6d18h
logdevice-admin-server-deployment-5c5fb9f8fb-27jlk   1/1     Running   0          6d18h
zookeeper-0                                          1/1     Running   0          6d22h
zookeeper-1                                          1/1     Running   0          10d
zookeeper-2                                          1/1     Running   0          6d
```

### Bootstrapping the Storage Cluster

Once all the logdevice pods are running and ready, you'll need to bootstrap the
cluster to enable all the nodes. To do that, run:

```sh
kubectl run hadmin -it --rm --restart=Never --image=hstreamdb/hstream -- \
    hadmin --host logdevice-admin-server-service \
    nodes-config \
    bootstrap --metadata-replicate-across 'node:3'
```

This will start a hadmin pod, that connects to the admin server and invokes the
`nodes-config bootstrap` hadmin command and sets the metadata replication
property of the cluster to be replicated across three different nodes. On
success, you should see something like:

```
Successfully bootstrapped the cluster
pod "hadmin" deleted
```

## Managing the Storage Cluster

```sh
kubectl run hadmin -it --rm --restart=Never --image=hstreamdb/hstream -- bash
```

Now you can run `hadmin` to manage the cluster:

```
hadmin --help
```

To check the state of the cluster, you can then run:

```
hadmin --host logdevice-admin-server-service status

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

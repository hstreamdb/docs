# Running on Kubernetes by Helm

This document describes how to run HStreamDB kubernetes using the helm chart
that we provide. The document assumes basic previous kubernetes knowledge. By
the end of this section, you'll have a fully running HStreamDB cluster on
kubernetes that's ready to receive reads/writes, process datas, etc.

## Building your Kubernetes Cluster

The first step is to have a running kubernetes cluster. You can use a managed
cluster (provided by your cloud provider), a self-hosted cluster or a local
kubernetes cluster using a tool like minikube. Make sure that kubectl points to
whatever cluster you're planning to use.

Also, you need a storageClass, you can create by `kubectl`or by your cloud
provider web page if it has. minikube provides a storage class called `standard`
by default, which is used by the helm chart by default.

## Starting HStreamDB

### Clone code and get helm dependencies

```sh
git clone https://github.com/hstreamdb/hstream.git
cd hstream/deploy/chart/hstream/
helm dependency build .
```

### Deploy HStreamDB by Helm

```sh
helm install my-hstream .
```

Helm chart also provides the `value.yaml` file where you can modify your
configuration, for example when you want to use other storage classes to deploy
the cluster, you can modify `logdevice.persistence.storageClass` and
`zookeeper.persistence.storageClass` in `value.yaml`, and use
`helm install my-hstream -f values.yaml .` to deploy.

### Check Cluster Status

The `helm install` command will deploy the zookeeper cluster, logdevice cluster
and hstream cluster, this can take some time, you can check the status of the
cluster with `kubectl get pods`, there will be some `Error` and
`CrashLoopBackOff` status during the cluster deployment, these will disappear
after some time, eventually you will see something like the following.

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

You can check the status of the HStreamDB cluster with the `hadmin server`
command.

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

## Manage HStore Cluster

Now you can run `hadmin store` to manage the hstore cluster:

```
kubectl exec -it my-hstream-0 -- bash -c "hadmin store --help"
```

To check the state of the cluster, you can then run:

```sh
kubectl exec -it my-hstream-0 -- bash -c "hadmin store --host my-hstream-logdevice-admin-server status"
+----+------------------------+----------+-------+--------------+----------+
| ID |     NAME               | PACKAGE  | STATE |    UPTIME    | LOCATION |
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

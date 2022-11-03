# Deployment with hdt

This document provides a way to start an HStreamDB cluster quickly using the deployment tool `hdt`.

## Pre-Require

- Start HStreamDB requires an operating system kernel version greater than at least Linux 4.14. Check with command:

  ```shell
  uname -r
  ```

- The local host needs to be able to connect to the remote server via SSH

- Make sure remote server has docker installed.

- Make sure that the log-in user has `sudo` execute privileges, and configure `sudo` without password.

- For nodes which deploy `HStore` instances, mount the data disk to `/mnt/data*/`.

    - "*" Matching incremental numbers, start from zero
    - one disk should mount to one directory. e.g. if we have two data disks `/dev/vdb` and `/dev/vdc`, then `/dev/vdb` should mount to `/mnt/data0` and `/dev/vdc` should mount to `/mnt/data1`

## Deploy `hdt` on the control machine

We'll use a deployment tool `hdt` to help us set up the cluster. The binaries are available here: https://github.com/hstreamdb/deployment-tool/releases.

1. Log in to the control machine and download the binaries.

2. Generate configuration template with command:

   ```shell
   ./hdt init
   ```

   The current directory structure will be as follows after running the `init` command:

   ```shell
   ├── hdt
   └── template                 
       ├── config.yaml
       ├── grafana
       │   ├── dashboards
       │   └── datasources
       ├── prometheus
       └── script
   ```

## Update `Config.yaml`

`template/config.yaml` contains the template for the configuration file. Refer to the description of the fields in the file and modify the template according to your actual needs.

Here we will deploy a cluster on 3 nodes, each consisting of a `HServer` instance, a `HStore` instance and a `Meta-Store` instance as a simple example. For hstream monitor stack, refer to [monitor components config](./quick-deploy-ssh.md#monitor-stack-components).

The final configuration file may looks like:

```shell
global:
  user: "root"
  key_path: "~/.ssh/id_rsa"
  ssh_port: 22

hserver:
  - host: 172.24.47.175
  - host: 172.24.47.174
  - host: 172.24.47.173

hstore:
  - host: 172.24.47.175
    enable_admin: true
  - host: 172.24.47.174
  - host: 172.24.47.173

meta_store:
  - host: 172.24.47.175
  - host: 172.24.47.174
  - host: 172.24.47.173
```

## Set up cluster

### set up cluster with ssh key-value pair

```shell
./hdt start -c template/config.yaml -i ~/.ssh/id_rsa -u root
```

### set up cluster with passwd

```shell
./hdt start -c template/config.yaml -p -u root
```

then type your password.

use `./hdt start -h` for more information

## Remove cluster

remove cluster will stop cluster and remove ***ALL*** related data.

### remove cluster with ssh key-value pair

```shell
./hdt remove -c template/config.yaml -i ~/.ssh/id_rsa -u root
```

### remove cluster with passwd

```shell
./hdt remove -c template/config.yaml -p -u root
```

then type your password.

## Detailed configuration items

This section describes in detail the meaning of each field in the configuration file. The configuration file is divided into three large sections: global configuration items, monitoring component configuration items and other component configuration items.

### Global

```shell
global:
  # # Username to login via SSH
  user: "root"
  # # The path of SSH identity file
  key_path: "~/.ssh/hstream-aliyun.pem"
  # # SSH service monitor port
  ssh_port: 22
  # # Replication factors of store metadata
  meta_replica: 3
  # # Local path to MetaStore config file
  meta_store_config_path: ""
  # # Local path to HStore config file
  hstore_config_path: ""
  # # HStore config file can be loaded from network filesystem, for example, the config file
  # # can be stored in meta store and loaded via network request. Set this option to true will
  # # force store load config file from its local filesystem.
  disable_store_network_config_path: true
  # # Local path to HServer config file
  hserver_config_path: ""
  # # Global container configuration
  container_config:
    cpu_limit: 200
    memory_limit: 8G
    disable_restart: true
    remove_when_exit: true
```

Global section set the default configuration value for all other configuration items. Here are some notes:

- `meta_replica` set the replication factors of HStreamDB metadata. This value should not exceed the number of `hstore` instances.
- `meta_store_config_path`、`hstore_config_path` and `hserver_config_path` are configuration file path for `meta_store`、`hstore` and `hserver` in the control machine. If the paths are set, these configuration files will be synchronized to the specified location on the node where the respective instance is located, and the corresponding configuration items will be updated when the instance is started.
- `container_config` let you set resource limitations for all containers.

### monitor

```shell
monitor:
  # # Node exporter port
  node_exporter_port: 9100
  # # Node exporter image
  node_exporter_image: "prom/node-exporter"
  # # Cadvisor port
  cadvisor_port: 7000
  # # Cadvisor image
  cadvisor_image: "gcr.io/cadvisor/cadvisor:v0.39.3"
  # # List of nodes that won't be monitored.
  excluded_hosts: []
  # # root directory for all monitor related config files.
  remote_config_path: "/home/deploy/monitor"
  # # root directory for all monitor related data files.
  data_dir: "/home/deploy/data/monitor"
  # # Set up grafana without login
  grafana_disable_login: true
  # # Global container configuration for monitor stacks.
  container_config:
    cpu_limit: 200
    memory_limit: 8G
    disable_restart: true
    remove_when_exit: true
```

Monitor section sets configuration items related to `cadvisor` and `node-exporter`

### hserver

```shell
hserver:
  # # The ip address of the HServer
  - host: 10.1.0.10
    # # HServer docker image
    image: "hstreamdb/hstream"
    # # HServer listen port
    port: 6570
    # # HServer internal port
    internal_port: 6571
    # # HServer configuration
    server_config:
      # # HServer log level, valid values: [critical|error|warning|notify|info|debug]
      server_log_level: info
      # # HStore log level, valid values: [critical|error|warning|notify|info|debug|spew]
      store_log_level: info
      # # Specific server compression algorithm, valid values: [none|lz4|lz4hc]
      compression: lz4
    # # Root directory of HServer config files
    remote_config_path: "/home/deploy/hserver"
    # # Root directory of HServer data files
    data_dir: "/home/deploy/data/hserver"
    # # HServer container configuration
    container_config:
      cpu_limit: 200
      memory_limit: 8G
      disable_restart: true
      remove_when_exit: true
```

HServer section sets configuration items for `hserver`

### hstore

```shell
hstore:
  - host: 10.1.0.10
    # # HStore docker image
    image: "hstreamdb/hstream"
    # # HStore admin port
    admin_port: 6440
    # # Root directory of HStore config files
    remote_config_path: "/home/deploy/hstore"
    # # Root directory of HStore data files
    data_dir: "/home/deploy/data/store"
    # # Total used disks
    disk: 1
    # # Total shards
    shards: 2
    # # The role of the HStore instance.
    role: "Both" # [Storage|Sequencer|Both]
    # # When Enable_admin is turned on, the instance can receive and process admin requests
    enable_admin: true
    # # HStore container configuration
    container_config:
      cpu_limit: 200
      memory_limit: 8G
      disable_restart: true
      remove_when_exit: true
```

HStore section sets configuration items for `hstore`.

- `admin_port`: HStore service will listen on this port.
- `disk` and `shards`: Set total used disks and total shards. For example, `disk: 2` and `shards: 4` means the hstore will persistant data in two disks, and each disk will contain 2 shards.
- `role`: a HStore instance can act as a Storage, a Sequencer or both, default is both.
- `enable_admin`: set the HStore instance with an admin server embedded.

### meta-store

```shell
meta_store:
  - host: 10.1.0.10
    # # Meta-store docker image
    image: "zookeeper:3.6"
    # # Meta-store port, currently only works for rqlite. zk will
    # # monitor on 4001
    port: 4001
    # # Raft port used by rqlite
    raft_port: 4002
    # # Root directory of Meta-Store config files
    remote_config_path: "/home/deploy/metastore"
    # # Root directory of Meta-store data files
    data_dir: "/home/deploy/data/metastore"
    # # Meta-store container configuration
    container_config:
      cpu_limit: 200
      memory_limit: 8G
      disable_restart: true
      remove_when_exit: true
```

Meta-store section sets configuration items for `meta-store`.

- `port` and `raft_port`: these are used by `rqlite`

### monitor stack components

```shell
http_server:
  - host: 10.1.0.15
    # # Http_server docker image
    image: "hstreamdb/http-server"
    # # Http_server service monitor port
    port: 8081
    # # Root directory of http_server config files
    remote_config_path: "/home/deploy/http-server"
    # # Root directory of http_server data files
    data_dir: "/home/deploy/data/http-server"
    container_config:
      cpu_limit: 200
      memory_limit: 8G
      disable_restart: true
      remove_when_exit: true

prometheus:
  - host: 10.1.0.15
    # # Prometheus docker image
    image: "prom/prometheus"
    # # Prometheus service monitor port
    port: 9090
    # # Root directory of Prometheus config files
    remote_config_path: "/home/deploy/prometheus"
    # # Root directory of Prometheus data files
    data_dir: "/home/deploy/data/prometheus"
    # # Prometheus container configuration
    container_config:
      cpu_limit: 200
      memory_limit: 8G
      disable_restart: true
      remove_when_exit: true

grafana:
  - host: 10.1.0.15
    # # Grafana docker image
    image: "grafana/grafana-oss:main"
    # # Grafana service monitor port
    port: 3000
    # # Root directory of Grafana config files
    remote_config_path: "/home/deploy/grafana"
    # # Root directory of Grafana data files
    data_dir: "/home/deploy/data/grafana"
    # # Grafana container configuration
    container_config:
      cpu_limit: 200
      memory_limit: 8G
      disable_restart: true
      remove_when_exit: true

alertmanager:
  # # The ip address of the Alertmanager Server.
  - host: 10.0.1.15
    # # Alertmanager docker image
    image: "prom/alertmanager"
    # # Alertmanager service monitor port
    port: 9093
    # # Root directory of Alertmanager config files
    remote_config_path: "/home/deploy/alertmanager"
    # # Root directory of Alertmanager data files
    data_dir: "/home/deploy/data/alertmanager"
    # # Alertmanager container configuration
    container_config:
      cpu_limit: 200
      memory_limit: 8G
      disable_restart: true
      remove_when_exit: true

hstream_exporter:
  - host: 10.1.0.15
    # # hstream_exporter docker image
    image: "hstreamdb/hstream-exporter"
    # # hstream_exporter service monitor port
    port: 9200
    # # Root directory of hstream_exporter config files
    remote_config_path: "/home/deploy/hstream-exporter"
    # # Root directory of hstream_exporter data files
    data_dir: "/home/deploy/data/hstream-exporter"
    container_config:
      cpu_limit: 200
      memory_limit: 8G
      disable_restart: true
      remove_when_exit: true
```

Currently, HStreamDB monitor stack contains the following components：`node-exporter`, `cadvisor`, `http-server`, `prometheus`, `grafana`, `alertmanager` and `hstream-exporter`.  The global configuration of the monitor stack is available in [monitor](./quick-deploy-ssh.md#monitor) field. 

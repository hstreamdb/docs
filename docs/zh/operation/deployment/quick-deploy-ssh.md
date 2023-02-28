# Deployment with hdt

This document provides a way to start an HStreamDB cluster quickly using the deployment tool `hdt`.

## Pre-Require

- The local host needs to be able to connect to the remote server via SSH

- Make sure remote server has docker installed.

- Make sure that the logged-in user has `sudo` execute privileges and configure `sudo` to run without prompting for a password.

- For nodes that deploy `HStore` instances, mount the data disks to `/mnt/data*`, where `*` matches an incremental number starting from zero.
  - Each disk should be mounted to a separate directory. For example, if there are two data disks, `/dev/vdb` and `/dev/vdc`, then `/dev/vdb` should be mounted to `/mnt/data0`, and `/dev/vdc` should be mounted to `/mnt/data1`.

## Deploy `hdt` on the control machine

We'll use a deployment tool `hdt` to help us set up the cluster. The binaries are available here: https://github.com/hstreamdb/deployment-tool/releases.

1. Log in to the control machine and download the binaries.

2. Generate configuration template with command:

   ```shell
   ./hdt init
   ```

   The current directory structure will be as follows after running the `init` command:

   ```markdown
   ├── hdt
   └── template                 
       ├── config.yaml
       ├── logdevice.conf
       ├── alertmanager
       |   └── alertmanager.yml
       ├── grafana
       │   ├── dashboards
       │   └── datasources
       ├── prometheus
       ├── hstream_console
       ├── filebeat
       ├── kibana
       │   └── export.ndjson
       └── script
   ```

## Update `Config.yaml`

`template/config.yaml` contains the template for the configuration file. Refer to the description of the fields in the file and modify the template according to your actual needs.

As a simple example, we will be deploying a cluster on three nodes, each consisting of an HServer instance, an HStore instance, and a Meta-Store instance. In addition, we will deploy HStream Console, Prometheus, and HStream Exporter on another node. For hstream monitor stack, refer to [monitor components config](./quick-deploy-ssh.md#monitor-stack-components).

The final configuration file may looks like:

```yaml
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
  
hstream_console:
  - host: 172.24.47.172
  
prometheus:
  - host: 172.24.47.172
  
hstream_exporter:
  - host: 172.24.47.172
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

This section describes the meaning of each field in the configuration file in detail. The configuration file is divided into three main sections: global configuration items, monitoring component configuration items, and other component configuration items.

### Global

```yaml
global:
  # # Username to login via SSH
  user: "root"
  # # The path of SSH identity file
  key_path: "~/.ssh/hstream-aliyun.pem"
  # # SSH service monitor port
  ssh_port: 22
  # # Replication factors of store metadata
  meta_replica: 1
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
  # # use grpc-haskell framework
  enable_grpc_haskell: false
  # # Local path to ElasticSearch config file
  elastic_search_config_path: ""
  # # Only enable for linux kernel which support dscp reflection(linux kernel version
  # # greater and equal than 4.x)
  enable_dscp_reflection: false
  # # Global container configuration
  container_config:
    cpu_limit: 200
    memory_limit: 8G
    disable_restart: true
    remove_when_exit: true
```

The Global section is used to set the default configuration values for all other configuration items. 

- `meta_replica` set the replication factors of HStreamDB metadata logs. This value should not exceed the number of `hstore` instances.
- `meta_store_config_path`、`hstore_config_path` and `hserver_config_path` are configuration file path for `meta_store`、`hstore` and `hserver` in the control machine. If the paths are set, these configuration files will be synchronized to the specified location on the node where the respective instance is located, and the corresponding configuration items will be updated when the instance is started.
- `enable_grpc_haskell`: use `grpc-haskell` framework. The default value is false, which will use `hs-grpc` framework.
- `enable_dscp_reflection`: if your operation system version is greater and equal to linux 4.x, you can set this field to true. 
- `container_config` let you set resource limitations for all containers.

### Monitor

```yaml
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

The Monitor section is used to specify the configuration options for the `cadvisor` and `node-exporter`.

### HServer

```yaml
hserver:
  # # The ip address of the HServer
  - host: 10.1.0.10
    # # HServer docker image
    image: "hstreamdb/hstream"
    # # The listener is an adderss that a server advertises to its clients so they can connect to the server.
    # # Each listener is specified as "listener_name:hstream://host_name:port_number". The listener_name is
    # # a name that identifies the listener, and the "host_name" and "port_number" are the IP address and
    # # port number that reachable from the client's network. Multi listener will split by comma.
    # # For example: public_ip:hstream://39.101.190.70:6582
    advertised_listener: ""
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

The HServer section is used to specify the configuration options for the `hserver` instance.

- `advertised_listener`: please refer to [advertised_listeners](../advertised_listeners.md) for detailed information. 

  

### HAdmin

```yaml
hadmin:
  - host: 10.1.0.10
    # # HAdmin docker image
    image: "hstreamdb/hstream"
    # # HAdmin listen port
    admin_port: 6440
    # # Root directory of HStore config files
    remote_config_path: "/home/deploy/hadmin"
    # # Root directory of HStore data files
    data_dir: "/home/deploy/data/hadmin"
    # # HStore container configuration
    container_config:
      cpu_limit: 2.00
      memory_limit: 8G
      disable_restart: true
      remove_when_exit: true
```

The HAdmin section is used to specify the configuration options for the `hadmin` instance.

- Hadmin is not a necessary component. You can configure `hstore` instance to take on the functionality of `hadmin` by setting the configuration option `enable_admin: true` within the hstore.

- If you have both a HAdmin instance and a HStore instance running on the same node, please note that they cannot both use the same `admin_port` for monitoring purposes. To avoid conflicts, you will need to assign a unique `admin_port` value to each instance. 

### HStore

```yaml
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

The HStore section is used to specify the configuration options for the `hstore` instance.

- `admin_port`: HStore service will listen on this port.
- `disk` and `shards`: Set total used disks and total shards. For example, `disk: 2` and `shards: 4` means the hstore will persistant data in two disks, and each disk will contain 2 shards.
- `role`: a HStore instance can act as a Storage, a Sequencer or both, default is both.
- `enable_admin`: If the 'true' value is assigned to this setting, the current hstore instance will be able to perform the same functions as hadmin.

### Meta-store

```yaml
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

The Meta-store section is used to specify the configuration options for the `meta-store` instance.

- `port` and `raft_port`: these are used by `rqlite`

### Monitor stack components

```yaml
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
    port: 9250
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

Currently, HStreamDB monitor stack contains the following components：`node-exporter`, `cadvisor`, `hstream-exporter`,  `grafana`, `alertmanager` and `hstream-exporter`.  The global configuration of the monitor stack is available in [monitor](./quick-deploy-ssh.md#monitor) field. 

### Elasticsearch, Kibana and Filebeat

```yaml
elasticsearch:
  - host: 10.1.0.15
  # # Elasticsearch service monitor port
  port: 9200
  # # Elasticsearch docker image
  image: "docker.elastic.co/elasticsearch/elasticsearch:8.5.0"
  # # Root directory of Elasticsearch config files
  remote_config_path: "/home/deploy/elasticsearch"
  # # Root directory of Elasticsearch data files
  data_dir: "/home/deploy/data/elasticsearch"
  # # Elasticsearch container configuration
  container_config:
    cpu_limit: 2.00
    memory_limit: 8G
    disable_restart: true
    remove_when_exit: true

kibana:
  - host: 10.1.0.15
  # # Kibana service monitor port
  port: 5601
  # # Kibana docker image
  image: "docker.elastic.co/kibana/kibana:8.5.0"
  # # Root directory of Kibana config files
  remote_config_path: "/home/deploy/kibana"
  # # Root directory of Kibana data files
  data_dir: "/home/deploy/data/kibana"
  # # Kibana container configuration
  container_config:
    cpu_limit: 2.00
    memory_limit: 8G
    disable_restart: true
    remove_when_exit: true

filebeat:
  - host: 10.1.0.10
    # # Filebeat docker image
    image: "docker.elastic.co/beats/filebeat:8.5.0"
    # # Root directory of Filebeat config files
    remote_config_path: "/home/deploy/filebeat"
    # # Root directory of Filebeat data files
    data_dir: "/home/deploy/data/filebeat"
    # # Filebeat container configuration
    container_config:
      cpu_limit: 2.00
      memory_limit: 8G
      disable_restart: true
      remove_when_exit: true
```


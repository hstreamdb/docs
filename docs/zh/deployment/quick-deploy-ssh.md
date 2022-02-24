# 使用 Docker 和 SSH 快速部署

This document provides a way to quickly start a HStreamDB cluster using docker and SSH.

本文描述了如何使用 docker 和 ssh 快速部署 HStreamDB 集群。

## 部署要求

- 要求本地主机能通过 SSH 与远端服务器建立连接
- 远端服务器需要安装好 Docker

## 获取启动脚本

```shell
git clone git@github.com:hstreamdb/hstream.git
cd hstream/script
```

## 创建启动配置文件

需要创建一个 json 格式的配置文件。配置文件的内容清参考模板：`script/dev_deploy_conf_example.json`

```shell
{
  "hosts": {
    "remote_ssh_host1": "192.168.10.1",
    "remote_ssh_host2": "192.168.10.2",
    "remote_ssh_host3": "192.168.10.3",
    "remote_ssh_host4": "192.168.10.4"
  },
  "local_store_config_path": "$PWD/logdevice.conf",
  "hstreamdb_config_path": "",
  "zookeeper-host": [
    "remote_ssh_host2",
    "remote_ssh_host3",
    "remote_ssh_host4"
  ],
  "hstore-host": ["remote_ssh_host2", "remote_ssh_host3", "remote_ssh_host4"],
  "hstore-admin-host": ["remote_ssh_host1"],
  "hserver-host": ["remote_ssh_host2", "remote_ssh_host3", "remote_ssh_host4"]
}
```

- `hosts`：hosts 字段中以键值对的形式保存了远端服务器的名字和 ip 地址信息。服务器的名字是用户自定义的，主要用于在之后的配置项中引用远端服务器
- `local_store_config_path`：该字段中需要填入 `hstore` 配置文件的路径
  - 具体可以参考 [configuration file](deploy-docker.md) 中`创建配置文件`部分
- `hstreamdb_config_path`：该字段中需要填入 `hstreamdb` 配置文件的路径
  - 具体可以参考 [HStreamDB Configuration](../reference/config.md) 
  - 注意：该字段是一个选填字段，如果不填入任何值，则会使用默认配置项进行启动
- `zookeeper-host`：指定创建 Zookeeper 实例的远端服务器节点。
- `hstore-host`：指定创建 hstore 实例的远端服务器节点。
- `hstore-admin-host`：指定创建 hadmin 实例的远端服务器节点。
- `hserver-host`：指定创建 hserver 实例的远端服务器节点。

## 集群管理

- 在创建完配置文件之后，可以通过以下指令来启动/停止一个 HStreamDB 集群

  ```shell
  # 启动集群
  dev-deploy --remote "" simple --config config_path start
  # 停止集群：只停止启动的容器
  dev-deploy --remote "" simple --config config_path stop
  # 移除集群：停止启动的容器，同时移除持久化的所有数据
  dev-deploy --remote "" simple --config config_path remove
  ```

  

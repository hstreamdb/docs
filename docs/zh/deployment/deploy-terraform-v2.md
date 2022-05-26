# 使用 Terraform 在阿里云部署 HStream

本文档描述了如何使用 Terraform 在阿里云部署 HStream。

可以从 [hstreamdb/terraform](git@github.com:hstreamdb/terraform.git) 获取本文中所使用的资源。

```shell
git clone git@github.com:hstreamdb/terraform.git
cd aliyun
```

## 准备工作

假设您已经对 Terraform 有基本的了解，下面是一些对此可能有帮助的链接：

- [Terraform HCL 语言文档](https://www.terraform.io/language)
- [Terraform 平台教程](https://learn.hashicorp.com/terraform?utm_source=terraform_io)

另外，需要有阿里云账户并配置好密钥对来进行接下来的操作。

### 安装 Terraform

可参考 [Terraform 命令行工具安装文档](https://learn.hashicorp.com/tutorials/terraform/install-cli)。

## 部署配置

仓库 [hstreamdb/terraform](git@github.com:hstreamdb/terraform.git) 中用于
在阿里云部署的目录（./aliyun）结构如下：

```
| - file
     | - clusterCfg.json.tftpl // 集群部署配置文件
       - logdevice.conf        // HStore 存储组件配置文件
       - prometheus-cfg        // Prometheus 监控组件配置文件
       ...
  - main.tf                    // Terraform 部署脚本入口
  - start.py                   // 用于启动 HStream 集群的脚本
  - terraform.tfvars           // 可配置的 Terraform 变量文件
  ...
```

以上是有关集群启动和相关组件的配置文件，请仔细核对配置结果。

### 设定集群相关配置

可以通过修改 `file/clusterCfg.json.tftpl` 来配置将要部署的 `HStream` 集群。
可以参考文档
[使用 Docker 和 SSH 快速部署](https://hstream.io/docs/en/latest/deployment/quick-deploy-ssh.html#create-a-configuration-file) 中创建配置文件一节来完成配置。

::: warning
请注意，使用仓库 [hstreamdb/terraform](git@github.com:hstreamdb/terraform.git) 部署时，
有两处配置与上述参考链接不同
:::


- **不需要**手动填充 `hosts` 字段，在 Terraform 运行过程中将会进行自动填充


- 在配置服务器节点时可以参考以下两条规则

  + 节点可以被分为两类：
    - `hs-s*`：使用针对存储优化的阿里云实例，一般都配备了 NVMe SSD，
               主要用于运行存储相关服务。
    - `hs-c*`：用于运行监控和计算相关服务。

  + 符号 `*` 是数字编号



- 例如，在默认配置文件中，将会启动 3 台 `hs-s*` 节点和 1 台
  `hs-c*` 节点：

  |  节点名 | 阿里云实例类型            |             运行的服务                                        |
  | :-----: | :-----------------------: | :-----------------------------------------------------------: |
  | `hs-s1` |      `i3en.2xlarge`       | `hstore`、`hserver`、`zookeeper`、`node-exporter`、`cadvisor` |
  | `hs-s2` |      `i3en.2xlarge`       | `hstore`、`hserver`、`zookeeper`、`node-exporter`、`cadvisor` |
  | `hs-s3` |      `i3en.2xlarge`       | `hstore`、`hserver`、`zookeeper`、`node-exporter`、`cadvisor` |
  | `hs-c1` |       `c5.4xlarge`        |   `prometheus`、`hstore-admin`、`node-exporter`、`cadvisor`   |

### 配置 HStore 存储组件

可以参考文档
[使用 Docker 和 SSH 快速部署](https://hstream.io/docs/en/latest/deployment/quick-deploy-ssh.html#create-a-configuration-file) 中创建配置文件一节来完成配置 `file/logdevice.conf` 处的配置文件。

与教程中不同，此处不需要手动修改配置中的 `zookeeper_uri` 字段，
在 Terraform 运行过程中将会进行自动填充。

### 配置 Terraform 变量

通过修改文件 `terraform.tfvars` 可以配置一些部署相关选项：

- `region`：部署实例的区域
- `image_id`：将要使用镜像的 ID，默认为 Ubuntu 20.04
- `cidr_block`: 配置 VPC 的网段
- `key_pair_name`：已经配置好，将要用于登陆和其他操作的密钥对名称
- `store_config` 与 `cal_config`: 在此处部署时，每台阿里云实例都被分为存储节点和计算节点中的一类，它们都可以分别配置如下属性：
  + `node_count`：此类节点的数量
  + `instance_type`: 此类节点的实例类型
  + `volume_type`: 此类节点的存储类型

## 部署并启动集群

配置好以上配置项后便可以部署并启动集群。

首先，配置需要的环境变量：

```shell
export ALICLOUD_ACCESS_KEY="<账户的 access key id>"
export ALICLOUD_SECRET_KEY="<账户的 secret access key>"
```

然后运行脚本 `./start.py -k <配置对应的私钥路径>` 启动集群。

# 使用 Terraform 部署 HStream

本文档描述了如何在支持 [Terraform 提供者](https://www.terraform.io/language/providers)的云平台上部署 HStream。
此流程主要基于文档[使用 Docker 和 SSH 快速部署](./quick-deploy-ssh.md)中所提供的部署方法。

## 寻找所需的 Terraform 提供者

Terraform 根据声明性的配置文件组织和部署和销毁服务器资源，
这些文件依赖于称为 “提供者” 的插件来与云提供商、SaaS 提供商或其它 API 进行交互。

常见的提供者可以在 [Terraform Register](https://registry.terraform.io/browse/providers) 中找到。
在本文档中，我们将使以用[华为云](https://www.huaweicloud.com/)部署 HStream 为例。

可以在此处查看有关华为云 Terraform 提供者的更多信息：

- [GitHub 仓库](https://github.com/huaweicloud/terraform-provider-huaweicloud)
- [Terraform Register 文档](https://registry.terraform.io/providers/huaweicloud/huaweicloud/latest/docs)

## 配置服务器资源

我们将介绍几个步骤来展示如何使用 Terraform 部署 HStream。

### 配置基本常量

Terraform 将云资源基础设施表示为 HCL 语言中的代码。
设置一些变量供我们在后面的开发中参考是很有用的。

首先，创建一个名为 `terraform.tfvars` 的文件。一个例子是：

```hcl
# 可参考 https://developer.huaweicloud.com/en-us/endpoint
# https://support.huaweicloud.com/intl/en-us/api-eip/eip_api_0003.html
region              = "cn-southwest-2"
region_network_type = "5_sbgp"

# 可参考 https://support.huaweicloud.com/intl/en-us/productdesc-evs/en-us_topic_0014580744.html
data_disk_type = "SSD"
# Int, GiB, 10..32768
data_disk_sizes = {
  "hadmin"    = 40
  "zookeeper" = 40
  "hstore"    = 40
  "hserver"   = 40
}

num_instances = {
  "hadmin"    = 1
  "zookeeper" = 3
  "hstore"    = 3
  "hserver"   = 3
}

image_name    = "Ubuntu 20.04 server 64bit"
flavor_name   = "s6.small.1"
key_pair_name = "your key pair name goes here"

network_uuid = "your network UUID goes here"
```

像以上定义 `num_instances` 这样的语法在 HCL 语言中定义了一个 `map`。
容器 `map` 中可用这样的语法来索引键所对应的值：`num_instances["hserver"]`。

这里所设定的变量仅作为示例使用，可以任意添加或删除这些变量中的一个或多个；
此处没有对这些变量所设定的值做出任何限制，只要它们是合法的值即可。

### 配置 ECS 实例

可以通过设置 `resource` 语句块来声明我们部署所需要的计算资源。一个例子是：

```hcl
resource "huaweicloud_compute_instance" "hstore" {
  count = var.num_instances["hstore"]

  name        = "hstore-${count.index}"
  image_name  = var.image_name
  flavor_name = var.flavor_name
  key_pair    = var.key_pair_name

  network {
    access_network = true
    uuid           = var.network_uuid
  }

  data_disks {
    type = var.data_disk_type
    size = var.data_disk_sizes["hstore"]
  }
}
```

（在[这里](https://registry.terraform.io/providers/huaweicloud/huaweicloud/latest/docs/resources/compute_instance)可以找到 `huaweicloud_compute_instance` 的文档）

上述的 `resource` 语句块声明了一列表的 ECS 实例，可以通过列表名和下标来访问单个实例：
例如使用类似 `huaweicloud_compute_instance.hstore[n]` 的语法（在这里 `n` 在不超出数量范围越界的情况下
可以是任意数字字面量、`int` 类型的变量，或者通过使用通配符 `*` 来全选）。

按照惯例，将这部分代码片段写入一个名叫 `main.tf` 的源文件，并且将其存到 Terraform 项目的根目录（也就是先前存放 `terraform.tfvars` 文件时所创建的文件夹）。
在写 `resource` 语句块前，还需要添加两个依赖。

首先，需要声明**每个**引用自 `terraform.tfvars` 文件的变量：

```hcl
variable "data_disk_sizes" {}

variable "num_instances" {
  type = map(string)
}
```

然后，引入并配置所使用的提供者依赖：

```hcl
terraform {
  required_providers {
    huaweicloud = {
      source  = "huaweicloud/huaweicloud"
      version = "~> 1.33.0"
    }
  }
}

provider "huaweicloud" {
  region = var.region
  # 可以使用通过分别设定 `HW_ACCESS_KEY` 与 `HW_SECRET_KEY` 环境变量
  # 来配置此处的 `access_key` 和 `secret_key` 变量
}
```

（可以在此处的 [Terraform Register 文档](https://registry.terraform.io/providers/huaweicloud/huaweicloud/latest/docs)中找到华为云提供者的最新版本信息）

### 配置所需网络通信

文档[使用 Docker 和 SSH 快速部署](./quick-deploy-ssh.md)中所提供的部署方法需要我们能够通过 SSH 连接
远程服务器，这需要远程服务器有可访问的公网 IP 并且暴露 SSH 所需的端口。

在华为云上创建 ECS 实例默认不会提供可供访问的公网 IP。可以通过使用另外一项服务，弹性公网 IP（Elastic IP）来获取并绑定到新创建（或已经存在）的 ECS 实例上。下面的代码示范了如何创建一列表的弹性公网 IP，然后分别绑定
到每台实例上：

```hcl
resource "huaweicloud_vpc_eip" "hserver_eip" {
  count = var.num_instances["hserver"]

  publicip {
    type = var.region_network_type
  }

  bandwidth {
    name        = "hserver-network-${count.index}"
    size        = 100
    share_type  = "PER"
    charge_mode = "traffic"
  }
}

resource "huaweicloud_compute_eip_associate" "hserver_associated" {
  count = var.num_instances["hserver"]

  public_ip   = huaweicloud_vpc_eip.hserver_eip[count.index].address
  instance_id = huaweicloud_compute_instance.hserver[count.index].id
}
```

上文所使用的 `output` 语句块可以用来选择性地展示应用一个 Terraform 计划所得到的结果。由于我们需要通过 IP 地址来
建立 SSH 连接，在这里使用它将其打印出来：


```hcl
output "hservers_access_ip" {
  value = huaweicloud_compute_instance.hserver[*].public_ip
}
```

### 配置所需的软件包依赖

通过 Terraform 部署 HStream 需要在 ECS 实例上有安装好的 [Docker](https://docs.docker.com/)。
在[这里](https://docs.docker.com/engine/install/centos/)可以找到安装 Docker 有关的指引信息。
Ubuntu 用户可以参考（[此处](https://docs.docker.com/engine/install/ubuntu/)）的文档。

## Deployment with Terraform CLI

我们已经在 HCL 文件中声明好了我们想要部署 HStream 集群所需的服务器资源。下一步是检查这些声明是否符合我们的目的和预期。在 Terraform HCL 项目根目录中运行以下命令：

```shell
terraform plan
```

在准备好创建服务器资源所需的 HCL 脚本后，可以通过运行命令 `terraform plan` 来检查执行这一计划将要发生的
的变化是否符合预期。执行这个命令需要在 `PATH` 中可以找到安装好的 Terraform。另外，需要设定好华为云的鉴权信息（通过设置环境变量或者在 HCL 脚本的 `provider` 语句块中配置）。

Then it would print something like (if running successfully):

如果运行命令成功，将会打印出类似如下的信息：

```
Terraform used the selected providers to generate the following execution plan. Resource actions are indicated with the following symbols:
  + create

Terraform will perform the following actions:

  # huaweicloud_compute_eip_associate.hadmin_associated[0] will be created
  + resource "huaweicloud_compute_eip_associate" "hadmin_associated" {
      + fixed_ip    = (known after apply)
      + id          = (known after apply)
      + instance_id = (known after apply)
      + port_id     = (known after apply)
      + public_ip   = (known after apply)
      + region      = (known after apply)
    }

...... ......

Plan: 12 to add, 0 to change, 0 to destroy.

Changes to Outputs:
  + hadmin_access_ip    = [
      + (known after apply),
    ]
  + hadmin_public_ip    = [
      + (known after apply),
    ]

...... ......

─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────

Note: You didn't use the -out option to save this plan, so Terraform can't guarantee to take exactly these actions if you run "terraform apply" now.
```

在预览和检查过将可能发生的变化后，如果变化不符合预期，可以通过修改配置文件来修正；如果计划符合预期行为，
则可以直接通过运行[命令](https://www.terraform.io/cli/commands/apply) `terraform apply` 以应用它。
如果运行成功，输出的信息会和之前类似，加上一个新出现的确认框：


```
Do you want to perform these actions?
  Terraform will perform the actions described above.
  Only 'yes' will be accepted to approve.

  Enter a value:
```

预定的计划完成后，Terraform 将生成一些输出。使用下面的命令可以查看运行的结果：

```shell
terraform output
```

which yields:

```
Outputs:

hadmin_access_ip = [
  "192.168.0.64",
]
hadmin_public_ip = [
  tostring(null),
]
```

可以通过使用 `output` 语句块来简化部署 HStream 集群的流程。在这里演示了如何
获取部署所必须的公网和内网 IP 信息，可以从每一台实例的
字段 `public_ip` 和 `access_ip_v4` 获得。注意，如果在创建好 ECS 实例后绑定弹性
公网 IP，需要使用命令 `terraform refresh` 刷新结果后才能在 `terraform output`
的结果中看到所需的信息。

```
Outputs:

hadmin_access_ip = [
  "192.168.0.64",
]
hadmin_public_ip = [
  "1**.6*.1**.1**",
]
```

[命令](https://www.terraform.io/cli/commands/output) `terraform output -json` 将输出结
果以 JSON 格式展示，可以方便地与 `jq` 一类的命令行工具进行交互。


```shell
terraform output -json | jq .'zookeeper_access_ip.value'
[
  "192.168.0.149"
]
```

在获取完所有需要的公网与内网 IP 后，便可
以配置 HStream 集群快速启动配置文件来进行部署了。
接下来的步骤可以参考文档[使用 Docker 和 SSH 快速部署](./quick-deploy-ssh.md)。


Terraform 还提供了命令来根据创建资源所使用的 TCL 文件来删除所创建的资源：

```shell
terraform apply -destroy
```

（注意：截至 2020 年2 月 25 日，华为云在应用该命令时不会删除创建计算实例创建的磁盘）。

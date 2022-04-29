# Deploy HStream on AWS with Terraform

This document describes how to deploy HStream on AWS with Terraform.

You can download related resources from
[hstreamdb/terraform](git@github.com:hstreamdb/terraform.git):

```shell
git clone git@github.com:hstreamdb/terraform.git
```

## Pre-Require

We assume that you have a basic understanding of terraform. Here are some
resources may help:

- [Terraform Language Documentation](https://www.terraform.io/language)
- [Terraform tutorials](https://learn.hashicorp.com/terraform?utm_source=terraform_io)

Also, you need to have an AWS account and create your own `key-pair` so that you
can use it to access AWS EC2 instance later.

## Install Terraform

Refer to
[terraform install doc](https://learn.hashicorp.com/tutorials/terraform/install-cli)

## Configuration

Let's take a look at the structure of the project first.

```
| - file
     | - clusterCfg.json.tftpl // cluster configuration file
       - logdevice.conf        // hstore configuration file
       - prometheus-cfg        // prometheus related config files
       ...
  - main.tf                    // terraform main program
  - start.py                   // a script use to start HStream cluster
  - terraform.tfvars           // external variables used by terraform
  ...
```

Listed above are the key components and associated configuration files to start
the cluster, which you need to configure very carefully.

### Configure cluster-related information

You should modify `file/clusterCfg.json.tftpl` to configure your own `HStream`
cluster. You can refer to
[Create a Configuration File](https://hstream.io/docs/en/latest/deployment/quick-deploy-ssh.html#create-a-configuration-file)
to complete the configuration file. **Notice: There are two places that differ
from the part described in the link**：

- You **don't** need to fill in the `hosts` filed, this part will be done by
  terraform.

- Refer to the corresponding server nodes according to the following rules:

  - Nodes can be divided into two categories：
    - `hs-s*`：This class of nodes uses AWS storage-optimized instances
      configured with NVMe SSDs and is mainly used to boot store related
      services.
    - `hs-c*`: This class of nodes are typically used to boot compute related
      services.
  - The `*` can only be a number

- As an example, in our default configuration, we start 3 `hs-s*` nodes and 1
  `hs-c*` node:

  |  nodes  | Amazon EC2 Instance Types |                           instance                            |
  | :-----: | :-----------------------: | :-----------------------------------------------------------: |
  | `hs-s1` |      `i3en.2xlarge`       | `hstore`、`hserver`、`zookeeper`、`node-exporter`、`cadvisor` |
  | `hs-s2` |      `i3en.2xlarge`       | `hstore`、`hserver`、`zookeeper`、`node-exporter`、`cadvisor` |
  | `hs-s3` |      `i3en.2xlarge`       | `hstore`、`hserver`、`zookeeper`、`node-exporter`、`cadvisor` |
  | `hs-c1` |       `c5.4xlarge`        |   `prometheus`、`hstore-admin`、`node-exporter`、`cadvisor`   |

### Configure hstore

Refer to
[Create a configuration file](https://hstream.io/docs/en/latest/deployment/deploy-docker.html#create-a-configuration-file)
to modify`file/logdevice.conf`. Also, **no need** to modify the `zookeeper_uri`
field, the program will modify it automatically.

### Configure terraform

Modify `terraform.tfvars` to config terraform

- `region`：The instance's region
- `image_id`：The system image you want to use, we choose ubuntu 20.04 here
- `delete_block_on_termination`：Whether to delete block devices at the same
  time when deleting instances
- `cidr_block`: Specific VPC
- `private_key_path`：The private key's path to access AWS instance
- `key_pair_name`：The key-pair's name you created before
- `store_config` and `cal_config`: We divide all AWS instances into two
  categories: one that performs primarily storage tasks, which we call "storage
  nodes", and another that performs primarily compute tasks, which we call
  "compute nodes". Each node has the following attributes：
  - `node_count`：The count of node instances
  - `instance_type`: Amazon EC2 Instance Types
  - `volume_type`: The type of storage volume
    - `volume_size`、`iops`、`throughput` are attributes of storage volume

## Set Up the Cluster

Now, we can set up our cluster.

First, you should export `AWS_SECRET_ACCESS_KEY`

```shell
export AWS_ACCESS_KEY_ID="<Your access key id>"
export AWS_SECRET_ACCESS_KEY="<Your secret access key>"
```

Then you can use `./start.py -k <your private key's path>` to set up the
cluster.

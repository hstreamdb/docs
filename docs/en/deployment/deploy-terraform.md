# Deploy HStream with Terraform

This document describes how to deploy HStream with Terraform on a cloud platform that
supports [Terraform provider](https://www.terraform.io/language/providers). This process is based on
the method provided in [Quick Deployment with Docker and SSH](./quick-deploy-ssh.md).

## Find The Provider

Terraform organise and deploy/destroy server resources in terms of declarative configuration files,
which relies on plugins called "providers" to interact with cloud providers, SaaS providers, and
other APIs.

Common providers can be found in
the [Terraform Register](https://registry.terraform.io/browse/providers). In this document, we are
going to use the [Huawei Cloud](https://www.huaweicloud.com) provider to deploy HStream as an
example.

You can check here for more information about the Huawei Cloud provider:

- the [GitHub repository](https://github.com/huaweicloud/terraform-provider-huaweicloud)
- the [Terraform Register
  documentation](https://registry.terraform.io/providers/huaweicloud/huaweicloud/latest/docs)

## Set Up the Server Resources

We are going to introduce a few steps to show how to deploy HStream with Terraform.

### Set Up basic constants

Terraform expressed infrastructure as code in the HCL language. It's useful to set some variables
for us to refer to in the following development.

Firstly, create a file named `terraform.tfvars`. An example would be like:

```hcl
# See https://developer.huaweicloud.com/en-us/endpoint
# https://support.huaweicloud.com/intl/en-us/api-eip/eip_api_0003.html
region              = "cn-southwest-2"
region_network_type = "5_sbgp"

# See https://support.huaweicloud.com/intl/en-us/productdesc-evs/en-us_topic_0014580744.html
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

The syntax for `num_instances` defines a map in the HCL language. The value can be accessed by key
in the following form `num_instances["hserver"]`.

The variables here are just for example usage, feel free to set, add or remove one or more of them.
There are no restrictions on the contents of variables here such as `data_disk_sizes`
, `num_instances`, `image_name` and so on as long as they are valid.

### Set Up ECS instance

We use the `resource` block to declare what compute resource we would like to have. An example would
be like:

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

(the documentation of `huaweicloud_compute_instance` can be
found [here](https://registry.terraform.io/providers/huaweicloud/huaweicloud/latest/docs/resources/compute_instance))

The resource block declares a list (by `count`) of ECS instances each can be accessed by its name
and index, for example, `huaweicloud_compute_instance.hstore[n]` (where `n` stands for arbitrary
number not out of bounds, an int variable, or the `*` to select all).

This fragment of code should be put in an HCL file, let us call it `main.tf` and save it to the
Terraform project root (that is, the folder that contains your previously defined `terraform.tfvars`
file) for convention and convenience. Before we put the resource block in, two extra dependencies
should be added first.

Firstly, we declare every variable we would like to refer to in the previously
defined `terraform.tfvars` file, using the following syntax.

```hcl
variable "data_disk_sizes" {}

variable "num_instances" {
  type = map(string)
}
```

Secondly, we introduce the provider which would be used.

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
  # setting `access_key` and `secret_key` via environment
  # variables `HW_ACCESS_KEY` and `HW_SECRET_KEY`
}
```

(the latest version can be found in
the [Terraform Register documentation](https://registry.terraform.io/providers/huaweicloud/huaweicloud/latest/docs)
of Huawei Cloud)

### Set Up Network

The deployment method provided in [Quick Deployment with Docker and SSH](./quick-deploy-ssh.md)
needs SSH access to remote servers, which needs each server to have accessible (public) IP
addresses.

The ECS instances of Huawei Cloud do not provide public IP addresses by default. Another service,
Elastic IP is invoked to serve as a purpose for associating ECS instance to a newly created (or
already existing) EIP. The following code fragment demonstrates a way to create a list of EIPs, then
associate them for each instance respectively.

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

The `output` block is useful to observe the result that applying a Terraform plan would yield. In
this case, we are going to inspect the public IP which can be used for establishing SSH connections.

```hcl
output "hservers_access_ip" {
  value = huaweicloud_compute_instance.hserver[*].public_ip
}
```

### Set Up Package Dependency

Deploy HStream with Terraform needs [Docker](https://docs.docker.com/) to be installed on ECS
instances. Install instructions can be
found [here](https://docs.docker.com/engine/install/centos/) ([here](https://docs.docker.com/engine/install/ubuntu/)
for Ubuntu).

## Deployment with Terraform CLI

As we had already declared what server resources do we want to have to deploy an HStream cluster in
the HCL file. The next step is to check whether these declarations are desirable for our purpose.
Run this command in the Terraform HCL project root:

```shell
terraform plan
```

Once the HCL script for needed server resources are properly set, you can run `terraform plan` to
see what changes that Terraform [plans](https://www.terraform.io/cli/commands/plan) to make to your
infrastructure. This command needs an installed Terraform executable on your PATH, adds on the
Huawei Cloud access key and secret key can be found by the provider (i.e. via environment variables
or configuration in the provider block).

Then it would print something like (if running successfully):

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

After previewing and checking whether the proposed changes match what you expected, you can turn to
modify the configuration or apply these changes. Apply these changes using another
[command](https://www.terraform.io/cli/commands/apply), `terraform apply`. The output would be
similar to the above one, plus:

```
Do you want to perform these actions?
  Terraform will perform the actions described above.
  Only 'yes' will be accepted to approve.

  Enter a value:
```

After applying the plan, Terraform would produce some output. To inspect them, run:

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

Using the HCL `output` block makes configuring HStream cluster easier. It is necessary to obtain
public IP addresses and access IP address, which is in the fields `public_ip` and `access_ip_v4` of
each compute instance. Note that if you are binding EIPs to ECSs after creating each instance (like
what we had done above), to yield the right `public_ip` you must do `terraform refresh`
before `terraform output`.

```
Outputs:

hadmin_access_ip = [
  "192.168.0.64",
]
hadmin_public_ip = [
  "1**.6*.1**.1**",
]
```

The [command](https://www.terraform.io/cli/commands/output) `terraform output -json` displays output
in JSON, which is easily interactive with command-line tools like `jq`.

```shell
terraform output -json | jq .'zookeeper_access_ip.value'
[
  "192.168.0.149"
]
```

After all needed `public_ip`s and `access_ip_v4`s are yielded, you can use them to configure the
HStream cluster configuration and deploy it with created server resources. You can refer
to [Quick Deployment with Docker and SSH](./quick-deploy-ssh.md) for more details.

Terraform also provides a command for deleting the resources created according to the TCL file, run:

```shell
terraform apply -destroy
```

(noted that at this time, 2020/2/25, Huawei Cloud would not delete the disks created by creating
compute instances when applying this command).

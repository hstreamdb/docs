# Quick Deployment with Docker and SSH

This document provides a way to start an HStreamDB cluster quickly using Docker
and SSH.

## Pre-Require

- The local host needs to be able to connect to the remote server via SSH
- Using SSH Config File to help remote connection
- The remote server has docker installed

## Fetching the Startup Script

```shell
mkdir script
wget -O script/dev-deploy https://raw.githubusercontent.com/hstreamdb/hstream/main/script/dev-deploy
wget -O script/dev_deploy_conf_example.json https://raw.githubusercontent.com/hstreamdb/hstream/main/script/dev_deploy_conf_example.json
```

## Create a Configuration File

Create a JSON-format config file to fit your situation. There is an example in
`script/dev_deploy_conf_example.json`.

```shell
{
    "hosts": {
        "remote_ssh_host1": "192.168.10.1",
        "remote_ssh_host2": "192.168.10.2",
        "remote_ssh_host3": "192.168.10.3",
        "remote_ssh_host4": "192.168.10.4"
    },
    "zookeeper": {
        "persistent-dir": "/data/zookeeper",
        "hosts": [
            "remote_ssh_host2",
            "remote_ssh_host3",
            "remote_ssh_host4"
        ],
        "enable-metrics-provider": true
    },
    "hstore": {
        "image": "hstreamdb/hstream:v0.9.3",
        "persistent-dir": "/data/store",
        "hosts": [
            "remote_ssh_host2",
            "remote_ssh_host3",
            "remote_ssh_host4"
        ],
        "local_config_path": "$PWD/logdevice.conf",
        "remote_config_path": "/root/.config/dev-deploy/logdevice.conf"
    },
    "hstore-admin": {
        "image": "hstreamdb/hstream:v0.9.3",
        "memory": "1024m",
        "cpus": "0.5",
        "hosts": [
            "remote_ssh_host1"
        ]
    },
    "hserver": {
        "image": "hstreamdb/hstream:v0.9.3",
        "memory": "2048m",
        "cpus": "1.5",
        "hosts": [
            "remote_ssh_host2",
            "remote_ssh_host3",
            "remote_ssh_host4"
        ]
    }
}
```

The `hosts` field stores remote server information in the form of key-value
pairs. The key is the hostname of the server in the SSH configuration file and
the value is the IP address of the server.

The field `hosts`, among other top-level configuration field objects which each
is about a service kind, is required in the configuration file. Other fields
are: `zookeeper`, `hstore`, `hstore-admin` and `hserver`. The configuration file
also supports filling in configuration items related to monitoring components
(such as prometheus, grafana, etc.), which are not core components and are not
described here.

The HStore configuration must be set before deployment. The path of config is
stored in the field `hstore.local_config_path` and `hstore.remote_config_path`,
respectively. The former is the path to the HStore config file on the local
machine which is to run the deployment script, while the latter is the
destination that the HStore config file would be uploaded to during deployment.
You can refer to the `Create a configuration file` section in the documentation
[Manual Deployment with Docker](deploy-docker.md) to create an HStore config
file.

The configuration of HServers is configured with the field
`hstore.local_config_path` and `hstore.remote_config_path`. You can refer to the
documentation [HStreamDB Configuration](../../reference/config.md) for details.
This is optional and if the value is not filled in, the default configuration
will be used to start.

Each JSON object for configuring a kind of service has a field named `hosts`
which indicates which server nodes are used to start corresponding service
instances.

::: tip Check the ZooKeeper related fields in the HStore config file to make
sure that the ZooKeeper nodes information is consistent.
:::

Each JSON object for configuring a kind of service also had some extra optional
fields for configuring the resource constraints of containers used by this kind
of service, such as `memory` and `cpus`. The usages of the above two are
analogous the ones in the
[Docker options](https://docs.docker.com/config/containers/resource_constraints/).

## Cluster Management

- After creating the configuration file, you can start/stop a hstreamdb cluster
  with these commands

  ```shell
  # start cluster
  dev-deploy --remote "" simple --config config_path start
  # stop cluster: just stop containers
  dev-deploy --remote "" simple --config config_path stop
  # remove cluster: stop containers and delete persistent data
  dev-deploy --remote "" simple --config config_path remove
  ```

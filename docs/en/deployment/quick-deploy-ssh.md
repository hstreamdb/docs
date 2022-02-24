# Quick Deployment with Docker and SSH

This document provides a way to quickly start a HStreamDB cluster using docker
and SSH.

## Pre-Require

- The local host needs to be able to connect to the remote server via SSH
- Using SSH Config File to help remote connect

  - [Reference](https://linuxize.com/post/using-the-ssh-config-file/)

- Remote server has docker installed

## Fetching the Startup Script

```shell
git clone git@github.com:hstreamdb/hstream.git
cd hstream/script
```

## Create a Configuration File

Create a json-format config file to fit your situation. There is an example in
`script/dev_deploy_conf_example.json`.

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

- `hosts`: The hosts field stores server information in the form of key-value
  pairs. The key is the HostName of the server in the SSH configuration file,
  while the value is the IP address of the server.
- `local_store_config_path`: Fill in the path of `hstore config file`.
  - Refer `Create a configuration file` part in
    [configuration file](deploy-docker.md) to create a hstore config file.
- `hstreamdb_config_path`: Fill in the path of `hstreamdb config file`.

  - Refer [HStreamDB Configuration](../reference/config.md) for detail
  - This is optional, if the value is not filled in, the default configuration
    will be used to start.
- `zookeeper-host`：Specify which server nodes are used to start Zookeeper
  instances.

  - **NOTES**: Check the zk-related fields in the `hstore config file` to make
    sure the zk node information is consistent
- `hstore-host`：Specify which server nodes are used to start hstore instances.
- `hstore-admin-host`：Specify which server nodes are used to start hadmin
  instances.
- `hserver-host`：Specify which server nodes are used to start hserver
  instances.

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

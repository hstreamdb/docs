# Cluster Management

HStreamDB supports horizontal scaling for both HServer and HStore. This piece of
documentation will focus on how to configure an HServer Cluster

## HServer Cluster: `seed-nodes`

### Bootstrap with `hstream init`

The default value of `seed-nodes` is "localhost" ("localhost:6571"). If the port
is not given to the URL, it will use the port value specified in `internal-port`
option. If you want to bootstrap an HServer cluster, please provide the address
of all the initial nodes in the configuration file of every seed, such as
"192.168.1.1,192.168.1.2,192.168.1.3". Or pass the seed nodes through the
command line option `--seed-nodes`.

```sh
hstream-server --config-path ... --seed-nodes "192.168.1.1,192.168.1.2,192.168.1.3" --server-id 100
hstream-server --config-path ... --seed-nodes "192.168.1.1,192.168.1.2,192.168.1.3" --server-id 101
hstream-server --config-path ... --seed-nodes "192.168.1.1,192.168.1.2,192.168.1.3" --server-id 102
```

After starting all the seed nodes, it is required to send an init signal to one
of the nodes in the cluster to notify that the cluster is ready to serve. The
init signal can be sent with "hstream init" command.

```sh
hstream --host 192.168.1.1 init
```

### New Node Join with `seed-nodes` Configuration

If you want to add a new node to the cluster, you can simply start the new node
with the `seed-nodes` configuration which contains one or more existing nodes in
the cluster. The new node will automatically join the cluster. Or pass the
seed nodes through the command line option `--seed-nodes`.

```sh
hstream-server --config-path ... --seed-nodes "192.168.1.1" --server-id 103
```

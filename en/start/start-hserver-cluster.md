# Start Local HServer Cluster with Docker

## Requirement

Please refer to [quickstart with docker](./quickstart-with-docker.md#installation) and make sure you have met all prerequisites.

## Start a 3-node local HStream-Server Cluster in Docker

::: warning
Do NOT use this configuration in your production environment!
:::

If you did not follow quick start and have not yet got a running local HStream-Server,
read this [section](#start-with-docker-compose) instead.

### If you already have a running standalone HStream-Server

You can manually start the other 2 servers and pass the same zkuri as the running server
to create a hserver cluster.
Note that every server needs the following options to be unique to work properly:

- **server-id     : the id has to be an integer. This is the identifier of every server.**
- **port          : the port number that client connects to.**
- **internal-port : the internal channel for server communication.**

For example, run the following commands to start a cluster with 3 nodes,
only if you followed quick start and did not change the [config](https://github.com/hstreamdb/hstream/raw/main/docker/quick-start.yaml) :

```sh
docker run -it --rm --name some-hstream-server-1 -v $DATA_DIR:/data/store --network hstream-quickstart hstreamdb/hstream hstream-server --store-config /data/store/logdevice.conf --zkuri 127.0.0.1:2181 --port 6580 --internal-port 6581 --server-id 101
```

```sh
docker run -it --rm --name some-hstream-server-2 -v $DATA_DIR:/data/store --network hstream-quickstart hstreamdb/hstream hstream-server --store-config /data/store/logdevice.conf --zkuri 127.0.0.1:2181 --port 6590 --internal-port 6591 --server-id 102
```

### Start with docker compose

Download [quickstart.yaml] and **copy the exact following contents under services section**:

```yaml
  hserver0:
    image: hstreamdb/hstream
    depends_on:
      - zookeeper
      - hstore
    ports:
      - "127.0.0.1:6580:6580"
    expose:
      - 6580
    networks:
      - hstream-quickstart
    volumes:
      - ${DATA_DIR:-/data/store}:/data/store
    command:
      - bash
      - "-c"
      - |
        set -e
        /usr/local/script/wait-for-storage.sh hstore 6440 zookeeper 2181 600 \
        /usr/local/bin/hstream-server \
        --host 0.0.0.0 --port 6590 \
        --internal-port 6591 \
        --address $$(hostname -I | awk '{print $$1}') \
        --server-id 101 \
        --zkuri zookeeper:2181 \
        --store-config /data/store/logdevice.conf \
        --store-admin-host hstore --store-admin-port 6440 \
        --replicate-factor 3 \

  hserver1:
    image: hstreamdb/hstream
    depends_on:
      - zookeeper
      - hstore
    ports:
      - "127.0.0.1:6590:6590"
    expose:
      - 6590
    networks:
      - hstream-quickstart
    volumes:
      - ${DATA_DIR:-/data/store}:/data/store
    command:
      - bash
      - "-c"
      - |
        set -e
        /usr/local/script/wait-for-storage.sh hstore 6440 zookeeper 2181 600 \
        /usr/local/bin/hstream-server \
        --host 0.0.0.0 --port 6590 \
        --internal-port 6591 \
        --address $$(hostname -I | awk '{print $$1}') \
        --server-id 102 \
        --zkuri zookeeper:2181 \
        --store-config /data/store/logdevice.conf \
        --store-admin-host hstore --store-admin-port 6440 \
        --replicate-factor 3 \
```

You can easily get a cluster with:

```sh
docker-compose -f quick-start.yaml up -d
docker-compose -f quick-start.yaml logs -f hserver hserver0 hserver1
```

## Start HStreamDB's interactive SQL CLI

[Start a cli session](./quickstart-with-docker.md#start-hstreamdb-s-interactive-sql-cli) in the similar way as when you have a standalone server.
An HStream Server Cluster does not affect how you use CLI.

```sh
docker run -it --rm --name some-hstream-cli --network host hstreamdb/hstream hstream-client --port 6570 --client-id 1
```

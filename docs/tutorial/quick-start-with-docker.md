Quickstart with Docker
======================

## Installation

### 1. Install docker

!!! note
    If you have already installed docker, you can skip this step.

See [Install Docker Engine](https://docs.docker.com/engine/install/),
and install it for your operating system. Please carefully check that you
meet all prerequisites.

Confirm that the Docker daemon is running:

```sh
docker verison
```

!!! Tips
    On Linux, Docker needs root privileges. You can also run Docker as
    a non-root user, see [Post-installation steps for Linux][non-root-docker].

### 2. Pull image

Pull the latest released image of HStreamDB from Docker Hub:

```sh
docker pull hstreamdb/hstream
```


## Start HStreamDB Server

### 1. Create a bridge network

The bridge network will enable the containers to communicate as a single
cluster while keeping them isolated from external networks.

```sh
docker network create -d bridge some-hstreamdb-net
```

### 2. Start the server

```sh
docker run -d --network some-hstreamdb-net --name some-hstreamdb hstreamdb/hstream
```

Datas are stored in the `VOLUME /data`, which can be used with
`-v /your/host/dir:/data` (see [use volumes](https://docs.docker.com/storage/volumes/)).

Default lisenling port is `6560`, you can expose the port outside of your host (e.g., via `-p` on `docker run`).

!!! warning
    If you expose the port outside of your host, it will be open to anyone.


## Connecting via redis-cli

```sh
docker run -it --rm --network some-hstreamdb-net redis redis-cli -h some-hstreamdb -p 6560
```

```
some-hstreamdb:6560> xadd users * name alice age 20
"1599444243554-0"
some-hstreamdb:6560> xadd users * name bob age 20
"1599444249940-0"
some-hstreamdb:6560> xrange users - +
1) 1) "1599444243554-0"
   2) 1) "name"
      2) "alice"
      3) "age"
      4) "20"
2) 1) "1599444249940-0"
   2) 1) "name"
      2) "bob"
      3) "age"
      4) "20"
```


## Building from source

See [this document](../development/build-from-source.md) for more details.




[non-root-docker]: https://docs.docker.com/engine/install/linux-postinstall/#manage-docker-as-a-non-root-user

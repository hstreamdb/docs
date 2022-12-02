#!/bin/sh
set -e

if [ -z "$(find go.mod)" ];
then
  exit 1
fi

if [ -z "$(find main.go)" ];
then
  exit 1
fi

if [ -z "$(find examples)" ];
then
  exit 1
fi

ZOOKEEPER_IMAGE='zookeeper:3.6'
HSTREAM_IMAGE='hstreamdb/hstream:latest'

DATA_STORE='/tmp/hstream/data'

mkdir -p $DATA_STORE


docker run -td --network host \
  --rm                        \
  --name hs-test-zk           \
    $ZOOKEEPER_IMAGE

sleep 5

docker run -td --network host    \
  --rm                           \
  --name hs-test-hstore          \
  -v     $DATA_STORE:/data/store \
    $HSTREAM_IMAGE               \
      ld-dev-cluster             \
        --root /data/store       \
        --use-tcp

sleep 5

docker run -td --network host                                       \
  --name hs-test-hserver0                                           \
  -v     $DATA_STORE:/data/store                                    \
    $HSTREAM_IMAGE                                                  \
      hstream-server                                                \
        --store-config /data/store/logdevice.conf --log-level debug \
        --port 6570 --server-id 0

sleep 2

docker run -td --network host                                       \
  --name hs-test-hserver1                                           \
  -v     $DATA_STORE:/data/store                                    \
    $HSTREAM_IMAGE                                                  \
      hstream-server                                                \
        --store-config /data/store/logdevice.conf --log-level debug \
        --port 6571 --server-id 1

sleep 2

docker run -td --network host                                       \
  --name hs-test-hserver2                                           \
  -v     $DATA_STORE:/data/store                                    \
    $HSTREAM_IMAGE                                                  \
      hstream-server                                                \
        --store-config /data/store/logdevice.conf --log-level debug \
        --port 6572 --server-id 2

sleep 10

docker ps

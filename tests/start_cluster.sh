#!/bin/bash
set -e

docker-compose -f assets/quick-start.yaml up -d
docker-compose -f assets/quick-start.yaml run --no-deps --rm hserver \
  bash -c 'set -e; \
    export timeout=120; \
    until ( \
      hstream --host hserver --port 6570 node check-running -n 1 && \
      hadmin server --host hserver --port 6570 status \
    ); do \
      >&2 echo "Waiting for cluster..."; \
      sleep 10; \
      timeout=$((timeout - 10)); \
      [ $timeout -le 0 ] && echo "Timeout!" && exit 1; \
    done; \
    exit 0 \
  '

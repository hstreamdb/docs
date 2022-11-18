#!/bin/bash
set -e

docker-compose -f assets/quick-start.yaml up -d
docker-compose -f assets/quick-start.yaml run --no-deps --rm hserver0 \
  bash -c 'set -e; \
    export timeout=120; \
    until ( \
      /usr/local/bin/hadmin server --host hserver0 --port 6570 status && \
      /usr/local/bin/hadmin server --host hserver1 --port 6572 status \
    ); do \
      >&2 echo "Waiting for cluster..."; \
      sleep 10; \
      timeout=$((timeout - 10)); \
      [ $timeout -le 0 ] && echo "Timeout!" && exit 1; \
    done; \
    exit 0 \
  '

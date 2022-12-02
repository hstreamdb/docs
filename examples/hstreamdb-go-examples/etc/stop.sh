#!/bin/sh
set -e

docker stop hs-test-hserver0 || echo "hs-test-hserver0 not running"
docker stop hs-test-hserver1 || echo "hs-test-hserver1 not running"
docker stop hs-test-hserver2 || echo "hs-test-hserver2 not running"

docker stop hs-test-hstore  || echo "hs-test-hstore not running"
docker stop hs-test-zk      || echo "hs-test-zk not running"

docker rm hs-test-hserver0 || echo "hs-test-hserver0 not running"
docker rm hs-test-hserver1 || echo "hs-test-hserver1 not running"
docker rm hs-test-hserver2 || echo "hs-test-hserver2 not running"

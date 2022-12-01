#!/bin/bash
set -e

IMAGE=$(cat assets/quick-start.yaml | grep "image:.*hstreamdb/hstream" | head -n 1 | awk '{print $2}')

echo "Docker image: $IMAGE"
STREAM=$(\
  docker run -t --rm --name some-hstream-cli --network host \
    $IMAGE hstream --port 6570 sql -e "CREATE STREAM demo;" \
  | sed 's/\s*$//')  # remove trailing spaces
[ "$STREAM" != "demo" ] && echo "\"$STREAM\"" && exit 1;

docker run -td --rm --name some-hstream-cli --network host \
  $IMAGE hstream --port 6570 sql -e \
  "SELECT * FROM demo WHERE humidity > 70 EMIT CHANGES;"

sleep 5

docker exec -t some-hstream-cli hstream --port 6570 sql -e "INSERT INTO demo (temperature, humidity) VALUES (22, 80);"
docker exec -t some-hstream-cli hstream --port 6570 sql -e "INSERT INTO demo (temperature, humidity) VALUES (15, 20);"
docker exec -t some-hstream-cli hstream --port 6570 sql -e "INSERT INTO demo (temperature, humidity) VALUES (31, 76);"
docker exec -t some-hstream-cli hstream --port 6570 sql -e "INSERT INTO demo (temperature, humidity) VALUES ( 5, 45);"
docker exec -t some-hstream-cli hstream --port 6570 sql -e "INSERT INTO demo (temperature, humidity) VALUES (27, 82);"
docker exec -t some-hstream-cli hstream --port 6570 sql -e "INSERT INTO demo (temperature, humidity) VALUES (28, 86);"

sleep 5

OUTPUT=$(mktemp)
echo "-> get results..."
docker logs some-hstream-cli > $OUTPUT
cat $OUTPUT
echo "-> compare results..."
cmp --silent $OUTPUT tests/quick_start_sql_001.txt || \
    (docker-compose -f assets/quick-start.yaml logs && exit 1)
echo "-> clean..."
docker exec -t some-hstream-cli hstream --port 6570 sql -e "DROP STREAM demo ;"
docker rm -f some-hstream-cli

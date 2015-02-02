#!/bin/bash
export PG_IMAGE=crealytics/debian_postgres
export PG_CONTAINER=caas_postgres
export PG_PORT_5432_TCP_PORT=23496

docker build --no-cache=false -t $PG_IMAGE .
docker rm $PG_CONTAINER

docker run -d -t -i -p $PG_PORT_5432_TCP_PORT:5432 --name $PG_CONTAINER $PG_IMAGE

set -e

function docker_cleanup {
  docker stop $PG_CONTAINER
  docker rm -v $PG_CONTAINER
}
trap docker_cleanup EXIT

export CLJ_ENV=test
lein test

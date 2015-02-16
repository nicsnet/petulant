#!/bin/bash
export PG_IMAGE=crealytics/debian_postgres
export PG_CONTAINER=caas_postgres
export PG_PORT_5432_TCP_PORT=5434

docker build --no-cache=false -t $PG_IMAGE .
docker rm $PG_CONTAINER

docker run -d -t -i -p $PG_PORT_5432_TCP_PORT:5432 --name $PG_CONTAINER $PG_IMAGE

set -e

function docker_cleanup {
  docker stop $PG_CONTAINER
  docker rm -v $PG_CONTAINER
}
trap docker_cleanup EXIT

# because boot2docker
export DOCKER_IP=$( [ $DOCKER_HOST ] && echo "192.168.59.103" || echo "localhost")

export CAAS_DB_URL_TEST="jdbc:postgresql://${DOCKER_IP}:5434/caas_test?user=caas_test&password=l3tm31n"
export CAAS_DB="caas_test"
export CAAS_DB_USER="caas_test"
export CAAS_DB_PASS="l3tm31n"
export CAAS_DB_HOST=$DOCKER_IP
export CAAS_DB_PORT=5434


lein with-profile test ragtime migrate
lein midje

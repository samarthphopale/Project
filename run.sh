#!/bin/bash

set -e

ENV=${1:-local}

if [ "$ENV" == "local" ]; then
    COMPOSE_FILE="docker/compose.local.yaml"
    ENV_FILE="env/.env.local"

elif [ "$ENV" == "ec2" ]; then
    COMPOSE_FILE="docker/compose.ec2.yaml"
    ENV_FILE="env/.env.ec2"

else
    echo "Usage: ./run.sh [local|ec2]"
    exit 1
fi


echo "================================="
echo "Using:"
echo "Compose : $COMPOSE_FILE"
echo "Env     : $ENV_FILE"
echo "================================="


cleanup() {
    echo ""
    echo "Stopping containers..."

    docker compose \
    --env-file $ENV_FILE \
    -f $COMPOSE_FILE \
    down --remove-orphans

    echo "Cleanup completed"
}

trap cleanup SIGINT SIGTERM


echo "Cleaning existing local and EC2 containers..."

# Stop local stack if running
docker compose \
--env-file env/.env.local \
-f docker/compose.local.yaml \
down --remove-orphans || true


# Stop EC2 stack if running
docker compose \
--env-file env/.env.ec2 \
-f docker/compose.ec2.yaml \
down --remove-orphans || true


echo "Building images..."

docker compose \
--env-file $ENV_FILE \
-f $COMPOSE_FILE \
build


echo "Starting services..."

docker compose \
--env-file $ENV_FILE \
-f $COMPOSE_FILE \
up --remove-orphans
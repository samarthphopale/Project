#!/bin/bash

# Exit immediately if any command fails
set -e

echo "🛑 Cleaning up old containers and orphans..."
docker compose -f docker/compose.yaml down --remove-orphans

echo "🧱 Rebuilding services from scratch..."
docker compose -f docker/compose.yaml build --no-cache

echo "🚀 Launching the application stack..."
docker compose -f docker/compose.yaml up

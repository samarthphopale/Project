#!/bin/bash

set -e

echo "🛑 Stopping Ollama..."

# Stop Ollama process
pkill ollama || echo "Ollama is not running"

sleep 2


echo "🛑 Stopping Tailscale..."

sudo brew services stop tailscale


echo ""
echo "=============================="
echo "Local AI Environment Stopped"
echo "=============================="

echo "Checking Ollama..."
if curl -s http://localhost:11434/api/tags > /dev/null
then
    echo "❌ Ollama is still running"
else
    echo "✅ Ollama stopped"
fi


echo "Checking Tailscale..."

if tailscale status > /dev/null 2>&1
then
    echo "⚠️ Tailscale still active"
else
    echo "✅ Tailscale stopped"
fi
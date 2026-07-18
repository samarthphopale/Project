#!/bin/bash

set -e

echo "🚀 Starting Tailscale service..."

sudo brew services start tailscale

echo "⏳ Waiting for Tailscale daemon..."

sleep 5

echo "🔐 Connecting Tailscale..."

tailscale up


echo "✅ Checking Tailscale..."

TAILSCALE_IP=$(tailscale ip -4)

if [ -z "$TAILSCALE_IP" ]; then
    echo "❌ Tailscale IP not available"
    exit 1
fi

echo "Tailscale IP: $TAILSCALE_IP"


echo ""
echo "🚀 Starting Ollama..."

export OLLAMA_HOST=0.0.0.0:11434

nohup ollama serve > ~/ollama.log 2>&1 &


echo "⏳ Waiting for Ollama..."

for i in {1..20}
do
    if curl -s http://localhost:11434/api/tags > /dev/null
    then
        echo "✅ Ollama is running"
        break
    fi

    sleep 2
done


echo ""
echo "=============================="
echo "AI Environment Ready"
echo "=============================="
echo "Ollama URL:"
echo "http://$TAILSCALE_IP:11434"
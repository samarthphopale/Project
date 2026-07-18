#!/bin/bash

set -e

echo "🛑 Stopping Ollama..."

# Stop Ollama if running via brew
brew services stop ollama >/dev/null 2>&1 || true

# Kill any remaining Ollama processes
pkill -f ollama >/dev/null 2>&1 || true

sleep 2

echo "🛑 Stopping Tailscale..."

# 🔥 IMPORTANT: Disconnect from Tailscale network
sudo tailscale down >/dev/null 2>&1 || true

# Stop brew service (if installed via brew)
brew services stop tailscale >/dev/null 2>&1 || true

# Stop macOS GUI app if running
osascript -e 'quit app "Tailscale"' >/dev/null 2>&1 || true

# Kill any remaining daemon processes
sudo pkill -f tailscaled >/dev/null 2>&1 || true

sleep 2

echo ""
echo "=============================="
echo "Local AI Environment Stopped"
echo "=============================="

# -----------------------------
# Check Ollama
# -----------------------------
echo ""
echo "Checking Ollama..."

if curl -s http://localhost:11434/api/tags > /dev/null
then
    echo "❌ Ollama is still running"
else
    echo "✅ Ollama stopped"
fi

# -----------------------------
# Check Tailscale
# -----------------------------
echo ""
echo "Checking Tailscale..."

TS_STATUS=$(tailscale status 2>/dev/null || true)

if echo "$TS_STATUS" | grep -Eq "Logged out|stopped"
then
    echo "✅ Tailscale fully stopped"
else
    echo "❌ Tailscale still connected"
    echo ""
    echo "Current status:"
    echo "$TS_STATUS"
fi
#!/bin/bash
set -a
source ./podman/.env
set +a

echo "🔨 Building & starting..."
podman-compose -f ./podman/heap-pod.yml up -d # --build (rebuild)

echo "⏳ Waiting for services..."
sleep 10

echo "🔍 Checking health..."
HEALTH=$(curl -s http://localhost:8080/health)
echo "$HEALTH" | jq .

if [[ "$HEALTH" != *"ok"* ]]; then
  echo "❌ Server not healthy, aborting"
  exit 1
fi

echo -e "\n📝 Test WRITE (creating item)..."
CREATE_RESPONSE=$(curl -s -X POST -H "X-API-Key: $API_KEY" -H "Content-Type: application/json" \
  -d '{"type":"TODO","title":"Simple Test Item","notes":"Automated test","priority":1}' \
  http://localhost:8080/api/items)
echo "$CREATE_RESPONSE" | jq .

echo -e "\n📖 Test READ (fetching items)..."
curl -s -H "X-API-Key: $API_KEY" http://localhost:8080/api/items | jq .

echo -e "\n✅ Build, write, and read test complete!"

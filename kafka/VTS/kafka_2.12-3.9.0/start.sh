#!/bin/bash

# Old scirpt to start Kafka and ZooKeeper
# ./bin/zookeeper-server-start.sh config/zookeeper.properties &
# ./bin/kafka-server-start.sh config/server.properties &

# New script to start Kafka and ZooKeeper with stale broker ID check
# Start ZooKeeper
echo "🟡 Starting ZooKeeper..."
./bin/zookeeper-server-start.sh config/zookeeper.properties > zookeeper.log 2>&1 &
ZOOKEEPER_PID=$!

# Wait for ZooKeeper to start by checking the port (2181)
echo "⏳ Waiting for ZooKeeper to start..."
while ! nc -z localhost 2181; do
  sleep 1
done
echo "✅ ZooKeeper is running."

# Check and delete stale Kafka broker node
if echo "ls /brokers/ids" | ./bin/zookeeper-shell.sh localhost:2181 | grep -q "\[0\]"; then
  echo "⚠️  Stale broker ID 0 found. Deleting..."
  echo "delete /brokers/ids/0" | ./bin/zookeeper-shell.sh localhost:2181
  echo "✅ Stale broker ID removed."
fi

# Start Kafka
echo "🟡 Starting Kafka..."
./bin/kafka-server-start.sh config/server.properties > kafka.log 2>&1 &
KAFKA_PID=$!

# Wait for Kafka to start (port 9092 by default)
echo "⏳ Waiting for Kafka to start..."
while ! nc -z localhost 9092; do
  sleep 1
done
echo "✅ Kafka is running."

# Final status
echo ""
echo "🎉 All services are up and running!"
echo "📌 ZooKeeper PID: $ZOOKEEPER_PID"
echo "📌 Kafka PID: $KAFKA_PID"


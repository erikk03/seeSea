#!/bin/bash

# Old script to stop Kafka and ZooKeeper
# ./bin/zookeeper-server-stop.sh
# ./bin/kafka-server-stop.sh

# New script to stop Kafka and ZooKeeper with PID checks
echo "⛔ Attempting to stop Kafka and ZooKeeper..."

# Find Kafka process (matches the exact main class)
KAFKA_PID=$(ps -ef | grep 'kafka.Kafka' | grep -v grep | awk '{print $2}')

# Find ZooKeeper process (matches zookeeper.QuorumPeerMain or similar)
ZOOKEEPER_PID=$(ps -ef | grep 'zookeeper' | grep -v grep | grep -E 'QuorumPeerMain|zookeeper-server' | awk '{print $2}')

if [[ -n "$KAFKA_PID" ]]; then
  echo "🔻 Stopping Kafka (PID: $KAFKA_PID)..."
  kill "$KAFKA_PID"
  wait "$KAFKA_PID" 2>/dev/null
  echo "✅ Kafka stopped."
else
  echo "ℹ️ Kafka is not running."
fi

if [[ -n "$ZOOKEEPER_PID" ]]; then
  echo "🔻 Stopping ZooKeeper (PID: $ZOOKEEPER_PID)..."
  kill "$ZOOKEEPER_PID"
  wait "$ZOOKEEPER_PID" 2>/dev/null
  echo "✅ ZooKeeper stopped."
else
  echo "ℹ️ ZooKeeper is not running."
fi

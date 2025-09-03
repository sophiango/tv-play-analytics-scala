SET 'execution.checkpointing.interval' = '10s';
SET 'state.backend' = 'filesystem';
SET 'state.checkpoints.dir' = 'file:///tmp/flink-checkpoints';
SET 'execution.checkpointing.mode' = 'AT_LEAST_ONCE';
SET 'execution.checkpointing.unaligned' = 'true';

CREATE CATALOG local_iceberg WITH (
  'type'='iceberg',
  'catalog-type'='hadoop',
  'warehouse'='file:///iceberg/warehouse',
  'property-version'='1'
);

USE CATALOG local_iceberg;
CREATE DATABASE IF NOT EXISTS tv;
USE tv;

CREATE TABLE IF NOT EXISTS watch_events (
  user_id INT,
  show_id INT,
  event_ts FLOAT,
  duration_sec INT,
  device STRING
) WITH (
  'format-version' = '2',
  'write.format.default' = 'parquet'
);

CREATE TEMPORARY TABLE IF NOT EXISTS kafka_watch_events (
  user_id INT,
  show_id INT,
  event_ts FLOAT,
  duration_sec INT,
  device STRING
) WITH (
  'connector' = 'kafka',
  'topic' = 'tv_play_events',
  'properties.bootstrap.servers' = 'kafka:29092',
  'properties.group.id' = 'flink-group',
  'scan.startup.mode' = 'earliest-offset',
  'format' = 'json',
  'json.timestamp-format.standard' = 'ISO-8601'
);

-- 5. Insert Streaming Data
INSERT INTO watch_events
SELECT user_id, show_id, event_time, duration_sec, device FROM kafka_watch_events;

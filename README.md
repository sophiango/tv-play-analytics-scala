# Real-time TV Play Event Analytics with Flink SQL, Kafka, and Apache Iceberg

## Overview
This project is a real-time data pipeline for analyzing TV play event streams using Apache Flink SQL, Kafka, and Apache Iceberg. Events are ingested from Kafka and stored into Iceberg tables using ad-hoc SQL jobs executed via the Flink SQL CLI.

### Project Goals
- Stream TV playback events into Kafka.
- Use Flink SQL to process streaming data.
- Store data into Apache Iceberg tables.
- Use Docker Compose for local development setup.

---

## Architecture
```txt
┌──────────┐     ┌────────────┐     ┌────────────┐     ┌────────────┐
│ Producer │ --> │   Kafka    │ --> │ Flink SQL  │ --> │  Iceberg DB│
└──────────┘     └────────────┘     └────────────┘     └────────────┘
```

---

## Stack
- **Apache Kafka**: Stream ingestion.
- **Apache Flink SQL**: Real-time processing and job execution.
- **Apache Iceberg**: Data lake table format.
- **Docker Compose**: Environment orchestration.

---

## How to Run
1. Start the environment:
```bash
docker compose up --build
```

2. Produce sample Kafka messages (optional helper script can be added).

3. Run Flink SQL Client manually:
```bash
docker exec -it jobmanager ./bin/sql-client.sh
```

4. Inside the Flink SQL CLI:
```sql
-- Load and run init.sql manually
-- to create catalog, tables, and streaming INSERT query
```

---

## Directory Structure
```txt
.
├── Dockerfile.flink              # Custom Flink image with connectors
├── docker-compose.yml            # All services: Kafka, Flink, etc.
├── flink-config/                 # log4j config, optional settings
├── flink-checkpoints/           # Checkpointing volume
├── iceberg_warehouse/           # Iceberg warehouse volume
├── init.sql                      # SQL to setup pipeline
├── README.md                     # You are here
└── .gitignore                    # Git hygiene
```

---

## .gitignore Suggestions
```gitignore
# Local warehouse data
iceberg_warehouse/

# Flink checkpoints and recovery
flink-checkpoints/

# Flink logs and config overrides
flink-config/

# IntelliJ, VSCode, etc.
.idea/
.vscode/

# Compiled
*.class
*.jar
*.log
*.out
*.tmp
```

---

## What Didn't Work
This section documents the efforts that failed or caused unexpected issues:

### 1. Fat JAR Compilation
- **Issue**: Could not compile Scala fat JAR with Iceberg and Flink dependencies due to missing transitive jars and dependency hell.
- **Resolution**: Switched to Flink SQL CLI with all connectors pre-packaged.

### 2. `catalog-type=rest`
- **Issue**: Failed to get REST Iceberg catalog working via Docker Compose due to dependency loading and hostname resolution issues (`iceberg-rest: name not known`).
- **Resolution**: Reverted to `catalog-type=hadoop`.

### 3. Checkpointing Behavior
- **Issue**: When Flink checkpointing was enabled, no data appeared in the Iceberg table.
- **Resolution**: Suspected the checkpointing folder volume wasn't properly writeable or lacked sync between JobManager and TaskManager.

### 4. Flink + Iceberg Compatibility
- **Issue**: Got `LinkageError` and `ClassNotFoundException` with certain versions of Iceberg/metrics libraries.
- **Resolution**: Use consistent Flink and Iceberg versions; avoid duplicating metrics libraries.

### 5. Visibility of Iceberg Data
- **Issue**: Could see data in Kafka table (`kafka_watch_events`), but `watch_events` remained empty unless checkpointing was disabled.
- **Resolution**: Flink requires checkpointing for stateful sinks (like Iceberg). Must ensure shared writeable volumes and consistent metadata.

### 6. Job Fails with Table Not Found
- **Issue**: Iceberg sink job failed if the target table `tv_play_analytics` didn't exist.
- **Resolution**: Create the sink table manually in `init.sql` before inserting.

### 7. SQL CLI Session Reset
- **Issue**: `sql-client.sh` session doesn't persist previous created catalog or databases.
- **Resolution**: Always re-run `init.sql` if using CLI outside the original startup script.

---

## What Still Not Work
Regardless multiple attempts, data didn't arrive at sink table (watch_events) as expected.

---

## Status
✅ Kafka to Flink SQL working.
⚠️ Iceberg write path is functional **only if** checkpointing is configured and shared volumes are correct.

---

## License
MIT

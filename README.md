
# 📺 TV Play Analytics with Apache Flink, Kafka, and Iceberg

This project demonstrates a real-time streaming data pipeline using:
- Apache Kafka for message ingestion
- Apache Flink (Scala) for processing and transformation
- Apache Iceberg as the data lake storage format
- Flink Table API and SQL for stream-table abstraction

The end goal is to read TV play events from Kafka, parse and transform them with Flink, and write them into partitioned Iceberg tables.

---

## 🧱 Architecture

```
TV Events → Kafka → Flink (Scala + Table API) → Iceberg (via Hadoop or REST catalog)
```

---

## 📂 Project Structure

```
tv_play_analytics/
├── docker-compose.yml         # Services: Kafka, Flink, Iceberg REST catalog
├── flink-job/
│   ├── build.sbt              # Dependencies (Flink, Iceberg)
│   └── src/main/scala/
│       ├── Main.scala         # Job logic: Kafka → Parse → TableEnv SQL → Iceberg sink
│       └── TVPlayEvent.scala  # Case class and deserializer
|		└── TVPlayEventDeserializer.scala
├── warehouse/         # Iceberg table warehouse (volume)
└── README.md
```

---

## 🔁 What Works

- ✅ Kafka consumer working inside Flink
- ✅ Flink TableEnvironment creation and SQL execution
- ✅ Kafka source table defined via SQL
- ✅ Parsing Kafka JSON events to case classes (`TVPlayEvent`)
- ✅ Dockerized Flink/Kafka integration with volume mounting

---

## ⚠️ What Didn't Work

### ❌ Iceberg Integration with Flink (Scala)
Despite multiple attempts, Flink failed to find the Iceberg catalog factory.

#### Problems Encountered:
- `"Could not find factory for identifier 'iceberg'"` error
- ClassNotFound exceptions for Hadoop classes like `org.apache.hadoop.conf.Configuration`
- Missing shaded JARs (`iceberg-flink-runtime`, `flink-table-planner-blink`, etc.)
- Dependency hell trying to align Flink, Iceberg, and Hadoop versions

### 🧨 Attempts Made
- Built custom Dockerfile with JARs copied into `/opt/flink/lib`
- Used both `catalog-type=hadoop` and `catalog-type=rest`
- Tried both Maven Central and Iceberg’s own repo
- Mounted JARs into Flink JobManager container manually
- Switched between Scala and PyFlink — same missing JAR/class issues

---

## 🧪 Next Steps (Recommended)

💡 Instead of continuing with Scala + fat JAR:

### ✅ Use Flink SQL Client + Docker
- Write all logic using Flink SQL DDL + INSERT INTO statements
- Mount `.sql` files and run from CLI using `flink-sql-client`
- Avoid classpath and build issues entirely

---

## 🙏 Lessons Learned

- Apache Flink + Iceberg integration requires **perfect dependency alignment** and shaded JARs
- Even for `catalog-type=rest`, Flink requires Iceberg JARs with Hadoop transitive deps
- PyFlink and Scala both suffer from JAR management friction
- Flink SQL Client + Docker is a great low-friction alternative

---

## 👩‍💻 Author

**Sophia Ngo** — *Data Engineering Enthusiast, Full-Stack Explorer*
[🌐 sophiango.site](https://sophiango.site) | 📧 ngo_sophia@icloud.com

---

## 📌 Project Goal

Build an end-to-end portfolio piece demonstrating:
- Kafka stream processing
- Flink Table API / SQL pipeline
- Modern data lake writing using Apache Iceberg

Despite technical blockers, the core structure and learnings are still valuable.

---

## 🐳 How to Run (Partial Working)

```bash
# Start Kafka, Flink, etc.
docker compose up -d

# Submit fat JAR (if working)
http://localhost:8081/#/submit
```

---

## 🗂️ TODO (If Revisited)

- [ ] Switch to Flink SQL client and `.sql` file orchestration
- [ ] Use MinIO + Iceberg REST catalog setup
- [ ] Add Grafana + Prometheus monitoring
- [ ] Produce mock TV events continuously to Kafka

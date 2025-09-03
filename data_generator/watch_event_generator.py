import random
from datetime import datetime
import json
import time

from kafka import KafkaProducer

"""
Example of event
{
    user_id: 6-10 digit-random,
    show_id: 6-10 digit-random,
    event_ts: datetime,
    duration_sec: int,
    device: string
}
"""

DEVICE_TYPE=["mobile", "tv", "tablet", "web"]

producer = KafkaProducer(
    bootstrap_servers='localhost:9092',
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

def generate_event():
    return {
        "user_id": random.randint(100000, 1000000000),
        "show_id": random.randint(100000, 1000000000),
        "event_ts": time.time(),
        "duration_sec": random.randint(1, 5400),
        "device": random.choice(DEVICE_TYPE)
    }

if __name__ == "__main__":
    while True:
        print("Generate data")
        event = generate_event()
        producer.send("tv_play_events", event)
        print("Sent ", event)
        time.sleep(1)

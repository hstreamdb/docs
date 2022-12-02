# [common]

# https://github.com/hstreamdb/hstreamdb-py/blob/main/examples/snippets/guides.py
import asyncio
import hstreamdb
import os

# NOTE: Replace with your own host and port
host = os.getenv("GUIDE_HOST", "127.0.0.1")
port = os.getenv("GUIDE_PORT", 6570)
stream_name = "your_stream"
subscription = "your_subscription"


# Run: asyncio.run(main(your_async_function))
async def main(*funcs):
    async with await hstreamdb.insecure_client(host=host, port=port) as client:
        for f in funcs:
            await f(client)


# [common]


# [create-stream]
async def create_stream(client):
    await client.create_stream(
        stream_name, replication_factor=1, backlog=24 * 60 * 60, shard_count=1
    )


# [create-stream]


# [delete-stream]
async def delete_stream(client):
    await client.delete_stream(stream_name, ignore_non_exist=True, force=True)


# [delete-stream]


# [list-streams]
async def list_streams(client):
    ss = await client.list_streams()
    for s in ss:
        print(s)


# [list-streams]


# [create-subscription]
async def create_subscription(client):
    await client.create_subscription(
        subscription,
        stream_name,
        ack_timeout=600,
        max_unacks=10000,
        offset=hstreamdb.SpecialOffset.EARLIEST,
    )


# [create-subscription]


# [delete-subscription]
async def delete_subscription(client):
    await client.delete_subscription(subscription, force=True)


# [delete-subscription]


# [list-subscription]
async def list_subscriptions(client):
    subscriptions = await client.list_subscriptions()
    for s in subscriptions:
        print(s)


# [list-subscription]


# [list-shards]
async def list_shards(client):
    shards = client.list_shards(stream_name)
    print(list(shards))


# [list-shards]


# [append-records]
async def append_records(client):
    payloads = [b"some_raw_binary_bytes", {"msg": "hi"}]
    rs = await client.append(stream_name, payloads)
    for r in rs:
        print("Append done, ", r)


# [append-records]


# [buffered-append-records]
class AppendCallback(hstreamdb.BufferedProducer.AppendCallback):
    count = 0

    def on_success(self, stream_name, payloads, stream_keyid):
        self.count += 1
        print(f"Batch {self.count}: Append success with {len(payloads)} payloads.")

    def on_fail(self, stream_name, payloads, stream_keyid, e):
        print("Append failed!")
        print(e)


async def buffered_append_records(client):
    p = client.new_producer(
        append_callback=AppendCallback(),
        size_trigger=10240,
        time_trigger=0.5,
        retry_count=2,
    )

    for i in range(50):
        await p.append(stream_name, b"some_raw_binary_bytes")
        await p.append(stream_name, {"msg": "hello"})

    await p.wait_and_close()


# [buffered-append-records]


# [subscribe-records]
class Processing:
    count = 0
    max_count: int

    def __init__(self, max_count):
        self.max_count = max_count

    async def __call__(self, ack_fun, stop_fun, rs_iter):
        print("max_count", self.max_count)
        rs = list(rs_iter)
        for r in rs:
            self.count += 1
            print(f"[{self.count}] Receive: {r}")
            if self.max_count > 0 and self.count >= self.max_count:
                await stop_fun()
                break

        await ack_fun(r.id for r in rs)


async def subscribe_records(client):
    consumer = client.new_consumer("new_consumer", subscription, Processing(10))
    await consumer.start()


# [subscribe-records]


# [read-reader]
async def read_reader(client):
    offset = hstreamdb.ShardOffset()
    offset.specialOffset = hstreamdb.SpecialOffset.EARLIEST
    max_records = 10
    async with client.with_reader(
        stream_name, "your_reader_id", offset, 1000
    ) as reader:
        records = await reader.read(max_records)
        for i, r in enumerate(records):
            print(f"[{i}] payload: {r.payload}")


# [read-reader]


if __name__ == "__main__":
    def safe_run(fun, *args):
        try:
            fun(*args)
        except Exception as e:
            print(e)

    try:
        asyncio.run(
            main(
                create_stream,
                list_streams,
                append_records,
                buffered_append_records,
                create_subscription,
                list_subscriptions,
                subscribe_records,
                read_reader,
            )
        )
    finally:
        safe_run(asyncio.run, main(delete_subscription))
        safe_run(asyncio.run, main(delete_stream))

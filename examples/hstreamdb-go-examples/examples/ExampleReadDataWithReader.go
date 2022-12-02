package examples

import (
	"context"
	"github.com/hstreamdb/hstreamdb-go/hstream"
	"log"
)

func ExampleReadDataWithReader() error {
	client, err := hstream.NewHStreamClient(YourHStreamServiceUrl)
	if err != nil {
		log.Fatalf("Create client error: %s", err)
	}
	defer client.Close()

	streamName := "testDefaultStream"
	readerId := "shardReader"

	shards, err := client.ListShards(streamName)
	if err != nil {
		log.Fatalf("List shards error: %s", err)
	}

	shardId := shards[0].ShardId

	reader, err := client.NewShardReader(streamName, readerId, shardId, hstream.WithReaderTimeout(100))
	if err != nil {
		log.Fatalf("Create reader error: %s", err)
	}
	defer client.DeleteShardReader(shardId, readerId)
	defer reader.Close()

	count := 0
	for {
		records, err := reader.Read(context.Background())
		if err != nil {
			log.Printf("Reader read error: %s\n", err.Error())
			continue
		}
		for _, record := range records {
			log.Printf("Reader read record [%s]:%v", record.GetRecordId().String(), record.GetPayload())
		}
		count += len(records)
		if count >= 100 {
			break
		}
	}
	return nil
}

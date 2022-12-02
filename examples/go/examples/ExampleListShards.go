package examples

import (
	"fmt"
	"github.com/hstreamdb/hstreamdb-go/hstream"
	"log"
)

func ExampleListShards() error {
	client, err := hstream.NewHStreamClient(YourHStreamServiceUrl)
	if err != nil {
		log.Fatalf("Create client error: %s", err)
	}
	defer client.Close()

	streamName := "testStream"
	shards, err := client.ListShards(streamName)
	if err != nil {
		log.Fatalf("Liste shards error: %s", err)
	}

	for _, shard := range shards {
		fmt.Printf("%+v\n", shard)
	}

	return nil
}

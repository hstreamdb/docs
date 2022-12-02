package examples

import (
	"fmt"
	"github.com/hstreamdb/hstreamdb-go/hstream"
	"log"
)

func ExampleListStreams() error {
	client, err := hstream.NewHStreamClient(YourHStreamServiceUrl)
	if err != nil {
		log.Fatalf("Creating client error: %s", err)
	}
	defer client.Close()

	streams, err := client.ListStreams()
	if err != nil {
		log.Fatalf("Listing streams error: %s", err)
	}

	for _, stream := range streams {
		fmt.Printf("%+v\n", stream)
	}

	return nil
}

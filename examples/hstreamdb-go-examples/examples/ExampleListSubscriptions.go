package examples

import (
	"fmt"
	"github.com/hstreamdb/hstreamdb-go/hstream"
	"log"
)

func ExampleListSubscriptions() error {
	client, err := hstream.NewHStreamClient(YourHStreamServiceUrl)
	if err != nil {
		log.Fatalf("Creating client error: %s", err)
	}
	defer client.Close()

	subscriptions, err := client.ListSubscriptions()
	if err != nil {
		log.Fatalf("Listing subscriptions error: %s", err)
	}

	for _, sub := range subscriptions {
		fmt.Printf("%+v\n", sub)
	}

	return nil
}

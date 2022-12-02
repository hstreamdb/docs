package examples

import (
	"github.com/hstreamdb/hstreamdb-go/hstream"
	"log"
)

func ExampleDeleteSubscription() error {
	client, err := hstream.NewHStreamClient(YourHStreamServiceUrl)
	if err != nil {
		log.Fatalf("Creating client error: %s", err)
	}
	defer client.Close()

	subId0 := "SubscriptionId0"
	subId1 := "SubscriptionId1"

	// force delete subscription
	if err = client.DeleteSubscription(subId0, true); err != nil {
		log.Fatalf("Force deleting subscription error: %s", err)
	}

	// delete subscription
	if err = client.DeleteSubscription(subId1, false); err != nil {
		log.Fatalf("Deleting subscription error: %s", err)
	}

	return nil
}

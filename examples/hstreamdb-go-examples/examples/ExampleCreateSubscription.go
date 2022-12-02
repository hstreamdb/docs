package examples

import (
	"github.com/hstreamdb/hstreamdb-go/hstream"
	"log"
)

func ExampleCreateSubscription() error {
	client, err := hstream.NewHStreamClient(YourHStreamServiceUrl)
	if err != nil {
		log.Fatalf("Creating client error: %s", err)
	}
	defer client.Close()

	streamName := "testStream"
	subId0 := "SubscriptionId0"
	subId1 := "SubscriptionId1"

	// Create a new subscription with ack timeout = 60s, max unAcked records num set to 10000 and set
	// subscriptionOffset to Earliest
	if err = client.CreateSubscription(subId0, streamName,
		hstream.WithAckTimeout(60),
		hstream.WithMaxUnackedRecords(10000),
		hstream.WithOffset(hstream.EARLIEST)); err != nil {
		log.Fatalf("Creating subscription error: %s", err)
	}

	if err = client.CreateSubscription(subId1, streamName,
		hstream.WithAckTimeout(600),
		hstream.WithMaxUnackedRecords(5000),
		hstream.WithOffset(hstream.LATEST)); err != nil {
		log.Fatalf("Creating subscription error: %s", err)
	}

	return nil
}

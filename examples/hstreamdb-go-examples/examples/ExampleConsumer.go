package examples

import (
	"github.com/hstreamdb/hstreamdb-go/hstream"
	"log"
	"time"
)

func ExampleConsumer() error {
	client, err := hstream.NewHStreamClient(YourHStreamServiceUrl)
	if err != nil {
		log.Fatalf("Creating client error: %s", err)
	}
	defer client.Close()

	subId := "SubscriptionId0"
	consumer := client.NewConsumer("consumer-1", subId)
	defer consumer.Stop()

	dataChan := consumer.StartFetch()
	timer := time.NewTimer(3 * time.Second)
	defer timer.Stop()

	for {
		select {
		case <-timer.C:
			log.Println("[consumer]: Streaming fetch stopped")
			return nil
		case recordMsg := <-dataChan:
			if recordMsg.Err != nil {
				log.Printf("[consumer]: Streaming fetch error: %s", err)
				continue
			}

			for _, record := range recordMsg.Result {
				log.Printf("[consumer]: Receive %s record: record id = %s, payload = %+v",
					record.GetRecordType(), record.GetRecordId().String(), record.GetPayload())
				record.Ack()
			}
		}
	}

	return nil
}

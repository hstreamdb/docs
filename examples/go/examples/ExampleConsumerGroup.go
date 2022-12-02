package examples

import (
	"github.com/hstreamdb/hstreamdb-go/hstream"
	"log"
	"sync"
	"time"
)

func ExampleConsumerGroup() error {
	client, err := hstream.NewHStreamClient(YourHStreamServiceUrl)
	if err != nil {
		log.Fatalf("Creating client error: %s", err)
	}
	defer client.Close()

	subId1 := "SubscriptionId1"

	var wg sync.WaitGroup
	wg.Add(2)

	go func() {
		consumer := client.NewConsumer("consumer-1", subId1)
		defer consumer.Stop()
		timer := time.NewTimer(5 * time.Second)
		defer timer.Stop()
		defer wg.Done()

		dataChan := consumer.StartFetch()
		for {
			select {
			case <-timer.C:
				log.Println("[consumer-1]: Stream fetching stopped")
				return
			case recordMsg := <-dataChan:
				if recordMsg.Err != nil {
					log.Printf("[consumer-1]: Stream fetching error: %s", err)
					continue
				}

				for _, record := range recordMsg.Result {
					log.Printf("[consumer-1]: Receive %s record: record id = %s, payload = %+v",
						record.GetRecordType(), record.GetRecordId().String(), record.GetPayload())
					record.Ack()
				}
			}
		}
	}()

	go func() {
		consumer := client.NewConsumer("consumer-2", subId1)
		defer consumer.Stop()
		timer := time.NewTimer(5 * time.Second)
		defer timer.Stop()
		defer wg.Done()

		dataChan := consumer.StartFetch()
		for {
			select {
			case <-timer.C:
				log.Println("[consumer-2]: Stream fetching stopped")
				return
			case recordMsg := <-dataChan:
				if recordMsg.Err != nil {
					log.Printf("[consumer-2]: Stream fetching error: %s", err)
					continue
				}

				for _, record := range recordMsg.Result {
					log.Printf("[consumer-2]: Receive %s record: record id = %s, payload = %+v",
						record.GetRecordType(), record.GetRecordId().String(), record.GetPayload())
					record.Ack()
				}
			}
		}
	}()

	wg.Wait()

	return nil
}

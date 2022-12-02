package examples

import (
	"github.com/hstreamdb/hstreamdb-go/hstream"
	"github.com/hstreamdb/hstreamdb-go/hstream/Record"
	"log"
)

func ExampleWriteProducer() error {
	client, err := hstream.NewHStreamClient(YourHStreamServiceUrl)
	if err != nil {
		log.Fatalf("Creating client error: %s", err)
	}
	defer client.Close()

	producer, err := client.NewProducer("testStream")
	if err != nil {
		log.Fatalf("Creating producer error: %s", err)
	}

	defer producer.Stop()

	payload := []byte("Hello HStreamDB")

	rawRecord, err := Record.NewHStreamRawRecord("testStream", payload)
	if err != nil {
		log.Fatalf("Creating raw record error: %s", err)
	}

	for i := 0; i < 100; i++ {
		appendRes := producer.Append(rawRecord)
		if resp, err := appendRes.Ready(); err != nil {
			log.Printf("Append error: %s", err)
		} else {
			log.Printf("Append response: %s", resp)
		}
	}

	return nil
}

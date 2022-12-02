package examples

import (
	"github.com/hstreamdb/hstreamdb-go/hstream"
	"github.com/hstreamdb/hstreamdb-go/hstream/Record"
	"log"
)

func ExampleWriteBatchProducer() error {
	client, err := hstream.NewHStreamClient(YourHStreamServiceUrl)
	if err != nil {
		log.Fatalf("Creating client error: %s", err)
	}
	defer client.Close()

	producer, err := client.NewBatchProducer("testDefaultStream", hstream.WithBatch(10, 500))
	if err != nil {
		log.Fatalf("Creating producer error: %s", err)
	}
	defer producer.Stop()

	result := make([]hstream.AppendResult, 0, 100)
	for i := 0; i < 100; i++ {
		rawRecord, _ := Record.NewHStreamHRecord("", map[string]interface{}{
			"id":      i,
			"isReady": true,
			"name":    "hRecord-example",
		})
		r := producer.Append(rawRecord)
		result = append(result, r)
	}

	for i, res := range result {
		resp, err := res.Ready()
		if err != nil {
			log.Printf("write error: %s\n", err.Error())
		}
		log.Printf("record[%d]=%s\n", i, resp.String())
	}
	return nil
}

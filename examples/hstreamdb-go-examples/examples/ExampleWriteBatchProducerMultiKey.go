package examples

import (
	"fmt"
	"github.com/hstreamdb/hstreamdb-go/hstream"
	"github.com/hstreamdb/hstreamdb-go/hstream/Record"
	"github.com/hstreamdb/hstreamdb-go/hstream/compression"
	"log"
	"math/rand"
	"sync"
)

func ExampleWriteBatchProducerMultiKey() error {
	client, err := hstream.NewHStreamClient(YourHStreamServiceUrl)
	if err != nil {
		log.Fatalf("Creating client error: %s", err)
	}
	defer client.Close()

	producer, err := client.NewBatchProducer("testStream",
		// optional: set record count and max batch bytes trigger
		hstream.WithBatch(10, 500),
		// optional: set timeout trigger
		hstream.TimeOut(1000),
		// optional: set client compression
		hstream.WithCompression(compression.Zstd),
		// optional: set flow control
		hstream.WithFlowControl(81920000))
	if err != nil {
		log.Fatalf("Creating producer error: %s", err)
	}
	defer producer.Stop()

	keys := []string{"sensor1", "sensor2", "sensor3", "sensor4", "sensor5"}
	rids := sync.Map{}
	wg := sync.WaitGroup{}
	wg.Add(5)

	for _, key := range keys {
		go func(key string) {
			result := make([]hstream.AppendResult, 0, 100)
			for i := 0; i < 100; i++ {
				temp := rand.Intn(100)/10.0 + 15
				rawRecord, _ := Record.NewHStreamHRecord(key, map[string]interface{}{
					key: fmt.Sprintf("temperature=%d", temp),
				})
				r := producer.Append(rawRecord)
				result = append(result, r)
			}
			rids.Store(key, result)
			wg.Done()
		}(key)
	}

	wg.Wait()
	rids.Range(func(key, value interface{}) bool {
		k := key.(string)
		res := value.([]hstream.AppendResult)
		for i := 0; i < 100; i++ {
			resp, err := res[i].Ready()
			if err != nil {
				log.Printf("write error: %s\n", err.Error())
			}
			log.Printf("[key: %s]: record[%d]=%s\n", k, i, resp.String())
		}
		return true
	})

	return nil
}

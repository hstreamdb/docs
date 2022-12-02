package examples

import (
	"github.com/hstreamdb/hstreamdb-go/hstream"
	"log"
)

func ExampleDeleteStream() error {
	client, err := hstream.NewHStreamClient(YourHStreamServiceUrl)
	if err != nil {
		log.Fatalf("Creating client error: %s", err)
	}
	defer client.Close()

	// force delete stream and ignore none exist stream
	if err := client.DeleteStream("testStream",
		hstream.EnableForceDelete,
		hstream.EnableIgnoreNoneExist); err != nil {
		log.Fatalf("Deleting stream error: %s", err)
	}

	if err := client.DeleteStream("testDefaultStream"); err != nil {
		log.Fatalf("Deleting stream error: %s", err)
	}

	return nil
}

package main

import (
	"github.com/hstreamdb/hstreamdb-go-examples/examples"
	"log"
	"reflect"
	"runtime"
)

func main() {
	xs := []func() error{
		examples.ExampleCreateStream,
		examples.ExampleListStreams,
		examples.ExampleListShards,

		examples.ExampleCreateSubscription,
		examples.ExampleListSubscriptions,

		examples.ExampleWriteProducer,
		examples.ExampleWriteBatchProducer,
		examples.ExampleWriteBatchProducerMultiKey,

		examples.ExampleConsumer,
		examples.ExampleReadDataWithReader,
		examples.ExampleConsumerGroup,

		examples.ExampleDeleteSubscription,
		examples.ExampleDeleteStream,
	}

	for _, x := range xs {
		if err := runFuncWithLog(x); err != nil {
			panic(err)
		}
	}

}

func getFuncName(i interface{}) string {
	return runtime.FuncForPC(reflect.ValueOf(i).Pointer()).Name()
}

func runFuncWithLog(f func() error) error {
	funcName := getFuncName(f)
	log.Printf("==== start %s ====", funcName)
	err := f()
	log.Printf("==== end %s ====", funcName)
	return err
}

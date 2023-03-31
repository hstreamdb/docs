package docs.code.examples;

import io.hstream.*;

public class QueryExample {
    public static void main(String[] args) throws Exception {
        // Client
        String serviceUrl = "hstream://127.0.0.1:26571";
        var client = HStreamClient.builder().serviceUrl(serviceUrl).build();
        // Query
        client.createStream("stream01");
        var sql = "create stream result_stream as select * from stream01;";
        var query = client.createQuery("query01", sql);

        Thread.sleep(20000);
        var producer = client.newProducer().stream("stream01").build();
        for (int i = 0; i < 10; i++) {
            var hRecord = HRecord.newBuilder()
                    .put("id", i % 3)
                    .put("value", i).build();
            producer.write(Record.newBuilder().hRecord(hRecord).build()).join();
        }
        client.createSubscription(Subscription.newBuilder()
                .stream(query.getResultStream())
                .subscription("sub01")
                .offset(Subscription.SubscriptionOffset.EARLIEST)
                .build());
        // Read
        var consumer = client.newConsumer()
                .subscription("sub01")
                .rawRecordReceiver((receivedRawRecord, responder) -> {
                    System.out.println("raw:" + receivedRawRecord.getRecordId());
                    responder.ack();
                })
                .hRecordReceiver((receivedHRecord, responder) -> {
                    System.out.println("record:" + receivedHRecord.getHRecord().toCompactJsonString());
                    responder.ack();
                })
                .build();
        consumer.startAsync().awaitRunning();
        Thread.sleep(10000);
        consumer.stopAsync().awaitTerminated();
        for (var q : client.listQueries()) {
            System.out.println("q:" + q.getName() + " status:" + q.getStatus());
            System.out.println("result stream:" + q.getResultStream());
        }

        // View
//        client.createView("create view view01 as select id, sum(value) from stream01 group by id;");
//        Thread.sleep(5000);
//        for (HRecord hRecord : client.executeViewQuery("select * from view01;")) {
//            System.out.println("View Result:" + hRecord.toCompactJsonString());
//        }
    }
}

# HStream Metrics

Table of metrics, and for usage, please refer to
[Monitoring](../operation/monitoring/grafana.md) chapter.

Note: For metrics with intervals, such as stats in categories like stream and
subscription, users can specify intervals (default intervals [1min, 5min,
10min]). The smaller the interval, the closer it gets to the rate in real-time.

<table>
  <thead>
    <tr>
      <th>Category<br /></th>
      <th>Metrics<br /></th>
      <th>Unit<br /></th>
      <th>Description<br /></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td rowspan="4">stream_counter</td>
      <td>append_total<br /></td>
      <td>#<br /></td>
      <td>Total number of append requests of a stream<br /></td>
    </tr>
    <tr>
      <td>append_failed</td>
      <td>#<br /></td>
      <td>Total number of failed append request of a stream</td>
    </tr>
    <tr>
      <td>append_in_bytes</td>
      <td>#<br /></td>
      <td>Total payload bytes successfully written to the stream</td>
    </tr>
    <tr>
      <td>append_in_records</td>
      <td>#<br /></td>
      <td>Total payload records successfully written to the stream</td>
    </tr>
    <tr>
      <td rowspan="4">stream</td>
      <td>append_in_bytes</td>
      <td>B/s<br /></td>
      <td>
        Rate of bytes received and successfully written to the stream.<br />
      </td>
    </tr>
    <tr>
      <td>append_in_records</td>
      <td>#/s <br /></td>
      <td>Rate of records received and successfully written to the stream</td>
    </tr>
    <tr>
      <td>append_in_requests</td>
      <td>#/s (QPS)</td>
      <td>Rate of append requests received per stream<br /></td>
    </tr>
    <tr>
      <td>append_failed_requests</td>
      <td>#/s (QPS)</td>
      <td>Rate of failed append requests received per stream</td>
    </tr>
    <tr>
      <td rowspan="8">subscription_counter</td>
      <td>send_out_bytes</td>
      <td>#</td>
      <td>Number of bytes sent by the server per subscription</td>
    </tr>
    <tr>
      <td>send_out_records</td>
      <td>#</td>
      <td>Number of records successfully sent by the server per subscription</td>
    </tr>
    <tr>
      <td>send_out_records_failed</td>
      <td>#</td>
      <td>Number of records failed to send by the server per subscription</td>
    </tr>
    <tr>
      <td>resend_records</td>
      <td>#</td>
      <td>Number of successfully resent records per subscription</td>
    </tr>
    <tr>
      <td>resend_records_failed</td>
      <td>#</td>
      <td>Number of records failed to resend per subscription</td>
    </tr>
    <tr>
      <td>received_acks</td>
      <td>#</td>
      <td>Number of acknowledgements received per subscription</td>
    </tr>
    <tr>
      <td>request_messages</td>
      <td>#</td>
      <td>Number of streaming fetch requests received from clients per subscription</td>
    </tr>
    <tr>
      <td>response_messages</td>
      <td>#</td>
      <td>Number of streaming send requests successfully sent to clients per subscription, including resends</td>
    </tr>
    <tr>
      <td rowspan="4">subscription</td>
      <td>send_out_bytes</td>
      <td>B/s</td>
      <td>Rate of bytes sent by the server per subscription</td>
    </tr>
    <tr>
      <td>acks / acknowledgements<br /></td>
      <td>#/s</td>
      <td>Rate of acknowledgements received per subscription</td>
    </tr>
    <tr>
      <td>request_messages</td>
      <td>#/s</td>
      <td>Rate of streaming fetch requests received from clients per subscription<br /></td>
    </tr>
    <tr>
      <td>response_messages</td>
      <td>#/s</td>
      <td>Rate of streaming send requests successfully sent to clients per subscription, including resends</td>
    </tr>
  </tbody>
</table>

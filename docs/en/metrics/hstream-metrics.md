<table>
<thead>
  <tr>
    <th>Category<br></th>
    <th>Metrics<br></th>
    <th>Unit<br></th>
    <th>Description<br></th>
    <th> <br></th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>stream_counter</td>
    <td> append_total<br></td>
    <td>#<br></td>
    <td>Total number of append requests of a stream<br></td>
    <td></td>
  </tr>
  <tr>
    <td></td>
    <td>append_failed</td>
    <td>#<br></td>
    <td>Total number of failed append request of a stream</td>
    <td></td>
  </tr>
  <tr>
    <td>stream</td>
    <td>append_in_bytes</td>
    <td>KB/s<br></td>
    <td>Rate of bytes received and successfully written to the stream.<br>Users can specify intervals, default intervals [1min, 5min, 10min]<br>The smaller the interval, the closer it gets to the rate in real-time.<br></td>
    <td></td>
  </tr>
  <tr>
    <td></td>
    <td>append_in_records</td>
    <td>#/s <br></td>
    <td>Rate of records received and successfully written to the stream</td>
    <td></td>
  </tr>
  <tr>
    <td></td>
    <td>append_in_requests</td>
    <td>#/s (QPS)</td>
    <td>Rate of append requests received per stream<br></td>
    <td></td>
  </tr>
  <tr>
    <td></td>
    <td>append_failed_requests</td>
    <td>#/s (QPS)</td>
    <td>Rate of failed append requests received per stream</td>
    <td></td>
  </tr>
  <tr>
    <td>subscription_counter</td>
    <td>resend_records</td>
    <td>#</td>
    <td>Total number of resent records per subscription</td>
    <td></td>
  </tr>
  <tr>
    <td>subscription</td>
    <td>send_out_bytes</td>
    <td>KB/s</td>
    <td>Rate of bytes sent by the server per subscription</td>
    <td></td>
  </tr>
  <tr>
    <td></td>
    <td>acks / acknowledgements<br></td>
    <td>#/s</td>
    <td>Rate of acknowledgements received per subscription</td>
    <td></td>
  </tr>
  <tr>
    <td></td>
    <td>request_messages</td>
    <td>#/s</td>
    <td>Rate of requests received from clients per subcription<br></td>
    <td></td>
  </tr>
  <tr>
    <td></td>
    <td>response_messages</td>
    <td>#/s</td>
    <td>Rate of response sent to clients per subscription</td>
    <td></td>
  </tr>
</tbody>
</table>

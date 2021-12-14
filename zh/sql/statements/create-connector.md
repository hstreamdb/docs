CREATE CONNECTOR
================

Create a new connector for fetching data from or writing data to an external system. A connector can be either a source or a sink one. **Note that source connector is not supported yet**.


## Synopsis

```sql
CREATE <SOURCE|SINK> CONNECTOR connector_name [IF NOT EXIST] WITH (connector_option [, ...]);
```

## Notes

- `connector_name` is a valid identifier.
- There is an optional `IF NOT EXIST` configuration to create a connector only if the connector with the same name does not exist.
- There is are some connector options in the `WITH` clause separated by commas. `TYPE` and `STREAM` are required to specify the type of a connector and which stream it fetches data from/writes data to. For details, see the following table.

<table>
    <thead>
        <tr>
            <th>TYPE</th>
            <th>Option</th>
            <th>Description</th>
            <th>Default Value</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td rowspan=5>mysql</td>
            <td>host</td>
            <td>Host of MySQL server</td>
            <td>"127.0.0.1"</td>
        </tr>
        <tr>
            <td>port</td>
            <td>Port of MySQL server</td>
            <td>3306</td>
        </tr>
        <tr>
            <td>username</td>
            <td>Username to login MySQL </td>
            <td>"root"</td>
        </tr>
        <tr>
            <td>password</td>
            <td>Password to login MySQL</td>
            <td>"password"</td>
        </tr>
        <tr>
            <td>database</td>
            <td>Database name to store data from HStreamDB</td>
            <td>"mysql"</td>
        </tr>
        <tr>
            <td rowspan=5>clickhouse</td>
            <td>host</td>
            <td>Host of ClickHouse server</td>
            <td>"127.0.0.1"</td>
        </tr>
        <tr>
            <td>port</td>
            <td>Port of ClickHouse server</td>
            <td>9000</td>
        </tr>
        <tr>
            <td>username</td>
            <td>Username to login ClickHouse </td>
            <td>"default"</td>
        </tr>
        <tr>
            <td>password</td>
            <td>Password to login ClickHouse</td>
            <td>""</td>
        </tr>
        <tr>
            <td>database</td>
            <td>Database name to store data from HStreamDB</td>
            <td>"default"</td>
        </tr>
    </tbody>
</table>

## Examples

```sql
CREATE SINK CONNECTOR mysql_conn WITH (TYPE = mysql, STREAM = foo, host = "127.0.0.1");
```

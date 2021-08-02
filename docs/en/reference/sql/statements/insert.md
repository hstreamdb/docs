# INSERT

Insert a record into specified stream.

## Synopsis

```sql
INSERT INTO stream_name (field_name [, ...]) VALUES (field_value [, ...]);
```

## Notes

- `field_value` represents the value of corresponding field, which is a [constant](../sql-overview.md#literals-constants). The correspondence between field type and inserted value is maintained by users themselves.

## Examples

```sql
INSERT INTO weather (cityId, temperature, humidity) VALUES (11254469, 12, 65);
```

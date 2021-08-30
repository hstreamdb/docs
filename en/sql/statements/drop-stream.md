DROP STREAM
===========

Drop a stream with the given name. If `IF EXISTS` is present, the statement won't fail if the stream does not exist.

## Synopsis

```sql
DROP STREAM stream_name [ IF EXISTS ];
```

## Notes

- `stream_name` is a valid identifier.
- `IF EXISTS` annotation is optional.

## Examples

```sql
DROP STREAM foo;

DROP STREAM foo IF EXISTS;
```

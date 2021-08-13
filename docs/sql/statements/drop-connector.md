DROP CONNECTOR
===========

Drop a connector with the given name. If `IF EXISTS` is present, the statement won't fail if the connector does not exist.

## Synopsis

```sql
DROP CONNECTOR connector_name [ IF EXISTS ];
```

## Notes

- `connector_name` is a valid identifier.
- `IF EXISTS` annotation is optional.

## Examples

```sql
DROP CONNECTOR foo;

DROP CONNECTOR foo IF EXISTS;
```

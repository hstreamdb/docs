DROP VIEW
===========

Drop a view with the given name. If `IF EXISTS` is present, the statement won't fail if the view does not exist.

## Synopsis

```sql
DROP VIEW view_name [ IF EXISTS ];
```

## Notes

- `view_name` is a valid identifier.
- `IF EXISTS` annotation is optional.

## Examples

```sql
DROP VIEW foo;

DROP VIEW foo IF EXISTS;
```

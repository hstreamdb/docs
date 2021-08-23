CREATE VIEW
===========

Create a new hstream view with the given name. An exception will be thrown if a view or stream with the same name already exists.

## Synopsis

```sql
CREATE VIEW view_name AS select_query;
```
## Notes
- `view_name` is a valid identifier.
- `select_query` is a valid `SELECT` (Stream) query. For more information, see `SELECT` section. It has to contains at least one aggregate function and a `GROUP BY` clause.

## Examples

```sql
CREATE VIEW foo AS SELECT a, SUM(a), COUNT(*) FROM bar GROUP BY b EMIT CHANGES;
```

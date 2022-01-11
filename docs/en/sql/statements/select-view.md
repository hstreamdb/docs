SELECT (View)
=============

Get a record from the specified view. The fields to get have to be already in the view.
It produces one or zero static record and costs little time.

## Synopsis

```sql
SELECT <* | column_name [AS field_alias] [, ...]>
  FROM view_name
  WHERE column_name = constant;
```

## Notes

Selecting from a view is a very fast operation that takes advantage of the concept of a view. So it has a more restricted syntax than selecting from a stream:

- The most important difference between `SELECT` from a stream and from a view is that the former has an `EMIT CHANGES` clause and the latter does not.
- `SELECT` clause can only contain `*` or column names with/without aliases. Other ones such as constants, arithmetical expressions, aggregate/scalar functions, etc. are not allowed. And the column names should be in the `SELECT` clause of the query when creating the corresponding view.
- `FROM` clause can only contain ONE view name. Clauses such as `JOIN` are not allowed.
- `WHERE` clause can only contain ONE conditional expression in the form of `column_name = constant`. And the `column_name` has to be the same as which in the `GROUP BY` clause when creating the corresponding view and the `constant` has to be a literal whose value is known at compile time, see [constant](../sql-overview.md#literals-constants).

## Examples

```sql
// Assume that this query has been executed successfully before
// CREATE VIEW my_view AS SELECT a, b, SUM(a), COUNT(*) AS cnt FROM foo GROUP BY b EMIT CHANGES;

SELECT `SUM(a)`, cnt, a FROM my_view WHERE b = 1;
```

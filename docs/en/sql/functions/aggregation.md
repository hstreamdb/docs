Aggregate functions perform a calculation on a set of values and return a single value.

```sql
COUNT(col)
COUNT(*)
```

Return the number of rows.
When `col` is specified, the count returned will be the number of rows.
When `*` is specified, the count returned will be the total number of rows.

```sql
AVG(col)
```

Return the average value of a given column.

```sql
SUM(col)
```

Return the sum value of a given column.

```sql
MAX(col)
```

Return the max value of a given column.

```sql
MIN(col)
```

Return the min value of a given column.

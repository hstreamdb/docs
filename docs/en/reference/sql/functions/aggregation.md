Aggregate Functions
===================
Aggregate functions perform a calculation on a set of values and return a single value.

```sql
COUNT(expression)
COUNT(*)
```

Return the number of rows.
When `expression` is specified, the count returned will be the number of matched rows.
When `*` is specified, the count returned will be the total number of rows.

```sql
AVG(expression)
```

Return the average value of a given expression.

```sql
SUM(expression)
```

Return the sum value of a given expression.

```sql
MAX(expression)
```

Return the max value of a given expression.

```sql
MIN(expression)
```

Return the min value of a given expression.

```sql
TOPK(expression_value, expression_k)
```

Return the top `K`(specified by `expression_k`) values of `expression_value` in a array.

```sql
TOPKDISTINCT(expression_value, expression_k)
```

Similar to `TOPK`, but only returns distinct values of `expression_value`.

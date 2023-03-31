CREATE VIEW
===========

Create a new hstream view with the given name. An exception will be thrown if a view or stream with the same name already exists.

A view is **NOT** just an alias but physically maintained in the memory and is updated incrementally. Thus queries on a view are really fast and do not require extra resources.

## Synopsis

```sql
CREATE VIEW view_name AS select_query;
```
## Notes
- `view_name` is a valid identifier.
- `select_query` is a valid `SELECT` query. For more information, see `SELECT` section. There is no extra restrictions on `select_query` but we recommend using at least one aggregate function and a `GROUP BY` clause. Otherwise, the query may be a little weird and consumes more resources. See the following examples:

```
// CREATE VIEW v1 AS SELECT id, SUM(sales) FROM s GROUP BY id;
// what the view contains at time
//            [t1]                            [t2]                            [t3]
//  {"id":1, "SUM(sales)": 10}  ->  {"id":1, "SUM(sales)": 10}  ->  {"id":1, "SUM(sales)": 30}
//                                  {"id":2, "SUM(sales)": 8}       {"id":2, "SUM(sales)": 15}

// CREATE VIEW AS SELECT id, sales FROM s;
// what the view contains at time
//           [t1]                       [t2]                       [t3]
// {"id":1, "sales": 10}  ->  {"id":1, "sales": 10}  ->  {"id":1, "sales": 10}
//                            {"id":2, "sales": 8}       {"id":1, "sales": 20}
//                                                       {"id":2, "sales": 8}
//                                                       {"id":2, "sales": 7}
```

## Examples

```sql
CREATE VIEW foo AS SELECT a, SUM(a), COUNT(*) FROM s1 GROUP BY b;
```

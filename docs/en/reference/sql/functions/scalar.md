Scalar functions operate on one or more values and then return a single value. They can be used wherever a value expression is valid.

Scalar functions are divided into serval kinds.

### Trigonometric Functions

All trigonometric functions perform a calculation, operate on a single numeric value and then return a single numeric value.

For values outside the domain, `NaN` is returned.

```sql
SIN(num_expr)
SINH(num_expr)
ASIN(num_expr)
ASINH(num_expr)
COS(num_expr)
COSH(num_expr)
ACOS(num_expr)
ACOSH(num_expr)
TAN(num_expr)
TANH(num_expr)
ATAN(num_expr)
ATANH(num_expr)
```

### Arithmetic Functions

The following functions perform a calculation, operate on a single numeric value and then return a single numeric value.

```sql
ABS(num_expr)
```

Absolute value.

```sql
CEIL(num_expr)
```
The function application `CEIL(n)` returns the least integer not less than `n`.

```sql
FLOOR(num_expr)
```

The function application `FLOOR(n)` returns the greatest integer not greater than `n`.

```sql
ROUND(num_expr)
```
The function application `ROUND(n)` returns the nearest integer to `n` the even integer if `n` is equidistant between two integers.

```sql
SQRT(num_expr)
```

The square root of a numeric value.

```sql
LOG(num_expr)
LOG2(num_expr)
LOG10(num_expr)
EXP(num_expr)
```

```sql
SIGN(num_expr)
```
The function application `SIGN(n)` returns the sign of a numeric value as an Integer.

- returns `-1` if `n` is negative
- returns `0` if `n` is exact zero
- returns `1` if `n` is positive
- returns `null` if `n` is exact `null`

### Predicate Functions

Function applications of the form `IS_A(x)` where `A` is the name of a type returns `TRUE` if the argument `x` is of type `A`, otherwise `FALSE`.

```sql
IS_INT(val_expr)
IS_FLOAT(val_expr)
IS_NUM(val_expr)
IS_BOOL(val_expr)
IS_STR(val_expr)
IS_MAP(val_expr)
IS_ARRAY(val_expr)
IS_DATE(val_expr)
IS_TIME(val_expr)
```

### String Functions

```sql
TO_STR(val_expr)
```

Convert a value expression to a readable string.

```sql
TO_LOWER(str)
```
Convert a string to lower case, using simple case conversion.

```sql
TO_UPPER(str)
```

Convert a string to upper case, using simple case conversion.

```sql
TRIM(str)
```

Remove leading and trailing white space from a string.

```sql
LEFT_TRIM(str)
```

Remove leading white space from a string.

```sql
RIGHT_TRIM(str)
```

Remove trailing white space from a string.

```sql
REVERSE(str)
```

Reverse the characters of a string.

```sql
STRLEN(str)
```

Returns the number of characters in a string.

```sql
TAKE(num_expr, str)
```

The function application `TAKE(n, s)` returns the prefix of the string of length `n`.

```sql
TAKEEND(num_expr, str)
```

The function application `TAKEEND(n, s)` returns the suffix remaining after taking `n` characters from the end of the string.

```sql
DROP(num_expr, str)
```

The function application `DROP(n, s)` returns the suffix of the string after the first `n` characters, or the empty string if n is greater than the length of the string.

```sql
DROPEND(num_expr, str)
```

The function application `DROPEND(n, s)` returns the prefix remaining after dropping `n` characters from the end of the string.

### Null Functions

```sql
IFNULL(val_expr, val_expr)
```

The function application `IFNULL(x, y)` returns `y` if `x` is `NULL`, otherwise `x`.

When the argument type is a complex type, for example, `ARRAY` or `MAP`, the contents of the complex type are not inspected.

```sql
NULLIF(val_expr, val_expr)
```

The function application `NULLIF(x, y)` returns `NULL` if `x` is equal to `y`, otherwise `x`.

When the argument type is a complex type, for example, `ARRAY` or `MAP`, the contents of the complex type are not inspected.

### Time and Date Functions

#### Time Format

Formats are analogous to [strftime](https://man7.org/linux/man-pages/man3/strftime.3.html).

| Format Name       | Raw Format String           |
| ----------------- | --------------------------- |
| simpleDateFormat  | "%Y-%m-%d %H:%M:%S"         |
| iso8061DateFormat | "%Y-%m-%dT%H:%M:%S%z"       |
| webDateFormat     | "%a, %d %b %Y %H:%M:%S GMT" |
| mailDateFormat    | "%a, %d %b %Y %H:%M:%S %z"  |

```sql
DATETOSTRING(val_expr, str)
```

Formatting seconds since 1970-01-01 00:00:00 UTC to string in GMT with the second string argument as the given format name.

```sql
STRINGTODATE(str, str)
```

Formatting string to seconds since 1970-01-01 00:00:00 UTC in GMT with the second string argument as the given format name.

### Array Functions

```sql
ARRAY_CONTAINS(arr_expr, val_expr)
```

Given an array, checks if the search value is contained in the array (of the same type).

```sql
ARRAY_DISTINCT(arr_expr)
```

Returns an array of all the distinct values, including `NULL` if present, from the input array. The output array elements are in order of their first occurrence in the input.

Returns `NULL` if the argument is `NULL`.

```sql
ARRAY_EXCEPT(arr_expr, arr_expr)
```

Returns an array of all the distinct elements from an array, except for those also present in a second array. The order of entries in the first array is preserved but duplicates are removed.

Returns `NULL` if either input is `NULL`.

```sql
ARRAY_INTERSECT(arr_expr, arr_expr)
```

Returns an array of all the distinct elements from the intersection of both input arrays. If the first list contains duplicates, so will the result. If the element is found in both the first and the second list, the element from the first list will be used.

Returns `NULL` if either input is `NULL`.

```sql
ARRAY_UNION(arr_expr, arr_expr)
```

Returns the array union of the two arrays. Duplicates, and elements of the first list, are removed from the second list, but if the first list contains duplicates, so will the result.

Returns `NULL` if either input is `NULL`.

```sql
ARRAY_JOIN(arr_expr)
ARRAY_JOIN(arr_expr, str)
```

Creates a flat string representation of all the primitive elements contained in the given array. The elements in the resulting string are separated by the chosen delimiter, which is an optional parameter that falls back to a comma `,`.

```sql
ARRAY_LENGTH(arr_expr)
```

Returns the length of a finite list.

Returns `NULL` if the argument is `NULL`.

```sql
ARRAY_MAX(arr_expr)
```

Returns the maximum value from within a given array of elements.

Returns `NULL` if the argument is `NULL`.

```sql
ARRAY_MIN(arr_expr)
```

Returns the minimum value from within a given array of elements.

Returns `NULL` if the argument is `NULL`.

```sql
ARRAY_REMOVE(arr_expr, val_expr)
```

Removes all elements from the input array equal to the second argument.

Returns `NULL` if the first argument is `NULL`.


```sql
ARRAY_SORT(arr_expr)
```

Sort an array. Elements are arranged from lowest to highest, keeping duplicates in the order they appeared in the input.

Returns `NULL` if the first argument is `NULL`.

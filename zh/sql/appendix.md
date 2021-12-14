Appendix
========

## Data Types

| type    | examples          |
| ------- | ------------------|
| Integer | 1, -1, 1234567    |
| Double  | 2.3, -3.56, 232.4 |
| Bool    | TRUE, FALSE       |
| Date    | 2020-06-10        |
| Time    | 11:18:30          |
| String  | "HStreamDB"       |
| Null    | NULL              |

## Keywords

| keyword           | description                                                                              |
|-------------------|------------------------------------------------------------------------------------------|
| `ABS`             | absolute value                                                                           |
| `ACOS`            | arccosine                                                                                |
| `ACOSH`           | inverse hyperbolic cosine                                                                |
| `AND`             | logical and operator                                                                     |
| `ARRAY_CONTAIN`   | given an array, checks if a search value is contained in the array                       |
| `ARRAY_DISTINCT`  | returns an array of all the distinct values                                              |
| `ARRAY_EXCEPT`    | `ARRAY_DISTINCT` except for those also present in the second array                       |
| `ARRAY_INTERSECT` | returns an array of all the distinct elements from the intersection of both input arrays |
| `ARRAY_JOIN`      | creates a flat string representation of all elements contained in the given array        |
| `ARRAY_LENGTH`    | return the length of the given array                                                     |
| `ARRAY_MAX`       | returns the maximum value from the given array of primitive elements                     |
| `ARRAY_MIN`       | returns the minimum value from the given array of primitive elements                     |
| `ARRAY_REMOVE`    | removes all elements from the input array equal to the second argument                   |
| `ARRAY_SORT`      | sort the given array                                                                     |
| `ARRAY_UNION`     | returns an array of all the distinct elements from the union of both input arrays        |
| `AS`              | stream or field name alias                                                               |
| `ASIN`            | arcsine                                                                                  |
| `ASINH`           | inverse hyperbolic sine                                                                  |
| `ATAN`            | arctangent                                                                               |
| `ATANH`           | inverse hyperbolic tangent                                                               |
| `AVG`             | average function                                                                         |
| `BETWEEN`         | range operator, used with `AND`                                                          |
| `BY`              | do something by certain conditions, used with `GROUP` or `ORDER`                         |
| `CEIL`            | rounds a number UPWARDS to the nearest integer                                           |
| `COS`             | cosine                                                                                   |
| `COSH`            | hyperbolic cosine                                                                        |
| `COUNT`           | count function                                                                           |
| `CREATE`          | create a stream / connector                                                              |
| `DATE`            | prefix of date constant                                                                  |
| `DAY`             | interval unit                                                                            |
| `DROP`            | drop a stream                                                                            |
| `EXP`             | exponent                                                                                 |
| `FLOOR`           | rounds a number DOWNWARDS to the nearest integer                                         |
| `FORMAT`          | specify the format of a stream                                                           |
| `FROM`            | specify where to select data from                                                        |
| `GROUP`           | group values by certain conditions, used with `BY`                                       |
| `HAVING`          | filter select values by a condition                                                      |
| `HOPPING`         | hopping window                                                                           |
| `IFNULL`          | if the first argument is `NULL` returns the second, else the first                       |
| `INNER`           | joining type, used with `JOIN`                                                           |
| `INSERT`          | insert data into a stream, used with `INTO`                                              |
| `INTERVAL`        | prefix of interval constant                                                              |
| `INTO`            | insert data into a stream, used with `INSERT`                                            |
| `IS_ARRAY`        | to determine if the given value is an array of values                                    |
| `IS_BOOL`         | to determine if the given value is a boolean                                             |
| `IS_DATE`         | to determine if the given value is a date value                                          |
| `IS_FLOAT`        | to determine if the given value is a float                                               |
| `IS_INT`          | to determine if the given value is an integer                                            |
| `IS_MAP`          | to determine if the given value is a map of values                                       |
| `IS_NUM`          | to determine if the given value is a number                                              |
| `IS_STR`          | to determine if the given value is a string                                              |
| `IS_TIME`         | to determine if the given value is a time value                                          |
| `JOIN`            | for joining two streams                                                                  |
| `LEFT`            | joining type, used with `JOIN`                                                           |
| `LEFT_TRIM`       | trim spaces from the left end of a string                                                |
| `LOG`             | logarithm with base e                                                                    |
| `LOG10`           | logarithm with base 10                                                                   |
| `LOG2`            | logarithm with base 2                                                                    |
| `MAX`             | maximum function                                                                         |
| `MIN`             | minimum function                                                                         |
| `MINUTE`          | interval unit                                                                            |
| `MONTH`           | interval unit                                                                            |
| `NOT`             | logical not operator                                                                     |
| `NULLIF`          | returns `NULL` if the first argument is equal to the second, otherwise the first         |
| `ON`              | specify conditions, used with `JOIN`                                                     |
| `OR`              | logical or operator                                                                      |
| `ORDER`           | sort values by certain conditions, used with `BY`                                        |
| `OUTER`           | joining type, used with `JOIN`                                                           |
| `REVERSE`         | reverse a string                                                                         |
| `RIGHT_TRIM`      | trim spaces from the right end of a string                                               |
| `ROUND`           | rounds a number to the nearest integer                                                   |
| `SECOND`          | interval unit                                                                            |
| `SELECT`          | query a stream                                                                           |
| `SESSION`         | session window                                                                           |
| `SHOW`            | show something to stdout                                                                 |
| `SIGN`            | return the sign of a numeric value as an INTEGER                                         |
| `SIN`             | sine                                                                                     |
| `SINH`            | hyperbolic sine                                                                          |
| `SQRT`            | square root                                                                              |
| `STREAM`          | specify a stream, used with `CREATE`                                                     |
| `STRLEN`          | get the length of a string                                                               |
| `SUM`             | sum function                                                                             |
| `TAN`             | tangent                                                                                  |
| `TANH`            | hyperbolic tangent                                                                       |
| `TIME`            | prefix of the time constant                                                              |
| `TO_LOWER`        | convert a string to lowercase                                                            |
| `TO_STR`          | convert a value to string                                                                |
| `TO_UPPER`        | convert a string to uppercase                                                            |
| `TRIM`            | trim spaces from both ends of a string                                                   |
| `TUMBLING`        | tumbling window                                                                          |
| `VALUES`          | specify inserted data, used with `INSERT INTO`                                           |
| `WEEK`            | interval unit                                                                            |
| `WHERE`           | filter selected values by a condition                                                    |
| `WITH`            | specify properties when creating a stream                                                |
| `WITHIN`          | specify time window when joining two streams                                             |
| `YEAR`            | interval unit                                                                            |

## Operators

| operator  | description                      |
| --------  | -------------------------------- |
| `=`       | equal to                         |
| `<>`      | not equal to                     |
| `<`       | less than                        |
| `>`       | greater than                     |
| `<=`      | less than or equal to            |
| `>=`      | greater than or equal to         |
| `+`       | addition                         |
| `-`       | subtraction                      |
| `*`       | multiplication                   |
| `.`       | access field of a stream         |
| `[]`      | access item of a map or an array |
| `AND`     | logical and operator             |
| `OR`      | logical or operator              |
| `NOT`     | logical not operator             |
| `BETWEEN` | range operator                   |

## Scalar Functions

| function     | description                                                                      |
|--------------|----------------------------------------------------------------------------------|
| `ABS`        | absolute value                                                                   |
| `ACOS`       | arccosine                                                                        |
| `ACOSH`      | inverse hyperbolic cosine                                                        |
| `ARRAY_CONTAIN` | given an array, checks if a search value is contained in the array            |
| `ARRAY_DISTINCT` | returns an array of all the distinct values                                  |
| `ARRAY_EXCEPT` | `ARRAY_DISTINCT` except for those also present in the second array             |
| `ARRAY_INTERSECT` | returns an array of all the distinct elements from the intersection of both input arrays |
| `ARRAY_JOIN` | creates a flat string representation of all elements contained in the given array|
| `ARRAY_LENGTH` | return the length of the given array                                           |
| `ARRAY_MAX` | returns the maximum value from the given array of primitive elements              |
| `ARRAY_MIN` | returns the minimum value from the given array of primitive elements              |
| `ARRAY_REMOVE` | removes all elements from the input array equal to the second argument         |
| `ARRAY_SORT` | sort the given array                                                             |
| `ARRAY_UNION` | returns an array of all the distinct elements from the union of both input arrays|
| `ASIN`       | arcsine                                                                          |
| `ASINH`      | inverse hyperbolic sine                                                          |
| `ATAN`       | arctangent                                                                       |
| `ATANH`      | inverse hyperbolic tangent                                                       |
| `CEIL`       | rounds a number UPWARDS to the nearest integer                                   |
| `COS`        | cosine                                                                           |
| `COSH`       | hyperbolic cosine                                                                |
| `EXP`        | exponent                                                                         |
| `FLOOR`      | rounds a number DOWNWARDS to the nearest integer                                 |
| `IFNULL`     | if the first argument is `NULL` returns the second, else the first               |
| `NULLIF`     | returns `NULL` if the first argument is equal to the second, otherwise the first |
| `IS_ARRAY`   | to determine if the given value is an array of values                            |
| `IS_BOOL`    | to determine if the given value is a boolean                                     |
| `IS_DATE`    | to determine if the given value is a date value                                  |
| `IS_FLOAT`   | to determine if the given value is a float                                       |
| `IS_INT`     | to determine if the given value is an integer                                    |
| `IS_MAP`     | to determine if the given value is a map of values                               |
| `IS_NUM`     | to determine if the given value is a number                                      |
| `IS_STR`     | to determine if the given value is a string                                      |
| `IS_TIME`    | to determine if the given value is a time value                                  |
| `LEFT_TRIM`  | trim spaces from the left end of a string                                        |
| `LOG`        | logarithm with base e                                                            |
| `LOG10`      | logarithm with base 10                                                           |
| `LOG2`       | logarithm with base 2                                                            |
| `REVERSE`    | reverse a string                                                                 |
| `RIGHT_TRIM` | trim spaces from the right end of a string                                       |
| `ROUND`      | rounds a number to the nearest integer                                           |
| `SIGN`       | return the sign of a numeric value as an INTEGER                                 |
| `SIN`        | sine                                                                             |
| `SINH`       | hyperbolic sine                                                                  |
| `SQRT`       | square root                                                                      |
| `STRLEN`     | get the length of a string                                                       |
| `TAN`        | tangent                                                                          |
| `TANH`       | hyperbolic tangent                                                               |
| `TO_LOWER`   | convert a string to lowercase                                                    |
| `TO_STR`     | convert a value to string                                                        |
| `TO_UPPER`   | convert a string to uppercase                                                    |
| `TRIM`       | trim spaces from both ends of a string                                           |

## Aggregate Functions

| function | description |
| -------- | ----------- |
| `AVG`    | average     |
| `COUNT`  | count       |
| `MAX`    | maximum     |
| `MIN`    | minimum     |
| `SUM`    | sum         |

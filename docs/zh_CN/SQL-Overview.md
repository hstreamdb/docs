SQL 是一种用于编程的领域特定语言，旨在管理数据库管理系统中的数据。SQL 的规范标准由美国国家标准协会（ANSI）维护。同时，SQL 还有许多变体和扩展，以表达更具体的程序。

[HStreamDB的SQL语法](https://github.com/hstreamdb/hstream/blob/master/hstream-sql/etc/SQL.cf)是基于SQL-92的一个子集，并添加了一些语法以支持流操作。

## 语法

SQL 输入由一系列语句组成，每条语句由一系列标记组成，并以分号(`;`)结尾。

一个标记可以是关键字参数、标识符、文字、运算符或特殊字符。规则的细节可以在[BNFC语法文件](https://github.com/hstreamdb/hstream/blob/master/hstream-sql/etc/SQL.cf)或[生成的alex文件](https://github.com/hstreamdb/hstream/blob/master/hstream-sql/etc/Lex.x)中找到。通常情况下，标记之间用空格隔开。

以下所列的例子都语法是符合语法规范的:

```sql
SELECT * FROM my_stream;

CREATE STREAM abnormal_weather AS SELECT * FROM weather WHERE temperature > 30 AND humidity > 80 WITH (FORMAT = "JSON");

INSERT INTO weather (cityId, temperature, humidity) VALUES (11254469, 12, 65);
```



## 关键词

一些标记，如 `SELECT` 、 `INSERT` 和 `WHERE` 是被保留的*关键词*，在 SQL 语法中具有特定的含义。

- 关键词是不区分大小写的，这意味着 `SELECT` 和 `select` 是等价的。

- 关键词不能作为标识符使用。

关于关键字的完整列表，请参见[附录](https://docs.hstream.io/reference/sql/appendix/)。



## 标识符

标识符是用户自定义的标记，就像流、字段等。例如，`my_stream` 可以作为流的名称，`temperature` 可以代表流中的一个元素。

当前，标识符只支持 C 的命名规则：一个标识符名称只能有字母（包括大写字母和小写字母）、数字和下划线。此外，一个标识符的第一个字符应该是字母或下划线。而且，标识符是区分大小写的，这意味着 `my_stream` 和 `MY_STREAM` 是不同的标识符。



## 常量

常量是指在执行前具有已知值的对象。目前有六种常量，分别为：整数、浮点数、字符串、日期、时间和时长。



### 整数

整数的形式是数字，由是一个或多个单数整数（0至9）组成。**要注意的是，目前还不支持科学记数法表示数字。另外，由于语法的限制，当你想表示一个负数`-n`时，请写`0-n`代替**。

### 浮点数

浮点数的形式为 "数字.数字"。 注意：

- **尚不支持的科学符号。**
- **由于语法上的限制，当你想表示一个负数`-n`时，请写`0-n`代替**。
- **目前还不支持 "1. "和".99 "等形式**.

### 字符串

字符串是由双引号 (`"`) 包围的任意字符组合，如 `"JSON"`。

### 日期

日期以 "DATE <年>-<月>-<日>"的形式表示精确到日的日期，其中"<年>"、"<月>"和"<日>"均为整数常数。注意，前面的 `DATE` 不能省略。

例如：`DATE 2021-01-02`。

### 时间

时间常数以 `TIME <小时>-<分钟>-<秒>` 的形式表示精确到秒的时间，其中 `<小时>` 、 `<分钟>` 和 `<秒>` 都是整数常数。注意，前面的`TIME`不能省略。

例如：`TIME 11:45:14`。

### 时长

时长表示一个时间段，形式为 `INTERVAL <数字> <时间单位>`，其中`<数字>`是一个整数常数，`<时间单位>` 是 `YEAR`、`MONTH`、`WEEK`、`DAY`、`MINUTE` 和  `SECOND` 之一。注意，前面的 `INTERVAL`不能省略。

例如：`INTERVAL 5 SECOND`。



## 运算符和函数[¶](https://docs.hstream.io/reference/sql/sql-overview/#operators-and-functions)

函数是表示某种计算的特殊关键字，如 `SUM` 和 `MIN` 。运算符是由特殊字符组成的后缀函数，如`>=`和`<>`。

关于函数和运算符的完整列表，请参见[附录](https://docs.hstream.io/reference/sql/appendix/)。



## 特殊字符

在SQL语法中，有一些特殊的字符具有特殊的含义。

- 圆括号(`()`)用于表达式之外，用于控制评估顺序或指定函数应用。
- 方括号(`[]`)用于 map 和 array，用于访问它们的子结构，如`some_map[temp]`和`some_array[1]`。**注意，目前还不支持**。
- 逗号(`,`)用于划分对象列表。
- 分号(`;`)表示SQL语句的结束。
- 星号(`*`)表示 "所有字段"，如`SELECT * FROM my_stream;`。
- 句号(`.`)用于访问流中的字段，如`my_stream.humidity`。



## 注释

以`//`开头的单行评论。

```
// 这是一项评论
```

同时，还支持 C 式多行注释。

```
/* 这是另一个
   评论
*/
```

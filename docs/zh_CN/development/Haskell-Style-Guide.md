本文是略经修改的 [Style Guide Used in Kowainik](https://kowainik.github.io/posts/2019-02-06-style-guide) 的翻译版。

## 格式指南的目标

文档的目的是帮助机遇 Haskell 开发的人在处理不同情况下的代码时有能有更顺畅的体验。为了达成初衷，指南定义了以下目标来提高生产力。

1. 让代码**更容易理解**：解决方案的想法不应该被隐藏在复杂和晦涩的代码后面。
2. 让代码**更容易阅读**：在看完现有的代码后，代码的安排应该是立即可见的。函数和变量的名称应该是透明和显然的。
3. 让代码**更容易写**：开发人员应该尽可能少地考虑代码的格式规则。本指南应该回答任何与特定代码的格式有关的问题。
4. 让代码**更容易维护**：本指南旨在减少使用版本控制系统维护软件包的负担，除非这与前面的观点相冲突。

!!! 提示 "基于现有源代码时的开发经验法则" 一般的规则是坚持使用你正在编辑的文件中已经使用的编码风格。如果你必须进行重大的风格修改，那么就把它们与功能修改分开提交，这样别人回过头来看修改记录时就可以很容易地把它们区分开来。

## 缩进

将代码块缩进2个空格。

始终将 `where` 关键字放在新的一行中。

```haskell
showSign :: Int -> String
showSign n
  | n == 0    = "Zero"
  | n < 0     = "Negative"
  | otherwise = "Positive"

greet :: IO ()
greet = do
  putStrLn "What is your name?"
  name <- getLine
  putStrLn $ greeting name
  where
    greeting :: String -> String
    greeting name = "Hey " ++ name ++ "!"
```

## 行的长度

首选的最大行长是80个字符。

!!! 提示 在行的长度方面没有硬性规定。有些行就是要比平时长一点。然而，如果你的代码行超过了这个限制，请尽量把代码分成小块，或者把长行分成多个短行。

## 空白位

不使用尾部的白色空格（使用一些工具来自动清理尾部的白色空格）。

在二元运算符的两边用一个空格环绕。

## 对齐

使用逗号在前的方式来格式化 module exports, lists, tuples, records 等。

```haskell
answers :: [Maybe Int]
answers =
  [ Just 42
  , Just 7
  , Nothing
  ]
```

如果一个函数定义不符合行数限制，那么就按照相同的分隔符对齐多行，如`::`,  `=>`, `->`。

```haskell
-- + Good
printQuestion
  :: Show a
  => Text  -- ^ Question text
  -> [a]   -- ^ List of available answers
  -> IO ()

-- + Acceptable if function name is short
fun :: Show a
    => Text  -- ^ Question text
    -> [a]   -- ^ List of available answers
    -> IO ()
```

逗号在前，将 records 每一个 field 单行对齐。

```haskell
-- + Good
data Foo = Foo
  { fooBar  :: Bar
  , fooBaz  :: Baz
  , fooQuux :: Quux
  } deriving (Eq, Show, Generic)
    deriving anyclass (FromJSON, ToJSON)

-- + Acceptable
data Foo =
  Foo { fooBar  :: Bar
      , fooBaz  :: Baz
      , fooQuux :: Quux
      } deriving (Eq, Show, Generic)
        deriving anyclass (FromJSON, ToJSON)
```

将和类型与每个构造函数放在单独一行中，并把 `=` 和 `|` 放在最前面。

```haskell
-- + Good
data TrafficLight
  = Red
  | Yellow
  | Green
  deriving (Eq, Ord, Enum, Bounded, Show, Read)

-- + Acceptable
data TrafficLight = Red
                  | Yellow
                  | Green
  deriving (Eq, Ord, Enum, Bounded, Show, Read)
```

尽量在函数定义内遵循上述规则，但不要狂热。

```haskell
-- + Good
createFoo = Foo
  <$> veryLongBar
  <*> veryLongBaz

-- + Acceptable
createFoo = Foo
        <$> veryLongBar
        <*> veryLongBaz

-- + Acceptable
createFoo =
  Foo <$> veryLongBar
      <*> veryLongBaz

-- - Bad
createFoo = Foo <$> veryLongBar
                <*> veryLongBaz

-- - Bad
createFoo =
  Foo  -- there's no need to put the constructor on a separate line and have an extra line
  <$> veryLongBar
  <*> veryLongBa
```

基本上，通常可以在不引入对齐依赖的情况下连接后续的行。尽量不要不必要地跨过多个短行。

如果一个函数应用必须产生多行以适应最大行长，那么在标题之后的每一行写一个参数，缩进一级。

```haskell
veryLongProductionName
  firstArgumentOfThisFunction
  secondArgumentOfThisFunction
  (DummyDatatype withDummyField1 andDummyField2)
  lastArgumentOfThisFunction
```

## 命名
### 函数和变量

- 函数和变量名称使用 **lowerCamelCase** 。
- 数据类型、类型库和构造函数使用 **UpperCamelCase** 。

!!!注意 只对**局部变量**使用 ids_with_underscores。

尽量不要创建新的操作符。

不要使用超短或不具描述性的名称，如 `a` 、`par` 、`g` ，除非这些变量的类型足够普遍。

不要为变量引入不必要的冗长名称。

出于可读性的考虑，当使用缩写作为较长名称的一部分时，不要将所有字母大写。例如，写 `TomlException` 而不是 `TOMLException` 。

Unicode 符号只允许在已经使用 unicode 符号的模块中使用。如果你创建了一个 unicode 名称，你也应该创建一个非 unicode 的名称作为别名。

###  数据类型

在Haskell中创建数据类型是非常容易的。通常，引入一个自定义的数据类型（枚举或newtype）而不是使用一个常用的数据类型（如Int、String、Set Text等）是一个好主意。

类型别名只允许用于更加细致地描述一个笼统的类型。

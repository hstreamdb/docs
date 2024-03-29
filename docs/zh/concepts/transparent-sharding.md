# 透明分区

## HStreamDB 中的透明分区

HStreamDB 中的透明分区意味着每个流将包含多个隐式分区，分布在多个服务器节点中。我
们相信，Stream 本身就是一个足够简洁和强大的抽象概念。因此，分区应该只是实现细节，而不
会暴露给用户。由于这些分区对用户来说是不可见的，所以在用户看来，每个流都是作为一
个整体管理的。

## 使用 HStreamDB 中的透明分区功能

透明分区功能不要求用户处理任何分区逻辑，如分区数量或分区映射。作为一个用户，他们
需要做的就是在试图向流写入 record 时给定分区键。每个键都对应着一个虚拟分区，
HServer 会把这些虚拟分区映射到存储组件中的物理分区。

如果用户不指定分区键，所有没有分区键的 Record 将被分配到 Stream 的默认分区。因此，
如果所有的 Record 都没有提供分区键，则系统将与没有分区是一样的行为。然而，无论
如何，用户都不会注意到这一点，因为在任何用户互动中都没有显式的分区逻辑。

## 为什么要做透明分区

分区是缓解单节点性能瓶颈和提高系统横向扩展能力的有效解决方案。但是，如果将分区逻
辑直接暴露给用户，那么 Stream 等更高层次的抽象就会变得支离破碎，增加学习和使用的
成本。对用户隐藏分区，将大大降低使用 HStreamDB 的复杂性，但仍然可以利用分区的优
势。

```java
public interface BaseStream<T, S extends BaseStream<T, S>> {
    /**
     * 同步标志，表示后续流开始同步执行
     */
    S sequential();

    /**
     * 异步标志，表示后续流开始异步执行
     */
    S parallel();

    /**
     * 顺序无关标志，表示后续流不关心顺序，一些无序可以提效的优化可以使用，不是特意打乱顺序
     */
    S unordered();
}
```

```java
public interface Stream<T> {
    /**
     * 过滤
     */
    Stream<T> filter(Predicate<? super T> predicate);

    /**
     * 映射
     */
    <R> Stream<R> map(Function<? super T, ? extends R> mapper);

    /**
     * 映射（一对多）多通过一个Stream表达，返回一个Stream
     */
    <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);

    /**
     * 去重
     */
    Stream<T> distinct();

    /**
     * 排序
     */
    Stream<T> sorted(Comparator<? super T> comparator);

    /**
     * 遍历（不结束流）
     */
    Stream<T> peek(Consumer<? super T> action);

    /**
     * 遍历（结束流）
     */
    void forEach(Consumer<? super T> action);

    /**
     * 限制个数
     */
    Stream<T> limit(long maxSize);

    /**
     * 跳过个数
     */
    Stream<T> skip(long n);

    /**
     * 多合一，因结果可能为空所以返回Optional包装
     */
    Optional<T> reduce(BinaryOperator<T> accumulator);
    Optional<T> min(Comparator<? super T> comparator);
    Optional<T> max(Comparator<? super T> comparator);

    /**
     * 聚合到集合类
     */
    <R, A> R collect(Collector<? super T, A, R> collector);

    /**
     * 聚合个数
     */
    long count();

    /**
     * 存在判定
     */
    boolean anyMatch(Predicate<? super T> predicate);
    boolean allMatch(Predicate<? super T> predicate);
    boolean noneMatch(Predicate<? super T> predicate);

    /**
     * 返回一个（顺序有关）
     */
    Optional<T> findFirst();

    /**
     * 返回一个（顺序无关）
     */
    Optional<T> findAny();
}
```


```java
public interface Collector<T, A, R> {
    /**
     * 创建结果容器
     */
    Supplier<A> supplier();

    /**
     * 值放入容器
     */
    BiConsumer<A, T> accumulator();

    /**
     * 两个值合并
     */
    BinaryOperator<A> combiner();

    /**
     * 值放入前转换为最终类型
     */
    Function<A, R> finisher();

    /**
     * 特征
     * CONCURRENT 并行
     * UNORDERED 无序
     * IDENTITY_FINISH 无需最终转换
     */
    Set<Characteristics> characteristics();
}
```

```java
public final class Collectors {
    /**
     * 转ArrayList
     */
    public static <T>
    Collector<T, ?, List<T>> toList() {
        return new CollectorImpl<>((Supplier<List<T>>) ArrayList::new, List::add,
                                   (left, right) -> { left.addAll(right); return left; },
                                   CH_ID);
    }

    /**
     * 转HashSet
     */
    public static <T>
    Collector<T, ?, Set<T>> toSet() {
        return new CollectorImpl<>((Supplier<Set<T>>) HashSet::new, Set::add,
                                   (left, right) -> { left.addAll(right); return left; },
                                   CH_UNORDERED_ID);
    }

    /**
     * 转有序集合，如TreeSet
     */
    public static <T, C extends Collection<T>>
    Collector<T, ?, C> toCollection(Supplier<C> collectionFactory) {
        return new CollectorImpl<>(collectionFactory, Collection<T>::add,
                                   (r1, r2) -> { r1.addAll(r2); return r1; },
                                   CH_ID);
    }

    /**
     * 转Map
     */
    public static <T, K, U, M extends Map<K, U>>
    Collector<T, ?, M> toMap(Function<? super T, ? extends K> keyMapper,
                                Function<? super T, ? extends U> valueMapper,
                                BinaryOperator<U> mergeFunction,
                                Supplier<M> mapSupplier) {
        BiConsumer<M, T> accumulator
                = (map, element) -> map.merge(keyMapper.apply(element),
                                              valueMapper.apply(element), mergeFunction);
        return new CollectorImpl<>(mapSupplier, accumulator, mapMerger(mergeFunction), CH_ID);
    }
}
```

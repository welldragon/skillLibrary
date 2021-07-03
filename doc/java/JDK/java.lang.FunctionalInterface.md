# 函数式接口

## Consumer

```java
public interface Consumer<T> {
    /**
    * 有入，无出
    */
    void accept(T t);

    // andThen，串联两个Consumer
}

// BiConsumer 双参数形式
```

## Predicate
```java
public interface Predicate<T> {
    /**
    * 有入，bool出
    */
    boolean test(T t);

    // and，negate，or，isEqual，复合逻辑运算
}

// BiPredicate 双参数形式
```

## Function
```java
public interface Function<T, R> {
    /**
    * 有入，有出
    */
    R apply(T t);

    // compose（前串），andThen（后串），串联两个Function
    // identity，入出相等
}

// UnaryOperator 入出参数类型相同
// BiFunction 双参数形式
// BinaryOperator 组合，返回min or max的结果
```

## Supplier
```java
public interface Supplier<T> {
    /**
    * 无入，有出
    */
    T get();
}
```

## Runnable
```java
public interface Runnable {
    /**
    * 无入，无出
    */
    public abstract void run();
}
```
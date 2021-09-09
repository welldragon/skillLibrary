```java
public final class Optional<T> {
    /**
     * 终结方法：没有抛出固定异常
     */
    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * 终结方法：没有返回指定值
     */
    public T orElse(T other) {
        return value != null ? value : other;
    }

    /**
     * 终结方法：没有现在去获取
     */
    public T orElseGet(Supplier<? extends T> other) {
        return value != null ? value : other.get();
    }
    
    /**
     * 终结方法：没有抛出指定异常
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }
}
```

* 转换过程中的异常是不会处理的

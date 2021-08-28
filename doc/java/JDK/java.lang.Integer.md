# case1

```java
Integer i = 2;
Integer j = i; // i，j指向同一个对象
i++; // Integer包装的int是final，所以i++时候，包装了一个新对象给i，所以j不变
System.out.println("i=" + i + ",j=" + j);
// 输出：i=3,j=2
```

# 源码

```java
public final class Integer {
    /**
     * -128 到 127会从缓存里拿，不会new
     */
    public static Integer valueOf(int i) {
        if (i >= IntegerCache.low && i <= IntegerCache.high)
            return IntegerCache.cache[i + (-IntegerCache.low)];
        return new Integer(i);
    }
    
    private static class IntegerCache {
        static final int low = -128;
        static final int high;
        static final Integer cache[];
        // ...
    }
}
```
```java
public class CompletableFuture<T> implements Future<T>, CompletionStage<T> {
    /**
     * 包装在CompletableFuture里执行，添加CompletionStage的回调方法
     */
    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        // asyncPool默认为ForkJoinPool.commonPool
        return asyncRunStage(asyncPool, runnable);
    }
}
```
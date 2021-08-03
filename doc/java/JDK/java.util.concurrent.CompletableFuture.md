```java
public class CompletableFuture<T> implements Future<T>, CompletionStage<T> {
    /**
     * 包装在CompletableFuture里执行，添加CompletionStage的回调方法
     * CompletableFuture的get返回是Void，只能用来获取执行阶段，因为Runnable没有返回值
     */
    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        // asyncPool默认为ForkJoinPool.commonPool
        // 支持传一个线程池替代默认
        return asyncRunStage(asyncPool, runnable);
    }

    /**
     * CompletableFuture的get返回Supplier的结果
     */
    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
        return asyncSupplyStage(asyncPool, supplier);
    }

    /**
     * 任意一个完成就算完成，返回的CompletableFuture是汇聚后的状态，实际每个CompletableFuture的值通过原本的get获取
     */
    public static CompletableFuture<Object> anyOf(CompletableFuture<?>... cfs) {
        return orTree(cfs, 0, cfs.length - 1);
    }

    /**
     * 全部完成就算完成
     */
    public static CompletableFuture<Void> allOf(CompletableFuture<?>... cfs) {
        return andTree(cfs, 0, cfs.length - 1);
    }

    /**
     * 固定值包装在CompletableFuture中，状态为已完成
     */
    public static <U> CompletableFuture<U> completedFuture(U value) {
        return new CompletableFuture<U>((value == null) ? NIL : value);
    }
}
```

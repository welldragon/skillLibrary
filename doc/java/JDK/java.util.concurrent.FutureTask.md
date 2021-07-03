可取消异步计算基本实现

```java
public class FutureTask<V> implements RunnableFuture<V> {
    private volatile int state;
    private static final int NEW          = 0; // 初始状态
    private static final int COMPLETING   = 1; // 计算结束，结果存储前
    private static final int NORMAL       = 2; // 计算成功，结果存储后
    private static final int EXCEPTIONAL  = 3; // 计算异常，异常存储后
    private static final int CANCELLED    = 4; // 主动取消，不打断
    private static final int INTERRUPTING = 5; // 主动取消，要打断，interrupt前
    private static final int INTERRUPTED  = 6; // 主动取消，要打断，interrupt后

    public FutureTask(Callable<V> callable) {
        if (callable == null)
            throw new NullPointerException();
        this.callable = callable;
        this.state = NEW; // 状态初始化 NEW
    }

    public void run() {
        if (state != NEW ||
            !UNSAFE.compareAndSwapObject(this, runnerOffset,
                                         null, Thread.currentThread()))
            return;
        try {
            Callable<V> c = callable;
            if (c != null && state == NEW) {
                V result;
                boolean ran;
                try {
                    result = c.call();
                    ran = true;
                } catch (Throwable ex) {
                    result = null;
                    ran = false;
                    // 计算异常，存储异常
                    setException(ex);
                }
                if (ran)
                    // 计算成功，存储计算结果
                    set(result);
            }
        } finally {
            runner = null;
            int s = state;
            if (s >= INTERRUPTING)
                handlePossibleCancellationInterrupt(s);
        }
    }

    protected void set(V v) {
        // 状态 NEW -> COMPLETING
        if (UNSAFE.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)) {
            outcome = v;
            // 状态 COMPLETING -> NORMAL
            UNSAFE.putOrderedInt(this, stateOffset, NORMAL);
            finishCompletion();
        }
    }

    protected void setException(Throwable t) {
        // 状态 NEW -> COMPLETING
        if (UNSAFE.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)) {
            outcome = t;
            // 状态 COMPLETING -> EXCEPTIONAL
            UNSAFE.putOrderedInt(this, stateOffset, EXCEPTIONAL);
            finishCompletion();
        }
    }

    // 主动取消，如果要打断多调用一个interrupt
    public boolean cancel(boolean mayInterruptIfRunning) {
        // 状态 NEW -> INTERRUPTING or CANCELLED
        if (!(state == NEW &&
              UNSAFE.compareAndSwapInt(this, stateOffset, NEW,
                  mayInterruptIfRunning ? INTERRUPTING : CANCELLED)))
            return false;
        try {
            if (mayInterruptIfRunning) {
                try {
                    Thread t = runner;
                    if (t != null)
                        t.interrupt();
                } finally {
                    // 状态 INTERRUPTING -> INTERRUPTED
                    UNSAFE.putOrderedInt(this, stateOffset, INTERRUPTED);
                }
            }
        } finally {
            finishCompletion();
        }
        return true;
    }

    // 判断是否结束
    public boolean isDone() {
        // 只要不是NEW，就很快有结果
        return state != NEW;
    }

    public V get() throws InterruptedException, ExecutionException {
        int s = state;
        if (s <= COMPLETING)
            // 等待结果
            s = awaitDone(false, 0L);
        return report(s);
    }
}
```

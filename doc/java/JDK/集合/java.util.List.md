# sort排序

1、Collections.sort就是调用List.sort

2、List.sort

```java
default void sort(Comparator<? super E> c) {
    // 转换为数组
    Object[] a = this.toArray();
    // 数组排序
    Arrays.sort(a, (Comparator) c);
    // 把数组排序的结果set回List
    ListIterator<E> i = this.listIterator();
    for (Object e : a) {
        i.next();
        i.set((E) e);
    }
}
```

3、Arrays.sort

```java
public static <T> void sort(T[] a, Comparator<? super T> c) {
    if (c == null) {
        sort(a);
    } else {
        // 设置环境变量，适配老代码，用老式排序算法，新算法有更严格的约束条件
        if (LegacyMergeSort.userRequested)
            // JDK8之前的老式排序算法
            legacyMergeSort(a, c);
        else
            // JDK8的排序算法
            TimSort.sort(a, 0, a.length, c, null, 0, 0);
    }
}
```

4、接口Comparable.compareTo的约束条件

* 可逆：sgn(x.compareTo(y)) == -sgn(y.compareTo(x))
* 可传递： (x.compareTo(y)>0 && y.compareTo(z)>0)表示x.compareTo(z)>0
* 相等：x.compareTo(y)==0意味着sgn(x.compareTo(z)) == sgn(y.compareTo(z))

5、排序不报错，但返回结果顺序不稳定

```java
// badcase
private static final Comparator<ProjectView> VIEW_COMPARATOR = (a, b) -> {
    if (a.getProjectType() < b.getProjectType()) {
        return -1;
    } else if (a.getProjectType() > b.getProjectType()) {
        return 1;
    } else if (a.getBeginDate().compareTo(DateDefault.BEGIN) == 0) { // a和b的比较和对方的值无关
        return 1;
    } else if (b.getBeginDate().compareTo(DateDefault.BEGIN) == 0) { // a和b的比较和对方的值无关
        return -1;
    } else {
        return a.getBeginDate().compareTo(b.getBeginDate());
    }
};
```

6、需要两字段排序的情况

```java
default Comparator<T> thenComparing(Comparator<? super T> other) {
    Objects.requireNonNull(other);
    return (Comparator<T> & Serializable) (c1, c2) -> {
        int res = compare(c1, c2);
        return (res != 0) ? res : other.compare(c1, c2); // 当比较器1判断相等时，使用比较器2
    };
}
```

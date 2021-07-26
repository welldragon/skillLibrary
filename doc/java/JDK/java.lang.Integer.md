```java
Integer i = 2;
Integer j = i; // i，j指向同一个对象
i++; // Integer包装的int是final，所以i++时候，包装了一个新对象给i，所以j不变
System.out.println("i=" + i + ",j=" + j);
// 输出：i=3,j=2
```

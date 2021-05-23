# 各种path和name的方法分不清

## 1、通过path String计算得到
```java
/**
 * 创建File时的路径，如果new File使用相对路径，getParent是拿不到父级路径
 */
public String getPath() {
    return path;
}

/**
 * 当前文件名或当级目录名，不带路径
 */
public String getName() {
    int index = path.lastIndexOf(separatorChar);
    if (index < prefixLength) return path.substring(prefixLength);
    return path.substring(index + 1);
}

/**
 * 父级目录名，带路径
 */
public String getParent() {
    int index = path.lastIndexOf(separatorChar);
    if (index < prefixLength) {
        if ((prefixLength > 0) && (path.length() > prefixLength))
            return path.substring(0, prefixLength);
        return null;
    }
    return path.substring(0, index);
}
```

## 2、基于操作系统的实现

```java
/**
 * Absolute [ˈæbsəluːt] 绝对的
 * 文件名+路径，保留../这类相对路径
 */
public String getAbsolutePath() {
    return fs.resolve(this);
}

/**
 * Canonical [kəˈnɑːnɪkl] 经典的
 * 文件名+路径，计算../这类相对路径之后的实际路径
 */
public String getCanonicalPath() throws IOException {
    if (isInvalid()) {
        throw new IOException("Invalid file path");
    }
    return fs.canonicalize(fs.resolve(this));
}
```

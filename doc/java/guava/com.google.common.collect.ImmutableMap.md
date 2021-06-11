```java
// 1组kv使用SingletonImmutableBiMap
public static <K, V> ImmutableMap<K, V> of(K k1, V v1) {
    return ImmutableBiMap.of(k1, v1);
}

final class SingletonImmutableBiMap<K, V> {
    final transient K singleKey;
    final transient V singleValue;

    // 获取就是比较key
    public V get(@Nullable Object key) {
        return singleKey.equals(key) ? singleValue : null;
    }
}

// 2-5组kv使用RegularImmutableMap
public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2) {
    return RegularImmutableMap.fromEntries(entryOf(k1, v1), entryOf(k2, v2));
}

static <K, V> ImmutableMap<K, V> fromEntryArray(int n, Entry<K, V>[] entryArray) {
    checkPositionIndex(n, entryArray.length);
    if (n == 0) {
      return (RegularImmutableMap<K, V>) EMPTY;
    }
    Entry<K, V>[] entries;
    if (n == entryArray.length) {
      entries = entryArray;
    } else {
      entries = createEntryArray(n);
    }
    int tableSize = Hashing.closedTableSize(n, MAX_LOAD_FACTOR);
    ImmutableMapEntry<K, V>[] table = createEntryArray(tableSize);
    int mask = tableSize - 1;
    for (int entryIndex = 0; entryIndex < n; entryIndex++) {
      Entry<K, V> entry = entryArray[entryIndex];
      K key = entry.getKey();
      V value = entry.getValue();
      checkEntryNotNull(key, value);
      int tableIndex = Hashing.smear(key.hashCode()) & mask;
      @Nullable ImmutableMapEntry<K, V> existing = table[tableIndex];
      ImmutableMapEntry<K, V> newEntry =
          (existing == null)
              ? makeImmutable(entry, key, value)
              : new NonTerminalImmutableMapEntry<K, V>(key, value, existing);
      table[tableIndex] = newEntry;
      entries[entryIndex] = newEntry;
      int bucketSize = checkNoConflictInKeyBucket(key, newEntry, existing);
      if (bucketSize > MAX_HASH_BUCKET_LENGTH) {
        // 单个bucket冲突大于MAX_HASH_BUCKET_LENGTH=8，用包装的HashMap
        return JdkBackedImmutableMap.create(n, entryArray);
      }
    }
    return new RegularImmutableMap<>(entries, table, mask);
  }

final class RegularImmutableMap<K, V> {
    // KV实体数组
    final transient Entry<K, V>[] entries;
    // bucket槽，相同hash会用链表
    private final transient ImmutableMapEntry<K, V>[] table;
    // 取余的
    private final transient int mask;

    // 获取
    public V get(@Nullable Object key) {
        return get(key, table, mask);
    }

    static <V> @Nullable V get(
        @Nullable Object key, ImmutableMapEntry<?, V> @Nullable [] keyTable, int mask) {
        if (key == null || keyTable == null) {
        return null;
        }
        int index = Hashing.smear(key.hashCode()) & mask;
        // hash找到对应的槽，遍历链表
        for (ImmutableMapEntry<?, V> entry = keyTable[index];
            entry != null;
            entry = entry.getNextInKeyBucket()) {
        Object candidateKey = entry.getKey();

        /*
        * 这里用equals，HashMap用==，性能上略低于HashMap，guava的解释是大多数用户性能不敏感
        */
        if (key.equals(candidateKey)) {
            return entry.getValue();
        }
        }
        return null;
    }
}
```
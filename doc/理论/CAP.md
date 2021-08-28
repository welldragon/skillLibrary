# 概念

在一个分布式系统中

* 一致性（Consistency）所有数据备份，在同一时刻是否同样的值
* 可用性（Availability）保证每个请求不管成功或者失败都有响应
* 分区容错性（Partition tolerance）系统中任意信息的丢失或失败不会影响系统的继续运作

CAP 原则指的是，这三个要素最多只能同时实现两点，不可能三者兼顾

* 满足CA舍弃P: 不在是分布式系统
* 满足CP舍弃A: 发生问题不做出响应
* 满足AP舍弃C(常见): 允许短时间不一致，保证最终一致性

# BASE

满足AP的经验总结

* Basically Available（基本可用）
* Soft state（软状态）
* Eventually consistent（最终一致性）

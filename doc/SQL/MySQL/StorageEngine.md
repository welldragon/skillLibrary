### Storage Engine

| |InnoDB|MyISAM|
|---|---|---|
|存储限制|64TB=2^32 *16KB,页号32位int,一页16KB<br>所有表和索引在一个文件或多个(独立表空间)文件|256TB<br>每个表3个文件.frm(表结构)、.myd(数据)、.myi(索引)
|事务| Yes
|锁粒度|Row|Table
| MVCC| Yes
|地理|Yes(实现OpenGIS,R-tree,Spatial Index(空间索引),GeoHash)|Yes
|B-tree|Yes|Yes
|T-tree|
|Hash索引|


ISAM=Indexed Sequential Access Method

MVCC=Multi-Version Concurrency Control

GIS=Geographic Information System
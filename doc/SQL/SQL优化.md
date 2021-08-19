# case1: union多路归并

```SQL
# 需求：两张学习记录表，查询某个用户ID的半年内的学习记录，时间倒排

# 原SQL
select course_name, ctime
from (
         select course_name, ctime
         from table_course_study_a
         where user_id = 111
           and ctime > '2021-01-01'
         union all
         select course_name, ctime
         from table_course_study_b
         where user_id = 111
           and ctime > '2021-01-01'
     ) t
order by ctime desc
limit 0,10
# table_course_study_a有索引(user_id, ctime)
# table_course_study_b有索引(user_id, ctime)

# 问题：因union导致执行计划中有filesort，分表查询两张学习记录表，结果缓存合并后在排序

# 优化SQL
select course_name, ctime
from (
         select course_name, ctime, user_id
         from table_course_study_a
         where user_id = 111
           and ctime > '2021-01-01'
         union all
         select course_name, ctime, user_id
         from table_course_study_b
         where user_id = 111
           and ctime > '2021-01-01'
     ) t
where user_id = 111
  and ctime > '2021-01-01'
order by ctime desc
limit 0,10

# 优化方案：user_id传递到外层，让内外使用相同的查询条件，让MySQL判定可以使用多路归并优化filesort，使得执行计划变成同时查询两张表的索引同时归并，到达10条就直接返回
```

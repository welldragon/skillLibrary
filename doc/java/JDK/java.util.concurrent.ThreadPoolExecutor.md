# 任务异常抛出

* executorService.execute 异常导致线程死亡，被回收
* executorService.submit 异常被收集起来，为了Future.get获取，线程不死亡
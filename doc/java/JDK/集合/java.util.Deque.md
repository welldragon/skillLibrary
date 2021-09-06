单向队列 queue

|队首<-队尾|异常|null|
|---|---|---|
|队尾添加|add|offer
|队首移除|remove|poll
|队首获取|element|peek

双向队列 deque

|队首<-队尾|异常|null| |异常|null|
|---|---|---|---|---|---|
|添加|addFirst|offerFirst| |addLast|offerLast
|移除|removeFirst|pollFirst| |removeLast|pollLast
|获取|getFirst|peekFirst| |getLast|peekLast

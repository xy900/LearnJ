# LearnJ
简单搭建SSM框架，用于学习J2EE中各种常用的技术，以及包括java基础在内的各种知识点。

### 技术选型
技术 | 名称  
---|---
Spring Framework | 容器，核心框架
Spring MVC  | MVC容器
Apache Shiro  | 安全框架
alibaba druid| 数据库连接池
MyBatis | ORM框架
Spring JDBC| 持久层框架
SFL4J 、Log4j、logback | 日志组件
Ehcache  | 缓存框架
Spring session | 集群环境中session共享解决方案
Jedis | redis的java客户端

## 学习流程
学习的流程记录，代码中均已实现。

### SSM架构搭建
- 主要将spring、Spring MVC、Mybatis进行整合，目前流行的springboot不用繁琐的配置，这里主要将环境搭建起来进行后面的学习。
- FactoryBean的使用，用于创建bean。
- 使用AOP记录日志。
- 使用shiro实现登录认证的功能。
- 使用spring session与redis解决集群中session共享问题。

### java
- JDK动态代理的实现。
- 使用数据库悲观锁，实现分布式锁。`select... for update`
- 使用redis的setnx操作，实现分布式锁。

### Ehcache
- 程序中创建Ehcache实例，进行测试。
- 测试Ehcache中的各种参数，例如`maxElementsInMemory`，`timeToIdleSeconds`，`overflowToDisk`，`timeToLiveSeconds`等。
- 修改mapper文件，将Ehcache作为Mybatis的二级缓存进行使用。
- 在spring中配置缓存管理器EhcacheManager，与spring整合；并且使用`spring cache`，通过`@Cacheable`等注解实现缓存的实现。
- 分布式Ehcache的实现（**待完成**）
>注意：在与mybatis和spring cache结合使用时，要注意缓存管理器EhcacheManager的创建过程。例如`CacheManager.create()`默认读取classpath目录下的ehcache.xml文件，找不到ehcache.xml文件则会加载ehcache.jar下的ehcache-failsafe.xml文件，并创建一个名为"\_\_DEFAULT\_\_"的CacheManager，为单例模式，再到spring注册CacheManager会报错。


### 多线程
- 使用`Object.wait()`模拟并发。
- **线程池ThreadPoolExecutor**的使用，包括提供的四种已经提供的方法`newCachedThreadPool`，`newFixedThreadPool`，`newScheduledThreadPool`，`newSingleThreadExecutor`。
- 自己定义线程池，弄清各参数的含义，以及何时使用**饱和策略**；了解jdk提供的四种饱和策略`AbortPolicy`，`DiscardPolicy`，`DiscardOldestPolicy`，`CallerRunsPolicy`。
- 各种**阻塞队列**的使用，如`ArrayBlockingQueue`,`LinkedBlockingQueue`,`SynchronousQueue`等。
- 查看阻塞队列源码，了解各自的不同于试用场景，实现消费者生产者模型。
- 熟悉**juc**包下的各种常用的类，如`CountDownLatch`,`CyclicBarrier`,`SemaphoreDemo`的使用。
- **valitile**关键字的作用。
- 了解**CAS**与**AQS**的知识，学习jdk提供的`ReentranceLock`, `ReentrantReadWriteLock`等锁的使用。
- 通过**Unsafe**类提供的CAS操作，自己定义一个类，实现无锁的线程安全的类。

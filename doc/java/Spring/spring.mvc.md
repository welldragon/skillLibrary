```java
public class DispatcherServlet {
    /**
     * spring mvc子容器初始化后，调用
     */
    @Override
    protected void onRefresh(ApplicationContext context) {
        initStrategies(context);
    }

    protected void initStrategies(ApplicationContext context) {
        // 初始化:文件上传的multipartResolver
        initMultipartResolver(context);
        // 初始化:国际化的localeResolver
        initLocaleResolver(context);
        // 初始化:主体样式的themeResolver
        initThemeResolver(context);
        // 初始化:请求和处理对象关系的HandlerMapping
        // RequestMappingHandlerMapping实现 处理@RequestMapping
        initHandlerMappings(context);
        // 初始化:请求适配器HandlerAdapter，负责转化MappingHandler的入参，获取ModelAndView
        // RequestMappingHandlerAdapter实现 配套RequestMappingHandlerMapping使用
        initHandlerAdapters(context);
        // 初始化:异常处理器HandlerExceptionResolver
        initHandlerExceptionResolvers(context);
        // 初始化:请求和视图名关系的RequestToViewNameTranslator
        initRequestToViewNameTranslator(context);
        // 初始化:视图处理器ViewResolver
        initViewResolvers(context);
        // 初始化:重定向缓存请求信息的FlashMapManager
        initFlashMapManager(context);
    }
}
```

```java
public abstract class AbstractHandlerMethodMapping<T> {
    /**
     * 存储RequestMapping和对应的处理方法的关系
     */
    class MappingRegistry {
        private final Map<T, HandlerMethod> mappingLookup = new LinkedHashMap<>();
        // ...
    }
    
    @Override
    public void afterPropertiesSet() {
        // 扫描bean，获取所有RequestMapping并注册
        initHandlerMethods();
    }

    /**
     * 通过path找到最佳的HandlerMethod
     */
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) {
        // ...
    }

    /**
     * HandlerMethod加上HandlerInterceptor，形成执行链
     */
    protected HandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest request) {
        // ...
    }
}

public abstract class RequestMappingInfoHandlerMapping extends AbstractHandlerMethodMapping<RequestMappingInfo> {
    
}

public class RequestMappingHandlerMapping extends RequestMappingInfoHandlerMapping {
    
}
```

```java
public class RequestMappingHandlerAdapter {
    @Override
    public void afterPropertiesSet() {
        initControllerAdviceCache();

        if (this.argumentResolvers == null) {
            List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
            // 初始化:参数注入
            this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
        }
        if (this.initBinderArgumentResolvers == null) {
            List<HandlerMethodArgumentResolver> resolvers = getDefaultInitBinderArgumentResolvers();
            this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
        }
        if (this.returnValueHandlers == null) {
            List<HandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
            // 初始化:返回值转换
            this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
        }
    }
}
```

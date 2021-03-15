# opt-log
基于springboot、annotation的操作日志

### 简介
* 使用annotation来标注方法，标记操作内容
* 使用SpEL来动态生成操作日志内容，使操作日志记录更加详细（记录操作内容ID等关键信息）
* 同一个方法，不同类型用户（admin，user等）使用时，获取不同的操作者
* 支持springboot2.0以上版本

### 使用
maven中引入
~~~xml
        <dependency>
            <groupId>kim.hanjie.easy</groupId>
            <artifactId>opt-log</artifactId>
            <version>0.1.0</version>
        </dependency>
~~~

### 例子
[opt-log-example](https://github.com/shanehan/opt-log-example)

### OptLog annotation
~~~java
public @interface OptLog {

    /**
     * 操作成功时记录的内容
     */
    String success();
    /**
     * 操作失败时记录的内容
     */
    String fail() default "";
    /**
     * 操作异常时记录的内容
     */
    String exception() default "";
    /**
     * 操作者
     */
    String operator() default "";
    /**
     * 业务唯一表示
     */
    String bizId() default "";
    /**
     * 模块信息
     */
    String module() default "";
    /**
     * 级别
     */
    int level() default 0;
    /**
     * 记录模式
     * ALL:只要操作就记录
     * SUCCESS:操作成功时记录
     * FAIL:操作失败时记录
     * EXCEPTION:操作异常时记录
     */
    OptLogMode[] logMode() default OptLogMode.ALL;

    enum OptLogMode {
        /**
         * 只要操作就记录
         */
        ALL,
        /**
         * 操作成功记录
         */
        SUCCESS,
        /**
         * 操作失败记录
         */
        FAIL,
        /**
         * 异常时记录
         */
        EXCEPTION,
        ;
    }
}
~~~

所有字段都支持SpEL，最终生成操作日志内容
~~~java
public class UserService {
    @OptLog(success = "'设置' + #id + '的密码为' + #password",
                fail = "'设置失败：' + #_retObj.message",
                bizId = "#id")
    public Result<Boolean> updatePasswordSuccess(Long id, String password) {
       return Result.ofSuccess();
    }
}
~~~
id = 1  
password = "123456"

最终生成success的内容为
~~~
设置1的密码为123456
~~~
bizId
~~~
1
~~~

### success, fail, exception的判断
执行一个方法，如何判断是success，fail，或exception？
默认情况下

方法抛异常为 **exception**  
返回值为null **fail**  
方法返回值void **success**  
方法返回值boolean 值为true **success**  
方法返回值Optional isPresent为true **success**  


判断策略[IOptStatusPolicy](/src/main/java/kim/hanjie/easy/opt/IOptStatusPolicy.java)
可以自己实现此接口或继承[OptStatusPolicy](/src/main/java/kim/hanjie/easy/opt/OptStatusPolicy.java)来实现自己的判断  
如：  
调用方法返回Result对象，包含成功失败信息  
方法内部用抛异常方式来处理数据检查，操作失败等，这种不是exception，而是fail    
示例如下
~~~java
public class Result<D> {

    private boolean success;
    private String message;
    private D data;
}
~~~


~~~java
public class CommonOptStatusPolicy extends OptStatusPolicy {

    @Override
    public OptStatus exceptionStatus(Throwable throwable) {
        if (throwable instanceof BizException) {
            return OptStatus.FAIL;
        }
        return super.exceptionStatus(throwable);
    }

    @Override
    protected OptStatus notNullReturnStatus(Method method, Object retObj) {
        if (retObj instanceof Result) {
            return ((Result) retObj).isSuccess() ? OptStatus.SUCCESS : OptStatus.FAIL;
        }
        return super.notNullReturnStatus(method, retObj);
    }
}
~~~
注册Bean
~~~java
@SpringBootApplication
public class OptExampleApplication {

    @Bean
    public IOptStatusPolicy optStatusPolicy() {
        return new CommonOptStatusPolicy();
    }
}
~~~

### 操作者 operator
* 直接通过参数获取
~~~java
public class UserService {
    @OptLog(success = "'设置' + #user?.id + '密码'", operator = "#adminId")
    public Result<Boolean> updatePassword(Long adminId, UserDO user) {
        return Result.ofFail("错啦");
    }
}
~~~
操作者operator = adminId的值

* 同一个方法可能会被多种用户调用  
~~~java
public class UserService {
    @OptLog(success = "'设置' + #id + '的密码'")
    public Result<Boolean> updatePassword(Long id, String password) {
        return Result.ofSuccess();
    }
}
~~~
有2套账号系统，一套用户（user）账号系统，一套管理员（admin）账号系统  
updatePassword用来更新用户密码，可以通过用户自己的请求来执行（操着为用户自己）  
也可通过后台管理员来重置密码用（操作者为admin管理员） 

可以通过[OptContext](/src/main/java/kim/hanjie/easy/opt/OptContext.java)来设置操作者的上下文  
可以通过servlet filter来统一设置哪些url是那种类型用户访问  
OptContext.put("adminId", "" + 100); 设置用户（此处写死仅作实例） 
OptContext.put("url", httpRequest.getRequestURI()); 也可以写入其他信息  

~~~java
public class AdminOptContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        OptContext.put("adminId", "" + 100);
        OptContext.put("reqId", UUID.randomUUID().toString());
        OptContext.put("url", httpRequest.getRequestURI());
        chain.doFilter(request, response);
        OptContext.clean();
    }
}
~~~
在日志记录器中通过OptLogRecord.getContextValue()来获取用户以及其他信息


### 业务主键 bizId
用于记录业务操作主键，用于后期查询

### 模块 module
模块信息，可以多级（一级模块.二级模块），到了记录器后处理

### 操作日志级别 level 
默认OptLog.level为0  
可以通过设置ignoreLevel来忽略一些操作日志的记录 
当日志级别大于ignoreLevel时，操作日志将被忽略掉
~~~yaml
easy:
  opt:
    ignoreLevel: 3
~~~
easy.opt.ignoreLevel 默认为Integer.MAX


### 记录器
实现接口[IOptLogRecorder](/src/main/java/kim/hanjie/easy/opt/IOptLogRecorder.java)  
~~~java
public interface IOptLogRecorder {
    /**
     * 是否支持记录这种操作
     * @param record 记录
     * @return true/false
     */
    boolean support(OptLogRecord record);
    /**
     * 记录操作日志
     * @param record 记录
     */
    void record(OptLogRecord record);
}
~~~
support方法中可以判断是否需要由此记录器记录  
record中可通过getContextValue()方法获取更多参数，如操作方（admin、user）来决定是否使用此记录器  
record方法用于记录，OptLogRecord.getOptDescription()来获取操作日志信息
日志记录是在单独在一个线程里来处理  
此处可以写数据库，或者通过MQ把操作日志发送到相应的服务去
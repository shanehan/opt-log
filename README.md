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

#### OptLog annotation
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
可以自己实现此接口或继承[OptStatusPolicy]()来实现自己的判断  
示例如下
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


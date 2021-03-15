package kim.hanjie.easy.opt;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hanjie
 * @date 2021/3/10
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
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

        public boolean isAll() {
            return this == ALL;
        }

        public boolean isSuccess() {
            return this == SUCCESS;
        }

        public boolean isFail() {
            return this == FAIL;
        }

        public boolean isException() {
            return this == EXCEPTION;
        }
    }
}

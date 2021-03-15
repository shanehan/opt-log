package kim.hanjie.common.opt;

import java.lang.reflect.Method;

/**
 * @author han
 * @date 2021/3/10
 */
public interface IOptStatusPolicy {


    /**
     * 方法抛出异常后，判断操作状态
     *
     * @param throwable 异常
     * @return OptStatus
     */
    OptStatus exceptionStatus(Throwable throwable);


    /**
     * 根据返回对象判断操作状态
     *
     * @param method 方法
     * @param retObj 调用方法的返回值
     * @return OptStatus
     */
    OptStatus returnStatus(Method method, Object retObj);
}

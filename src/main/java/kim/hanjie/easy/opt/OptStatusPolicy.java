package kim.hanjie.easy.opt;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author han
 * @date 2021/3/10
 */
public class OptStatusPolicy implements IOptStatusPolicy {


    /**
     * 方法抛出异常后，判断操作状态
     *
     * @param throwable 异常
     * @return OptStatus
     */
    @Override
    public OptStatus exceptionStatus(Throwable throwable) {
        return OptStatus.EXCEPTION;
    }

    /**
     * 根据返回对象判断操作状态
     *
     * @param retObj 调用方法的返回值
     * @return OptStatus
     */
    @Override
    public OptStatus returnStatus(Method method, Object retObj) {
        if (method.getReturnType() == void.class) {
            return OptStatus.SUCCESS;
        }
        if (retObj == null) {
            return nullReturnStatus(method);
        }
        OptStatus optStatus = notNullReturnStatus(method, retObj);
        if (optStatus != null) {
            return optStatus;
        }
        return OptStatus.SUCCESS;
    }

    protected OptStatus nullReturnStatus(Method method) {
        return OptStatus.FAIL;
    }

    protected OptStatus notNullReturnStatus(Method method, Object retObj) {
        if (retObj instanceof Boolean) {
            return ((Boolean) retObj) ? OptStatus.SUCCESS : OptStatus.FAIL;
        }
        if (retObj instanceof Optional) {
            return ((Optional) retObj).isPresent() ? OptStatus.SUCCESS : OptStatus.FAIL;
        }
        return null;
    }
}

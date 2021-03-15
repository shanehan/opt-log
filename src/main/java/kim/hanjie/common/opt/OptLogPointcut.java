package kim.hanjie.common.opt;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author han
 * @date 2021/3/10
 */
public class OptLogPointcut extends StaticMethodMatcherPointcut {

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        Set<OptLog> allMergedAnnotations = AnnotatedElementUtils.findAllMergedAnnotations(method, OptLog.class);
        return !allMergedAnnotations.isEmpty();
    }
}

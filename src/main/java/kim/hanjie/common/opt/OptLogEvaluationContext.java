package kim.hanjie.common.opt;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author han
 * @date 2021/3/10
 */
public class OptLogEvaluationContext extends MethodBasedEvaluationContext {

    public OptLogEvaluationContext(Object rootObject, Method method, Object[] arguments,
                                   ParameterNameDiscoverer parameterNameDiscoverer,
                                   Object retObj, String errorMsg, Map<String, String> optContext) {
        super(rootObject, method, arguments, parameterNameDiscoverer);
        setVariable("_retObj", retObj);
        setVariable("_errorMsg", errorMsg);
        setVariable("_context", optContext);
    }
}

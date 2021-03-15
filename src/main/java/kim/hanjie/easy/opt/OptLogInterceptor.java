package kim.hanjie.easy.opt;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 操作日志切面
 *
 * @author han
 * @date 2021/3/10
 */
public class OptLogInterceptor implements MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptLogInterceptor.class);

    private OptLogExpressionEvaluator expressionEvaluator = new OptLogExpressionEvaluator();
    private IOptLogRecordHandler optLogRecordHandler;
    private IOptStatusPolicy optStatusPolicy;
    private OptLogConfig optLogConfig;

    public void setOptLogRecordHandler(IOptLogRecordHandler optLogRecordHandler) {
        this.optLogRecordHandler = optLogRecordHandler;
    }

    public void setOptStatusPolicy(IOptStatusPolicy optStatusPolicy) {
        this.optStatusPolicy = optStatusPolicy;
    }

    public void setOptLogConfig(OptLogConfig optLogConfig) {
        this.optLogConfig = optLogConfig;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object proceed = null;
        Throwable throwable = null;
        try {
            proceed = invocation.proceed();
        } catch (Exception e) {
            throwable = e;
        }
        Method method = invocation.getMethod();
        Class<?> targetClass = invocation.getThis() == null ? null : AopProxyUtils.ultimateTargetClass(invocation.getThis());
        Set<OptLog> optLogs = AnnotatedElementUtils.findAllMergedAnnotations(method, OptLog.class);
        records(optLogs, targetClass, method, invocation.getArguments(), proceed, throwable);
        if (throwable != null) {
            throw throwable;
        }
        return proceed;
    }

    private void records(Set<OptLog> optLogs, Class<?> targetClass, Method method, Object[] args, Object retObj, Throwable throwable) {
        if (optLogs.isEmpty()) {
            return;
        }
        OptStatus status = status(method, retObj, throwable);
        if (status == null) {
            return;
        }
        try {
            for (OptLog optLog : optLogs) {
                // 检查是否要打印
                if (!need(status, optLog)) {
                    continue;
                }
                // 检查打印级别
                if (!levelCheck(optLog.level())) {
                    continue;
                }
                Map<String, String> optContext = OptContext.getCopyOfContextMap();
                OptLogRecord record = process(status, optLog, targetClass, method, args, retObj, throwable == null ? null : throwable.getMessage(), optContext);
                optLogRecordHandler.recordOptLog(record);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    private OptLogRecord process(OptStatus status, OptLog optLog, Class<?> targetClass, Method method, Object[] args, Object retObj, String errorMsg, Map<String, String> optContext) {
        ArrayList<String> templates = new ArrayList<>();
        if (status == OptStatus.SUCCESS) {
            templates.add(optLog.success());
        } else if (status == OptStatus.FAIL) {
            templates.add(optLog.fail());
        } else {
            templates.add(optLog.exception());
        }
        templates.add(optLog.operator());
        templates.add(optLog.bizId());
        templates.add(optLog.module());
        Map<String, String> process = process(templates, targetClass, method, args, retObj, errorMsg, optContext);
        return new OptLogRecord(status, process.get(optLog.success()), process.get(optLog.fail()), process.get(optLog.exception()), process.get(optLog.operator())
                , process.get(optLog.bizId()), process.get(optLog.module()), optLog.level(), targetClass.getName(), method.getName(), optContext);
    }

    private Map<String, String> process(Collection<String> templates, Class<?> targetClass, Method method, Object[] args, Object retObj, String errorMsg, Map<String, String> optContext) {
        Map<String, String> expressionValues = new HashMap<>(16);
        EvaluationContext evaluationContext = expressionEvaluator.createEvaluationContext(targetClass, method, args, retObj, errorMsg, optContext);
        for (String tpl : templates) {
            if (tpl == null || tpl.isEmpty()) {
                expressionValues.put(tpl, tpl);
                continue;
            }
            AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
            try {
                String value = expressionEvaluator.parseExpression(evaluationContext, annotatedElementKey, tpl);
                expressionValues.put(tpl, value);
            } catch (Exception e) {
                expressionValues.put(tpl, tpl);
                LOGGER.error("解析操作日志SpEL【" + tpl + "】错误，" + e.getMessage());
            }
        }
        return expressionValues;
    }


    /**
     * 获取执行此方法的状态
     */
    private OptStatus status(Method method, Object retObj, Throwable throwable) {
        if (throwable != null) {
            return optStatusPolicy.exceptionStatus(throwable);
        }
        return optStatusPolicy.returnStatus(method, retObj);
    }

    private boolean need(OptStatus optStatus, OptLog optLog) {
        OptLog.OptLogMode[] optLogModes = optLog.logMode();
        for (OptLog.OptLogMode optLogMode : optLogModes) {
            if (optLogMode.isAll()) {
                return true;
            }
            if (optLogMode.isSuccess() && optStatus == OptStatus.SUCCESS) {
                return true;
            }
            if (optLogMode.isFail() && optStatus == OptStatus.FAIL) {
                return true;
            }
            if (optLogMode.isException() && optStatus == OptStatus.EXCEPTION) {
                return true;
            }
        }
        return false;
    }

    private boolean levelCheck(int level) {
        return optLogConfig.getIgnoreLevel() >= level;
    }


}

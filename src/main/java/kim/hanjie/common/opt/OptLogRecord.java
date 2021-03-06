package kim.hanjie.common.opt;


import java.util.Map;

/**
 * @author han
 * @date 2021/3/11
 */
public class OptLogRecord {

    private OptStatus status;
    private long optTime;
    private String success;
    private String fail;
    private String exception;
    private String operator;
    private String bizId;
    private String module;
    private int level;
    private Class clazz;
    private String method;
    private Map<String, String> context;

    public OptLogRecord(OptStatus status, long optTime, String success, String fail, String exception, String operator, String bizId, String module, int level, Class clazz, String method, Map<String, String> context) {
        this.status = status;
        this.optTime = optTime;
        this.success = success;
        this.fail = fail;
        this.exception = exception;
        this.operator = operator;
        this.bizId = bizId;
        this.module = module;
        this.level = level;
        this.clazz = clazz;
        this.method = method;
        this.context = context;
    }

    public String getOptDescription() {
        if (status == OptStatus.SUCCESS) {
            return success;
        } else if (status == OptStatus.FAIL) {
            return fail;
        } else {
            return exception;
        }
    }

    public OptStatus getStatus() {
        return status;
    }

    public long getOptTime() {
        return optTime;
    }

    public String getSuccess() {
        return success;
    }

    public String getFail() {
        return fail;
    }

    public String getException() {
        return exception;
    }

    public String getOperator() {
        return operator;
    }

    public String getBizId() {
        return bizId;
    }

    public String getModule() {
        return module;
    }

    public int getLevel() {
        return level;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public String getContextValue(String key) {
        if (key == null) {
            return null;
        }
        if (context == null) {
            return null;
        }
        return context.get(key);
    }

    @Override
    public String toString() {
        return "OptLogRecord{" +
                "status=" + status +
                ", optTime=" + optTime +
                ", success='" + success + '\'' +
                ", fail='" + fail + '\'' +
                ", exception='" + exception + '\'' +
                ", operator='" + operator + '\'' +
                ", bizId='" + bizId + '\'' +
                ", module='" + module + '\'' +
                ", level=" + level +
                ", clazz=" + clazz +
                ", method='" + method + '\'' +
                ", context=" + context +
                '}';
    }
}

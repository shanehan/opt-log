package kim.hanjie.easy.opt;

/**
 * @author han
 * @date 2021/3/10
 */
public class OptLogTpl {

    private String successTpl;
    private String failTpl;
    private String exceptionTpl;
    private String operatorTpl;
    private String bizIdTpl;
    private String moduleTpl;

    public OptLogTpl(OptLog optLog) {
        successTpl = optLog.success();
        failTpl = optLog.fail();
        exceptionTpl = optLog.exception();
        operatorTpl = optLog.operator();
        failTpl = optLog.fail();
        bizIdTpl = optLog.bizId();
        moduleTpl = optLog.module();
    }

    public String getSuccessTpl() {
        return successTpl;
    }

    public String getFailTpl() {
        return failTpl;
    }

    public String getExceptionTpl() {
        return exceptionTpl;
    }

    public String getOperatorTpl() {
        return operatorTpl;
    }

    public String getBizIdTpl() {
        return bizIdTpl;
    }

    public String getModuleTpl() {
        return moduleTpl;
    }
}

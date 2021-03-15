package kim.hanjie.easy.opt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author han
 * @date 2021/3/10
 */
public class LoggerOptLogRecorder implements IOptLogRecorder {

    private Logger logger = LoggerFactory.getLogger("opt-log");

    /**
     * 是否支持记录这种操作
     *
     * @param record 记录
     * @return true/false
     */
    @Override
    public boolean support(OptLogRecord record) {
        return true;
    }


    /**
     * 记录操作日志
     *
     * @param record 记录
     */
    @Override
    public void record(OptLogRecord record) {
        logger.info(record.toString());
    }
}

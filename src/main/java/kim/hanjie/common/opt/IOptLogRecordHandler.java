package kim.hanjie.common.opt;

/**
 * 日志处理器
 *
 * @author han
 * @date 2021/3/8
 */
public interface IOptLogRecordHandler {

    /**
     * 处理操作日志
     *
     * @param record OptLogRecord
     */
    void recordOptLog(OptLogRecord record);
}

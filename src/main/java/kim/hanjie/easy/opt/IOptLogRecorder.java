package kim.hanjie.easy.opt;

/**
 * 日志记录器
 *
 * @author han
 * @date 2021/3/10
 */
public interface IOptLogRecorder {

    /**
     * 是否支持记录这种操作
     *
     * @param record 记录
     * @return true/false
     */
    boolean support(OptLogRecord record);


    /**
     * 记录操作日志
     *
     * @param record 记录
     */
    void record(OptLogRecord record);
}

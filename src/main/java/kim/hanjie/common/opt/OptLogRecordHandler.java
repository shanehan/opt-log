package kim.hanjie.common.opt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author han
 * @date 2021/3/8
 */
public class OptLogRecordHandler implements IOptLogRecordHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptLogRecordHandler.class);
    private OptLogConfig optLogConfig;
    private List<IOptLogRecorder> recorders;
    private ThreadPoolExecutor threadPoolExecutor;

    public OptLogRecordHandler(List<IOptLogRecorder> recorders, OptLogConfig optLogConfig) {
        this.optLogConfig = optLogConfig;
        this.recorders = recorders;
        threadPoolExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new OptRecordTreadFactory(),
                new ThreadPoolExecutor.DiscardPolicy());
    }

    @Override
    public void recordOptLog(OptLogRecord record) {
        threadPoolExecutor.submit(new RecordWorker(record));
    }

    private static class OptRecordTreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "opt-record");
        }
    }


    private class RecordWorker implements Runnable {

        private OptLogRecord record;

        public RecordWorker(OptLogRecord record) {
            this.record = record;
        }

        @Override
        public void run() {
            doRecord(record);
        }

        private void doRecord(OptLogRecord record) {
            for (IOptLogRecorder recorder : recorders) {
                try {
                    if (recorder.support(record)) {
                        recorder.record(record);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

    }
}

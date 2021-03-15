package kim.hanjie.common.opt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
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
    private BlockingQueue<OptLogRecord> blockingQueue;
    private OptLogConfig optLogConfig;

    public OptLogRecordHandler(List<IOptLogRecorder> recorders, OptLogConfig optLogConfig) {
        this.optLogConfig = optLogConfig;
        blockingQueue = new LinkedBlockingQueue<>();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1), new OptRecordTreadFactory(),
                new ThreadPoolExecutor.DiscardPolicy());
        RecordWorker worker = new RecordWorker(recorders, blockingQueue);
        threadPoolExecutor.submit(worker);
    }

    @Override
    public void recordOptLog(OptLogRecord record) {
        try {
            blockingQueue.put(record);
        } catch (InterruptedException ignored) {
        }
    }

    private static class OptRecordTreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "opt-record");
        }
    }


    private static class RecordWorker implements Runnable {

        private List<IOptLogRecorder> recorders;
        private BlockingQueue<OptLogRecord> blockingQueue;

        public RecordWorker(List<IOptLogRecorder> recorders, BlockingQueue<OptLogRecord> blockingQueue) {
            this.recorders = recorders;
            this.blockingQueue = blockingQueue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    OptLogRecord record = blockingQueue.take();
                    doRecord(record);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
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

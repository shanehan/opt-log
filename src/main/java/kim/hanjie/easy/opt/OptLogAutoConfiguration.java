package kim.hanjie.easy.opt;

import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

/**
 * @author han
 * @date 2021/3/10
 */
@Configuration
@Import(OptLogProperties.class)
public class OptLogAutoConfiguration {

    @Bean
    public DefaultPointcutAdvisor optLogPointcutAdvisor(List<IOptLogRecorder> recorders, OptLogProperties optLogProperties) {
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setAdvice(optLogInterceptor(recorders, optLogProperties));
        advisor.setPointcut(optLogPointcut());
        return advisor;
    }

    @Bean
    public OptLogPointcut optLogPointcut() {
        return new OptLogPointcut();
    }

    @Bean
    public OptLogInterceptor optLogInterceptor(List<IOptLogRecorder> recorders, OptLogProperties optLogProperties) {
        OptLogInterceptor optLogInterceptor = new OptLogInterceptor();
        OptLogConfig optLogConfig = optLogConfig(optLogProperties);
        optLogInterceptor.setOptLogRecordHandler(optLogRecordHandler(recorders, optLogConfig));
        optLogInterceptor.setOptStatusPolicy(optStatusPolicy());
        optLogInterceptor.setOptLogConfig(optLogConfig);
        return optLogInterceptor;
    }

    @Bean
    public OptLogConfig optLogConfig(OptLogProperties optLogProperties) {
        OptLogConfig config = new OptLogConfig();
        Integer ignoreLevel = optLogProperties.getIgnoreLevel();
        if (ignoreLevel == null) {
            ignoreLevel = Integer.MAX_VALUE;
        }
        config.setIgnoreLevel(ignoreLevel);
        return config;
    }

    @Bean
    @ConditionalOnMissingBean
    public IOptLogRecordHandler optLogRecordHandler(List<IOptLogRecorder> recorders, OptLogConfig optLogConfig) {
        return new OptLogRecordHandler(recorders, optLogConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public IOptStatusPolicy optStatusPolicy() {
        return new OptStatusPolicy();
    }

    @Bean
    @ConditionalOnMissingBean
    public IOptLogRecorder logOptLogRecorder() {
        return new LoggerOptLogRecorder();
    }
}

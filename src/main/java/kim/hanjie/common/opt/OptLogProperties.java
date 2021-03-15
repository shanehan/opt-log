package kim.hanjie.common.opt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author han
 * @date 2021/3/10
 */
@Component
@ConfigurationProperties(prefix = "kim.common.opt")
public class OptLogProperties {

    private Integer ignoreLevel;

    public Integer getIgnoreLevel() {
        return ignoreLevel;
    }

    public void setIgnoreLevel(Integer ignoreLevel) {
        this.ignoreLevel = ignoreLevel;
    }
}

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

    private Integer ignoreLevel = Integer.MAX_VALUE;
    private Integer order;

    public Integer getIgnoreLevel() {
        return ignoreLevel;
    }

    public void setIgnoreLevel(Integer ignoreLevel) {
        this.ignoreLevel = ignoreLevel;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}

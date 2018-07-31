package pl.upaid.graphitedemo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "monitoring")
public class MonitoringProperties {
    private String prefix;
    private String host;
    private Integer port;
    private Integer duration;
}

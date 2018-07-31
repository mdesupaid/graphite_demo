package pl.upaid.graphitedemo;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.GraphiteSender;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricFilter.ALL;
import static java.util.concurrent.TimeUnit.SECONDS;

@RequiredArgsConstructor
@Configuration
@EnableMetrics(proxyTargetClass = true)
public class MetricsConfiguration extends MetricsConfigurerAdapter {
    private final MetricRegistry metricRegistry;
    private final MonitoringProperties monitoringProperties;

    @Bean(destroyMethod = "stop")
    GraphiteReporter graphiteReporter() {
        registerMetrics();
        return createGraphiteReporter();
    }

    private GraphiteReporter createGraphiteReporter() {
        final GraphiteSender graphite = new Graphite(new InetSocketAddress(monitoringProperties.getHost(), monitoringProperties.getPort()));
        final GraphiteReporter reporter = GraphiteReporter.forRegistry(metricRegistry)
                                                          .prefixedWith(monitoringProperties.getPrefix())
                                                          .convertRatesTo(TimeUnit.MILLISECONDS)
                                                          .convertDurationsTo(TimeUnit.MILLISECONDS)
                                                          .filter(ALL)
                                                          .build(graphite);
        reporter.start(monitoringProperties.getDuration(), SECONDS);
        return reporter;
    }

    private void registerMetrics() {
        MetricSet jvmMetrics = createJvmMetrics();
        metricRegistry.registerAll(jvmMetrics);
    }

    private MetricSet createJvmMetrics() {
        return () -> {
            Map<String, Metric> metrics = new HashMap<>();
            metrics.put("gc", new GarbageCollectorMetricSet());
            metrics.put("file-descriptors", new FileDescriptorRatioGauge());
            metrics.put("memory-usage", new MemoryUsageGaugeSet());
            metrics.put("threads", new ThreadStatesGaugeSet());
            return metrics;
        };
    }
}

package com.github.constantinet.tradedatavalidator.configuration;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

import static com.github.constantinet.tradedatavalidator.ComponentNames.METRICS_CONSOLE_REFRESH_RATE;

@Configuration
@EnableMetrics
public class MetricsConfiguration extends MetricsConfigurerAdapter {

    private static final int DEFAULT_REFRESH_RATE = 60;

    private final int refreshRateInSeconds;

    @Autowired
    public MetricsConfiguration(@Value("${" + METRICS_CONSOLE_REFRESH_RATE + ":" + DEFAULT_REFRESH_RATE + "}")
                                    final int refreshRateInSeconds) {
        this.refreshRateInSeconds = refreshRateInSeconds;
    }

    @Override
    public MetricRegistry getMetricRegistry() {
        return new MetricRegistry();
    }

    @Override
    public void configureReporters(final MetricRegistry metricRegistry) {
        super.configureReporters(metricRegistry);

        ConsoleReporter.forRegistry(metricRegistry)
                .build()
                .start(refreshRateInSeconds, TimeUnit.SECONDS);
    }
}

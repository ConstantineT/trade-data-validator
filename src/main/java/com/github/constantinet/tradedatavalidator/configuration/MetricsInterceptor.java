package com.github.constantinet.tradedatavalidator.configuration;

import com.codahale.metrics.MetricRegistry;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class MetricsInterceptor extends HandlerInterceptorAdapter {

    private static final String ATTRIBUTE_NAME = "timerMetrics";

    private final MetricRegistry metricRegistry;

    public MetricsInterceptor(final MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws Exception {
        request.setAttribute(ATTRIBUTE_NAME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request,
                                final HttpServletResponse response,
                                final Object handler,
                                final Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            final Method method = ((HandlerMethod) handler).getMethod();
            final Class<?> clazz = method.getDeclaringClass();
            if (clazz.getName().startsWith("com.github.constantinet.tradedatavalidator")) {
                final long startTime = (long) request.getAttribute(ATTRIBUTE_NAME);
                final long duration = System.currentTimeMillis() - startTime;

                final String name = clazz.getSimpleName() + "." + method.getName() + "." + response.getStatus();
                metricRegistry.timer(name).update(duration, TimeUnit.MILLISECONDS);
            }
        }
    }
}

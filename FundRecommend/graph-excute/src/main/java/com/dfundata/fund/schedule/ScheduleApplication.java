package com.dfundata.fund.schedule;

import io.sentry.Sentry;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * sentry 监控
 */
@SpringBootApplication
@ComponentScan(value = "com.dfundata.fund")
@MapperScan(value = "com.dfundata.fund.schedule.mapper")
public class ScheduleApplication {

    @Bean
    public HandlerExceptionResolver sentryExceptionResolver() {
        return new io.sentry.spring.SentryExceptionResolver();
    }

    @Bean
    public ServletContextInitializer sentryServletContextInitializer() {
        return new io.sentry.spring.SentryServletContextInitializer();
    }


    public static void main(String[] args) {
        SpringApplication.run(ScheduleApplication.class);
        Sentry.init("https://370e1ed72a454e0ebdf9bd3638eec465@o464839.ingest.sentry.io/5477916");

    }

}

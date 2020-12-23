package com.dfund.recom.algo;

import io.sentry.Sentry;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * 2020/11/23 3:32 下午
 *
 * @author Seldom
 */

@SpringBootApplication(scanBasePackages="com.dfund")
@MapperScan(value = "com.dfund.recom.algo.mapper")
public class AlgoApplication {
    @Bean
    public HandlerExceptionResolver sentryExceptionResolver() {
        return new io.sentry.spring.SentryExceptionResolver();
    }

    @Bean
    public ServletContextInitializer sentryServletContextInitializer() {
        return new io.sentry.spring.SentryServletContextInitializer();
    }

    public static void main(String[] args) {
        SpringApplication.run(AlgoApplication.class);
        Sentry.init("https://370e1ed72a454e0ebdf9bd3638eec465@o464839.ingest.sentry.io/5477916");

    }
}

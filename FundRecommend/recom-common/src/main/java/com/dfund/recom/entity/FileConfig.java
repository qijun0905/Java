package com.dfund.recom.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 2020/12/1 9:54 上午
 *
 * @author Seldom
 */
@ConfigurationProperties(prefix = "upload")
@Component
@Order
public class FileConfig {

    public static String localtion;
    public static String maxFileSize;
    public static String maxRequestSize;

    public static void setLocaltion(String localtion) {
        FileConfig.localtion = localtion;
    }

    public static void setMaxFileSize(String maxFileSize) {
        FileConfig.maxFileSize = maxFileSize;
    }

    public static void setMaxRequestSize(String maxRequestSize) {
        FileConfig.maxRequestSize = maxRequestSize;
    }
}

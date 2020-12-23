package com.dfundata.fund.redis.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Properties;

/**
 * 读取配置文件工具类
 */
public class ConfigUtils {

  private static Properties getConfigPro(Type clazz){
    Properties properties = new Properties();
    InputStream in =  clazz.getClass().getResourceAsStream("/jedisConfig/config.properties");//加载
    try {
      if(in == null){
        File file = new File(System.getProperty("user.dir") + "/jedisConfig/config.properties");
        InputStream inputStream = new FileInputStream(file);
        BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        properties.load(bf);
      }else{
        BufferedReader bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        properties.load(bf);
      }
      String proEnv = properties.getProperty("env");
      InputStream in1 =  clazz.getClass().getResourceAsStream("/jedisConfig/config-" +proEnv+".properties");//加载
      if(in1 == null){
        File f2 = new File(System.getProperty("user.dir") + "/jedisConfig/config-" +proEnv+".properties");
        InputStream in2 = new FileInputStream(f2);
        BufferedReader bf = new BufferedReader(new InputStreamReader(in2, "UTF-8"));
        properties.load(bf);
      }else{
        BufferedReader bf = new BufferedReader(new InputStreamReader(in1, "UTF-8"));
        properties.load(bf);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return properties;
  }
  
  public static String getConfigStrValue(String key){
    Properties configPro = getConfigPro(ConfigUtils.class);
    String value = configPro.getProperty(key);
    if(StringUtils.isBlank(value) || "null".equals(value)){
      return  null;
    }
    return value;
  }

  public static Integer getConfigIntValue(String key){
    Properties configPro = getConfigPro(ConfigUtils.class);
    String property = configPro.getProperty(key);
    if(StringUtils.isNotBlank(property)){
      return Integer.valueOf(property);
    }
    return null;
  }

  

  
  public static void main(String[] args) {
//    System.out.println(getKafkaServers());
//    System.out.println(getRedisHost());
    System.out.println(getConfigStrValue("redis.password") == null);
  }

}

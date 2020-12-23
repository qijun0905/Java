package com.dfundata.fund.schedule.task;

import com.dfundata.fund.schedule.service.GraphService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 2020/10/26 5:32 下午
 *
 * @author Seldom
 */
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class DynamicScheduleTask implements SchedulingConfigurer {

    private Logger logger = LoggerFactory.getLogger(DynamicScheduleTask.class);

    @Autowired
    private GraphService service;

    @Mapper
    public interface CronMapper {
        @Select("select cron from cron limit 1")
        public String getCron();
    }

//    @Autowired      //注入mapper
//    @SuppressWarnings("all")
//    CronMapper cronMapper;

    /**
     * 执行定时任务.
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        taskRegistrar.addTriggerTask(
                //1.添加任务内容(Runnable)
                () ->
//                        System.out.println("执行动态定时任务: " + LocalDateTime.now().toLocalTime()),
                        sqlSchedule(),
                //2.设置执行周期(Trigger)
                triggerContext -> {
                    //2.1 从数据库获取执行周期
                    String cron = "* * * * */1 ?";
//                    String cron = cronMapper.getCron();
                    //2.2 合法性校验.
                    if (StringUtils.isEmpty(cron)) {
                        // Omitted Code ..
                    }
                    //2.3 返回执行周期(Date)
                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
                }
        );
    }


    /**
     * 定时执行sql文件
     */
    public void sqlSchedule(){
        try {
//            service.executeCheck();
        }catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }

    }

}
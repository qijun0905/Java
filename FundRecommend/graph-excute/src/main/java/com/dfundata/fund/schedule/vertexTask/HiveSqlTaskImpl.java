package com.dfundata.fund.schedule.vertexTask;

import com.dfundata.fund.schedule.service.HiveSqlTask;
import com.dfundata.fund.schedule.utils.H2Utils;
import com.dfundata.fund.schedule.utils.MyDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Date;

/**
 * @author ly
 * @date 2020-04-15 10:33
 * @description
 */
@Component
public class HiveSqlTaskImpl implements ITask {

    private Logger logger = LoggerFactory.getLogger(HiveSqlTaskImpl.class);

    @Autowired
    private HiveSqlTask hiveSqlTask;

    @Async
    @Override
    public ListenableFuture<DirectedAcyclicGraph<String, DefaultEdge>> execute(String taskId
            , Integer initDate
            , String procedure
            , DirectedAcyclicGraph<String, DefaultEdge> graph) {
        Date date = MyDateUtils.integerToDate(initDate);
        logger.info("{} at {} start execute {} ",Thread.currentThread().getName(),initDate,taskId);
        String querySql = "select status from POT_STATUS where task_id = '"+taskId.trim()+"'";
        String updateSql= null;
        String s = H2Utils.executeQuerySql(querySql);
        if(StringUtils.isNotBlank(s) && "0".equals(s)){
            try{
                hiveSqlTask.executeHiveSqlTask(date,procedure);
                updateSql  = "update POT_STATUS set status = '1' where task_id = '" +taskId.trim()+"'";
            }catch (Exception e){
                updateSql  = "update POT_STATUS set status = '1',err_code = '202',err_msg = '"+
                        e.getMessage()+"' where task_id = '" +taskId.trim()+"'";
            }
            //        sleep();
            // 执行完之后将状态修改下

        }else{
            updateSql = "update POT_STATUS set err_Code = '201',set err_msg = 'have executed' where task_id = '" +taskId.trim()+"'";
        }
        try{
            H2Utils.executeSql(updateSql);
        }catch (Exception e){
            logger.error(e.getMessage());
        }

        graph.removeVertex(taskId);
        logger.info("{} at {} delteTask {} ",Thread.currentThread().getName(),initDate,taskId);
        logger.info("{} at {} execute {} end",Thread.currentThread().getName(),initDate,taskId);
        return new AsyncResult<>(graph);
    }

}

package com.dfundata.fund.schedule.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dfundata.fund.schedule.entity.GraphParams;
import com.dfundata.fund.schedule.service.GraphService;
import com.dfundata.fund.schedule.task.DynamicScheduleTask;
import com.dfundata.fund.schedule.utils.FileUtils;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 2020/10/15 9:26 上午
 *
 * @author Seldom
 */

@RestController
@RequestMapping(value = "graph/test")
public class GraphScan {


    @Autowired
    private GraphService graphService;

    @Autowired
    private DynamicScheduleTask scheduleTask;

    /**
     * 执行有向图
     * @return
     */
    @GetMapping("archiveTask")
    public String executeGraph() throws Exception{
        scheduleTask.configureTasks(new ScheduledTaskRegistrar());
        graphService.checkGraphEdge();
        return "success";
    }

    /**
     * 某 taskId 执行重试
     * @param taskId
     * @return
     */
    @GetMapping("taskRetry")
    public String taskRetry(String taskId) {

        return "success";
    }

    /**
     * 获取某taskId的执行错误原因
     * @param taskId
     * @return
     */
    @GetMapping("getErrMsg")
    public String getErrMsg(String taskId) {

        return "success";
    }
}

package com.dfund.recom.task.web;


import com.alibaba.fastjson.JSONObject;
import com.dfund.recom.entity.ResponseContext;
import com.dfund.recom.task.entity.TaskInfo;
import com.dfund.recom.task.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 2020/11/27 4:00 下午
 *
 * @author Seldom
 */
@ResponseBody
@Controller
@RequestMapping("task")
public class TaskController {

    private Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @RequestMapping("/taskList")
    public ResponseContext getTaskList(Integer pageNum,Integer pageSize,String taskName,
                                       String taskType,String scope,String algorithmName){
        logger.info("任务列表条件查询，pageNum {},pageSize {},taskName {},taskType {}," +
                "scope {} ,algorithmName {}",pageNum,pageSize,taskName,taskType,scope,algorithmName);
        return ResponseContext.getSuccess(taskService.getTaskList(pageNum,pageSize,taskName,taskType,scope,algorithmName));
    }

    @RequestMapping("/taskAdd")
    public ResponseContext taskAdd(@RequestBody TaskInfo taskInfo){
        logger.info("任务配置增加 taskInfo {} "+taskInfo.toString());
        Integer i = taskService.insertTask(taskInfo);
        if(i > 0){
            return ResponseContext.saveSuccess("success");
        }else{
            return ResponseContext.fail(ResponseContext.SAVE_FAIL);
        }

    }

    @RequestMapping("/taskUpd")
    public ResponseContext updateTaskInfo(@RequestBody TaskInfo taskInfo){
        logger.info("任务配置修改 taskInfo {} "+taskInfo.toString());
        Integer i = taskService.updateTaskInfoById(taskInfo);
        if(i > 0){
            return ResponseContext.updaeSuccess("success");
        }else{
            return ResponseContext.fail(ResponseContext.SAVE_FAIL);
        }

    }

    @RequestMapping("/taskExecute")
    public void taskExecute(String taskId){
        logger.info("任务执行 taskId {} ",taskId);
        taskService.executeTask(taskId);
    }

}

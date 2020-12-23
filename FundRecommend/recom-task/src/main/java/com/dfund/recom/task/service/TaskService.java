package com.dfund.recom.task.service;

import com.alibaba.fastjson.JSONObject;
import com.dfund.recom.task.entity.TaskInfo;
import com.dfund.recom.task.mapper.TaskBaseInfoMapper;
import com.dfund.recom.utils.MyDateUtils;
import com.dfund.recom.utils.NullUtis;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 2020/12/1 3:29 下午
 *
 * @author Seldom
 */
@Service
public class TaskService {

    @Autowired
    private TaskBaseInfoMapper taskBaseInfoMapper;

    public PageInfo<TaskInfo> getTaskList(Integer pageNum, Integer pageSize, String taskName,
                                          String taskType, String scope, String algorithmName){
        TaskInfo taskInfo = new TaskInfo();
        if(StringUtils.isBlank(taskName) && StringUtils.isBlank(taskType) && StringUtils.isBlank(scope) && StringUtils.isBlank(algorithmName)){
            // 无条件查询
        }else{
            if(NullUtis.isNotNull(taskName)){
                taskInfo.setTaskName(taskName);
            }
            if(NullUtis.isNotNull(taskType)){
                taskInfo.setTaskType(taskType);
            }
            if(NullUtis.isNotNull(scope)){
                taskInfo.setClientScope(scope);
            }
            if(NullUtis.isNotNull(algorithmName)){
                taskInfo.setAlgorithmName(algorithmName);
            }
        }
        PageHelper.startPage(pageNum,pageSize);
        List<TaskInfo> list = taskBaseInfoMapper.selectByCondition(taskInfo);
        PageInfo<TaskInfo> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    public Integer insertTask(TaskInfo taskInfo) {
//        TaskInfo taskInfo = new TaskInfo();
//        // todo 判空
//        String taskName = jsonObject.getString("taskName");
//        String taskType = jsonObject.getString("taskType");
//        String clientScope = jsonObject.getString("clientScope");
//        String rejectClient = jsonObject.getString("rejectClient");
//        String algorithmName = jsonObject.getString("algorithmName");
//        Object recomNumUp = jsonObject.get("recomNumUp");
//        Object taskSdate = jsonObject.get("taskSdate");
//        Object taskEdate = jsonObject.get("taskEdate");
//        String triggerTime = jsonObject.getString("triggerTime");
//        String istdate = jsonObject.getString("istdate");
//        String taskDescription = jsonObject.getString("taskDescription");
//        taskInfo.setTaskName(taskName);
//        taskInfo.setTaskType(taskType);
//        taskInfo.setClientScope(clientScope);
//        taskInfo.setRejectClient(rejectClient);
//        taskInfo.setAlgorithmName(algorithmName);
//        taskInfo.setRecomNumUp((Integer) recomNumUp);
//        taskInfo.setTaskSdate((Date) taskSdate);
//        taskInfo.setTaskEdate((Date) taskEdate);
//        taskInfo.setTriggerTime(triggerTime);
//        taskInfo.setIstdate(istdate);
//        taskInfo.setTaskDescription(taskDescription);
        int yyyyMMdd = Integer.parseInt(MyDateUtils.getDate(new Date(), "yyyyMMdd"));
        Date taskEdate = taskInfo.getTaskEdate();
        taskInfo.setInitDate(yyyyMMdd);
        int i = taskBaseInfoMapper.insertSelective(taskInfo);
        Integer taskId = taskInfo.getTaskId();
        return taskId ;
    }

    public Integer updateTaskInfoById(TaskInfo taskInfo) {
        taskInfo.setUpdTime(new Date());
        return taskBaseInfoMapper.updateTaskInfoByTaskId(taskInfo);
    }


    public void executeTask(String taskId) {

    }
}

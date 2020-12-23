package com.dfund.recom.task.mapper;


import com.dfund.recom.task.entity.TaskInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskBaseInfoMapper {


    List<TaskInfo> selectByCondition(TaskInfo taskInfo);

    int insertSelective(TaskInfo taskInfo);

    Integer updateTaskInfoByTaskId(TaskInfo taskInfo);
}

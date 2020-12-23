package com.dfund.recom.task.entity;

import java.util.Date;

/**
 * 2020/12/1 3:31 下午
 *
 * @author Seldom
 */
public class TaskInfo {

    private Integer taskId;
    private String taskName;
    private String taskType;
    private String triggerType;
    private Date taskSdate;
    private Date taskEdate;
    private String istdate;
    private String triggerTime;
    private String clientScope;
    private String rejectClient;
    private Integer recomNumUp;
    private Integer seriesCode;
    private String seriesName;
    private String algorithmVision;
    private String algorithmName;
    private String algorithmId;
    private String creater;
    private Date createTime;
    private String taskDescription;
    private String updUser;
    private Date updTime;
    private Integer initDate;
    private String isDel;

    @Override
    public String toString() {
        return "TaskInfo{" +
                "taskId='" + taskId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", taskType='" + taskType + '\'' +
                ", triggerType='" + triggerType + '\'' +
                ", taskSdate=" + taskSdate +
                ", taskEdate=" + taskEdate +
                ", istdate=" + istdate +
                ", triggerTime=" + triggerTime +
                ", clientScope='" + clientScope + '\'' +
                ", rejectClient='" + rejectClient + '\'' +
                ", recomNumUp=" + recomNumUp +
                ", seriesCode=" + seriesCode +
                ", seriesName='" + seriesName + '\'' +
                ", algorithmVision='" + algorithmVision + '\'' +
                ", algorithmName='" + algorithmName + '\'' +
                ", algorithmId='" + algorithmId + '\'' +
                ", creater='" + creater + '\'' +
                ", createTime=" + createTime +
                ", taskDescription='" + taskDescription + '\'' +
                ", updUser='" + updUser + '\'' +
                ", updTime='" + updTime + '\'' +
                ", initDate='" + initDate + '\'' +
                ", isDel='" + isDel + '\'' +
                '}';
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public Date getTaskSdate() {
        return taskSdate;
    }

    public void setTaskSdate(Date taskSdate) {
        this.taskSdate = taskSdate;
    }

    public Date getTaskEdate() {
        return taskEdate;
    }

    public void setTaskEdate(Date taskEdate) {
        this.taskEdate = taskEdate;
    }

    public String getIstdate() {
        return istdate;
    }

    public void setIstdate(String istdate) {
        this.istdate = istdate;
    }

    public String getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
    }

    public String getClientScope() {
        return clientScope;
    }

    public void setClientScope(String clientScope) {
        this.clientScope = clientScope;
    }

    public String getRejectClient() {
        return rejectClient;
    }

    public void setRejectClient(String rejectClient) {
        this.rejectClient = rejectClient;
    }

    public Integer getRecomNumUp() {
        return recomNumUp;
    }

    public void setRecomNumUp(Integer recomNumUp) {
        this.recomNumUp = recomNumUp;
    }

    public Integer getSeriesCode() {
        return seriesCode;
    }

    public void setSeriesCode(Integer seriesCode) {
        this.seriesCode = seriesCode;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getAlgorithmVision() {
        return algorithmVision;
    }

    public void setAlgorithmVision(String algorithmVision) {
        this.algorithmVision = algorithmVision;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(String algorithmId) {
        this.algorithmId = algorithmId;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getUpdUser() {
        return updUser;
    }

    public void setUpdUser(String updUser) {
        this.updUser = updUser;
    }

    public Date getUpdTime() {
        return updTime;
    }

    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }

    public Integer getInitDate() {
        return initDate;
    }

    public void setInitDate(Integer initDate) {
        this.initDate = initDate;
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }
}

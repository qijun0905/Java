package com.dfundata.fund.schedule.entity;

import java.util.Date;

/**
 * 2020/11/20 2:04 下午
 *
 * @author Seldom
 */
public class Algorithm {

    private Integer algorithmId;
    private String algorithmName;
    private String algorithmVision;
    // 0 - 主推基金精准营销  1- 个性化推荐
    private String algorithmType;
    // 客群范围 以｜分割
    private String applyClientScope;
    private String creater;
    private Date createTime;
    private String updUser;
    private Date updTime;
    private String algorithmDescription;
    // 是否下线 0 在线，1-下线
    private String algorithmState;
    private String pythonfile;
    private String pythonUrl;
    private String modelfile;
    private String modelUrl;
    private String initDate;

    public Integer getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(Integer algorithmId) {
        this.algorithmId = algorithmId;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getAlgorithmVision() {
        return algorithmVision;
    }

    public void setAlgorithmVision(String algorithmVision) {
        this.algorithmVision = algorithmVision;
    }

    public String getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(String algorithmType) {
        this.algorithmType = algorithmType;
    }

    public String getApplyClientScope() {
        return applyClientScope;
    }

    public void setApplyClientScope(String applyClientScope) {
        this.applyClientScope = applyClientScope;
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

    public String getAlgorithmDescription() {
        return algorithmDescription;
    }

    public void setAlgorithmDescription(String algorithmDescription) {
        this.algorithmDescription = algorithmDescription;
    }

    public String getAlgorithmState() {
        return algorithmState;
    }

    public void setAlgorithmState(String algorithmState) {
        this.algorithmState = algorithmState;
    }

    public String getPythonfile() {
        return pythonfile;
    }

    public void setPythonfile(String pythonfile) {
        this.pythonfile = pythonfile;
    }

    public String getPythonUrl() {
        return pythonUrl;
    }

    public void setPythonUrl(String pythonUrl) {
        this.pythonUrl = pythonUrl;
    }

    public String getModelfile() {
        return modelfile;
    }

    public void setModelfile(String modelfile) {
        this.modelfile = modelfile;
    }

    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    public String getInitDate() {
        return initDate;
    }

    public void setInitDate(String initDate) {
        this.initDate = initDate;
    }

    @Override
    public String toString() {
        return "Algorithm{" +
                "algorithmId=" + algorithmId +
                ", algorithmName='" + algorithmName + '\'' +
                ", algorithmVision='" + algorithmVision + '\'' +
                ", algorithmType='" + algorithmType + '\'' +
                ", applyClientScope='" + applyClientScope + '\'' +
                ", creater='" + creater + '\'' +
                ", createTime=" + createTime +
                ", updUser='" + updUser + '\'' +
                ", updTime=" + updTime +
                ", algorithmDescription='" + algorithmDescription + '\'' +
                ", algorithmState='" + algorithmState + '\'' +
                ", pythonfile='" + pythonfile + '\'' +
                ", pythonUrl='" + pythonUrl + '\'' +
                ", modelfile='" + modelfile + '\'' +
                ", modelUrl='" + modelUrl + '\'' +
                ", initDate='" + initDate + '\'' +
                '}';
    }
}

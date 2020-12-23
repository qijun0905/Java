package com.dfund.recom.algo.entity;

import java.util.Date;

/**
 * 2020/11/25 4:03 下午
 *
 * @author Seldom
 */
public class Factor {

    private Integer algorithmId;
    private Integer factorId;
    private String factorName;
    private String factorType;
    private String factorTable;
    private String factorColumn;
    private Integer initDate;

    @Override
    public String toString() {
        return "Factor{" +
                "algorithmId=" + algorithmId +
                ", factorId=" + factorId +
                ", factorName='" + factorName + '\'' +
                ", factorType='" + factorType + '\'' +
                ", factorTable='" + factorTable + '\'' +
                ", factorColumn='" + factorColumn + '\'' +
                ", initDate=" + initDate +
                '}';
    }

    public Integer getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(Integer algorithmId) {
        this.algorithmId = algorithmId;
    }

    public Integer getFactorId() {
        return factorId;
    }

    public void setFactorId(Integer factorId) {
        this.factorId = factorId;
    }

    public String getFactorName() {
        return factorName;
    }

    public void setFactorName(String factorName) {
        this.factorName = factorName;
    }

    public String getFactorType() {
        return factorType;
    }

    public void setFactorType(String factorType) {
        this.factorType = factorType;
    }

    public String getFactorTable() {
        return factorTable;
    }

    public void setFactorTable(String factorTable) {
        this.factorTable = factorTable;
    }

    public String getFactorColumn() {
        return factorColumn;
    }

    public void setFactorColumn(String factorColumn) {
        this.factorColumn = factorColumn;
    }

    public Integer getInitDate() {
        return initDate;
    }

    public void setInitDate(Integer initDate) {
        this.initDate = initDate;
    }
}

package com.dfundata.fund.schedule.entity;

/**
 * 2020/10/15 9:28 上午
 *
 * @author Seldom
 */
public class GraphParams {

    private String taskId;

    private String type;

    private String status;

    private String input;

    private String output;

    @Override
    public String toString() {
        return "GraphParams{" +
                "taskId='" + taskId + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", input='" + input + '\'' +
                ", output='" + output + '\'' +
                '}';
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}

package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Todo {

    @JsonProperty("_id")
    private String taskId;
    private String item;
    private Boolean isCompleted;

    public Todo() {
    }

    public Todo(String item) {
        this.item = item;
    }

    public Todo(String item, Boolean isCompleted) {
        this.item = item;
        this.isCompleted = isCompleted;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}

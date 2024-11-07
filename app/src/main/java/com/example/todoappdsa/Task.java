package com.example.todoappdsa;

import java.util.Date;

public class Task {
    private String taskName;
    private int priority;
    private Date deadline;

    public Task(String taskName, int priority, Date deadline) {
        this.taskName = taskName;
        this.priority = priority;
        this.deadline = deadline;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getPriority() {
        return priority;
    }

    public Date getDeadline() {
        return deadline;
    }
}

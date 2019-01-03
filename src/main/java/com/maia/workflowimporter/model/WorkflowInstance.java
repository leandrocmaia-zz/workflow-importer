package com.maia.workflowimporter.model;

import lombok.Data;

@Data
public class WorkflowInstance {
    Long id;
    Workflow workflow;
    Assignee assignee;
    String step;
    Status status;

    public enum Status {
        RUNNING, NEW, DONE, PAUSED;
    }
}

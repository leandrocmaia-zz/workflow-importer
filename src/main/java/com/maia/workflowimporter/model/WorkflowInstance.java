package com.maia.workflowimporter.model;

import lombok.Data;

@Data
public class WorkflowInstance {
    Long id;
    Long workflowId;
    String assignee;
    String step;
    String status;
}

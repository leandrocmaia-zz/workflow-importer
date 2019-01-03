package com.maia.workflowimporter.model;

import lombok.Data;

@Data
public class WorkflowInstance {
    Long id;
    Workflow workflow;
    Contractor assignee;
    String step;
    String status;
}

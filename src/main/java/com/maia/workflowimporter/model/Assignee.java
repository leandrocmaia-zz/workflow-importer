package com.maia.workflowimporter.model;

import lombok.Data;

@Data
public abstract class Assignee {
    String name;
    String email;

    public Assignee() {
    }
}

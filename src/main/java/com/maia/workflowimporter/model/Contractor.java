package com.maia.workflowimporter.model;

import lombok.Data;

@Data
public class Contractor {
    String alias;
    String name;
    String email;

    public Contractor(String email) {
        this.email = email;
    }

    public Contractor() {
    }
}

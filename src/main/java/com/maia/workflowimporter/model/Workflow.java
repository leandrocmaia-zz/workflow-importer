package com.maia.workflowimporter.model;

import lombok.Data;

@Data
public class Workflow {
  Long id;
  String name;
  String author;
  Integer version;

  public Workflow() {}

  public Workflow(Long id) {
    this.id = id;
  }
}

package com.maia.workflowimporter.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Contractor extends Assignee {

    String alias;

}

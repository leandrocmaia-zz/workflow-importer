package com.maia.workflowimporter.migrator.impl;

import java.io.File;
import lombok.Getter;

public abstract class BaseMigrator {

  final String[] logicSeparator = {"start", "end"};
  final String delimiter = ":";

  @Getter File file;
}

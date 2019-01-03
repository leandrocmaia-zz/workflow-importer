package com.maia.workflowimporter.migrator.impl;

import lombok.Getter;

import java.io.File;

public abstract class BaseMigrator {

    final String[] logicSeparator = {"start", "end"};
    final String delimiter = ":";

    @Getter
    File file;

}

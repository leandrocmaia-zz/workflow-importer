package com.maia.workflowimporter.migrator.impl;

import com.maia.workflowimporter.migrator.Migrator;
import com.maia.workflowimporter.model.Workflow;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WorkflowMigrator extends BaseMigrator implements Migrator {

    @Getter
    List<Workflow> workflows = new ArrayList<>();

    public WorkflowMigrator(File file) {
        this.file = file;
    }

    @Override
    public void parse() {
        try (BufferedReader fileBufferReader = new BufferedReader(new FileReader(file))) {
            String l;
            Workflow workflow = null;
            while ((l = fileBufferReader.readLine()) != null) {
                String line = l.trim();
                if (logicSeparator[0].equals((line))) {
                    workflow = new Workflow();
                }
                if (line.contains("id")) {
                    workflow.setId(new Long(line.split(delimiter)[1].trim()));
                } else if (line.contains("name")) {
                    workflow.setName(line.split(delimiter)[1].trim());
                } else if (line.contains("author")) {
                    workflow.setAuthor(line.split(delimiter)[1].trim());
                } else if (line.contains("version")) {
                    workflow.setVersion(new Integer(line.split(delimiter)[1].trim()));
                }
                if (logicSeparator[1].equals((line))) {
                    workflows.add(workflow);
                }
            }
        } catch (FileNotFoundException e) {
            log.error("File not found. {}", e.getMessage());
        } catch (IOException e) {
            log.error("Error while reading file. {}", e.getMessage());
        }

    }

    @Override
    public void logSummary() {

    }

}

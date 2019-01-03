package com.maia.workflowimporter.migrator;

import com.maia.workflowimporter.model.WorkflowInstance;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WorkflowInstanceMigrator {

    String[] logicSeparator = {"start", "end"};

    @Getter
    File file;
    @Getter
    List<WorkflowInstance> workflowInstances = new ArrayList<>();
    @Getter
    List<WorkflowInstance> workflowInstancesWithError = new ArrayList<>();
    @Getter
    List<String> linesOutsideLoop = new ArrayList<>();


    public WorkflowInstanceMigrator(File file) {
        this.file = file;
        parseFile();
    }

    public void parseFile() {
        try (BufferedReader fileBufferReader = new BufferedReader(new FileReader(file))) {
            String l;
            WorkflowInstance workflowInstance = null;
            boolean insideLoop = false;
            fileBufferReader.readLine(); // skip header
            while ((l = fileBufferReader.readLine()) != null) {
                String line = l.trim();
                if (logicSeparator[0].equals((line))) {
                    workflowInstance = new WorkflowInstance();
                    insideLoop = true;
                    continue;
                }
                if (logicSeparator[1].equals((line))) {
                    if (insideLoop) {
                        workflowInstances.add(workflowInstance);
                    }
                    insideLoop = false;
                    continue;
                }
                if (insideLoop) {
                    String value = null;
                    try {
                        value = line.split(":")[1].trim();
                    } catch (Exception e) {
                        log.error("Error parsing value." , e);
                        workflowInstancesWithError.add(workflowInstance);
                        continue;
                    }
                    if (line.contains("id")) {
                        workflowInstance.setId(new Long(value));
                    } else if (line.contains("workflowId")) {
                        workflowInstance.setWorkflowId(new Long(value));
                    } else if (line.contains("assignee")) {
                        workflowInstance.setAssignee(value);
                    } else if (line.contains("step")) {
                        workflowInstance.setStep(value);
                    } else if (line.contains("status")) {
                        workflowInstance.setStatus(value);
                    } else {
                        log.error("Missing attribute: {}", line);
                        workflowInstancesWithError.add(workflowInstance);
                    }
                } else {
                    log.error("Not inside loop: {}", line);
                    linesOutsideLoop.add(line);
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

package com.maia.workflowimporter.migrator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maia.workflowimporter.model.Contractor;
import com.maia.workflowimporter.model.Workflow;
import com.maia.workflowimporter.model.WorkflowInstance;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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

    private EmployeeMigrator employeeMigrator;
    private ContractorMigrator contractorMigrator;
    private WorkflowMigrator workflowMigrator;

    public WorkflowInstanceMigrator(File file,
                                    EmployeeMigrator employeeMigrator,
                                    ContractorMigrator contractorMigrator,
                                    WorkflowMigrator workflowMigrator) {
        this.file = file;
        this.employeeMigrator = employeeMigrator;
        this.contractorMigrator = contractorMigrator;
        this.workflowMigrator = workflowMigrator;
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
                    final String key;
                    final String value;
                    final WorkflowInstance finalWorkflowInstance = workflowInstance;

                    try {
                        key = line.split(":")[0].trim();
                        value = line.split(":")[1].trim();
                    } catch (Exception e) {
                        log.error("Error parsing value." , e);
                        workflowInstancesWithError.add(workflowInstance);
                        continue;
                    }
                    if (key.equals("id")) {
                        workflowInstance.setId(new Long(value));
                    } else if (key.equals("workflowId")) {
                        // lookup workflow

                        workflowInstance.setWorkflow(workflowMigrator
                                .getWorkflows()
                                .stream()
                                .filter(a -> a.getId().equals(new Long(value)))
                                .findFirst()
                                .orElseGet(() -> {
                                    log.warn("Workflow {} not found in lookup.", value);
                                    workflowInstancesWithError.add(finalWorkflowInstance);
                                    return new Workflow(new Long(value));
                                }));

                    } else if (key.equals("assignee")) {

                        // lookup contractor
                        Contractor contractor = contractorMigrator
                                .getContractors()
                                .stream()
                                .filter(a -> a.getEmail().equals(value))
                                .findFirst()
                                .orElseGet(() -> {
                                    log.warn("Contractor {} not found in lookup.", value);
                                    workflowInstancesWithError.add(finalWorkflowInstance);
                                    return new Contractor(value);
                                });

                        workflowInstance.setAssignee(contractor);
                    } else if (key.equals("step")) {
                        workflowInstance.setStep(value);
                    } else if (key.equals("status")) {
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

    public void printResults() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        log.info("All instances and its workflows:");
        log.info(gson.toJson(getWorkflowInstances()));
        log.info("All inconsistent data:");
        log.info("With error / incomplete: {}", gson.toJson(getWorkflowInstancesWithError()));
        log.info("Lines unparseable: {}", gson.toJson(getLinesOutsideLoop()));

        List<WorkflowInstance> running = getWorkflowInstances()
                .stream()
                .filter(a -> a.getStatus() != null)
                .filter(a -> a.getStatus().equals("RUNNING"))
                .collect(toList());

        log.info("Workflows with running instances {}: {}",
                running.size() +  "/" + getWorkflowInstances().size(),
                gson.toJson(running));
    }

}

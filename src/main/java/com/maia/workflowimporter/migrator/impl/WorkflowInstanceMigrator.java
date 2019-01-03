package com.maia.workflowimporter.migrator.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maia.workflowimporter.migrator.Migrator;
import com.maia.workflowimporter.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
public class WorkflowInstanceMigrator extends BaseMigrator implements Migrator {

    @Getter
    List<WorkflowInstance> workflowInstances = new ArrayList<>();
    @Getter
    List<WorkflowInstance> workflowInstancesWithError = new ArrayList<>();
    @Getter
    List<String> unparsableLines = new ArrayList<>();

    private EmployeeMigrator employeeMigrator;
    private ContractorMigrator contractorMigrator;
    private WorkflowMigrator workflowMigrator;

    /**
     * There is strong dependency from other migrators in this one. Using dependency injection via constructor.
     *
     * @param file
     * @param employeeMigrator
     * @param contractorMigrator
     * @param workflowMigrator
     */
    public WorkflowInstanceMigrator(File file,
                                    EmployeeMigrator employeeMigrator,
                                    ContractorMigrator contractorMigrator,
                                    WorkflowMigrator workflowMigrator) {
        this.file = file;
        this.employeeMigrator = employeeMigrator;
        this.contractorMigrator = contractorMigrator;
        this.workflowMigrator = workflowMigrator;
    }

    /**
     * initializing all migrators
     */
    private void init() {
        employeeMigrator.parse();
        contractorMigrator.parse();
        workflowMigrator.parse();
    }

    @Override
    public void parse() {
        this.init();
        // try with resources using streamed fileinput for large files
        try (BufferedReader fileBufferReader = new BufferedReader(new FileReader(file))) {
            WorkflowInstance workflowInstance = null;
            boolean insideLoop = false;
            fileBufferReader.readLine(); // skip header
            String l;
            while ((l = fileBufferReader.readLine()) != null) {
                String line = l.trim();
                // start logic
                if (logicSeparator[0].equals((line))) {
                    workflowInstance = new WorkflowInstance();
                    insideLoop = true;
                    continue;
                }

                // end logic
                if (logicSeparator[1].equals((line))) {
                    if (insideLoop) {
                        workflowInstances.add(workflowInstance);
                    }
                    insideLoop = false;
                    continue;
                }

                // loop logic, could be made in a generic way using reflections in the other classes
                // but would overcomplicate (KISS)
                if (insideLoop) {
                    final String key;
                    final String value;
                    final WorkflowInstance finalWorkflowInstance = workflowInstance; // for lambda

                    try {
                        key = line.split(delimiter)[0].trim(); // TODO dynamic formatting (hardcoded K:V)
                        value = line.split(delimiter)[1].trim();
                    } catch (Exception e) {
                        log.error("Error parsing value." , e);
                        workflowInstancesWithError.add(workflowInstance);
                        continue;
                    }
                    if (key.equals("id")) {
                        workflowInstance.setId(new Long(value));
                    } else if (key.equals("workflowId")) {
                        // lookup workflow table
                        Workflow workflowLookup = workflowMigrator
                                .getWorkflows()
                                .stream()
                                .filter(a -> a.getId().equals(new Long(value)))
                                .findFirst()
                                .orElseGet(() -> {
                                    log.warn("Workflow {} not found in lookup.", value);
                                    workflowInstancesWithError.add(finalWorkflowInstance);
                                    return new Workflow(new Long(value));
                                });

                        workflowInstance.setWorkflow(workflowLookup);

                    } else if (key.equals("assignee")) { // lookup for assignee
                        Assignee assignee = null;
                        // lookup contractor table
                        Optional<Contractor> contractor = contractorMigrator
                                .getContractors()
                                .stream()
                                .filter(a -> a.getEmail().equals(value))
                                .findFirst();
                        if (contractor.isPresent()) {
                            assignee = contractor.get();
                        } else {
                            // lookup employee table
                            Optional<Employee> employee = employeeMigrator
                                    .getEmployees()
                                    .stream()
                                    .filter(a -> a.getEmail().equals(value))
                                    .findFirst();

                            if (employee.isPresent()) {
                                assignee = employee.get();
                            } else {
                                // none found
                                log.warn("Assignee {} not found in lookup contractor/employee.", value);
                                workflowInstancesWithError.add(finalWorkflowInstance);
                            }
                        }
                        workflowInstance.setAssignee(assignee);
                    } else if (key.equals("step")) {
                        workflowInstance.setStep(WorkflowInstance.Step.lookup(value));
                    } else if (key.equals("status")) {
                        workflowInstance.setStatus(WorkflowInstance.Status.valueOf(value));
                    } else {
                        log.error("Missing attribute: {}", line);
                        workflowInstancesWithError.add(workflowInstance);
                    }
                } else {
                    // unstructured lines
                    log.error("Not inside loop: {}", line);
                    unparsableLines.add(line);
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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        log.info("All instances and its workflows:");
        log.info(gson.toJson(getWorkflowInstances()));
        log.info("All inconsistent data:");
        log.info("With error / incomplete: {}", gson.toJson(getWorkflowInstancesWithError()));
        log.info("Unparseable lines: {}", gson.toJson(getUnparsableLines()));

        List<WorkflowInstance> running = getWorkflowInstances()
                .stream()
                .filter(a -> a.getStatus() != null)
                .filter(a -> a.getStatus().equals(WorkflowInstance.Status.RUNNING))
                .collect(toList());

        log.info("Workflows with running instances {}: {}",
                running.size() +  "/" + getWorkflowInstances().size(),
                gson.toJson(running));


        log.info("All contractors who are assigned to instances: {}",
                gson.toJson(
                        running.stream()
                                .filter(Contractor.class::isInstance)
                                .map(Contractor.class::cast)
                                .map(a-> a.getName())
                                .collect(toList())));
    }

}

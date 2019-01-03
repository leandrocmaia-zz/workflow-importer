package com.maia.workflowimporter.migrator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WorkflowInstanceMigratorTest {

    File contractorsData;
    File employeesData;
    File workflowsData;
    File workflowInstancesData;
    WorkflowInstanceMigrator migrator;

    @Before
    public void setUp() throws Exception {
        contractorsData = new ClassPathResource("contractors.data", getClass().getClassLoader()).getFile();
        employeesData = new ClassPathResource("employees.data", getClass().getClassLoader()).getFile();
        workflowsData = new ClassPathResource("workflows.data", getClass().getClassLoader()).getFile();
        workflowInstancesData = new ClassPathResource("workflowInstances.data", getClass().getClassLoader()).getFile();

        migrator = new WorkflowInstanceMigrator(
                workflowInstancesData,
                new EmployeeMigrator(employeesData),
                new ContractorMigrator(contractorsData),
                new WorkflowMigrator(workflowsData));

    }

    @Test
    public void should_read_eachline_and_populate_object() {
        assertEquals(migrator.getWorkflowInstances().size(), 18);
        assertEquals(migrator.getWorkflowInstancesWithError().size(), 2);
        assertEquals(migrator.getLinesOutsideLoop().size(), 2);
    }

    @Test
    public void should_capture_all() throws IOException {
        File file = new ClassPathResource("workflowInstances.inconsistent.data", getClass().getClassLoader()).getFile();

        WorkflowInstanceMigrator migrator = new WorkflowInstanceMigrator(file,
                new EmployeeMigrator(employeesData),
                new ContractorMigrator(contractorsData),
                new WorkflowMigrator(workflowsData));

        assertEquals(migrator.getWorkflowInstances().size(), 4);
        assertEquals(migrator.getWorkflowInstancesWithError().size(), 2);
        assertEquals(migrator.getLinesOutsideLoop().size(), 4);
    }

    @Test
    public void should_log_summary() throws IOException {

        File file = new ClassPathResource("workflowInstances.inconsistent.data", getClass().getClassLoader()).getFile();

        WorkflowInstanceMigrator migrator = new WorkflowInstanceMigrator(file,
                new EmployeeMigrator(employeesData),
                new ContractorMigrator(contractorsData),
                new WorkflowMigrator(workflowsData));

        migrator.logSummary();
        assertTrue(true);

    }
}

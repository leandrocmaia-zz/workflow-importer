package com.maia.workflowimporter.migrator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.maia.workflowimporter.migrator.impl.ContractorMigrator;
import com.maia.workflowimporter.migrator.impl.EmployeeMigrator;
import com.maia.workflowimporter.migrator.impl.WorkflowInstanceMigrator;
import com.maia.workflowimporter.migrator.impl.WorkflowMigrator;
import com.maia.workflowimporter.model.Contractor;
import com.maia.workflowimporter.model.Employee;
import com.maia.workflowimporter.model.WorkflowInstance;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class WorkflowInstanceMigratorTest {

  File contractorsData;
  File employeesData;
  File workflowsData;
  File workflowInstancesData;
  WorkflowInstanceMigrator migrator;

  @Before
  public void setUp() throws Exception {
    contractorsData =
        new ClassPathResource("contractors.data", getClass().getClassLoader()).getFile();
    employeesData = new ClassPathResource("employees.data", getClass().getClassLoader()).getFile();
    workflowsData = new ClassPathResource("workflows.data", getClass().getClassLoader()).getFile();
    workflowInstancesData =
        new ClassPathResource("workflowInstances.data", getClass().getClassLoader()).getFile();

    migrator =
        new WorkflowInstanceMigrator(
            workflowInstancesData,
            new EmployeeMigrator(employeesData),
            new ContractorMigrator(contractorsData),
            new WorkflowMigrator(workflowsData));

    migrator.parse();
  }

  @Test
  public void should_read_eachline_and_populate_object() {
    assertEquals(migrator.getWorkflowInstances().size(), 18);
    assertEquals(migrator.getWorkflowInstancesWithError().size(), 2);
    assertEquals(migrator.getUnparsableLines().size(), 2);

    assertEquals(migrator.getWorkflowInstances().get(0).getStatus(), WorkflowInstance.Status.NEW);
    assertEquals(migrator.getWorkflowInstances().get(0).getStep(), WorkflowInstance.Step.APPROVED);
    assertEquals(
        migrator.getWorkflowInstances().get(1).getStatus(), WorkflowInstance.Status.RUNNING);
    assertEquals(
        migrator.getWorkflowInstances().get(1).getStep(),
        WorkflowInstance.Step.READY_FOR_ARCHIVING);

    assertTrue(migrator.getWorkflowInstances().get(0).getAssignee() instanceof Employee);
    assertTrue(migrator.getWorkflowInstances().get(1).getAssignee() instanceof Employee);
    assertTrue(migrator.getWorkflowInstances().get(2).getAssignee() instanceof Employee);
    assertTrue(migrator.getWorkflowInstances().get(3).getAssignee() instanceof Contractor);
    assertTrue(migrator.getWorkflowInstances().get(4).getAssignee() instanceof Contractor);
    assertTrue(migrator.getWorkflowInstances().get(5).getAssignee() instanceof Employee);

    assertEquals(
        migrator
            .getWorkflowInstances()
            .stream()
            .filter(a -> a.getStep() != null) // filtering out incompletes without step
            .filter(
                a ->
                    a.getStep()
                        .equals(
                            WorkflowInstance.Step.UNDEFINED)) // no Step lookup should have failed
            .findAny(),
        Optional.empty());
  }

  @Test
  public void should_capture_all() throws IOException {
    File file =
        new ClassPathResource("workflowInstances.inconsistent.data", getClass().getClassLoader())
            .getFile();

    WorkflowInstanceMigrator migrator =
        new WorkflowInstanceMigrator(
            file,
            new EmployeeMigrator(employeesData),
            new ContractorMigrator(contractorsData),
            new WorkflowMigrator(workflowsData));

    migrator.parse();

    assertEquals(migrator.getWorkflowInstances().size(), 4);
    assertEquals(migrator.getWorkflowInstancesWithError().size(), 2);
    assertEquals(migrator.getUnparsableLines().size(), 4);
  }

  @Test
  public void should_log_summary() throws IOException {

    File file =
        new ClassPathResource("workflowInstances.inconsistent.data", getClass().getClassLoader())
            .getFile();

    WorkflowInstanceMigrator migrator =
        new WorkflowInstanceMigrator(
            file,
            new EmployeeMigrator(employeesData),
            new ContractorMigrator(contractorsData),
            new WorkflowMigrator(workflowsData));

    migrator.parse();

    migrator.logSummary();
    assertTrue(true);
  }
}

package com.maia.workflowimporter;

import com.maia.workflowimporter.migrator.impl.ContractorMigrator;
import com.maia.workflowimporter.migrator.impl.EmployeeMigrator;
import com.maia.workflowimporter.migrator.impl.WorkflowInstanceMigrator;
import com.maia.workflowimporter.migrator.impl.WorkflowMigrator;

import java.io.File;

public class Context {

	public static void main(String[] args) {

        // application context provides data source for the migrators, could be local files or remote. Migrators are agnostic to source.
        File contractorsData = new File("src/main/resources/contractors.data");
        File employeesData = new File("src/main/resources/employees.data");
        File workflowsData = new File("src/main/resources/workflows.data");
        File workflowInstancesData = new File("src/main/resources/workflowInstances.data");

        WorkflowInstanceMigrator migrator = new WorkflowInstanceMigrator(
                workflowInstancesData,
                new EmployeeMigrator(employeesData),
                new ContractorMigrator(contractorsData),
                new WorkflowMigrator(workflowsData));

        migrator.logSummary();

    }

}


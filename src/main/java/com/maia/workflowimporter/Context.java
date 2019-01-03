package com.maia.workflowimporter;

import com.maia.workflowimporter.migrator.ContractorMigrator;
import com.maia.workflowimporter.migrator.EmployeeMigrator;
import com.maia.workflowimporter.migrator.WorkflowInstanceMigrator;
import com.maia.workflowimporter.migrator.WorkflowMigrator;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

public class Context {

	public static void main(String[] args) {
		try {

			// application context provides data source for the migrators, could be local files or remote. Migrators are agnostic to source.
			File contractorsData = new ClassPathResource("contractors.data", Context.class.getClassLoader()).getFile();
			File employeesData = new ClassPathResource("employees.data", Context.class.getClassLoader()).getFile();
			File workflowsData = new ClassPathResource("workflows.data", Context.class.getClassLoader()).getFile();
			File workflowInstancesData = new ClassPathResource("workflowInstances.data", Context.class.getClassLoader()).getFile();

			WorkflowInstanceMigrator migrator = new WorkflowInstanceMigrator(
					workflowInstancesData,
					new EmployeeMigrator(employeesData),
					new ContractorMigrator(contractorsData),
					new WorkflowMigrator(workflowsData));

			migrator.printResults();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}


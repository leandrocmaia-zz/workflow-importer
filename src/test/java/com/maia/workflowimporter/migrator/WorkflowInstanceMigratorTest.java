package com.maia.workflowimporter.migrator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WorkflowInstanceMigratorTest {

    File file;

    @Before
    public void setUp() throws Exception {
        file = new ClassPathResource("workflowInstances.inconsistent.data", getClass().getClassLoader()).getFile();
    }

    @Test
    public void should_read_employee_file() {
        WorkflowInstanceMigrator migrator = new WorkflowInstanceMigrator(file);
        assertNotNull(migrator.getFile());
    }

    @Test
    public void should_read_eachline_and_populate_object() {
        WorkflowInstanceMigrator migrator = new WorkflowInstanceMigrator(file);
        assertEquals(migrator.getWorkflowInstances().size(), 4);
        assertEquals(migrator.getWorkflowInstancesWithError().size(), 1);
        assertEquals(migrator.getLinesOutsideLoop().size(), 4);

    }

    @Test
    public void should_capture_all() throws IOException {
        File file = new ClassPathResource("workflowInstances.data", getClass().getClassLoader()).getFile();

        WorkflowInstanceMigrator migrator = new WorkflowInstanceMigrator(file);
        assertEquals(migrator.getWorkflowInstances().size(), 18);
        assertEquals(migrator.getWorkflowInstancesWithError().size(), 1);
        assertEquals(migrator.getLinesOutsideLoop().size(), 2);

    }

}

package com.maia.workflowimporter.migrator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WorkflowMigratorTest {

    File file;

    @Before
    public void setUp() throws Exception {
        file = new ClassPathResource("workflows.data", getClass().getClassLoader()).getFile();
    }

    @Test
    public void should_read_file() {
        WorkflowMigrator migrator = new WorkflowMigrator(file);
        assertNotNull(migrator.getFile());
    }

    @Test
    public void should_read_eachline_and_populate_object() {
        WorkflowMigrator migrator = new WorkflowMigrator(file);
        assertEquals(migrator.getWorkflows().size(), 3);
        assertEquals(migrator.getWorkflows().get(0).getName(), "Purchase Request Approval Sub-Workflow");
    }

}

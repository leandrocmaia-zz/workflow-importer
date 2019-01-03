package com.maia.workflowimporter.migrator;

import com.maia.workflowimporter.migrator.impl.WorkflowMigrator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WorkflowMigratorTest {

    File file;
    WorkflowMigrator migrator;

    @Before
    public void setUp() throws Exception {
        file = new ClassPathResource("workflows.data", getClass().getClassLoader()).getFile();
        migrator = new WorkflowMigrator(file);
        migrator.parse();
    }

    @Test
    public void should_read_file() {
        assertNotNull(migrator.getFile());
    }

    @Test
    public void should_read_eachline_and_populate_object() {
        assertEquals(migrator.getWorkflows().size(), 3);
        assertEquals(migrator.getWorkflows().get(0).getName(), "Purchase Request Approval Sub-Workflow");
    }

}

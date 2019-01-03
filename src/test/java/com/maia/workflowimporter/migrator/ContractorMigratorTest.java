package com.maia.workflowimporter.migrator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ContractorMigratorTest {

    File file;

    @Before
    public void setUp() throws Exception {
        file = new ClassPathResource("contractors.data", getClass().getClassLoader()).getFile();
    }

    @Test
    public void should_read_employee_file() {
        ContractorMigrator migrator = new ContractorMigrator(file);
        assertNotNull(migrator.getFile());
    }

    @Test
    public void should_read_eachline_and_populate_object() {
        ContractorMigrator migrator = new ContractorMigrator(file);
        assertEquals(migrator.getContractors().size(), 3);
        assertEquals(migrator.getContractors().get(0).getAlias(), "con24");
        assertEquals(migrator.getContractors().get(2).getAlias(), "con07");
    }

}

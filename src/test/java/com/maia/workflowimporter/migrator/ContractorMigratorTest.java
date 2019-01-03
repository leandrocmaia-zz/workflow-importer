package com.maia.workflowimporter.migrator;

import com.maia.workflowimporter.migrator.impl.ContractorMigrator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ContractorMigratorTest {

    File file;
    ContractorMigrator migrator;

    @Before
    public void setUp() throws Exception {
        file = new ClassPathResource("contractors.data", getClass().getClassLoader()).getFile();
        migrator = new ContractorMigrator(file);
        migrator.parse();
    }

    @Test
    public void should_read_file() {
        assertNotNull(migrator.getContractors());
    }

    @Test
    public void should_read_eachline_and_populate_object() {
        assertEquals(migrator.getContractors().size(), 3);
        assertEquals(migrator.getContractors().get(0).getAlias(), "con24");
        assertEquals(migrator.getContractors().get(2).getAlias(), "con07");
    }

}

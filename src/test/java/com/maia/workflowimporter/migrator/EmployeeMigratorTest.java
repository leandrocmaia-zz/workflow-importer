package com.maia.workflowimporter.migrator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EmployeeMigratorTest {

    File file;

    @Before
    public void setUp() throws Exception {
        file = new ClassPathResource("employees.data", getClass().getClassLoader()).getFile();
    }

    @Test
    public void should_read_employee_file() {
        EmployeeMigrator migrator = new EmployeeMigrator(file);
        assertNotNull(migrator.getFile());
    }

    @Test
    public void should_read_eachline_and_populate_object() {
        EmployeeMigrator migrator = new EmployeeMigrator(file);
        assertEquals(migrator.getEmployees().size(), 6);
        assertEquals(migrator.getEmployees().get(0).getEmail(), "john.doe@company.local");
        assertEquals(migrator.getEmployees().get(5).getEmail(), "h.doster@company.local");
    }

}

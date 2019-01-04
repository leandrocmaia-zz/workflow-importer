package com.maia.workflowimporter.migrator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.maia.workflowimporter.migrator.impl.EmployeeMigrator;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class EmployeeMigratorTest {

  File file;
  EmployeeMigrator migrator;

  @Before
  public void setUp() throws Exception {
    file = new ClassPathResource("employees.data", getClass().getClassLoader()).getFile();
    migrator = new EmployeeMigrator(file);
    migrator.parse();
  }

  @Test
  public void should_read_employee_file() {
    assertNotNull(migrator.getFile());
  }

  @Test
  public void should_read_eachline_and_populate_object() {
    assertEquals(migrator.getEmployees().size(), 6);
    assertEquals(migrator.getEmployees().get(0).getEmail(), "john.doe@company.local");
    assertEquals(migrator.getEmployees().get(5).getEmail(), "h.doster@company.local");
  }
}

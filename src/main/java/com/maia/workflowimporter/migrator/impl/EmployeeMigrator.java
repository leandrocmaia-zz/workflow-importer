package com.maia.workflowimporter.migrator.impl;

import com.maia.workflowimporter.migrator.Migrator;
import com.maia.workflowimporter.model.Employee;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmployeeMigrator extends BaseMigrator implements Migrator {

  @Getter List<Employee> employees = new ArrayList<>();

  public EmployeeMigrator(File file) {
    this.file = file;
  }

  @Override
  public void parse() {
    try (BufferedReader fileBufferReader = new BufferedReader(new FileReader(file))) {
      String l;
      Employee employee = null;
      while ((l = fileBufferReader.readLine()) != null) {
        String line = l.trim();
        if (logicSeparator[0].equals((line))) {
          employee = new Employee();
        }
        if (line.contains("employeeId")) {
          employee.setId(line.split(delimiter)[1].trim());
        } else if (line.contains("fullName")) {
          employee.setName(line.split(delimiter)[1].trim());
        } else if (line.contains("email")) {
          employee.setEmail(line.split(delimiter)[1].trim());
        }
        if (logicSeparator[1].equals((line))) {
          employees.add(employee);
        }
      }
    } catch (FileNotFoundException e) {
      log.error("File not found. {}", e.getMessage());
    } catch (IOException e) {
      log.error("Error while reading file. {}", e.getMessage());
    }
  }

  @Override
  public void logSummary() {}
}

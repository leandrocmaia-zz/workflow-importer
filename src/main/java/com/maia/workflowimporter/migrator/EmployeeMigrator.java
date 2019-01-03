package com.maia.workflowimporter.migrator;

import com.maia.workflowimporter.model.Employee;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EmployeeMigrator {

    String[] logicSeparator = {"start", "end"};

    @Getter
    File file;
    @Getter
    List<Employee> employees = new ArrayList<>();

    public EmployeeMigrator(File file) {
        this.file = file;
        parseFile();
    }

    public void parseFile() {
        try (BufferedReader fileBufferReader = new BufferedReader(new FileReader(file))) {
            String l;
            Employee employee = null;
            while ((l = fileBufferReader.readLine()) != null) {
                String line = l.trim();
                if (logicSeparator[0].equals((line))) {
                    employee = new Employee();
                }
                if (line.contains("employeeId")) {
                    employee.setId(line.split(" : ")[1]);
                } else if (line.contains("fullName")) {
                    employee.setName(line.split(" : ")[1]);
                } else if (line.contains("email")) {
                    employee.setEmail(line.split(" : ")[1]);
                }
                if (logicSeparator[1].equals((line))) {
                    employees.add(employee);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

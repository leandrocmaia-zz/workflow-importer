package com.maia.workflowimporter.migrator.impl;

import com.maia.workflowimporter.migrator.Migrator;
import com.maia.workflowimporter.model.Contractor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ContractorMigrator extends BaseMigrator implements Migrator {

    @Getter
    List<Contractor> contractors = new ArrayList<>();

    public ContractorMigrator(File file) {
        this.file = file;
    }

    @Override
    public void parse() {
        try (BufferedReader fileBufferReader = new BufferedReader(new FileReader(file))) {
            String l;
            Contractor contractor = null;
            while ((l = fileBufferReader.readLine()) != null) {
                String line = l.trim();
                if (logicSeparator[0].equals((line))) {
                    contractor = new Contractor();
                }
                if (line.contains("contractorName")) {
                    contractor.setAlias(line.split(delimiter)[1].trim());
                } else if (line.contains("fullName")) {
                    contractor.setName(line.split(delimiter)[1].trim());
                } else if (line.contains("email")) {
                    contractor.setEmail(line.split(delimiter)[1].trim());
                }
                if (logicSeparator[1].equals((line))) {
                    contractors.add(contractor);
                }
            }
        } catch (FileNotFoundException e) {
            log.error("File not found. {}", e.getMessage());
        } catch (IOException e) {
            log.error("Error while reading file. {}", e.getMessage());
        }
    }

    @Override
    public void logSummary() {

    }

}

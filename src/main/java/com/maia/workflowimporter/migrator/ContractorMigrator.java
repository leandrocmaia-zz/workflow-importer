package com.maia.workflowimporter.migrator;

import com.maia.workflowimporter.model.Contractor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ContractorMigrator {

    String[] logicSeparator = {"start", "end"};

    @Getter
    File file;
    @Getter
    List<Contractor> contractors = new ArrayList<>();

    public ContractorMigrator(File file) {
        this.file = file;
        parseFile();
    }

    public void parseFile() {
        try (BufferedReader fileBufferReader = new BufferedReader(new FileReader(file))) {
            String l;
            Contractor contractor = null;
            while ((l = fileBufferReader.readLine()) != null) {
                String line = l.trim();
                if (logicSeparator[0].equals((line))) {
                    contractor = new Contractor();
                }
                if (line.contains("contractorName")) {
                    contractor.setAlias(line.split(" : ")[1]);
                } else if (line.contains("fullName")) {
                    contractor.setName(line.split(" : ")[1]);
                } else if (line.contains("email")) {
                    contractor.setEmail(line.split(" : ")[1]);
                }
                if (logicSeparator[1].equals((line))) {
                    contractors.add(contractor);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

package com.maia.workflowimporter.migrator;

import com.maia.workflowimporter.model.Workflow;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WorkflowMigrator {

    String[] logicSeparator = {"start", "end"};

    @Getter
    File file;
    @Getter
    List<Workflow> workflows = new ArrayList<>();

    public WorkflowMigrator(File file) {
        this.file = file;
        parseFile();
    }

    public void parseFile() {
        try (BufferedReader fileBufferReader = new BufferedReader(new FileReader(file))) {
            String l;
            Workflow workflow = null;
            while ((l = fileBufferReader.readLine()) != null) {
                String line = l.trim();
                if (logicSeparator[0].equals((line))) {
                    workflow = new Workflow();
                }
                if (line.contains("id")) {
                    workflow.setId(new Long(line.split(":")[1].trim()));
                } else if (line.contains("name")) {
                    workflow.setName(line.split(":")[1].trim());
                } else if (line.contains("author")) {
                    workflow.setAuthor(line.split(":")[1].trim());
                } else if (line.contains("version")) {
                    workflow.setVersion(new Integer(line.split(":")[1].trim()));
                }
                if (logicSeparator[1].equals((line))) {
                    workflows.add(workflow);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

package com.taskmanager.app.service;

import com.taskmanager.app.entity.TaskEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

@Service
public class TaskExportService {

    public ByteArrayInputStream tasksToCsv(List<TaskEntity> tasks) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        // CSV HEADER
        writer.println("Task Name,Description,Priority,Status,Due Date,Created At");

        for (TaskEntity task : tasks) {
            writer.printf(
                    "\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    task.getName(),
                    task.getDescription() == null ? "" : task.getDescription(),
                    task.getPriority(),
                    task.getStatus(),
                    task.getDueDate(),
                    task.getCreatedAt()
            );
        }

        writer.flush();
        return new ByteArrayInputStream(out.toByteArray());
    }
}

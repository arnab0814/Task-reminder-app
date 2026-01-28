package com.taskmanager.app.service;

import com.taskmanager.app.entity.TaskEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ✅ OTP EMAIL (PLAIN TEXT – CORRECT)
    public void sendOtp(String toEmail, String otp) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("OTP Verification - Task Manager");
            helper.setText(
                    "Your OTP is: " + otp +
                            "\n\nThis OTP is valid for 5 minutes.\n\n" +
                            "Do not share this OTP with anyone.",
                    false
            );

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    // ✅ TASK REMINDER EMAIL (HTML – CORRECT)
    public void sendTaskReminderEmail(
            String to,
            String userName,
            List<TaskEntity> tasks
    ) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Today's Task Reminder");

            StringBuilder body = new StringBuilder();
            body.append("<h2>Hi ").append(userName).append(",</h2>");
            body.append("<p>You have the following tasks scheduled:</p>");
            body.append("<ul>");

            for (TaskEntity task : tasks) {
                body.append("<li>")
                        .append("<b>").append(task.getName()).append("</b>")
                        .append(" — Priority: ")
                        .append(task.getPriority())
                        .append("</li>");
            }

            body.append("</ul>");
            body.append("<p>Please complete them on time.</p>");
            body.append("<br><b>– Task Manager App</b>");

            helper.setText(body.toString(), true); // 🔥 HTML ENABLED

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send task reminder email", e);
        }
    }

    public void sendTaskCsvEmail(
            String to,
            String userName,
            byte[] csvData
    ) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Your Task List (CSV)");
            helper.setText(
                    "Hi " + userName +
                            ",\n\nAttached is your task list in CSV format.\n\n– Task Manager",
                    false
            );

            helper.addAttachment(
                    "tasks.csv",
                    new ByteArrayResource(csvData)
            );

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send CSV email", e);
        }
    }

}

package vn.kyler.job_hunter.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import vn.kyler.job_hunter.domain.Job;
import vn.kyler.job_hunter.repository.JobRepository;
import vn.kyler.job_hunter.repository.SubscriberRepository;
import vn.kyler.job_hunter.util.SecurityUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

    public EmailService(JavaMailSender javaMailSender,SpringTemplateEngine springTemplateEngine) {
        this.javaMailSender = javaMailSender;
        this.springTemplateEngine = springTemplateEngine;
    }

//    @Async
    public void handleSendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);
            this.javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            System.out.println("ERROR SEND EMAIL: " + e);
        }
    }

//    @Async
    public void handleSendEmailFromTemplateSync(String to, String subject, String templateName,String name, List<Job> jobs) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("jobs", jobs);
        String content = this.springTemplateEngine.process(templateName, context);
        this.handleSendEmailSync(to, subject, content, false, true);
    }
}

package dev.twiceb.emailservice.amqp;

import dev.twiceb.common.dto.request.EmailRequest;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailListener {

    private static final Logger logger = LoggerFactory.getLogger(EmailListener.class);
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine thymeleafTemplateEngine; //

    @Value("${spring.mail.username}")
    private String username;

    @SneakyThrows
    @RabbitListener(queues = "q.mail")
    public void sendMessageHtml(EmailRequest emailRequest) {
        logger.info("SENDMESSAGEHTML() is invoked");
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(emailRequest.getAttributes());
        String htmlBody =
                thymeleafTemplateEngine.process(emailRequest.getTemplate(), thymeleafContext);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(new InternetAddress(username, "Team SyncTurtle"));
        helper.setTo(emailRequest.getTo());
        helper.setSubject(emailRequest.getSubject());
        helper.setText(htmlBody, true);
        mailSender.send(message);
    }
}

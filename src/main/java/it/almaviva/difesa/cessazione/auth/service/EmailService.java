package it.almaviva.difesa.cessazione.auth.service;

import it.almaviva.difesa.cessazione.auth.constant.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private static final String NOREPLY_ADDRESS = "noreply@cessazione.it";

    @Value("${cessazione.url}")
    private String cessazioneUrl;

    @Value("${cessazione.sendmail.enable}")
    private boolean sendEmailEnabled;

    @Value("${cessazione.sendmail.mock.enable}")
    private boolean useMockEnabled;

    @Value("${cessazione.sendmail.mock.distributionlist}")
    private String[] distributionList;

    public final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    public void sendHTMLMessage(String to, String subject, String template,
                                Map<String, Object> variables) throws MailException, MessagingException {
        if (sendEmailEnabled) {
            String content = build(template, variables);

            MimeMessage message = emailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(NOREPLY_ADDRESS);
            setTo(helper, to);
            helper.setSubject(subject);
            helper.setText(content, true);
            emailSender.send(helper.getMimeMessage());
        } else {
            log.debug("Invio mail non è abilitato");
        }
    }

    public void sendHTMLMessageWithAttachments(String to, String subject, String template,
                                               Map<String, Object> variables, String fileName,
                                               byte[] file) throws MailException, MessagingException {
        if (sendEmailEnabled) {
            String content = build(template, variables);

            MimeMessage message = emailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(NOREPLY_ADDRESS);
            setTo(helper, to);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.addAttachment(fileName, new ByteArrayResource(file));
            emailSender.send(helper.getMimeMessage());
        } else {
            log.debug("Invio mail non è abilitato");
        }
    }

    //process the desired template and replace all placeholders with the values passed via the input Map
    private String build(String template, Map<String, Object> variables) {
        Context context = new Context();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        if (template.equalsIgnoreCase(Constant.TEMPLATE_EMAIL_NOTIFICATION))
            context.setVariable("url", cessazioneUrl);

        return templateEngine.process(template, context);
    }

    private void setTo(MimeMessageHelper helper, String to) throws MessagingException {
        if (useMockEnabled) {
            helper.setTo(distributionList);
        } else {
            helper.setTo(to);
        }
    }

}

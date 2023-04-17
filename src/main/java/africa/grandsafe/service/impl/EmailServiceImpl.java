package africa.grandsafe.service.impl;

import africa.grandsafe.exceptions.GenericException;
import africa.grandsafe.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    @Async
    @Override
    public void sendEmail(String toMail, String email){
        try{
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mailMessage, "utf-8");
            mimeMessageHelper.setSubject("[GrandSafe Wallet] GrandSafe Account Verification");
            mimeMessageHelper.setTo(toMail);
            mimeMessageHelper.setFrom("lekan.sofuyi01@gmail.com");
            mimeMessageHelper.setText(email, true);
            javaMailSender.send(mailMessage);
        } catch(MessagingException | MailException exception){
            throw new GenericException(exception.getMessage());
        }
    }
}
package com.cafe.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EmailUtils {
    @Autowired
    JavaMailSender javamail;

    public void sendGroupMail(String to, String subject, String text, List<String> list){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("aftab.knit@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if(list!=null && !list.isEmpty()) {
            message.setCc(getCcArray(list));
        }
        System.out.println("in sendSimpleMail method in EmailUtils");
        javamail.send(message);
    }

    public void sendIndividualMail(String to, String subject, String mailMessage) throws MessagingException {
        MimeMessage mimeMessage = javamail.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom("aftab.knit@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        //String htmlMessage = "<p><b>Your login details for Cafe management is</b><br><b>Email: </b> " + to + " <br><b>Password is: </b> "+ password +"<br><a href=\"http://localhost:4200/\">Click here to login</a></p>";
        String htmlMessage = mailMessage;
        mimeMessage.setContent(htmlMessage,"text/html");
        javamail.send(mimeMessage);
    }

    private String[] getCcArray(List<String> cclist){
        String[] cc = new String[(cclist.size())];
        for(int i=0; i<cclist.size(); i++){
            cc[i]=cclist.get(i);
        }
        return cc;
    }
}

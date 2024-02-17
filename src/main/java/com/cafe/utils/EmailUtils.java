package com.cafe.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EmailUtils {
    @Autowired
    JavaMailSender javamail;

    public void sendSimpleMail(String to, String subject, String text, List<String> list){
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

    private String[] getCcArray(List<String> cclist){
        String[] cc = new String[(cclist.size())];
        for(int i=0; i<cclist.size(); i++){
            cc[i]=cclist.get(i);
        }
        return cc;
    }
}

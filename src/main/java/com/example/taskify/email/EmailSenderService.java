package com.example.taskify.email;

import com.example.taskify.controller.form.EmailForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailSenderService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleEmail(EmailForm form) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("dondimon71@gmail.com");
        message.setTo(form.getToEmail());
        message.setText("Hi! You have a new task: " + form.getTitle() + ". Description: " + form.getDescription() + ". Deadline: " + form.getDeadline() + ". Good luck!");
        message.setSubject("You have a new task!");
        javaMailSender.send(message);
        log.info("Mail sent successfully");
    }
}

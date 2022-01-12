package com.example.taskify.email;

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

    public void sendSimpleEmail(String toEmail,
                                String title,
                                String description,
                                String deadline) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("dondimon71@gmail.com");
        message.setTo(toEmail);
        message.setText("Hi! You have a new task: " + title + ". Description: " + description + ". Deadline: " + deadline + ". Good luck!");
        message.setSubject("You have a new task!");
        javaMailSender.send(message);
        log.info("Mail sent successfully");
    }
}

package com.korchak.thymeleaf.service;

import com.korchak.thymeleaf.component.EmailConf;
import com.korchak.thymeleaf.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  //  private EmailConf emailConf;
  private JavaMailSender javaMailSender;

  @Autowired
  public EmailService(JavaMailSender javaMailSender) {
//    this.emailConf = emailConf;
    this.javaMailSender = javaMailSender;
  }


  public void sendEmail(User user) throws MailException {

//    mailSender.setPort(emailConf.getPort());
//    mailSender.setHost(emailConf.getHost());
//    mailSender.setUsername(emailConf.getUsername());
//    mailSender.setPassword(emailConf.getPassword());

    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("Controller");
    message.setTo(user.getEmail());
    message.setSubject("Password reminding");
    message.setText("Your password is: \"" + user.getPassword().getPassword() + "\"" +
        "\n Administration.");

    javaMailSender.send(message);
  }


  public void sendEmailToStudent(String userEmail, String studentEmail, String emailText,
      String subject) throws MailException {

    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(userEmail);
    message.setTo(studentEmail);
    message.setSubject(subject);
    message.setText(emailText + "\n\nContact me - " + userEmail);
    javaMailSender.send(message);
  }


}

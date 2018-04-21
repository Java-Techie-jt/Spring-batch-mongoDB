package com.javatechie.spring.batch.api.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailUtil {

	@Autowired
	private JavaMailSender sender;

	public String sendEmail(String to, String TextBody) {
		String msg = "";
		SimpleMailMessage message = new SimpleMailMessage();
		try {
			message.setTo(to);
			message.setText(TextBody);
			message.setSubject("Payment Dues Alert");
			message.setFrom("javatechie4u@gmail.com");
			sender.send(message);
			msg = "mail triggered successfully to : " + to;
		} catch (Exception e) {
			msg = e.getMessage();
		}
		return msg;
	}

}

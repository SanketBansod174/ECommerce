package com.app.controller;

import java.io.File;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.dto.OTPVerifyUpdatePassword;
import com.app.pojos.Email;
import com.app.service.IUserService;

@RestController
@CrossOrigin
@RequestMapping("/email")
public class SendMailController {
	@Autowired
	private JavaMailSender sender;
	@Autowired 
	private IUserService userService;
	
	
	@PostMapping("/send_email")
	public ResponseEntity<?> processEmailAndSend(@RequestBody Email em )
	{
		System.out.println(em.getDestEmail()+"    "+em.getMessage());
		SimpleMailMessage mesg = new SimpleMailMessage();
		mesg.setTo(em.getDestEmail());
		mesg.setSubject(em.getSubject());
		// Random ramdom = new Random();
		// long otp =   ramdom.nextInt(999999);
		mesg.setText(em.getMessage());
		sender.send(mesg);
		return ResponseEntity.status(HttpStatus.OK).body("mail sent successfully to "+em.getDestEmail());
	}
	@PostMapping("/send_email/attach")
	public ResponseEntity<?> sendMessageWithAttachment(@RequestBody MultipartFile imageFile) throws MessagingException
	{
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message,true);
		helper.setTo("omkarmohite505@gmail.com");
		helper.setSubject("1111111111111");
		helper.setText("22222222222222");
//		FileSystemResource file = new FileSystemResource(new File("C:/Users/rahul/OneDrive/Pictures/ID Photo/OM 1.jpeg"));
		helper.addAttachment("photo", imageFile);
		sender.send(message);
		return ResponseEntity.status(HttpStatus.OK).body("Email sent");
	}
	
	@PostMapping("/send_otp")
	public ResponseEntity<?> SendOTP(@RequestBody OTPVerifyUpdatePassword update ) 
	{
		String destEmail = update.getDestEmail();
		System.out.println("-----------sending otp-----------");
		System.out.println(" Email "+destEmail);
		SimpleMailMessage mesg = new SimpleMailMessage();
		mesg.setTo(destEmail);
		mesg.setSubject("OTP for verification");
		Random ramdom = new Random();
		int otp =   ramdom.nextInt(999999);
		userService.storeOTP(destEmail, otp);
		mesg.setText("Enter this OTP for verification : "+otp+"            Do not share it with anyone !!!!!");
		sender.send(mesg);
		return ResponseEntity.status(HttpStatus.OK).body("OTP sent successfully to Your email");
	}

}

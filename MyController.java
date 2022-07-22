package com.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@CrossOrigin
@RequestMapping("/api/home")
public class MyController {
	
	@GetMapping("/home1")
	public ResponseEntity<?> showHomePage()
	{
		System.out.println("----------inside home --------");
		return ResponseEntity.status(HttpStatus.OK).body("JWT Success");
	}

}

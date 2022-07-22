package com.app.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.razorpay.*;
import com.app.dto.CreatedPaymentOrderDTO;
import com.app.dto.PayOrder;
import com.app.dto.UpdatedPaymentOrderDTO;
import com.app.service.IPaymentService;

@RestController
@CrossOrigin
public class PaymentController {
	@Value("${Razorpay.key_id}")
	private String key_id;
	@Value("${Razorpay.key_secret}")
	private String key_secret;
	
	
	@Autowired
	private IPaymentService paymentService;
	
	@PostMapping("/user/create_order")
	public ResponseEntity<?> createOrder(@RequestBody PayOrder pay) throws RazorpayException{
		System.out.println("Amount : "+pay.getAmt());
		System.out.println("In create order method");
		
		var client = new RazorpayClient(key_id, key_secret);
		JSONObject ob = new JSONObject();
		ob.put("amount", pay.getAmt()*100);  // amount need to send on paise  so thats why multiplied by 100
		ob.put("currency", "INR");
		ob.put("receipt", "txn_123");
		
		Order order = client.Orders.create(ob);
		CreatedPaymentOrderDTO myOrderRecord = new CreatedPaymentOrderDTO();
		System.out.println("  --------- "+order);
		myOrderRecord.setOrderId(order.get("id"));
		myOrderRecord.setAmount(order.get("amount"));
		myOrderRecord.setAmount_paid(order.get("amount_paid"));
		myOrderRecord.setStatus(order.get("status"));
		paymentService.storePaymentOrder(myOrderRecord);
				
		
		return ResponseEntity.status(HttpStatus.OK).body(order.toString());
	}
	
	@PostMapping("/user/payment/success")
	public ResponseEntity<?> storeSuccessPaymentDetails(@RequestBody UpdatedPaymentOrderDTO order) throws Exception{
		System.out.println("----------   "+order);
		paymentService.updatePaymentOrder(order);
		return ResponseEntity.status(HttpStatus.OK).body("Payment Details Stored"); 
	}

}

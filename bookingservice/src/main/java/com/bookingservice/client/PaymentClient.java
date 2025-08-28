package com.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bookingservice.dto.ProductRequest;
import com.bookingservice.dto.RefundRequest;
import com.bookingservice.dto.RefundResponse;
import com.bookingservice.dto.StripeResponse;


@FeignClient(name = "PAYMENTSERVICE") 
public interface PaymentClient {
	
	@PostMapping("/product/v1/checkout")
    StripeResponse createPaymentSession(@RequestBody ProductRequest request);

//	void refundPayment(String transactionId);
	
	@PostMapping("/product/v1/refund")
    RefundResponse refundPayment(@RequestBody RefundRequest refundRequest);

}
package com.bookingservice.dto;

public class RefundRequest {
	
	private String paymentIntentId; // Stripe paymentIntent ID
    private Long bookingId;       // Booking reference
    private String reason;        // Optional: duplicate, requested_by_customer, etc.
    

    
	public RefundRequest(String paymentIntentId, Long bookingId, String reason) {
		super();
		this.paymentIntentId = paymentIntentId;
		this.bookingId = bookingId;
		this.reason = reason;
	}
	
	
	public String getPaymentIntentId() {
		return paymentIntentId;
	}
	public void setPaymentIntentId(String paymentIntentId) {
		this.paymentIntentId = paymentIntentId;
	}
	public Long getBookingId() {
		return bookingId;
	}
	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
    
    

}

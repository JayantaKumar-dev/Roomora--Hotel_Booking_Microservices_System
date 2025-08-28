package com.bookingservice.dto;

public class RefundResponse {
	
	private boolean success;
    private String refundId;
    private String message;
    

	public RefundResponse(boolean success, String refundId, String message) {
		super();
		this.success = success;
		this.refundId = refundId;
		this.message = message;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getRefundId() {
		return refundId;
	}
	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
    
    

}

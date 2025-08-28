package com.paymentservice.dto;

public class RefundResponse {
	
	private boolean success;
    private String refundId;
    private String status;
    private String message;
    

	public RefundResponse(boolean success, String refundId, String status, String message) {
		super();
		this.success = success;
		this.refundId = refundId;
		this.status = status;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
    
    

}

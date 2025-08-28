package com.paymentservice.dto;

public class ProductRequest {
	
	private String name;
    private long amount; // In paise (₹10.00 = 1000)
    private String currency;
    private long quantity;
    
    
    
    // Getters and Setters
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

    
    
    

}

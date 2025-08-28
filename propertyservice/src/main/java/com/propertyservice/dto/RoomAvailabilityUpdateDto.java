package com.propertyservice.dto;

import java.time.LocalDate;
import java.util.List;

public class RoomAvailabilityUpdateDto {
	
    private Long roomId;
    private List<LocalDate> dates;
    private int decreaseBy; // usually 1
    
    
	public Long getRoomId() {
		return roomId;
	}
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}
	public List<LocalDate> getDates() {
		return dates;
	}
	public void setDates(List<LocalDate> dates) {
		this.dates = dates;
	}
	public int getDecreaseBy() {
		return decreaseBy;
	}
	public void setDecreaseBy(int decreaseBy) {
		this.decreaseBy = decreaseBy;
	}
    
    
}
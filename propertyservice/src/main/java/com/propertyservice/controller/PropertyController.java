package com.propertyservice.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 
import com.propertyservice.dto.APIResponse;
import com.propertyservice.dto.ErrorResponse;
import com.propertyservice.dto.PropertyDto;
import com.propertyservice.dto.RoomAvailabilityUpdateDto;
import com.propertyservice.entity.Property;
import com.propertyservice.entity.RoomAvailability;
import com.propertyservice.entity.Rooms;
import com.propertyservice.exception.ResourceNotFoundException;
import com.propertyservice.repository.RoomAvailabilityRepository;
import com.propertyservice.service.PropertyService;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/v1/property")
public class PropertyController {
	
	@Autowired
	private PropertyService propertyService;
	
	@Autowired
	private RoomAvailabilityRepository availabilityRepository;
	

	@PostMapping(
		    value = "/add-property",
		    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,  // Ensures the endpoint accepts multipart/form-data
		    produces = MediaType.APPLICATION_JSON_VALUE
		)
		public APIResponse<PropertyDto> addProperty(
		        @RequestParam("property") String propertyJson,  // Use RequestParam to get the property as a raw JSON string
		        @RequestParam("files") MultipartFile[] files) {  // Use RequestParam to handle files


		    // Parse the property JSON into PropertyDto
		    ObjectMapper objectMapper = new ObjectMapper();
		    PropertyDto dto = null;
		    try {
		        dto = objectMapper.readValue(propertyJson, PropertyDto.class);  // Convert JSON string to PropertyDto
		    } catch (JsonProcessingException e) {
		        //logger.error("Error parsing property JSON", e);
		    	APIResponse<PropertyDto> errorResponse = new APIResponse<>();
		        errorResponse.setMessage("Invalid property JSON format");
		        errorResponse.setStatus(400); // Bad Request
		        errorResponse.setData(null); // No data since parsing failed
		        return errorResponse;
		    }
		    
		    // Process the property and files
		    PropertyDto propertyDto = propertyService.addProperty(dto, files);
		    
		    APIResponse<PropertyDto> response = new APIResponse<>();
		    response.setMessage("Property Details Submitted Successfully");
		    response.setStatus(201);
		    response.setData(propertyDto);
		    
		    return response;
	}
	
	
	@GetMapping("/search-property")
	public APIResponse<List<Property>> searchProperty(
	        @RequestParam String name,
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

	    APIResponse<List<Property>> response = propertyService.searchProperty(name, date);

	    if (response.getData() == null || response.getData().isEmpty()) {
	        throw new ResourceNotFoundException("No Property matches the given locations and date");
	    }

	    return response;
	}
	
	@GetMapping("/search-property/with-date-range")
	public APIResponse<List<Property>> searchProperty(
	    @RequestParam String name,
	    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
	    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

		APIResponse<List<Property>> matchedProperties = propertyService.searchPropertyWithAvailability(name, checkIn, checkOut);
	    return matchedProperties;
	}
	
	@PostMapping("/check-room-availability")
	public Boolean isRoomAvailable(
	    @RequestParam Long roomId,
	    @RequestBody List<LocalDate> dates) {

	    List<RoomAvailability> availabilities = availabilityRepository.findByRoomIdAndAvailableDateIn(roomId, dates);
	    boolean isAvailable = availabilities.size() == dates.size() &&
	                          availabilities.stream().allMatch(a -> a.getAvailableCount() > 0);
	    
	    return isAvailable;
	}

	
	
	@PutMapping("/update-availability")
	public APIResponse<Boolean> updateAvailability(@RequestBody RoomAvailabilityUpdateDto dto) {
	    for (LocalDate date : dto.getDates()) {
	        Optional<RoomAvailability> availability = availabilityRepository.findByRoomIdAndAvailableDate(dto.getRoomId(), date);
	        if (availability.isPresent()) {
	            RoomAvailability ra = availability.get();
	            ra.setAvailableCount(ra.getAvailableCount() - dto.getDecreaseBy());
	            availabilityRepository.save(ra);
	        }
	    }
	    
	    APIResponse<Boolean> response = new APIResponse<>();
	    response.setMessage("Rooms Availability Updated");
	    response.setStatus(200);
	    response.setData(true);
	    
	    return response;
	}


	
	
	@GetMapping("/property-id")
	public APIResponse<PropertyDto> getPropertyById(@RequestParam long id){
		APIResponse<PropertyDto> response = propertyService.findPropertyById(id);
		return response;
	}
	
	@GetMapping("/room-available-room-id")
	public APIResponse<List<RoomAvailability>> getTotalRoomsAvailable(@RequestParam long id){
		List<RoomAvailability> totalRooms = propertyService.getTotalRoomsAvailable(id);
		
		APIResponse<List<RoomAvailability>> response = new APIResponse<>();
	    response.setMessage("Total rooms");
	    response.setStatus(200);
	    response.setData(totalRooms);
	    return response;
	}
	
	@GetMapping("/room-id")
	public APIResponse<Rooms> getRoomType(@RequestParam long id){
		Rooms room = propertyService.getRoomById(id);
		
		APIResponse<Rooms> response = new APIResponse<>();
	    response.setMessage("Total rooms");
	    response.setStatus(200);
	    response.setData(room);
	    return response;
	}

	
}
	

package com.propertyservice.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyservice.constants.AppConstants;
import com.propertyservice.dto.APIResponse;
import com.propertyservice.dto.EmailRequest;
import com.propertyservice.dto.PropertyDto;
import com.propertyservice.dto.RoomsDto;
import com.propertyservice.entity.Area;
import com.propertyservice.entity.City;
import com.propertyservice.entity.Country;
import com.propertyservice.entity.Property;
import com.propertyservice.entity.PropertyPhotos;
import com.propertyservice.entity.RoomAvailability;
import com.propertyservice.entity.Rooms;
import com.propertyservice.entity.State;
import com.propertyservice.exception.DuplicateResourceException;
import com.propertyservice.exception.ResourceNotFoundException;
import com.propertyservice.repository.AreaRepository;
import com.propertyservice.repository.CityRepository;
import com.propertyservice.repository.CountryRepository;
import com.propertyservice.repository.PropertyPhotosRepository;
import com.propertyservice.repository.PropertyRepository;
import com.propertyservice.repository.RoomAvailabilityRepository;
import com.propertyservice.repository.RoomRepository;
import com.propertyservice.repository.StateRepository;

@Service
public class PropertyService {
	
	private static final Logger log = LoggerFactory.getLogger(PropertyService.class);
	
	@Autowired
	private PropertyRepository propertyRepository;
	@Autowired
	private AreaRepository areaRepository;
	@Autowired
	private CityRepository cityRepository;
	@Autowired
	private StateRepository stateRepository;
	@Autowired
	private CountryRepository countryRepository;
	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private RoomAvailabilityRepository availabilityRepository;
	@Autowired
	private S3Service s3Service;
	@Autowired
	private PropertyPhotosRepository propertyPhotosRepository;
	@Autowired
	private KafkaTemplate<String, EmailRequest> kafkaTemplate;

	
	@Transactional
	public PropertyDto addProperty(PropertyDto dto, MultipartFile[] files) {
		
		
		Area area = areaRepository.findByName(dto.getArea()).get();
	    City city = cityRepository.findByName(dto.getCity()).get();
	    State state = stateRepository.findByName(dto.getState()).get();
	    Country country = countryRepository.findByName(dto.getCountry()).get();
		
//		String cityName = dto.getCity();
//		 City city = cityRepository.findByName(cityName)
//		            .orElseThrow(() -> new ResourceNotFoundException("City not found: " + cityName));
//		 
//		 String areaName = dto.getArea();
//		 Area area = areaRepository.findByName(areaName)
//		            .orElseThrow(() -> new ResourceNotFoundException("Area not found: " + areaName));
//		 
//		 String stateName = dto.getState();
//		 State state = stateRepository.findByName(stateName)
//		            .orElseThrow(() -> new ResourceNotFoundException("State not found: " + stateName));
//		 
//		 String countryName = dto.getCountry();
//		 Country country = countryRepository.findByName(countryName)
//		            .orElseThrow(() -> new ResourceNotFoundException("Country not found: " + countryName));
//		 
		 
		 String cleanedName = dto.getName().trim().toLowerCase();

		 boolean exists = propertyRepository.existsByName(cleanedName);

		 if (exists) {
		     throw new DuplicateResourceException("Property with this name already exists.");
		 }



		 
		 
		 Property property = new Property();
		    property.setName(dto.getName());
		    property.setNumberOfBathrooms(dto.getNumberOfBathrooms());
		    property.setNumberOfBeds(dto.getNumberOfBeds());
		    property.setNumberOfRooms(dto.getNumberOfRooms());
		    property.setNumberOfGuestAllowed(dto.getNumberOfGuestAllowed());
		    property.setPropertyOwnerLanguage(dto.getPropertyOwnerLanguage());
		    property.setArea(area);
		    property.setCity(city);
		    property.setState(state);
		    property.setCountry(country);
		    
		    Property savedProperty = propertyRepository.save(property);
		    
		    
		 // Save Rooms
		    List<RoomsDto> savedRoomDtos = new ArrayList<>();
		    for(RoomsDto roomsDto : dto.getRooms()) {
		        Rooms rooms = new Rooms();
		        rooms.setProperty(savedProperty);
		        rooms.setRoomType(roomsDto.getRoomType());
		        rooms.setBasePrice(roomsDto.getBasePrice());
		        Rooms savedRoom = roomRepository.save(rooms);
		        
		     // Initialize availability for this room
		        initializeRoomAvailability(savedRoom, property.getNumberOfRooms()); // Add roomCount to RoomsDto

		        RoomsDto rDto = new RoomsDto();
		        rDto.setId(savedRoom.getId());
		        rDto.setRoomType(savedRoom.getRoomType());
		        rDto.setBasePrice(savedRoom.getBasePrice());
		        
		        savedRoomDtos.add(rDto);
		    }
		    
		    
//		    EmailRequest emailRequest = new EmailRequest("jayantatechnical28@gmail.com","Property Added","Your Property Details are now Live");
//		    kafkaTemplate.send(AppConstants.TOPIC, emailRequest);

		    // Upload files to S3
//		    List<String> fileUrls = s3Service.uploadFiles(files);
//
//		    for(String url : fileUrls) {
//		    	PropertyPhotos photos = new PropertyPhotos();
//		    	photos.setUrl(url);
//		    	photos.setProperty(savedProperty);
//		    	propertyPhotosRepository.save(photos);
//		    }
		    
		    
		    // Prepare response DTO
		    PropertyDto propertyDto = new PropertyDto();
		    propertyDto.setId(savedProperty.getId());
		    propertyDto.setName(savedProperty.getName());
		    propertyDto.setNumberOfBeds(savedProperty.getNumberOfBeds());
		    propertyDto.setNumberOfRooms(savedProperty.getNumberOfRooms());
		    propertyDto.setNumberOfBathrooms(savedProperty.getNumberOfBathrooms());
		    propertyDto.setNumberOfGuestAllowed(savedProperty.getNumberOfGuestAllowed());
		    propertyDto.setPropertyOwnerLanguage(savedProperty.getPropertyOwnerLanguage());
		    propertyDto.setArea(area.getName());
		    propertyDto.setCity(city.getName());
		    propertyDto.setState(state.getName());
		    propertyDto.setCountry(country.getName());
		    propertyDto.setRooms(savedRoomDtos);
		    //propertyDto.setImageUrls(fileUrls);
		    
		    
		return propertyDto;
	}
	
	
	
	private void initializeRoomAvailability(Rooms room, int roomCount) {
	    for (int i = 0; i < 5; i++) {
	        LocalDate date = LocalDate.now().plusDays(i);
	        RoomAvailability availability = new RoomAvailability();
	        availability.setAvailableDate(date);
	        availability.setAvailableCount(roomCount);
	        availability.setPrice(room.getBasePrice());
	        availability.setRoom(room);
	        availabilityRepository.save(availability);
	    }
	}

	
//	@Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
//	@Transactional
//	public void maintainAvailabilityCalendar() {
//		try {
//			
//	        log.info("Starting availability calendar maintenance");
//	        
//	        // 1. Get all rooms
//	        List<Rooms> allRooms = roomRepository.findAll();
//	        
//	        // 2. For each room, update availability for the new day
//	        allRooms.forEach(room -> {
//	            // Delete yesterday's record (optional)
//	        	availabilityRepository.deleteByRoomAndAvailableDate(room, LocalDate.now().minusDays(1));
//	            
//	            // Check if today's record exists
//	            Optional<RoomAvailability> todayAvailability = 
//	            		availabilityRepository.findByRoomAndAvailableDate(room, LocalDate.now());
//	            
//		    
//	            if (!todayAvailability.isPresent()) {
//	            	RoomAvailability roomAvailability = todayAvailability.get();
//	                // Create new availability for today
//	                RoomAvailability availability = new RoomAvailability();
//	                availability.setAvailableDate(LocalDate.now());
//	                availability.setAvailableCount(roomAvailability.getAvailableCount()); // Or your default count
//	                availability.setPrice(room.getBasePrice());
//	                availability.setRoom(room);
//	                availabilityRepository.save(availability);
//	            }
//	        });
//		    
//		 log.info("Completed availability calendar maintenance");
//		 
//	    } catch (Exception e) {
//	        log.error("Error maintaining availability calendar", e);
//	        throw e;
//	    }
//	}
	
	
	
	@Scheduled(cron = "0 * * * * ?") // Runs daily at midnight
	@Transactional
	public void maintainAvailabilityCalendar() {
	    try {
	        log.info("Starting availability calendar maintenance");

	        List<Rooms> allRooms = roomRepository.findAll();

	        for (Rooms room : allRooms) {
	            LocalDate yesterday = LocalDate.now().minusDays(1);
	            LocalDate today = LocalDate.now();

	         // Delete all records before today
	            availabilityRepository.deletePastAvailabilities(room, today);

//	            // 2. Check if today's record exists
//	            Optional<RoomAvailability> todayAvailability =
//	                availabilityRepository.findByRoomAndAvailableDate(room, today);
//
//	            if (!todayAvailability.isPresent()) {
//	                // Option A: Get yesterday's availability to carry forward count
//	                Optional<RoomAvailability> yesterdayAvailability =
//	                    availabilityRepository.findByRoomAndAvailableDate(room, yesterday);
//
//	                int availableCount = yesterdayAvailability
//	                        .map(RoomAvailability::getAvailableCount)
//	                        .orElse(room.getProperty().getNumberOfRooms()); // default value if yesterday not found
//
//	                RoomAvailability newAvailability = new RoomAvailability();
//	                newAvailability.setAvailableDate(today);
//	                newAvailability.setAvailableCount(availableCount);
//	                newAvailability.setPrice(room.getBasePrice());
//	                newAvailability.setRoom(room);
//
//	                availabilityRepository.save(newAvailability);
//	                log.info("Created availability for room {} on {}", room.getId(), today);
//	            }
	        }

	        log.info("Completed availability calendar maintenance");

	    } catch (Exception e) {
	        log.error("Error maintaining availability calendar", e);
	        throw e;
	    }
	}

	
	
	
	
	public APIResponse<List<Property>> searchProperty(String locationName, LocalDate date) {
	    List<Property> properties = propertyRepository.searchProperty(locationName, date);

	    APIResponse<List<Property>> response = new APIResponse<>();
	    response.setMessage("Search result");
	    response.setStatus(200);
	    response.setData(properties);

	    return response;
	}
	
	public APIResponse<List<Property>> searchPropertyWithAvailability(String name, LocalDate checkIn, LocalDate checkOut) {
		
		if (checkIn.isAfter(checkOut)) {
		       throw new IllegalArgumentException("Check-in date must be before check-out date");
		}
		
	    List<Property> properties = propertyRepository.searchPropertyWithAvailability(name, checkIn, checkOut); 
	    
	    if (properties.isEmpty()) {
	        throw new ResourceNotFoundException("No properties available for the given location and date range");
	    }

	    APIResponse<List<Property>> response = new APIResponse<>();
	    response.setMessage("Search result");
	    response.setStatus(200);
	    response.setData(properties);

	    return response;
	}

	
	
	public APIResponse<PropertyDto> findPropertyById(long id){
		APIResponse<PropertyDto> response = new APIResponse<>();
		PropertyDto dto  = new PropertyDto();
		Optional<Property> opProp = propertyRepository.findById(id);
		if(opProp.isPresent()) {
			Property property = opProp.get();
			dto.setArea(property.getArea().getName());
			dto.setCity(property.getCity().getName());
			dto.setState(property.getState().getName());
			dto.setCountry(property.getCountry().getName());
			
			List<Rooms> rooms = property.getRooms();
			
			List<RoomsDto> roomsDto = new ArrayList<>();
			
			for(Rooms room:rooms) {
				RoomsDto roomDto = new RoomsDto();
				BeanUtils.copyProperties(room, roomDto);
				roomsDto.add(roomDto);
			}
			
			dto.setRooms(roomsDto);
			BeanUtils.copyProperties(property, dto);
			
			response.setMessage("Matching Record");
			response.setStatus(200);
			response.setData(dto);
			return response;
		}
		
		return null;
	}

	public List<RoomAvailability> getTotalRoomsAvailable(long id) {
		return availabilityRepository.findByRoomId(id);
		
	}
	
	
	public Rooms getRoomById(long id) {
		return roomRepository.findById(id).get();
	}


}

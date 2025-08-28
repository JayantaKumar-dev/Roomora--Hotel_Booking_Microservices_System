package com.bookingservice.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookingservice.client.PaymentClient;
import com.bookingservice.client.PropertyClient;
import com.bookingservice.dto.APIResponse;
import com.bookingservice.dto.BookingDto;
import com.bookingservice.dto.ProductRequest;
import com.bookingservice.dto.PropertyDto;
import com.bookingservice.dto.RefundRequest;
import com.bookingservice.dto.RefundResponse;
import com.bookingservice.dto.RoomAvailability;
import com.bookingservice.dto.RoomAvailabilityUpdateDto;
import com.bookingservice.dto.Rooms;
import com.bookingservice.dto.StripeResponse;
import com.bookingservice.entity.BookingDate;
import com.bookingservice.entity.Bookings;
import com.bookingservice.exception.ResourceNotFoundException;
import com.bookingservice.repository.BookingDateRepository;
import com.bookingservice.repository.BookingRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;
import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import com.stripe.model.checkout.Session;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {
	
	@Value("${stripe.webhook.secret}")
    private String endpointSecret; // Add this in your config
	
	@Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void initStripe() {
        Stripe.apiKey = stripeSecretKey;
    }

	@Autowired
	private PropertyClient propertyClient;
	
	@Autowired
	private PaymentClient paymentClient;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private BookingDateRepository bookingDateRepository;
	
	
	@PostMapping("/add-to-cart")
	public APIResponse<String> cart(@RequestBody BookingDto bookingDto) {
		
		// ✅ Validate dates
	    LocalDate today = LocalDate.now();
	    LocalDate checkInDate = bookingDto.getDate().get(0);
	    LocalDate checkOutDate = bookingDto.getDate().get(bookingDto.getDate().size() - 1);

	    if (checkInDate.isBefore(today)) {
	        APIResponse<String> response = new APIResponse<>();
	        response.setMessage("❌ Cannot book for past dates");
	        response.setStatus(400);
	        response.setData(null);
	        return response;
	    }
	    
	    if (!checkOutDate.isAfter(checkInDate)) {
	        APIResponse<String> response = new APIResponse<>();
	        response.setMessage("❌ Check-out date must be after check-in date");
	        response.setStatus(400);
	        response.setData(null);
	        return response;
	    }
		
	    // Check availability via Feign
	    boolean available = propertyClient.isRoomAvailable(bookingDto.getRoomId(), bookingDto.getDate());
	    if (!available) {
	    	APIResponse<String> response = new APIResponse<>();
	    	response.setMessage("Room is not available for selected dates");
	    	response.setStatus(404);
	    	response.setData(null);
	        return response;
	    }
	    
	 //  Calculate total nights
	    long nights = ChronoUnit.DAYS.between(
	    	checkInDate, 
	    	checkOutDate
	    );
	    int totalNights = (int) nights;
	    
//	    if (nights <= 0) {
//	        throw new IllegalArgumentException("Check-out date must be after check-in date");
//	    }
	    
	    APIResponse<Rooms> roomResponse = propertyClient.getRoomType(bookingDto.getRoomId());
	    Rooms room = roomResponse.getData();
//	    APIResponse<Property> propertyResponse = propertyClient.getPropertyById(bookingDto.getPropertyId());
//	    Property propertyResponse = roomResponse.getData();
	    
	    double totalPrice = room.getBasePrice() * totalNights;
	    
	 // 4. Create Stripe payment session
	    ProductRequest paymentRequest = new ProductRequest();
	    paymentRequest.setName(room.getRoomType());
	    paymentRequest.setAmount((long) (totalPrice * 100)); // Stripe needs amount in paise
	    paymentRequest.setCurrency("INR");
	    paymentRequest.setQuantity(1);
	    
	    StripeResponse stripeResponse = paymentClient.createPaymentSession(paymentRequest);

	    
	    
	    // 2. Save booking
	    Bookings booking = new Bookings();
	    booking.setName(bookingDto.getName());
	    booking.setEmail(bookingDto.getEmail());
	    booking.setMobile(bookingDto.getMobile());
	    booking.setPropertyId(bookingDto.getPropertyId());
	    booking.setRoomId(bookingDto.getRoomId());
	    booking.setCheckInDate(bookingDto.getDate().get(0));
	    booking.setCheckOutDate(bookingDto.getDate().get(bookingDto.getDate().size() - 1));
	    booking.setTotalNights(totalNights);
	    booking.setTotalPrice(totalPrice);
	    booking.setPaymentStatus("PENDING");
	    booking.setTransactionId(stripeResponse.getSessionId());
	    // booking.setPaymentIntentId(stripeResponse.getPaymentIntentId());
	    booking.setStatus("IN_PROGRESS");
	    booking = bookingRepository.save(booking);

	    // 3. Save BookingDates
	    for (LocalDate date : bookingDto.getDate()) {
	        BookingDate bd = new BookingDate();
	        bd.setBooking(booking);
	        bd.setDate(date);
	        bookingDateRepository.save(bd);
	    }

	    // 4. Decrease room availability
//	    RoomAvailabilityUpdateDto updateDto = new RoomAvailabilityUpdateDto();
//	    updateDto.setRoomId(bookingDto.getRoomId());
//	    updateDto.setDates(bookingDto.getDate());
//	    updateDto.setDecreaseBy(1); // 1 room reserved
//	    propertyClient.updateAvailability(updateDto);

	    
	 // Response
	    APIResponse<String> response = new APIResponse<>();
	    response.setStatus(200);
	    response.setMessage("Booking initiated, complete payment to confirm");
	    response.setData(stripeResponse.getSessionUrl());
    	
        return response;
	    
	}
	
	
	
	
	@PostMapping("/complete-booking")
	public APIResponse<String> handleStripeEvent(HttpServletRequest request) {
	    try {
	        System.out.println("🎯 Webhook called!");
	        String payload;
	        try (Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A")) {
	            payload = s.hasNext() ? s.next() : "";
	        }

	        String sigHeader = request.getHeader("Stripe-Signature");
	        Event event;
	        try {
	            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
	        } catch (SignatureVerificationException e) {
	        	APIResponse<String> response = new APIResponse<>();
	            response.setStatus(400);
	            response.setMessage("❌ Invalid signature");
	            response.setData(null);
	            return response;
	        }

	        System.out.println("🔍 Received Event Type: " + event.getType());

	        if ("checkout.session.completed".equalsIgnoreCase(event.getType())) {
	            System.out.println("✅ Event matched: Processing booking...");

	            // Parse raw JSON manually
	            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
	            String rawJson = dataObjectDeserializer.getRawJson();
	            System.out.println("🧾 Raw JSON: " + rawJson);

	            if (rawJson != null) {
	                ObjectMapper mapper = new ObjectMapper();
	                JsonNode jsonNode = mapper.readTree(rawJson);
	                String sessionId = jsonNode.get("id").asText();
	                System.out.println("🆔 Extracted Session ID: " + sessionId);

	                // Retrieve full session from Stripe using API key
	                Stripe.apiKey = stripeSecretKey; // must have this
	                Session session = Session.retrieve(sessionId);
	                String paymentIntentId = session.getPaymentIntent();

	                Optional<Bookings> bookingOpt = bookingRepository.findByTransactionId(sessionId);
	                System.out.println("🗃 Booking found? " + bookingOpt.isPresent());

	                if (bookingOpt.isPresent()) {
	                    Bookings booking = bookingOpt.get();
	                    booking.setPaymentStatus("PAID");
	                    booking.setStatus("CONFIRMED");
	                    booking.setPaymentIntentId(paymentIntentId); // ✅ set here
	                    bookingRepository.save(booking);

	                    List<LocalDate> dates = bookingDateRepository.findDatesByBookingId(booking.getId());
	                    RoomAvailabilityUpdateDto dto = new RoomAvailabilityUpdateDto();
	                    dto.setRoomId(booking.getRoomId());
	                    dto.setDates(dates);
	                    dto.setDecreaseBy(1);
	                    propertyClient.updateAvailability(dto);

	                    System.out.println("✅ Booking updated to PAID & CONFIRMED");
	                    APIResponse<String> response = new APIResponse<>();
                        response.setStatus(200);
                        response.setMessage("✅ Booking confirmed");
                        response.setData(null);
                        return response;
	                } else {
	                    System.out.println("❌ Booking not found for session ID: " + sessionId);
	                    APIResponse<String> response = new APIResponse<>();
                        response.setStatus(404);
                        response.setMessage("❌ Booking not found for session ID");
                        response.setData(null);
                        return response;
	                }
	            } else {
	                System.out.println("⚠️ No raw JSON in event data");
	            }
	        } else {
	            System.out.println("ℹ️ Webhook received but event type ignored");
	        }

	        APIResponse<String> response = new APIResponse<>();
	        response.setStatus(200);
	        response.setMessage("ℹ️ Webhook received but event type ignored");
	        response.setData(null);
	        return response;

	    } catch (Exception e) {
	        e.printStackTrace();
	        APIResponse<String> response = new APIResponse<>();
	        response.setStatus(500);
	        response.setMessage("❌ Error handling webhook: " + e.getMessage());
	        response.setData(null);
	        return response;
	    }
	}

	
	
	@PutMapping("/cancel/{bookingId}")
	public APIResponse<String> cancelBooking(
	        @PathVariable Long bookingId ) {
		
		int freeCancellationHours=24;

	    Bookings booking = bookingRepository.findById(bookingId)
	            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

	    if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
	  
	        APIResponse<String> response = new APIResponse<>();
	        response.setStatus(400);
	        response.setMessage("❌ Booking is not in a cancellable state");
	        response.setData(null);
	        return response;
	    }
	    
	    LocalDateTime now = LocalDateTime.now();
	    LocalDateTime cancellationDeadline = booking.getCreatedAt().plusHours(freeCancellationHours);
	    boolean isBeforeCheckIn = LocalDate.now().isBefore(booking.getCheckInDate());

	    // Check if current date is after check-in date
	    if (!isBeforeCheckIn) {
	        
	        APIResponse<String> response = new APIResponse<>();
	        response.setStatus(400);
	        response.setMessage("❌ Cannot cancel booking after check-in date. Please contact customer support.");
	        response.setData(null);
	        return response;
	    }



	    // 1. Mark booking as CANCELLED
	    booking.setStatus("CANCELLED");
	    booking.setUpdatedAt(now);

	    // 2. Mark payment as REFUND_PENDING if already paid
//	    if ("PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
//	        booking.setPaymentStatus("REFUND_PENDING");
//	    }
//
//	    bookingRepository.save(booking);
	    
	    
	    // If payment was already done, mark refund status
        if ("PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
            if (now.isBefore(cancellationDeadline)) {
            	// Within 24 hours - ✅ instant refund
                try {
                	// Here you would call your payment service to process instant refund
                	//paymentClient.refundPayment(booking.getTransactionId());
                	RefundResponse refundResponse = paymentClient.refundPayment(new RefundRequest(booking.getPaymentIntentId(), booking.getId(), "requested_by_customer"));
                	
                	if (refundResponse != null && refundResponse.isSuccess()) {
                	    booking.setPaymentStatus("REFUNDED");
                	    booking.setRefundTransactionId(refundResponse.getRefundId());
                	    bookingRepository.save(booking);
                	    increaseAvailability(booking);

                	    APIResponse<String> response = new APIResponse<>();
                	    response.setStatus(200);
                	    response.setMessage("✅ Booking cancelled successfully. Refund processed successfully.");
                	    response.setData(null);
                	    return response;
                	} else {
                	    APIResponse<String> response = new APIResponse<>();
                	    response.setStatus(500);
                	    response.setMessage("❌ Refund failed: " +
                	        (refundResponse != null ? refundResponse.getMessage() : "No response from payment service"));
                	    response.setData(null);
                	    return response;
                	}
                    
                    
                } catch (Exception e) {
                    
                    APIResponse<String> response = new APIResponse<>();
        	        response.setStatus(500);
        	        response.setMessage("❌ Refund failed: " + e.getMessage());
        	        response.setData(null);
        	        return response;
                }


            } else {
                // ⏳ Refund pending approval by admin
            	// After 24 hours but before check-in - admin-mediated refund
                booking.setPaymentStatus("REFUND_PENDING");
                // Here you might want to trigger a notification to admin/property owner
                bookingRepository.save(booking);
                increaseAvailability(booking);

                APIResponse<String> response = new APIResponse<>();
    	        response.setStatus(200);
    	        response.setMessage("""
    		            ✅ Booking cancelled successfully. Refund requires approval from property owner.
    		            Our team will contact you regarding your refund within 3 business days.
    		            """);
    	        response.setData(null);
    	        return response;
            }
        }
	    

        bookingRepository.save(booking);
        increaseAvailability(booking);

	    APIResponse<String> response = new APIResponse<>();
        response.setStatus(200);
        response.setMessage("✅ Booking cancelled successfully. No payment was made, so no refund needed.");
        response.setData(null);
        return response;
	}
	
	
	
	@PutMapping("/refund/{bookingId}")
    public APIResponse<String> processRefund(@PathVariable Long bookingId) {
        Bookings booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!"REFUND_PENDING".equalsIgnoreCase(booking.getPaymentStatus())) {
            
            APIResponse<String> response = new APIResponse<>();
            response.setStatus(400);
            response.setMessage("❌ Refund not applicable for this booking");
            response.setData(null);
            return response;
        }
        
        if ("REFUNDED".equalsIgnoreCase(booking.getPaymentStatus())) {
            APIResponse<String> response = new APIResponse<>();
            response.setStatus(400);
            response.setMessage("⚠️ Refund already processed for this booking");
            response.setData(null);
            return response;
        }


        try {
        	// Call Payment Gateway (Stripe, etc.)
        	//paymentClient.refundPayment(booking.getTransactionId());
        	RefundResponse refundResponse = paymentClient.refundPayment(new RefundRequest(booking.getPaymentIntentId(), booking.getId(), "requested_by_customer"));
        	
        	if (refundResponse != null && refundResponse.isSuccess()) {
        	    booking.setPaymentStatus("REFUNDED");
        	    booking.setRefundTransactionId(refundResponse.getRefundId());
        	    bookingRepository.save(booking);

        	    APIResponse<String> response = new APIResponse<>();
        	    response.setStatus(200);
        	    response.setMessage("✅ Refund processed successfully for Booking ID: " + booking.getId());
        	    response.setData(null);
        	    return response;
        	} else {
        	    APIResponse<String> response = new APIResponse<>();
        	    response.setStatus(500);
        	    response.setMessage("❌ Refund failed: " +
        	        (refundResponse != null ? refundResponse.getMessage() : "No response from payment service"));
        	    response.setData(null);
        	    return response;
        	}

        } catch (Exception e) {
            
            APIResponse<String> response = new APIResponse<>();
            response.setStatus(500);
            response.setMessage("❌ Stripe refund failed: " + e.getMessage());
            response.setData(null);
            return response;
        }
    }
	
	

	
    // Helper to increase room availability
    private void increaseAvailability(Bookings booking) {
        RoomAvailabilityUpdateDto updateDto = new RoomAvailabilityUpdateDto();
        updateDto.setRoomId(booking.getRoomId());
        updateDto.setDates(booking.getBookedDates().stream()
                .map(BookingDate::getDate)
                .collect(Collectors.toList()));
        updateDto.setDecreaseBy(-1); // Increase availability by 1
        propertyClient.updateAvailability(updateDto);
    }


	
	
}
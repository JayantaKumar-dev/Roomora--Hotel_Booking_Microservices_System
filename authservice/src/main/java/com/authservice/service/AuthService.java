package com.authservice.service;

//import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

import com.authservice.payload.APIResponse;
import com.authservice.payload.EmailRequest;
import com.authservice.constants.AppConstants;
import com.authservice.entity.User;
import com.authservice.repository.UserRepository;
import com.authservice.payload.UserDto;


@Service
public class AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private KafkaTemplate<String, EmailRequest> kafkaTemplate;

	
	
	private static final String EMAIL_REGEX =
	        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
	private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
	
	

	public APIResponse<String> register(UserDto userDto){
		
		//API Response Object
		APIResponse<String> response = new APIResponse<>();
		
		//Check whether username exists
		
		if(userRepository.existsByUsername(userDto.getUsername())) {
			response.setMessage("Registration Failed");
			response.setStatus(500);
			response.setData("User with username already exists");
			return response;
		}
		//Check whether email exists
		
		if(userRepository.existsByEmail(userDto.getEmail())) {
			response.setMessage("Registration Failed");
			response.setStatus(500);
			response.setData("User with Email Id exists");
			return response;
		}
		
		
		//Validate email format
		if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches()) {
		    response.setMessage("Registration Failed");
		    response.setStatus(400);
		    response.setData("Invalid Email Format");
		    return response;
		}


		
		
		//Encode the password before saving that to the database
		
		String encryptedPassword = passwordEncoder.encode(userDto.getPassword());
		
		User user = new User();
		user.setName(userDto.getName());
		user.setUsername(userDto.getUsername());
		user.setEmail(userDto.getEmail());
		user.setPassword(encryptedPassword);
		
		if ("GUEST".equalsIgnoreCase(userDto.getUserRole())) {
			user.setRole("ROLE_GUEST");
		}else if("OWNER".equalsIgnoreCase(userDto.getUserRole())){
			user.setRole("ROLE_OWNER");
		}else throw new IllegalArgumentException("User role not valid with: " + userDto.getUserRole());
		//user.setRole("ROLE_USER");
		
		User savedUser = userRepository.save(user);
		
		if(savedUser==null) {
			//Custom Exception
			response.setMessage("Registration Failed");
	        response.setStatus(500);
	        response.setData("Could not save user");
	        return response;
		}
		//Finally save the user and return response as APIResponse
		
		
		// ✅ Send registration success email via Kafka
	    EmailRequest emailRequest = new EmailRequest(
	            savedUser.getEmail(),
	            "Welcome to Roomora - Registration Successful 🎉",
	            "Dear " + savedUser.getName() + ",\n\n" +
	            "We are delighted to inform you that your registration with Roomora has been successfully completed.\n\n" +
	            "You can now log in using your registered email and start exploring our services.\n\n" +
	            "📌 Next Steps:\n" +
	            "- Complete your profile for better recommendations\n" +
	            "- Explore variours Hotels and properties\n" +
	            "- Stay tuned for exciting updates\n\n" +
	            "If you face any issues, feel free to reach us at support@roomora.com.\n\n" +
	            "Best Regards,\n" +
	            "Roomora Team"
	    );

	    kafkaTemplate.send(AppConstants.TOPIC, emailRequest);

	    
	    //Finally save the user and return response as APIResponse
	    response.setMessage("Registration Done");
	    response.setStatus(201);
	    response.setData("User is registered successfully");

		return response;
	}
	
}

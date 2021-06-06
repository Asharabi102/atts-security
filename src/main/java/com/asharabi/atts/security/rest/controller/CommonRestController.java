package com.asharabi.atts.security.rest.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.asharabi.atts.security.api.dto.AuthenticationRequest;
import com.asharabi.atts.security.api.dto.AuthenticationResponse;
import com.asharabi.atts.security.api.dto.UserDTO;
import com.asharabi.atts.security.exceptions.UserNotFoundException;
import com.asharabi.atts.security.jwt.JwtUtil;
import com.asharabi.atts.security.model.User;
import com.asharabi.atts.security.service.CustomUserDetailsService;
import com.asharabi.atts.security.service.UserService;

import net.bytebuddy.utility.RandomString;

@RestController
public class CommonRestController {

	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private DozerBeanMapper mapper;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private UserService userService;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@GetMapping(value = "/home")
	public UserDTO viewHome(@RequestBody UserDTO userdto) {
		User user = userDetailsService.addUser(userdto);
		userdto = mapper.map(user, UserDTO.class);
		return userdto;
	}

	@GetMapping(value = "/login")
	public UserDTO viewLogin(@RequestBody UserDTO userdto) {
		User user = userDetailsService.addUser(userdto);
		userdto = mapper.map(user, UserDTO.class);
		return userdto;
	}

	@PostMapping(value = "/login")
	public AuthenticationResponse login(@RequestBody AuthenticationRequest req) {
		try {
			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
		} catch (AuthenticationException e) {
			log.error("Bad credentials: Incorrect username or password");
			throw new BadCredentialsException("Incorrect username or password");
		}
		UserDetails userDetails = userDetailsService.loadUserByUsername(req.getUsername());
		final String jwt = jwtTokenUtil.generateToken(userDetails);
		return new AuthenticationResponse(jwt);
	}

	@PostMapping(value = "/register")
	public UserDTO registration(@RequestBody UserDTO userdto) {
		User user = userDetailsService.addUser(userdto);
		userdto = mapper.map(user, UserDTO.class);
		return userdto;
	}

	@PostMapping(value = "/forgot")
	public Map<String, String> sendForgot(@RequestBody UserDTO dto) {
		String email = dto.getEmail();
		String token = RandomString.make(30);
		Map<String, String> map = new HashMap<>();
		try {
			userService.updateResetPasswordToken(token, email);
			String resetPasswordLink = dto.getPortalURL() + "/reset_password?token=" + token;
			sendEmail(email, resetPasswordLink);
			map.put("success", "We have sent a reset password link to your email. Please check.");
		} catch (UserNotFoundException ex) {
			map.put("error", ex.getMessage());
		} catch (Exception e) {
			map.put("error", "Error while sending email: " + e.getMessage());
		}
		return map;
	}

	@PostMapping(value = "/resetPasswordCheck")
	public boolean resetPasswordCheck(@RequestBody String token) {
		User user = userService.getByResetPasswordToken(token);
		return user != null;
	}

	@PostMapping(value = "/resetPassword2")
	public boolean resetPassword2(@RequestBody UserDTO userDTO) {
		boolean updated = false;
		User user = userService.getByResetPasswordToken(userDTO.getResetPasswordToken());
		if (user != null) {
			User updatePassword = userService.updatePassword(user, userDTO.getPassword());
			if (updatePassword.getPassword().equals(bCryptPasswordEncoder.encode(userDTO.getPassword())))
				updated = true;
		}
		return updated;
	}
	
	@PostMapping(value = "/resetPassword")
	public boolean resetPassword(@RequestBody UserDTO userDTO) {
		boolean updated = false;
		User user = userService.findByEmail(userDTO.getEmail());
		if (user != null) {
			String oldPassword = user.getPassword();
			User updatePassword = userService.updatePassword(user, userDTO.getPassword());
			if (!updatePassword.getPassword().equals(oldPassword))
				updated = true;
		}
		return updated;
	}

	public void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom("contact@asharabi-atts.com", "ATTS Support");
		helper.setTo(recipientEmail);
		String subject = "Here's the link to reset your password";
		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + link
				+ "\">Change my password</a></p>" + "<br>" + "<p>Ignore this email if you do remember your password, "
				+ "or you have not made the request.</p>";
		helper.setSubject(subject);
		helper.setText(content, true);
		mailSender.send(message);
	}
}

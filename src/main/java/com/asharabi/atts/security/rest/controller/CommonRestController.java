package com.asharabi.atts.security.rest.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.asharabi.atts.security.exceptions.UserNotFoundException;
import com.asharabi.atts.security.jwt.JwtUtil;
import com.asharabi.atts.security.model.User;
import com.asharabi.atts.security.model.dto.AuthenticationRequest;
import com.asharabi.atts.security.model.dto.AuthenticationResponse;
import com.asharabi.atts.security.model.dto.UserDTO;
import com.asharabi.atts.security.service.CustomUserDetailsService;
import com.asharabi.atts.security.service.UserService;
import com.asharabi.atts.security.util.WebHelper;

import net.bytebuddy.utility.RandomString;

@RestController
public class CommonRestController {

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
	public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest req) {
		try {
			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
		} catch (BadCredentialsException e) {
			throw new BadCredentialsException("Incorrect username or password", e);
		}
		UserDetails userDetails = userDetailsService.loadUserByUsername(req.getUsername());
		final String jwt = jwtTokenUtil.generateToken(userDetails);
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}

	@PostMapping(value = "/register")
	public UserDTO registration(@RequestBody UserDTO userdto) {
		User user = userDetailsService.addUser(userdto);
		userdto = mapper.map(user, UserDTO.class);
		return userdto;
	}

	// TODO reset password

	@GetMapping(value = "/forgot")
	public String forgot() {
		return "forgot_password_form";
	}

	@PostMapping(value = "/forgot")
	public String sendForgot(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");
		String token = RandomString.make(30);
		try {
			userService.updateResetPasswordToken(token, email);
			String resetPasswordLink = WebHelper.getSiteURL(request) + "/reset_password?token=" + token;
			sendEmail(email, resetPasswordLink);
			model.addAttribute("message", "We have sent a reset password link to your email. Please check.");

		} catch (UserNotFoundException ex) {
			model.addAttribute("error", ex.getMessage());
		} catch (Exception e) {
			model.addAttribute("error", "Error while sending email: " + e.getMessage());
		}
		return "forgot_password_form";
	}

	@GetMapping(value = "/resetPassword")
	public String resetPassword(@Param(value = "token") String token, Model model) {
		User user = userService.getByResetPasswordToken(token);
		model.addAttribute("token", token);
		if (user == null) {
			model.addAttribute("message", "Invalid Token");
			return "message";
		}
		return "reset_password_form";
	}

	@PostMapping(value = "/resetPassword")
	public String resetPassword(HttpServletRequest request, Model model) {
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		User user = userService.getByResetPasswordToken(token);
		model.addAttribute("title", "Reset your password");
		if (user == null) {
			model.addAttribute("message", "Invalid Token");
			return "message";
		} else {
			userService.updatePassword(user, password);
			model.addAttribute("message", "You have successfully changed your password.");
		}
		return "message";
	}

	public void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom("contact@shopme.com", "Shopme Support");
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

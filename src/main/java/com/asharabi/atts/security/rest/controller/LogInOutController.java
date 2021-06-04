package com.asharabi.atts.security.rest.controller;

import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.asharabi.atts.security.jwt.JwtUtil;
import com.asharabi.atts.security.model.User;
import com.asharabi.atts.security.model.dto.AuthenticationRequest;
import com.asharabi.atts.security.model.dto.AuthenticationResponse;
import com.asharabi.atts.security.model.dto.UserDTO;
import com.asharabi.atts.security.service.CustomUserDetailsService;

@RestController
public class LogInOutController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private DozerBeanMapper mapper;

	private final Logger log = LoggerFactory.getLogger(LogInOutController.class);

	@PostMapping(value = "/register")
	public UserDTO registration(@RequestBody UserDTO userdto) {
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
	
	//TODO reset password 

}

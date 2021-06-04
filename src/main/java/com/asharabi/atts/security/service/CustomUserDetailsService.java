package com.asharabi.atts.security.service;

import java.util.Optional;

import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.asharabi.atts.security.exceptions.AlreadyExistsException;
import com.asharabi.atts.security.model.CustomUserDetails;
import com.asharabi.atts.security.model.User;
import com.asharabi.atts.security.model.dto.UserDTO;
import com.asharabi.atts.security.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DozerBeanMapper dozerBeanMapper;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> userOptional = userRepository.findByEmail(email);
		User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));
		return new CustomUserDetails(user);

	}

	public User addUser(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public User findByEmail(String email) {
		Optional<User> userOptional = userRepository.findByEmail(email);
		if (userOptional.isPresent()) {
			return userOptional.get();
		}
		return null;
	}

	public User addUser(UserDTO userDTO) {
		String email = userDTO.getEmail();
		Optional<User> userOptional = userRepository.findByEmail(email);
		userOptional.ifPresent(user -> {
			throw new AlreadyExistsException("Duplicate email: Email already used by another profile");
		});
		User user = dozerBeanMapper.map(userDTO, User.class);
		return addUser(user);
	}

	public CustomUserDetails getCurrentUser() {
		Optional<CustomUserDetails> principal = Optional
				.of((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (principal.isPresent()) {
			return principal.get();
		} else
			throw new IllegalArgumentException("You are not logged in");
	}

}
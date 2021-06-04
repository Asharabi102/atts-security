package com.asharabi.atts.security.service;

import java.util.Optional;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.asharabi.atts.security.exceptions.UserNotFoundException;
import com.asharabi.atts.security.model.User;
import com.asharabi.atts.security.model.dto.UserDTO;
import com.asharabi.atts.security.repository.UserRepository;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	private DozerBeanMapper dozerBeanMapper;

	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	public User addUser(UserDTO userDTO) {
		User user = dozerBeanMapper.map(userDTO, User.class);
		user.setPassword(userDTO.getPassword());
		user = customUserDetailsService.addUser(user);
		return user;
	}

	public User editUser(UserDTO userDTO) {
		int userId = userDTO.getId();
		User user = findById(userId);
		String password = userDTO.getPassword();
		if (password != null) {
			password = password.trim();
			if (!password.isEmpty()) {
				user.setPassword(password);
			}
		}
		user.setEmail(userDTO.getEmail());
		user = customUserDetailsService.addUser(user);
		return user;
	}

	public User update(User user) {
		return userRepository.save(user);
	}

	public User findByEmail(String email) {
		Optional<User> userOptional = userRepository.findByEmail(email);
		if (userOptional.isPresent()) {
			return userOptional.get();
		}
		return null;
	}

	public User findById(long id) {
		Optional<User> userOptional = userRepository.findById(id);
		if (userOptional.isPresent()) {
			return userOptional.get();
		}
		return null;
	}

	public void deleteById(long id) {
		Optional<User> userOptional = userRepository.findById(id);
		if (userOptional.isPresent()) {
			userRepository.delete(userOptional.get());
		}
	}

	public void updateResetPasswordToken(String token, String email) throws UserNotFoundException {
		User user = findByEmail(email);
		if (user != null) {
			user.setResetPasswordToken(token);
			userRepository.save(user);
		} else {
			throw new UserNotFoundException("Could not find any user with the email " + email);
		}
	}

	public User getByResetPasswordToken(String token) {
		Optional<User> userOptional = userRepository.findByResetPasswordToken(token);
		if (userOptional.isPresent()) {
			return userOptional.get();
		}
		return null;
	}

	public void updatePassword(User user, String newPassword) {
		String encodedPassword = bCryptPasswordEncoder.encode(newPassword);
		user.setPassword(encodedPassword);
		user.setResetPasswordToken(null);
		userRepository.save(user);
	}

}
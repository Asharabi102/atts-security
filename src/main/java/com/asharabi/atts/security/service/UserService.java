package com.asharabi.atts.security.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asharabi.atts.security.model.User;
import com.asharabi.atts.security.model.dto.UserDTO;
import com.asharabi.atts.security.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private DozerBeanMapper dozerBeanMapper;

	public User addUser(UserDTO userDTO) {
		User user = dozerBeanMapper.map(userDTO, User.class);
		user.setPassword(userDTO.getPassword());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user = customUserDetailsService.addUser(user);
		return user;
	}

	public User editUser(UserDTO userDTO) {
		int userId = userDTO.getId();
		User user = findById(userId);
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());

		String password = userDTO.getPassword();

		if (password != null) {
			password = password.trim();
			if (password.length() > 0) {
				user.setPassword(password);
			}
		}
		user.setLastName(userDTO.getLastName());
		user.setEmail(userDTO.getEmail());
		user = customUserDetailsService.addUser(user);
		return user;
	}

	public User update(User user) {
		return userRepository.save(user);
	}

	public List<User> getAllUser() {
		return userRepository.findAll();
	}

	public User findByEmail(String email) {
		Optional<User> user = userRepository.findByEmail(email);
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}

	public User updateRoleById(long id) {
		Optional<User> optional = userRepository.findById(id);
		if (optional.isPresent()) {
			User user = optional.get();
			update(user);
			return user;
		}
		return null;
	}

	public User findById(long id) {
		Optional<User> optional = userRepository.findById(id);
		if (optional.isPresent()) {
			User user = optional.get();
			return user;
		}
		return null;
	}

	public void deleteById(long id) {
		Optional<User> optional = userRepository.findById(id);
		if (optional.isPresent()) {
			User user = optional.get();
			userRepository.delete(user);
		}
	}

}
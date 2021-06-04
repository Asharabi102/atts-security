package com.asharabi.atts.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asharabi.atts.security.model.User;


public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByEmail(String email);
}
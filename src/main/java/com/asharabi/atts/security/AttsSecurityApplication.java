package com.asharabi.atts.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AttsSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttsSecurityApplication.class, args);
	}

}

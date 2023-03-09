package com.manager.schoolmateapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//Security is disabled for the start test
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SchoolMateApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolMateApiApplication.class, args);
	}

}

package com.assignment.asm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AsmApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsmApplication.class, args);
	}

}

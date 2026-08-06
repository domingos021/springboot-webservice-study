package com.diniz.springbootstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CursoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CursoApplication.class, args);
	}

}

	/*
	Cliente (Frontend, Mobile, outro sistema)
				  |
				  | HTTP / JSON
				  ↓
		  Spring Boot (Backend)
				  |
				  ↓
		 Camadas da aplicação
				  |
	   ┌──────────┼──────────┐
	   ↓          ↓          ↓
	Controller  Service   Repository
						   |
						   ↓
					  JPA/Hibernate
						   |
						   ↓
					  PostgreSQL
	 */



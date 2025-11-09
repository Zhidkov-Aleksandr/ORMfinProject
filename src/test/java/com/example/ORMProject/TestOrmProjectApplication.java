package com.example.ORMProject;

import org.springframework.boot.SpringApplication;

public class TestOrmProjectApplication {

	public static void main(String[] args) {
		SpringApplication.from(OrmProjectApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

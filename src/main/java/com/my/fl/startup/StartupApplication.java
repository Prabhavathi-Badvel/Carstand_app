package com.my.fl.startup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.my.fl.startup"})
@EnableJpaRepositories(basePackages = "com.my.fl.startup.repo")

public class StartupApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(StartupApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("CarStand api started...");
	}
}

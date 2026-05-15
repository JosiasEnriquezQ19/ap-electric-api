package com.security.movil.mapElectric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MapElectricApplication {

	public static void main(String[] args) {
		SpringApplication.run(MapElectricApplication.class, args);
	}

}

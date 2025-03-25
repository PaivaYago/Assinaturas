package com.globo.assinaturas.Assinaturas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AssinaturasApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssinaturasApplication.class, args);
	}

}

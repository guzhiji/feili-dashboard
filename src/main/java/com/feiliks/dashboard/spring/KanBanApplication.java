package com.feiliks.dashboard.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class KanBanApplication {

	public static void main(String[] args) {
		SpringApplication.run(KanBanApplication.class, args);
	}
}

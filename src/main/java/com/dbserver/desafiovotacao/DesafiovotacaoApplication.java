package com.dbserver.desafiovotacao;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import java.util.TimeZone;

@SpringBootApplication
public class DesafiovotacaoApplication {

	@Value("${application.timezone:GMT-3:00}")
	private String applicationTimeZone;

	public static void main(String[] args) {
		SpringApplication.run(DesafiovotacaoApplication.class, args);
	}

	@PostConstruct
	public void executeAfterMain() {
		TimeZone.setDefault(TimeZone.getTimeZone(applicationTimeZone));
	}
}

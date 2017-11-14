package com.namics.oss.spring.support.i18n.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * Application.
 *
 * @author rgsell, Namics AG
 * @since 14.09.17 13:14
 */
@SpringBootApplication
@ImportResource("applicationContext.xml")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

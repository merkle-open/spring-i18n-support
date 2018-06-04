/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.web.config;

import com.namics.oss.spring.support.i18n.service.MessageManagementService;
import com.namics.oss.spring.support.i18n.service.ReloadService;
import com.namics.oss.spring.support.i18n.web.controller.FileController;
import com.namics.oss.spring.support.i18n.web.controller.MessageSourceDataController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;

/**
 * I18nWebConfig.
 *
 * @author lboesch, Namics AG
 * @since 20.06.14 13:28
 */
@Configuration
@EnableAsync
public class I18nWebConfig extends WebMvcConfigurationSupport {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		super.addViewControllers(registry);
		registry.addViewController("/").setViewName("redirect:list.html");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		super.addResourceHandlers(registry);
		registry.addResourceHandler("/*.html").addResourceLocations("classpath:/META-INF/terrific/assets/");
		registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/META-INF/terrific/assets/font/");
		registry.addResourceHandler("/**/*.html").addResourceLocations("classpath:/META-INF/terrific/");
		registry.addResourceHandler("/**/*.css", "/**/*.js").addResourceLocations("classpath:/META-INF/terrific/");
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Override
	protected void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		super.configureContentNegotiation(configurer);
		configurer.favorPathExtension(false);
	}

	@Bean
	public MessageSourceDataController dataController(MessageManagementService messageManagementService, ReloadService reloadService) {
		return new MessageSourceDataController(messageManagementService, reloadService);
	}

	@Bean
	public FileController fileController(MessageManagementService messageManagementService) {
		return new FileController(messageManagementService);
	}

	@Bean
	public MultipartResolver multipartResolver() {
		return new CommonsMultipartResolver();
	}

}

package com.namics.oss.spring.support.i18n.autoconfigure;

import com.namics.oss.spring.support.i18n.web.config.I18nWebConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.inject.Inject;

import static org.springframework.util.StringUtils.hasText;


/**
 * SpringI18nSupportWebAutoConfiguration.
 *
 * @author rgsell, Namics AG
 * @since 14.09.17 09:19
 */
@Configuration
@ConditionalOnClass(I18nWebConfig.class)
@EnableConfigurationProperties(SpringI18nSupportProperties.class)
public class SpringI18nSupportWebAutoConfiguration {

	protected static final String defaultServletMapping = "/i18n/*";
	protected static final String defaultServletName = "i18nServlet";

	@Inject
	protected SpringI18nSupportProperties springI18nSupportProperties;

	@Bean
	public ServletRegistrationBean i18nSupportServlet() {
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(I18nWebConfig.class);

		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		dispatcherServlet.setApplicationContext(applicationContext);

		ServletRegistrationBean registrationBean = new ServletRegistrationBean(dispatcherServlet, getServletMapping());
		registrationBean.setName(getServletName());
		registrationBean.setLoadOnStartup(1);
		return registrationBean;
	}


	protected String getServletMapping() {
		if (hasText(springI18nSupportProperties.getWeb().getServletMapping())) {
			return springI18nSupportProperties.getWeb().getServletMapping();
		}
		return defaultServletMapping;
	}

	protected String getServletName() {
		if (hasText(springI18nSupportProperties.getWeb().getServletName())) {
			return springI18nSupportProperties.getWeb().getServletName();
		}
		return defaultServletName;
	}

}

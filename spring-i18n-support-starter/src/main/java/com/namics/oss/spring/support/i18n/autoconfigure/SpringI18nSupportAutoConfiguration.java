package com.namics.oss.spring.support.i18n.autoconfigure;

import com.namics.oss.spring.support.i18n.DaoMessageSource;
import com.namics.oss.spring.support.i18n.config.I18nConfigTemplate;
import com.namics.oss.spring.support.i18n.dao.MessageSourceDao;
import com.namics.oss.spring.support.i18n.dao.SimpleJdbcMessageSourceDao;
import com.namics.oss.spring.support.i18n.service.ReloadService;
import com.namics.oss.spring.support.i18n.service.ReloadServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

/**
 * SpringI18nSupportAutoConfiguration.
 *
 * @author rgsell, Namics AG
 * @since 13.09.17 16:21
 */
@Import(I18nConfigTemplate.class)
public class SpringI18nSupportAutoConfiguration {

	@Bean
	public ReloadService messageSourceReloadService() {
		return new ReloadServiceImpl();
	}

	@Bean(name = "messageSource")
	public DaoMessageSource i18nMessageSource(MessageSourceDao messageSourceDao) {
		DaoMessageSource source = new DaoMessageSource();
		source.setMessageSourceDao(messageSourceDao);
		source.setUseCodeAsDefaultMessage(true);
		return source;
	}

	@Bean(name = "jdbcMessageSourceDao")
	public SimpleJdbcMessageSourceDao jdbcMessageSourceDa(DataSource dataSource) {
		SimpleJdbcMessageSourceDao jdbcMessageSourceDao = new SimpleJdbcMessageSourceDao();
		jdbcMessageSourceDao.setDataSource(dataSource);
		return jdbcMessageSourceDao;
	}
}

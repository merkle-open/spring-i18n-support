package com.namics.oss.spring.support.i18n.autoconfigure;

import com.namics.oss.spring.support.i18n.DaoMessageSource;
import com.namics.oss.spring.support.i18n.config.I18nConfigTemplate;
import com.namics.oss.spring.support.i18n.dao.MessageSourceDao;
import com.namics.oss.spring.support.i18n.dao.SimpleJdbcMessageSourceDao;
import com.namics.oss.spring.support.i18n.service.ReloadService;
import com.namics.oss.spring.support.i18n.service.ReloadServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.sql.DataSource;

import static org.springframework.util.StringUtils.hasText;

/**
 * SpringI18nSupportAutoConfiguration.
 *
 * @author rgsell, Namics AG
 * @since 13.09.17 16:21
 */
@Import(I18nConfigTemplate.class)
@EnableConfigurationProperties(SpringI18nSupportProperties.class)
public class SpringI18nSupportAutoConfiguration {

	@Inject
	protected SpringI18nSupportProperties springI18nSupportProperties;

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
	@ConditionalOnMissingBean
	public SimpleJdbcMessageSourceDao jdbcMessageSourceDa(DataSource dataSource) {
		SimpleJdbcMessageSourceDao jdbcMessageSourceDao = new SimpleJdbcMessageSourceDao();
		jdbcMessageSourceDao.setDataSource(dataSource);
		if (hasText(getTableName())) {
			jdbcMessageSourceDao.setTable(getTableName());
		}
		if (hasText(getMessageColumnName())) {
			jdbcMessageSourceDao.setMessage(getMessageColumnName());
		}
		if (hasText(getTypeColumnName())) {
			jdbcMessageSourceDao.setType(getTypeColumnName());
		}
		if (hasText(getCodeIdColumnName())) {
			jdbcMessageSourceDao.setCodeId(getCodeIdColumnName());
		}
		if (hasText(getLangIdColumnName())) {
			jdbcMessageSourceDao.setLangId(getLangIdColumnName());
		}
		return jdbcMessageSourceDao;
	}

	/**
	 * table name for the message resources
	 *
	 * @return table name
	 */
	protected String getTableName() {
		return springI18nSupportProperties.getDataSource().getTableName();
	}

	protected String getMessageColumnName() {
		return springI18nSupportProperties.getDataSource().getMessageColumnName();
	}

	protected String getTypeColumnName() {
		return springI18nSupportProperties.getDataSource().getTypeColumnName();
	}

	protected String getCodeIdColumnName() {
		return springI18nSupportProperties.getDataSource().getCodeIdColumnName();
	}

	protected String getLangIdColumnName() {
		return springI18nSupportProperties.getDataSource().getLangIdColumnName();
	}

}

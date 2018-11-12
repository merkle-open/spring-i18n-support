package com.namics.oss.spring.support.i18n.autoconfigure;

import com.namics.oss.spring.support.i18n.DaoMessageSource;
import com.namics.oss.spring.support.i18n.dao.MessageSourceDao;
import com.namics.oss.spring.support.i18n.dao.MessageSourceManagementDao;
import com.namics.oss.spring.support.i18n.dao.SimpleJdbcMessageSourceDao;
import com.namics.oss.spring.support.i18n.excel.ExcelReader;
import com.namics.oss.spring.support.i18n.excel.ExcelWriter;
import com.namics.oss.spring.support.i18n.service.MessageManagementService;
import com.namics.oss.spring.support.i18n.service.MessageManagementServiceImpl;
import com.namics.oss.spring.support.i18n.service.ReloadService;
import com.namics.oss.spring.support.i18n.service.ReloadServiceImpl;
import com.namics.oss.spring.support.i18n.text.SqlScriptWriter;
import com.namics.oss.spring.support.i18n.text.TextWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;

/**
 * SpringI18nSupportAutoConfiguration.
 *
 * @author rgsell, Namics AG
 * @since 13.09.17 16:21
 */
@Configuration
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
		source.setUseCodeAsDefaultMessage(springI18nSupportProperties.getFallback().isUseCodeAsDefaultMessage());

		//set fallbacks if at least one is enabled and one fallback lang is available
		boolean fallbackForKnownLanguages = springI18nSupportProperties.getFallback().isFallbackForKnownLanguages();
		boolean fallbackForUnknownLanguages = springI18nSupportProperties.getFallback().isFallbackForUnknownLanguages();
		List<String> fallbackLanguage = springI18nSupportProperties.getFallback().getFallbackLanguage();
		if (!isEmpty(fallbackLanguage) && (fallbackForKnownLanguages || fallbackForUnknownLanguages)) {
			source.setFallbackForKnownLanguages(fallbackForKnownLanguages);
			source.setFallbackForUnknownLanguages(fallbackForUnknownLanguages);
			source.setFallbacks(IntStream.range(0, fallbackLanguage.size()).boxed().collect(toMap(Function.identity(), fallbackLanguage::get)));

		}
		return source;
	}

	@Bean
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

	@Bean
	public AnnotationMBeanExporter annotationMBeanExporter() {
		return new AnnotationMBeanExporter();
	}

	@Bean
	@ConditionalOnMissingBean
	public ExcelReader excelReader() {
		return new ExcelReader();
	}

	@Bean
	@ConditionalOnMissingBean
	public ExcelWriter excelWriter() {
		return new ExcelWriter();
	}

	@Bean
	@ConditionalOnMissingBean
	public TextWriter sqlScriptWriter() {
		SqlScriptWriter sqlScriptWriter = new SqlScriptWriter();
		if (hasText(getScriptTemplatePath())) {
			sqlScriptWriter.setScriptTemplate(new DefaultResourceLoader().getResource(getScriptTemplatePath()));
		}
		if (hasText(getSqlInsertStatementTemplate())) {
			sqlScriptWriter.setInsertStatementTemplate(getSqlInsertStatementTemplate());
		}
		if (hasText(getSqlBodyPlaceholder())) {
			sqlScriptWriter.setBodyPlaceholder(getSqlBodyPlaceholder());
		}
		if (hasText(getSqlLineBreak())) {
			sqlScriptWriter.setLineBreak(getSqlLineBreak());
		}
		if (hasText(getTableName())) {
			sqlScriptWriter.setTable(getTableName());
		}
		if (hasText(getMessageColumnName())) {
			sqlScriptWriter.setMessage(getMessageColumnName());
		}
		if (hasText(getTypeColumnName())) {
			sqlScriptWriter.setType(getTypeColumnName());
		}
		if (hasText(getCodeIdColumnName())) {
			sqlScriptWriter.setCodeId(getCodeIdColumnName());
		}
		if (hasText(getLangIdColumnName())) {
			sqlScriptWriter.setLangId(getLangIdColumnName());
		}
		return sqlScriptWriter;
	}

	@Bean
	public MessageManagementService messageManagementService(MessageSourceManagementDao messageSourceDao,
	                                                         ExcelReader excelReader,
	                                                         ExcelWriter excelWriter,
	                                                         TextWriter textWriter) {
		MessageManagementServiceImpl service = new MessageManagementServiceImpl();
		service.setExcelReader(excelReader);
		service.setExcelWriter(excelWriter);
		service.setTextWriter(textWriter);
		service.setMessageSourceDao(messageSourceDao);
		return service;
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

	protected String getScriptTemplatePath() {
		return springI18nSupportProperties.getDataSource().getSqlExportScriptTemplatePath();
	}

	protected String getSqlInsertStatementTemplate() {
		return springI18nSupportProperties.getDataSource().getSqlExportInsertStatementTemplate();
	}

	protected String getSqlBodyPlaceholder() {
		return springI18nSupportProperties.getDataSource().getSqlExportBodyPlaceholder();
	}

	protected String getSqlLineBreak() {
		return springI18nSupportProperties.getDataSource().getSqlExportLineBreak();
	}
}

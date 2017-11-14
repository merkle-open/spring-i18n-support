/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.dao;

import com.namics.oss.spring.support.i18n.context.support.Reloadable;
import com.namics.oss.spring.support.i18n.dao.jpa.model.MessageResource;
import com.namics.oss.spring.support.i18n.excel.ExcelReader;
import com.namics.oss.spring.support.i18n.exception.I18nException;
import com.namics.oss.spring.support.i18n.exception.ReloadableResourceException;
import com.namics.oss.spring.support.i18n.model.MessageCacheKey;
import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

/**
 * ExcelMessageSourceDao to serve messages read from a configured excel file.
 *
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 14, 2010
 */
public class ExcelMessageSourceDao implements MessageSourceDao, InitializingBean, Reloadable {

	private static final Logger LOG = LoggerFactory.getLogger(ExcelMessageSourceDao.class);

	private Resource resource;

	private ExcelReader excelReader;

	private Map<MessageCacheKey, MessageResource> messages = new TreeMap<>();

	private List<String> availableLanguages = new ArrayList<>();

	/**
	 * {@inheritDoc}
	 *
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@PostConstruct
	@Override
	public void afterPropertiesSet() throws IOException, I18nException {
		LOG.info("Setup message source with input file " + this.resource.getFilename());
		InputStream in = this.resource.getInputStream();
		List<MessageResourceEntry> data = this.excelReader.processExcel(in);
		if (!CollectionUtils.isEmpty(data)) {
			for (MessageResourceEntry resource : data) {
				String code = resource.getCodeId();
				for (Entry<Locale, String> entry : resource.getNameMappings().entrySet()) {
					Locale locale = entry.getKey();
					String value = entry.getValue();
					if (!this.availableLanguages.contains(locale.toString())) {
						this.availableLanguages.add(locale.toString());
					}
					MessageCacheKey key = new MessageCacheKey(locale, code);
					if (LOG.isDebugEnabled()) {
						LOG.debug("Create cache entry for " + ToStringBuilder.reflectionToString(key, ToStringStyle.SHORT_PREFIX_STYLE));
					}
					this.messages.put(key, new MessageResource().code(code).lang(locale.toString()).message(value));
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see Reloadable#reload()
	 */
	@Override
	public void reload() throws ReloadableResourceException {
		try {
			afterPropertiesSet();
		} catch (Exception e) {
			throw new ReloadableResourceException("Problem reloading the message source", e);
		}
	}

	@Override
	public List<String> findDistinctLang() {
		return this.availableLanguages;
	}

	/**
	 * Setter for resource. @param resource the resource to set
	 */
	@Required
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * Setter for excelReader. @param excelReader the excelReader to set
	 */
	@Required
	public void setExcelReader(ExcelReader excelReader) {
		this.excelReader = excelReader;
	}

	@Override
	public List<MessageResource> findAll() {
		List<MessageResource> resources = new ArrayList<>();
		resources.addAll(this.messages.values());
		return Collections.unmodifiableList(resources);
	}

	@Override
	public List<MessageResource> findByCodeAndLang(String code, String lang) {
		List<MessageResource> resources = new ArrayList<>();
		MessageCacheKey key = new MessageCacheKey(new Locale(lang), code);
		if (this.messages.containsKey(key)) {
			resources.add(this.messages.get(key));
		}
		return resources;
	}

	@Override
	public MessageResource findTopByCodeAndLang(String code, String lang) {
		List<MessageResource> byCodeAndLang = findByCodeAndLang(code, lang);
		if (!org.springframework.util.CollectionUtils.isEmpty(byCodeAndLang)) {
			return byCodeAndLang.get(0);
		}
		return null;
	}

}

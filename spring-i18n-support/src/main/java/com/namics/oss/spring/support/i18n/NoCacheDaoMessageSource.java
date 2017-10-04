/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n;

import com.namics.oss.spring.support.i18n.exception.ReloadableResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * MessageSource that resolves the messages from a configurable DAO Object without any cache. This is the simplest implementation of
 * AbstractDaoMessageSource without any caching. This implementation causes a lot of load on the DAO! If you do not have real time update requirements
 * consider using {@link DaoMessageSource} instead!
 *
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.2 - Oct 14, 2010
 */
public class NoCacheDaoMessageSource extends AbstractDaoMessageSource {
	/**
	 * Logger-Category.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(NoCacheDaoMessageSource.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MessageFormat resolveCode(String code,
	                                    Locale locale) {
		try {
			String messageForLocale = this.getMessageForLocale(code, locale);
			if (messageForLocale != null) {
				return this.createMessageFormat(messageForLocale, locale);
			}
		} catch (Exception ex) {
			this.logException(code, locale, ex, LOG);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.springframework.context.support.AbstractMessageSource#resolveCodeWithoutArguments(java.lang.String, java.util.Locale)
	 */
	@Override
	protected String resolveCodeWithoutArguments(String code,
	                                             Locale locale) {
		return this.getMessageForLocale(code, locale);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() throws ReloadableResourceException {
		LOG.info("NoCacheDaoMessageSource is not reloadable");
	}
}

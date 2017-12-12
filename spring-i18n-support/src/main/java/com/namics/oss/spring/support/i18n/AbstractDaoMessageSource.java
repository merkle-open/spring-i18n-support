/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n;

import com.namics.oss.spring.support.i18n.context.support.Reloadable;
import com.namics.oss.spring.support.i18n.dao.MessageSourceDao;
import com.namics.oss.spring.support.i18n.dao.jpa.model.MessageResource;
import com.namics.oss.spring.support.i18n.exception.ReloadableResourceException;
import org.apache.commons.lang.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * MessageSource that resolves the messages from a configurable DAO Object, needs to be implemented.
 *
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.2 - Oct 14, 2010
 */
public abstract class AbstractDaoMessageSource extends AbstractMessageSource implements Reloadable {
	/**
	 * Logger-Category.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractDaoMessageSource.class);

	/**
	 * DAO for getting the message sources.
	 */
	private MessageSourceDao messageSourceDao;

	/**
	 * Sorted Map to configure a fallback order if fallback intended. can be configured in spring context using map element in properties.
	 * <pre>
	 * 	&lt;property name="fallbacks">
	 * 		&lt;map>
	 * 			&lt;entry key="1" value="de" />
	 * 			&lt;entry key="2" value="fr" />
	 * 		&lt;/map>
	 * 	&lt;/property>
	 * </pre>
	 */
	private SortedMap<Integer, String> fallbacks;

	/**
	 * message source will try to return fallbacks for languages known in the system, default=false.
	 */
	private boolean fallbackForKnownLanguages;

	/**
	 * message source will try to return fallbacks for languages unknown in the system, default=false.
	 */
	private boolean fallbackForUnknownLanguages;

	protected static final Pattern REGEX_PLACEHOLDER = Pattern.compile("\\{[0-9]+\\}");

	/**
	 * {@inheritDoc}
	 *
	 * @see Reloadable#reload()
	 */
	@Override
	public void reload() throws ReloadableResourceException {
		try {
			// make sure the reloadable dao is reloaded before clear
			if (this.messageSourceDao instanceof Reloadable) {
				((Reloadable) this.messageSourceDao).reload();
			}
		} catch (Exception ex) {
			throw new ReloadableResourceException(ex.getMessage(), ex, NoCacheDaoMessageSource.class.getName());
		}
	}

	/**
	 * Resolves Message for a certain locale according to fallback scenarios.
	 *
	 * @param code   code of message to resolve
	 * @param locale locale to resolve the message for
	 * @return the message string from backend to be used as {@link MessageFormat}
	 */
	protected String getMessageForLocale(String code,
			Locale locale) {
		String msg = this.getMessageForLocaleFromBackend(code, locale);
		if (!StringUtils.hasText(msg)) {
			msg = this.getFallback(code, locale);
		}
		msg = this.postProcessMessage(msg);
		LOG.debug("Resolved {} for {} : {}", new Object[] { code, locale, msg });
		return msg;
	}

	/**
	 * Resolves Message for a certain locale form the backend.
	 *
	 * @param code   code of message to resolve
	 * @param locale locale to resolve the message for
	 * @return the raw message string from backend
	 */
	protected String getMessageForLocaleFromBackend(String code,
			Locale locale) {
		List<MessageResource> messages = this.messageSourceDao.findByCodeAndLang(code, locale.toString());
		if (messages != null && messages.size() > 0) {
			return messages.get(0).getMessage();
		}
		return null;
	}

	/**
	 * Does some post processing on the resolved message.
	 *
	 * @param msg
	 * @return
	 */
	protected String postProcessMessage(String msg) {
		String result = msg;
		if (StringUtils.hasText(result) && REGEX_PLACEHOLDER.matcher(result).find()) {
			result = result.replaceAll("'", "''");
		}
		return result;
	}

	/**
	 * resolves fallbacks first derived from requested locale, second by configured scenarios.
	 *
	 * @param code   code to resolve message
	 * @param locale language to get the message for
	 * @return the fallback if any, null else
	 */
	protected String getFallback(String code,
			Locale locale) {
		Locale country = new Locale(locale.getLanguage(), locale.getCountry());
		Locale language = new Locale(locale.getLanguage());

		// derived form original requested locale
		String derived = this.getLocaleBasedFallback(code, locale, country, language);

		if (derived != null) {
			return derived;
		}

		return this.getScenarioFallback(code, locale, country, language);
	}

	/**
	 * resolves fallbacks derived from requested locale.
	 *
	 * @param code     code to get the fallback message for
	 * @param locale   the original locale, including possibel variants
	 * @param country  the language + country locale
	 * @param language the language only locale
	 */
	private String getLocaleBasedFallback(String code,
			Locale locale,
			Locale country,
			Locale language) {
		if (StringUtils.hasText(locale.getVariant())) {
			// use de_CH instead of de_CH_xyz
			LOG.debug("Remove variant for country fallback");
			String msg = this.getMessageForLocaleFromBackend(code, country);
			if (StringUtils.hasText(msg)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("found country fallback for code [" + code + "] lang[" + locale + "] :" + msg);
				}
				return msg;
			}
		}
		if (StringUtils.hasText(locale.getCountry())) {
			// use de instead of de_CH
			LOG.debug("Remove variant and country for language fallback");
			String msg = this.getMessageForLocaleFromBackend(code, language);
			if (StringUtils.hasText(msg)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("found language fallback for code [" + code + "] lang[" + locale + "] :" + msg);
				}
				return msg;
			}
		}
		return null;
	}

	/**
	 * resolves fallbacks by configured scenarios.
	 *
	 * @param code     code to get the fallback message for
	 * @param locale   the original locale, including possibel variants
	 * @param country  the language + country locale
	 * @param language the language only locale
	 */
	private String getScenarioFallback(String code,
			Locale locale,
			Locale country,
			Locale language) {
		// fallback scenarios
		if (LOG.isDebugEnabled()) {
			LOG.debug("Use further fallbacks based on configuration for code[" + code + "] lang[" + locale + "]");
		}
		List<String> locales = this.messageSourceDao.findDistinctLang();

		// fallback languages are based on any existing matching locale de de_CH de_CH_xyz
		boolean langKnown = locales.contains(locale.toString()) || locales.contains(country.toString()) || locales.contains(language.toString());

		if (this.fallbackForUnknownLanguages && !langKnown || this.fallbackForKnownLanguages && langKnown) {
			for (Entry<Integer, String> fallback : this.fallbacks.entrySet()) {
				String msg = this.getMessageForLocaleFromBackend(code, LocaleUtils.toLocale(fallback.getValue()));
				if (StringUtils.hasText(msg)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("found fallback for code [" + code + "] lang[" + locale + "] :" + msg);
					}
					return msg;
				}
			}
		}
		return null;
	}

	/**
	 * Writes an exception log.
	 *
	 * @param code   message code involved
	 * @param locale the locale involved
	 * @param ex     the exception to log
	 * @param logger the logger to log to
	 */
	protected void logException(String code,
			Locale locale,
			Exception ex,
			Logger logger) {
		if (logger.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("FAILED: resolveCode [");
			sb.append(code);
			sb.append("] for language [");
			sb.append(locale.toString());
			sb.append("] because of: ");
			sb.append(ex.getClass().getName());
			sb.append("Message:");
			sb.append(ex.getMessage() != null ? ex.getMessage().replaceAll("\n", " ") : "null");
			logger.info(sb.toString());
		}
	}

	// CHECKSTYLE:OFF

	@Required
	public void setMessageSourceDao(MessageSourceDao messageSourceDao) {
		this.messageSourceDao = messageSourceDao;
	}

	/**
	 * Setter for fallbacks. @param fallbacks the fallbacks to set
	 *
	 * @param fallbacks
	 */
	public void setFallbacks(Map<Integer, ?> fallbacks) {
		SortedMap<Integer, String> normalized = new TreeMap<Integer, String>();
		for (Entry<Integer, ?> entry : fallbacks.entrySet()) {
			if (entry.getValue() instanceof Locale) {
				normalized.put(entry.getKey(), ((Locale) entry.getValue()).toString());
			} else if (entry.getValue() instanceof String) {
				normalized.put(entry.getKey(), (String) entry.getValue());
			}
		}
		this.fallbacks = normalized;
	}

	/**
	 * Setter for fallbackForKnownLanguages. @param fallbackForKnownLanguages the fallbackForKnownLanguages to set
	 */
	public void setFallbackForKnownLanguages(boolean fallbackForKnownLanguages) {
		this.fallbackForKnownLanguages = fallbackForKnownLanguages;
	}

	/**
	 * Setter for fallbackForUnknownLanguages. @param fallbackForUnknownLanguages the fallbackForUnknownLanguages to set
	 */
	public void setFallbackForUnknownLanguages(boolean fallbackForUnknownLanguages) {
		this.fallbackForUnknownLanguages = fallbackForUnknownLanguages;
	}

	// CHECKSTYLE:ON

}

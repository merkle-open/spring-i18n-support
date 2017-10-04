/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n;

import com.namics.oss.spring.support.i18n.context.support.Reloadable;
import com.namics.oss.spring.support.i18n.exception.ReloadableResourceException;
import com.namics.oss.spring.support.i18n.legacy.CachingMapDecorator;
import com.namics.oss.spring.support.i18n.model.MessageCacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

/**
 * MessageSource that resolves the messages from a configurable DAO Object using {@link CachingMapDecorator} maps for caching. This is the most
 * recommend implementation to use if you do not need precise cache configuration or realtime label updates. This caching reduces load on the dao
 * tremendously.
 *
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 14, 2010
 */
public final class DaoMessageSource extends AbstractDaoMessageSource implements Reloadable
{
	/**
	 * Logger-Category.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(DaoMessageSource.class);

	/**
	 * Map for caching message formats.
	 */
	private Map<MessageCacheKey, MessageFormat> cachedMessageFormats = new FormatCachingMapDecorator();

	/**
	 * Map for caching message formats.
	 */
	private Map<MessageCacheKey, String> cachedMessages = new MessageCachingMapDecorator();

	@Override
	public void reload() throws ReloadableResourceException
	{
		try
		{
			this.cachedMessageFormats.clear();
			this.cachedMessages.clear();
		} catch (Exception ex)
		{
			throw new ReloadableResourceException(ex.getMessage(), ex, DaoMessageSource.class.getName());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MessageFormat resolveCode(String code,
										Locale locale)
	{
		try
		{
			return this.cachedMessageFormats.get(new MessageCacheKey(locale, code));
		} catch (Exception ex)
		{
			this.logException(code, locale, ex, LOG);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.springframework.context.support.AbstractMessageSource#resolveCodeWithoutArguments(java.lang.String, java.util.Locale)
	 */
	@Override
	protected String resolveCodeWithoutArguments(String code,
												 Locale locale)
	{
		return this.cachedMessages.get(new MessageCacheKey(locale, code));
	}

	/**
	 * CachingMapDecorator that handles the caching of message sources.
	 *
	 * @author aschaefer, namics ag
	 * @since Namics commons i18n 1.0 - Nov 12, 2010
	 */
	private class MessageCachingMapDecorator extends CachingMapDecorator<MessageCacheKey, String>
	{

		/**
		 * serialVersionUID.
		 */
		private static final long serialVersionUID = 1523418181467939071L;

		/**
		 * Method to create cached object if provided key is not in the cache yet.
		 *
		 * @param langCacheKey the language dependent cache key
		 * @return depending on the provided object
		 */
		@Override
		public String create(MessageCacheKey langCacheKey)
		{
			return DaoMessageSource.this.getMessageForLocale(langCacheKey.getCode(), langCacheKey.getLocale());
		}
	}

	private class FormatCachingMapDecorator extends CachingMapDecorator<MessageCacheKey, MessageFormat>
	{
		/**
		 * serialVersionUID.
		 */
		private static final long serialVersionUID = -5856315166060953113L;

		/**
		 * Method to create cached object if provided key is not in the cache yet.
		 *
		 * @param langCacheKey the language dependent cache key
		 * @return depending on the provided object
		 */
		@Override
		public MessageFormat create(MessageCacheKey langCacheKey)
		{
			String msg = DaoMessageSource.this.cachedMessages.get(langCacheKey);
			if (StringUtils.hasText(msg))
			{
				return DaoMessageSource.this.createMessageFormat(msg, langCacheKey.getLocale());
			}
			return null;
		}
	}
}

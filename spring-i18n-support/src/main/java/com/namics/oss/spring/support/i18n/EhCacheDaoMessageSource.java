/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n;

import java.text.MessageFormat;
import java.util.Locale;

import com.namics.oss.spring.support.i18n.context.support.Reloadable;
import com.namics.oss.spring.support.i18n.model.MessageCacheKey;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.namics.oss.spring.support.i18n.exception.ReloadableResourceException;

/**
 * MessageSource that resolves the messages from a configurable DAO Object using an EhCache for caching. Be sure to have the required ehcache
 * dependencies in your classpath if you want to use this implementation. If you cannot include ehcache, considder using DaoMesageSource.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.2 - Oct 14, 2010
 */
public class EhCacheDaoMessageSource extends AbstractDaoMessageSource implements Reloadable
{
	/** Logger-Category. */
	private static final Logger LOG = LoggerFactory.getLogger(EhCacheDaoMessageSource.class);

	private Ehcache messageCache;

	private Ehcache formatCache;

	/**
	 * {@inheritDoc}
	 * 
	 * @see AbstractDaoMessageSource#reload()
	 */
	@Override
	public void reload() throws ReloadableResourceException
	{
		try
		{
			this.messageCache.removeAll();
			this.formatCache.removeAll();
		}
		catch (Exception ex)
		{
			throw new ReloadableResourceException(ex.getMessage(), ex, DaoMessageSource.class.getName());
		}
	}

	/** {@inheritDoc} */
	@Override
	protected MessageFormat resolveCode(String code,
										Locale locale)
	{
		MessageCacheKey cacheKey = new MessageCacheKey(locale, code);
		Element cacheElement = null;
		try
		{
			cacheElement = this.formatCache.get(cacheKey);
			if (cacheElement != null && !cacheElement.isExpired())
			{
				// ok... we got a valid response in cache
				LOG.info("Message value with key [" + cacheKey + "] found in cache... ");
			}
			else
			{
				// not in cache get from datastore
				cacheElement = new Element(cacheKey, this.createMessageFormat(this.getMessageForLocale(code, locale), locale));
				LOG.info("Message value with key [" + cacheKey + "] put into cache... ");
			}
		}
		catch (Exception e)
		{
			this.logException(code, locale, e, LOG);
		}
		finally
		{
			// we have to put null or Object into cache to unlock the blocked
			// read-threads!
			if (cacheElement == null)
			{
				cacheElement = new Element(cacheKey, null);
			}
			// put element into cache
			this.formatCache.put(cacheElement);
		}
		return (MessageFormat) cacheElement.getValue();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.springframework.context.support.AbstractMessageSource#resolveCodeWithoutArguments(java.lang.String, java.util.Locale)
	 */
	@Override
	protected String resolveCodeWithoutArguments(	String code,
													Locale locale)
	{
		MessageCacheKey cacheKey = new MessageCacheKey(locale, code);
		Element cacheElement = null;
		try
		{
			cacheElement = this.messageCache.get(cacheKey);
			if (cacheElement != null && !cacheElement.isExpired())
			{
				// ok... we got a valid response in cache
				LOG.info("Message value with key [" + cacheKey + "] found in cache... ");
			}
			else
			{
				// not in cache get from datastore
				cacheElement = new Element(cacheKey, this.getMessageForLocale(code, locale));
				LOG.info("Message value with key [" + cacheKey + "] put into cache... ");
			}
		}
		catch (Exception e)
		{
			this.logException(code, locale, e, LOG);
		}
		finally
		{
			// we have to put null or Object into cache to unlock the blocked
			// read-threads!
			if (cacheElement == null)
			{
				cacheElement = new Element(cacheKey, null);
			}
			// put element into cache
			this.messageCache.put(cacheElement);
		}
		return (String) cacheElement.getValue();
	}

	// CHECKSTYLE:OFF

	public void setMessageCache(Ehcache messageCache)
	{
		this.messageCache = messageCache;
	}

	public void setFormatCache(Ehcache formatCache)
	{
		this.formatCache = formatCache;
	}

	// CHECKSTYLE:ON
}

/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.model;

import java.util.Locale;

/**
 * MessageCacheKey used for caching message keys.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 14, 2010
 */
public final class MessageCacheKey implements Comparable<MessageCacheKey>
{
	private Locale locale;

	private String code;

	private int hashcode;

	/**
	 * MessageCacheKey.
	 * 
	 * @param locale the users locale
	 * @param code the message code to resolve
	 */
	public MessageCacheKey(Locale locale, String code)
	{
		super();
		this.locale = locale;
		this.code = code;
		this.hashcode = generateHash();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MessageCacheKey o)
	{
		return hashCode() - o.hashCode();
	}

	/** Getter for locale. @return the locale */
	public Locale getLocale()
	{
		return this.locale;
	}

	/** Getter for code. @return the code */
	public String getCode()
	{
		return this.code;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return this.hashcode;
	}

	/**
	 * generates an initial hash code for this cache key for better performance for excessive usage.
	 * 
	 * @return the hash value for this object.
	 */
	public int generateHash()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.code == null ? 0 : this.code.hashCode());
		result = prime * result + (this.locale == null ? 0 : this.locale.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof MessageCacheKey))
		{
			return false;
		}
		MessageCacheKey other = (MessageCacheKey) obj;
		if (this.code == null)
		{
			if (other.code != null)
			{
				return false;
			}
		}
		else if (!this.code.equals(other.code))
		{
			return false;
		}
		if (this.locale == null)
		{
			if (other.locale != null)
			{
				return false;
			}
		}
		else if (!this.locale.equals(other.locale))
		{
			return false;
		}
		return true;
	}

}

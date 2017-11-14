/*
 * Copyright 2000-2009 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.model;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * MessageResourceEntry.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 14, 2010
 */
public final class MessageResourceEntry
{
	/** the reference key in the db. */
	private String codeId;

	/** an optional type to categorize messages. */
	private String type;

	/** mapping of language and name value. */
	private Map<Locale, String> nameMappings = new HashMap<Locale, String>();

	/**
	 * Returns the number of available mappings for this entry.
	 * 
	 * @return the count of items
	 */
	public int size()
	{
		return this.nameMappings != null ? this.nameMappings.size() : 0;
	}

	/**
	 * Methode returns String with value in certain language.
	 * 
	 * @param language
	 *            language to return
	 * @return value in provided languageS
	 */
	public String getName(String language)
	{
		return this.getNameMappings().get(language);
	}

	/**
	 * Methode returns String with value in certain language.
	 * 
	 * @param locale
	 *            locale to return
	 * @return value in provided languageS
	 */
	public String getName(Locale locale)
	{
		return this.getNameMappings().get(locale);
	}

	/**
	 * add a new language to entry.
	 * 
	 * @param locale
	 *            language to ad
	 * @param name
	 *            value to ad
	 */
	public void addLang(Locale locale,
						String name)
	{
		this.getNameMappings().put(locale, name);
	}

	/** The String codeId. @return the codeId */
	public String getCodeId()
	{
		return this.codeId;
	}

	/** The codeId. @param codeId the codeId to set */
	public void setCodeId(String codeId)
	{
		this.codeId = codeId;
	}

	/** The Map&lt;String,String&gt; nameMappings. @return the nameMappings */
	public Map<Locale, String> getNameMappings()
	{
		return this.nameMappings;
	}

	/** The nameMappings. @param nameMappings the nameMappings to set */
	public void setNameMappings(Map<Locale, String> nameMappings)
	{
		this.nameMappings = nameMappings;
	}

	/** Getter for type. @return the type */
	public String getType()
	{
		return this.type;
	}

	/** Setter for type. @param type the type to set */
	public void setType(String type)
	{
		this.type = type;
	}

	@Override
	public String toString() {
		return "MessageResourceEntry{" +
		       "codeId='" + codeId + '\'' +
		       ", type='" + type + '\'' +
		       ", nameMappings=" + nameMappings +
		       '}';
	}
}

/*
 * Copyright 2000-2012 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.i18n;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;
import junit.framework.Assert;

import org.junit.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.namics.oss.spring.support.i18n.properties.PropertiesWriter;

/**
 * PropertiesWriterTest.
 * 
 * @author aschaefer, Namics AG
 * @since 1.4 26.04.2012
 */
public class PropertiesWriterTest
{

	private static final String FR_TEST = "c'est française André Schäfer";

	private static final String DE_TEST = "'komisch'' \"";

	@Test
	public void simpleTest() throws Throwable
	{
		try
		{
			String dir = this.getClass().getResource("/").getFile();
			String resource = "writer";
			File dirFile = new File(dir);
			new PropertiesWriter().writePropertiesSet(this.messages(), new Locale("de"), resource, dirFile, "ISO-8859-1");

			ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
			messageSource.setBasename(resource);
			Assert.assertEquals(FR_TEST, messageSource.getMessage("test.key", null, new Locale("fr")));
			Assert.assertEquals(DE_TEST, messageSource.getMessage("test.key", null, new Locale("de")));
		}
		finally
		{

		}
	}

	/**
	 * JavaDoc.
	 * 
	 * @return
	 */
	protected List<MessageResourceEntry> messages()
	{
		List<MessageResourceEntry> messages = new ArrayList<MessageResourceEntry>();
		MessageResourceEntry entry = new MessageResourceEntry();
		entry.setCodeId("test.key");
		entry.addLang(new Locale("de"), DE_TEST);
		entry.addLang(new Locale("fr"), FR_TEST);
		messages.add(entry);

		entry = new MessageResourceEntry();
		entry.setCodeId("test.key.2");
		entry.addLang(new Locale("de"), "deutsch");
		entry.addLang(new Locale("it"), "italiano");
		messages.add(entry);

		entry = new MessageResourceEntry();
		entry.setCodeId("test.key.3");
		entry.addLang(new Locale("de"), "deutsch");
		entry.addLang(new Locale("en"), "english");
		messages.add(entry);

		entry = new MessageResourceEntry();
		entry.setCodeId("test.key.4");
		entry.addLang(new Locale("en"), "english");
		messages.add(entry);
		return messages;
	}
}

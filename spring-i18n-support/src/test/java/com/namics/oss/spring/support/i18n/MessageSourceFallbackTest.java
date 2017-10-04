/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test different fallback configurations of the message source.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 14, 2010
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:**fallbackJdbcMessageSourceContext.xml" })
public class MessageSourceFallbackTest
{

	@Autowired
	@Qualifier("noFallbackMessageSource")
	private MessageSource noFallbackMessageSource;

	@Autowired
	@Qualifier("fallbackUnknownMessageSource")
	private MessageSource fallbackUnknownMessageSource;

	@Autowired
	@Qualifier("fallbackKnownMessageSource")
	private MessageSource fallbackKnownMessageSource;

	@Autowired
	@Qualifier("fallbackAllMessageSource")
	private MessageSource fallbackAllMessageSource;

	@Test
	public void testNoFallbackMessageSource()
	{
		Assert.assertEquals("deutsch", this.noFallbackMessageSource.getMessage("test.key", null, new Locale("de")));
		Assert.assertEquals("english", this.noFallbackMessageSource.getMessage("test.key", null, new Locale("en")));
		Assert.assertEquals("francaise", this.noFallbackMessageSource.getMessage("test.key", null, new Locale("fr")));
		Assert.assertEquals("italiano", this.noFallbackMessageSource.getMessage("test.key", null, new Locale("it")));

		Assert.assertEquals("deutsch", this.noFallbackMessageSource.getMessage("test.key.de", null, new Locale("de")));
		try
		{
			this.noFallbackMessageSource.getMessage("test.key.de", null, new Locale("en"));
			Assert.fail();
		}
		catch (NoSuchMessageException e)
		{
			// OK
		}
		try
		{
			Assert.assertEquals(null, this.noFallbackMessageSource.getMessage("test.key.de", null, new Locale("fr")));
			Assert.fail();
		}
		catch (NoSuchMessageException e)
		{
			// OK
		}
		try
		{
			Assert.assertEquals(null, this.noFallbackMessageSource.getMessage("test.key.de", null, new Locale("it")));
			Assert.fail();
		}
		catch (NoSuchMessageException e)
		{
			// OK
		}
		try
		{
			Assert.assertEquals(null, this.noFallbackMessageSource.getMessage("test.key.de", null, new Locale("ar")));
			Assert.fail();
		}
		catch (NoSuchMessageException e)
		{
			// OK
		}

	}

	@Test
	public void testFallbackUnknownMessageSource()
	{
		Assert.assertEquals("deutsch", this.fallbackUnknownMessageSource.getMessage("test.key", null, new Locale("de")));
		Assert.assertEquals("english", this.fallbackUnknownMessageSource.getMessage("test.key", null, new Locale("en")));
		Assert.assertEquals("francaise", this.fallbackUnknownMessageSource.getMessage("test.key", null, new Locale("fr")));
		Assert.assertEquals("italiano", this.fallbackUnknownMessageSource.getMessage("test.key", null, new Locale("it")));

		Assert.assertEquals("deutsch", this.fallbackUnknownMessageSource.getMessage("test.key.de", null, new Locale("de")));
		try
		{
			this.fallbackUnknownMessageSource.getMessage("test.key.de", null, new Locale("en"));
			Assert.fail();
		}
		catch (NoSuchMessageException e)
		{
			// OK
		}
		try
		{
			Assert.assertEquals(null, this.fallbackUnknownMessageSource.getMessage("test.key.de", null, new Locale("fr")));
			Assert.fail();
		}
		catch (NoSuchMessageException e)
		{
			// OK
		}
		try
		{
			Assert.assertEquals(null, this.fallbackUnknownMessageSource.getMessage("test.key.de", null, new Locale("it")));
			Assert.fail();
		}
		catch (NoSuchMessageException e)
		{
			// OK
		}

		Assert.assertEquals("deutsch", this.fallbackUnknownMessageSource.getMessage("test.key.de", null, new Locale("ar")));
		Assert.assertEquals("francaise", this.fallbackUnknownMessageSource.getMessage("test.key.fr", null, new Locale("ar")));
	}

	@Test
	public void testFallbackKnownMessageSource()
	{
		Assert.assertEquals("deutsch", this.fallbackKnownMessageSource.getMessage("test.key", null, new Locale("de")));
		Assert.assertEquals("english", this.fallbackKnownMessageSource.getMessage("test.key", null, new Locale("en")));
		Assert.assertEquals("francaise", this.fallbackKnownMessageSource.getMessage("test.key", null, new Locale("fr")));
		Assert.assertEquals("italiano", this.fallbackKnownMessageSource.getMessage("test.key", null, new Locale("it")));

		Assert.assertEquals("deutsch", this.fallbackKnownMessageSource.getMessage("test.key.de", null, new Locale("de")));
		Assert.assertEquals("deutsch", this.fallbackKnownMessageSource.getMessage("test.key.de", null, new Locale("en")));
		Assert.assertEquals("deutsch", this.fallbackKnownMessageSource.getMessage("test.key.de", null, new Locale("fr")));
		Assert.assertEquals("deutsch", this.fallbackKnownMessageSource.getMessage("test.key.de", null, new Locale("it")));

		try
		{
			Assert.assertEquals("deutsch", this.fallbackKnownMessageSource.getMessage("test.key.de", null, new Locale("ar")));
			Assert.fail();
		}
		catch (NoSuchMessageException e)
		{
			// OK
		}
		try
		{
			Assert.assertEquals("francaise", this.fallbackKnownMessageSource.getMessage("test.key.fr", null, new Locale("ar")));
			Assert.fail();
		}
		catch (NoSuchMessageException e)
		{
			// OK
		}
	}

	@Test
	public void testFallbackAllMessageSource()
	{
		Assert.assertEquals("deutsch", this.fallbackAllMessageSource.getMessage("test.key", null, new Locale("de")));
		Assert.assertEquals("english", this.fallbackAllMessageSource.getMessage("test.key", null, new Locale("en")));
		Assert.assertEquals("francaise", this.fallbackAllMessageSource.getMessage("test.key", null, new Locale("fr")));
		Assert.assertEquals("italiano", this.fallbackAllMessageSource.getMessage("test.key", null, new Locale("it")));

		Assert.assertEquals("deutsch", this.fallbackAllMessageSource.getMessage("test.key.de", null, new Locale("de")));
		Assert.assertEquals("deutsch", this.fallbackAllMessageSource.getMessage("test.key.de", null, new Locale("en")));
		Assert.assertEquals("deutsch", this.fallbackAllMessageSource.getMessage("test.key.de", null, new Locale("fr")));
		Assert.assertEquals("deutsch", this.fallbackAllMessageSource.getMessage("test.key.de", null, new Locale("it")));
		Assert.assertEquals("deutsch", this.fallbackAllMessageSource.getMessage("test.key.de", null, new Locale("ar")));
		Assert.assertEquals("francaise", this.fallbackAllMessageSource.getMessage("test.key.fr", null, new Locale("ar")));

	}
}

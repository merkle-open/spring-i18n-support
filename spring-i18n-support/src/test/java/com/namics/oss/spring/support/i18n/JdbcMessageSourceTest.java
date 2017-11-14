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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.namics.oss.spring.support.i18n.context.support.Reloadable;

/**
 * TODO JavaDoc JdbcMessageSourceTest.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 18, 2010
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:**simpleJdbcMessageSourceContext.xml" })
public class JdbcMessageSourceTest
{
	@Autowired
	private MessageSource messageSource;

	@Autowired
	@Qualifier("messageSource")
	private Reloadable reloadable;

	@Test
	public void testMessageSource()
	{
		Assert.assertNotNull(this.messageSource);
		Assert.assertEquals("deutsch", this.messageSource.getMessage("test.key", null, new Locale("de")));
		Assert.assertEquals("francaise", this.messageSource.getMessage("test.key", null, new Locale("fr")));
	}

	public void setMessageSource(MessageSource messageSource)
	{
		this.messageSource = messageSource;

	}

	@Test
	public void testRelaod()
	{
		this.reloadable.reload();
	}
}

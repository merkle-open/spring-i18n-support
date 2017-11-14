/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

/**
 * TODO JavaDoc DefaultSetupTest.
 *
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 14, 2010
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:**resourceBundleMessageSourceContext.xml" })
public class ResourceBundleMessageSourceTest {

	@Autowired
	private MessageSource messageSource;
	private static Locale savedLocale;

	@BeforeClass
	public static void setLocalToGerman() {
		savedLocale = Locale.getDefault();
		Locale.setDefault(new Locale("de", "CH"));
	}

	@AfterClass
	public static void setJVMDefaultLocaleAgain() {
		Locale.setDefault(savedLocale);
	}

	@Test
	public void testExistingDeDe() {
		Assert.assertEquals("DEUTSCH", this.messageSource.getMessage("test.full", null, new Locale("de")));
	}

	@Test
	public void testExistingDeFr() {
		Assert.assertEquals(this.messageSource.getMessage("test.full", null, LocaleContextHolder.getLocale()), this.messageSource
				.getMessage("test.full", null, new Locale("fr")));
	}

	@Test
	public void testNonExistingDe() {
		Assert.assertEquals("DEFAULT", this.messageSource.getMessage("test.default", null, new Locale("de")));
	}

	@Test
	public void testNonExistingFr() {
		Assert.assertEquals("DEFAULT", this.messageSource.getMessage("test.default", null, new Locale("fr")));
	}

	@Test
	public void testExistingEnEn() {
		Assert.assertEquals("ENGLISH", this.messageSource.getMessage("test.en", null, new Locale("en")));
	}

	@Test
	public void testExistingEnDe() {
		Assert.assertEquals("DEFAULT", this.messageSource.getMessage("test.en", null, new Locale("de")));
	}

	@Test
	public void testExistingDeEn() {
		Assert.assertEquals("DEFAULT", this.messageSource.getMessage("test.de", null, new Locale("en")));
	}

	@Test
	public void testExistingDeIt() {
		Assert.assertEquals(this.messageSource.getMessage("test.full", null, LocaleContextHolder.getLocale()), this.messageSource
				.getMessage("test.de", null, new Locale("it")));
	}

	@Test
	public void testExistingFr_CHFr_CH() {
		Assert.assertEquals("FRANCAISE", this.messageSource.getMessage("test.full", null, new Locale("fr", "CH")));
	}

	@Test
	public void testExistingFr_CHFr() {
		Assert.assertEquals(this.messageSource.getMessage("test.full", null, LocaleContextHolder.getLocale()), this.messageSource
				.getMessage("test.full", null, new Locale("fr")));
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}

/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n;

import java.util.Locale;

import com.namics.oss.spring.support.i18n.util.LocaleComparator;
import junit.framework.Assert;

import org.junit.Test;

/**
 * JavaDoc LocaleComparatorTest.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Nov 12, 2010
 */
public class LocaleComparatorTest
{

	@Test
	public void testNullBoth()
	{
		Assert.assertEquals(0, new LocaleComparator().compare(null, null));
	}

	@Test
	public void testNullLeft()
	{
		Assert.assertEquals(Integer.MAX_VALUE, new LocaleComparator().compare(null, new Locale("de")));
	}

	@Test
	public void testNullRight()
	{
		Assert.assertEquals(Integer.MIN_VALUE, new LocaleComparator().compare(new Locale("de"), null));
	}

	@Test
	public void testAlpha()
	{
		Assert.assertEquals("de".compareTo("fr"), new LocaleComparator().compare(new Locale("de"), new Locale("fr")));
	}
}

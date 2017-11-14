/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;
import com.namics.oss.spring.support.i18n.service.MessageManagementService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JavaDoc MessageSourceManagementServiceTest.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 19, 2010
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:**simpleJdbcMessageSourceContext.xml" })
public class MessageSourceManagementServiceTest
{

	@Autowired
	private MessageManagementService messageManagementService;

	@Autowired
	private MessageSource messageSource;

	@Test
	public void testInsertNewMessage() throws Exception
	{
		Assert.assertNull(this.messageManagementService.getMessage("test.management.new", new Locale("de")));
		this.messageManagementService.putMessage("test.management.new", new Locale("de"), "test.1");
		Assert.assertEquals("test.1", this.messageManagementService.getMessage("test.management.new", new Locale("de")));
		Assert.assertEquals("test.1", this.messageSource.getMessage("test.management.new", null, new Locale("de")));
	}

	@Test
	public void testUpdateExistingMessage() throws Exception
	{
		Assert.assertNull(this.messageManagementService.getMessage("test.management.new.2", new Locale("de")));
		this.messageManagementService.putMessage("test.management.new.2", new Locale("de"), "test.2");
		Assert.assertEquals("test.2", this.messageManagementService.getMessage("test.management.new.2", new Locale("de")));
		this.messageManagementService.putMessage("test.management.new.2", new Locale("de"), "test.3");
		Assert.assertEquals("test.3", this.messageManagementService.getMessage("test.management.new.2", new Locale("de")));
		Assert.assertEquals("test.3", this.messageSource.getMessage("test.management.new.2", null, new Locale("de")));
	}

	@Test
	public void testExportExcel97() throws Exception
	{
		FileOutputStream fout = null;
		try
		{
			String dir = this.getClass().getResource("/").getFile();
			fout = new FileOutputStream(new File(dir + "test-service-export-97.xls"));
			this.messageManagementService.exportToExcel97(fout);
		}
		finally
		{
			if (fout != null)
			{
				try
				{
					fout.flush();
					fout.close();
				}
				finally
				{
					fout = null;
				}
			}
		}
	}

	@Test
	public void testExportExcel2007() throws Exception
	{
		FileOutputStream fout = null;
		try
		{
			String dir = this.getClass().getResource("/").getFile();
			fout = new FileOutputStream(new File(dir + "test-service-export-2007.xlsx"));
			this.messageManagementService.exportToExcel2007(fout);
		}
		finally
		{
			if (fout != null)
			{
				try
				{
					fout.flush();
					fout.close();
				}
				finally
				{
					fout = null;
				}
			}
		}
	}

	@Test
	public void testExportText() throws Exception
	{
		FileOutputStream fout = null;
		try
		{
			String dir = this.getClass().getResource("/").getFile();
			fout = new FileOutputStream(new File(dir + "test-service-export-text.txt"));
			this.messageManagementService.exportToText(fout);
		}
		finally
		{
			if (fout != null)
			{
				try
				{
					fout.flush();
					fout.close();
				}
				finally
				{
					fout = null;
				}
			}
		}
	}

	@Test
	public void testExcelImport97() throws Exception
	{
		InputStream in = null;
		try
		{
			in = this.getClass().getResourceAsStream("/excel/test-valid.xls");
			assertNotNull("file could not be opend", in);
			assertTrue(in.available() > 0);
			this.messageManagementService.importFromExcel(in);
			MessageResourceEntry message = this.messageManagementService.getMessage("test.key.full");
			Assert.assertNotNull(message);
			Assert.assertEquals("Sheet1", message.getType());

		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				finally
				{
					in = null;
				}
			}
		}

	}

	@Test
	public void testExcelImport2007() throws Exception
	{
		InputStream in = null;
		try
		{
			in = this.getClass().getResourceAsStream("/excel/test-valid.xls");
			assertNotNull("file could not be opend", in);
			assertTrue(in.available() > 0);
			this.messageManagementService.importFromExcel(in);
			MessageResourceEntry message = this.messageManagementService.getMessage("test.key.full");
			Assert.assertNotNull(message);
			Assert.assertEquals("Sheet1", message.getType());
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				finally
				{
					in = null;
				}
			}
		}

	}

	@Test
	public void testDeleteExistingMessage() throws Exception
	{
		Assert.assertNotNull(this.messageManagementService.getMessage("test.management.new.2", new Locale("de")));
		this.messageManagementService.deleteMessage("test.management.new.2");
		Assert.assertNull(this.messageManagementService.getMessage("test.management.new.2"));
	}

	@Test
	public void testAvailableLanguages() throws Exception
	{
		List<Locale> locales = this.messageManagementService.getAvailableLanguages();
		Assert.assertNotNull(locales);
		Assert.assertTrue(locales.size() > 4);
		Assert.assertTrue(locales.contains(new Locale("de")));

	}

	/** Setter for messageManagementService. @param messageManagementService the messageManagementService to set */
	public void setMessageManagementService(MessageManagementService messageManagementService)
	{
		this.messageManagementService = messageManagementService;
	}

}

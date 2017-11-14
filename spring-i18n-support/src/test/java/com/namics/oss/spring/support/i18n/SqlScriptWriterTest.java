/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;
import com.namics.oss.spring.support.i18n.text.SqlScriptWriter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

/**
 * JavaDoc ExcelWriterTest.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.3 - Jan 21, 2011
 */
public class SqlScriptWriterTest
{

	private SqlScriptWriter writer = new SqlScriptWriter();

	@Before
	public void before()
	{
		this.writer.setScriptTemplate(new DefaultResourceLoader().getResource("classpath:templates/template.sql"));
	}

	@Test
	public void testWriteSimpleXls3() throws Exception
	{
		FileOutputStream fout = null;
		try
		{
			String dir = this.getClass().getResource("/").getFile();
			fout = new FileOutputStream(new File(dir + "test-out3.sql"));
			List<MessageResourceEntry> messages = messages();

			this.writer.createOutput(messages, fout);
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
		entry.addLang(new Locale("de"), "deutsch");
		entry.addLang(new Locale("fr"), "francaise");
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

		entry = new MessageResourceEntry();
		entry.setCodeId("test.key.4");
		entry.addLang(new Locale("en"), "_ # $ ( ) { } \\ /");
		messages.add(entry);
		return messages;
	}

	@Test
	public void testWriteSimpleXlsx() throws Exception
	{
		FileOutputStream fout = null;
		try
		{
			String dir = this.getClass().getResource("/").getFile();
			fout = new FileOutputStream(new File(dir + "test-out.sql"));
			BufferedOutputStream bout = new BufferedOutputStream(fout);
			List<MessageResourceEntry> messages = messages();

			this.writer.createOutput(messages, bout);
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
	public void testWriteMultisheetFile() throws Exception
	{
		FileOutputStream fout = null;
		try
		{
			String dir = this.getClass().getResource("/").getFile();
			List<MessageResourceEntry> messages = messages();
			Map<String, List<MessageResourceEntry>> data = new HashMap<String, List<MessageResourceEntry>>();
			data.put("sheet 1", messages);
			this.writer.createOutput(data, new File(dir + "test-out-f.sql"));
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
	public void testWriteMultisheetStream() throws Exception
	{
		FileOutputStream fout = null;
		try
		{
			String dir = this.getClass().getResource("/").getFile();
			List<MessageResourceEntry> messages = messages();
			Map<String, List<MessageResourceEntry>> data = new HashMap<String, List<MessageResourceEntry>>();
			data.put("sheet 1", messages);
			fout = new FileOutputStream(new File(dir + "test-out.sql"));
			BufferedOutputStream bout = new BufferedOutputStream(fout);
			this.writer.createOutput(data, bout);
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
}

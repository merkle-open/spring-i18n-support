/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import com.namics.oss.spring.support.i18n.excel.ExcelReader;
import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JavaDoc ExcelReaderTest.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 14, 2010
 */
public class ExcelReaderTest
{
	private static final Logger LOG = LoggerFactory.getLogger(ExcelReaderTest.class);

	private ExcelReader reader = new ExcelReader();

	@Test
	public void testValidXlsx() throws Exception
	{
		LOG.info(this.getClass().toString());
		LOG.info(this.getClass().getClassLoader().toString());
		LOG.info(this.getClass().getResource("/").toString());
		InputStream in = this.getClass().getResourceAsStream("/excel/test-valid.xlsx");
		assertNotNull("file could not be opend", in);
		assertTrue(in.available() > 0);
		List<MessageResourceEntry> result = this.reader.processExcel(in);
		assertNotNull(result);
		assertEquals(4, result.size());
		checkSpecialCharacters(result);
	}

	@Test
	public void testValidXlsxFile() throws Exception
	{
		LOG.info(this.getClass().toString());
		LOG.info(this.getClass().getClassLoader().toString());
		LOG.info(this.getClass().getResource("/").toString());
		File in = new File(this.getClass().getResource("/excel/test-valid.xlsx").getPath());
		List<MessageResourceEntry> result = this.reader.processExcel(in);
		assertNotNull(result);
		assertEquals(4, result.size());
		checkSpecialCharacters(result);
	}

	@Test
	public void testValidXls() throws Exception
	{
		LOG.info(this.getClass().toString());
		LOG.info(this.getClass().getClassLoader().toString());
		LOG.info(this.getClass().getResource("/").toString());
		InputStream in = this.getClass().getResourceAsStream("/excel/test-valid.xls");
		assertNotNull("file could not be opend", in);
		assertTrue(in.available() > 0);
		List<MessageResourceEntry> result = this.reader.processExcel(in);
		assertNotNull(result);
		assertEquals(4, result.size());
		checkSpecialCharacters(result);
		for (MessageResourceEntry entry : result)
		{
			if (entry.getCodeId().equals("test.key.fr"))
			{
				assertEquals("Apostroph'''''éà", entry.getNameMappings().get(new Locale("fr")));
				break;
			}
		}
	}

	private void checkSpecialCharacters(List<MessageResourceEntry> entries)
	{
		for (MessageResourceEntry entry : entries)
		{
			for (String value : entry.getNameMappings().values())
			{
				assertFalse(value.contains(String.valueOf('…')));
				assertFalse(value.contains(String.valueOf('—')));
				assertFalse(value.contains(String.valueOf('–')));
				assertFalse(value.contains(String.valueOf('¬')));
				assertFalse(value.contains(String.valueOf('`')));
				assertFalse(value.contains(String.valueOf('´')));
				assertFalse(value.contains(String.valueOf('‘')));
				assertFalse(value.contains(String.valueOf('’')));
				assertFalse(value.contains(String.valueOf('’')));
				assertFalse(value.contains(String.valueOf('’')));
				assertFalse(value.contains(String.valueOf('“')));
				assertFalse(value.contains(String.valueOf('”')));
			}
		}
	}
}

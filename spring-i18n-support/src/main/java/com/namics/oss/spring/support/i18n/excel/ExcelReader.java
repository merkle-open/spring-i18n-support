/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.excel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.namics.oss.spring.support.i18n.exception.I18nException;
import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Util class that uses apache poi library to parse an excel resource file in namics format.
 * The excel sheet may contain several sheets.
 * Each sheet of the file must have the default form:
 * <ul>
 * <li>Line 1 (Header): KEY | de | fr | more iso-language-codes ...</li>
 * <li>Line 2..n (Values): message.key | Deutsch | Francaise | other translation</li>
 * </ul>
 * <p>
 * The header columns define the language used for the resulting resource bundle. <br />
 * Important for treatment:
 * </p>
 * <ul>
 * <li>Header row is mandatory and defining, no empty cells in between languages are allowed</li>
 * <li>Column header for languages must be valid lowercase two-letter ISO-639 codes.</li>
 * <li>Empty rows in the content pane are ignored</li>
 * <li>keys without translations are ignored</li>
 * <li>Rows with empty KEY cell in the content body are ignored</li>
 * <li>Empty translation cells in the content body are ignored</li>
 * <li>Content cells in a column without header are ignored</li>
 * </ul>
 * The concrete row and column numbers can be configured to match more requirements like comments.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 13, 2010
 */
@Component("defaultExcelReader")
public class ExcelReader implements Excel
{
	private static final Logger LOG = LoggerFactory.getLogger(ExcelReader.class);

	/** Default value for languageHeaderRow = 0. */
	public static final int DEFAULT_HEADER_ROW = 0;

	/** Default value for keyColumn = 0. */
	public static final int DEFAULT_KEY_COLUMN = 0;

	/** Default value for firstLanguageColumn = 0. */
	public static final int DEFAULT_FIRST_LANG_COL = 1;

	/** pattern to escape funny excel character ellipsis. */
	private static final String ESCAPE_TRIPLECOLON = String.valueOf((char) 0x2026);

	/** pattern to escape funny excel characters to represent a hyphen/dash. */
	private static final String ESCAPE_DASH = (char) 0xac + "|" + (char) 0x2014 + "|" + (char) 0x2013;

	/** pattern to escape funny excel characters to represent a single quote or apostroph. */
	private static final String ESCAPE_APOSTROPH = "`|´|'|‘|’";

	/** pattern to escape funny excel characters to represent a double quote. */
	private static final String ESCAPE_DOUBLE_QUOTE = "“|”";

	/** languageHeaderRow defines the row number in the sheet that defines the header (default:0, first row). */
	private int languageHeaderRow = DEFAULT_HEADER_ROW;

	/** keyColumn defines the column that is used for the message code (default:0, the first column). */
	private int keyColumn = DEFAULT_KEY_COLUMN;

	/** firstLanguageColumn defines the first column used for languages (default:1, second column). */
	private int firstLanguageColumn = DEFAULT_FIRST_LANG_COL;

	/**
	 * Constructor to create reader for default excel layout.
	 */
	public ExcelReader()
	{
		super();
	}

	/**
	 * Constructor to create a reader for a customized excel.
	 * You can use the DEFAULT_* constants to set defaults for certain values.
	 * 
	 * @param languageHeaderRow defines the row number in the sheet that defines the header (default:0, first row).
	 * @param keyColumn defines the column that is used for the message code (default:0, the first column).
	 * @param firstLanguageColumn defines the first column used for languages (default:1, second column).
	 */
	public ExcelReader(int languageHeaderRow, int keyColumn, int firstLanguageColumn)
	{
		super();
		this.languageHeaderRow = languageHeaderRow;
		this.keyColumn = keyColumn;
		this.firstLanguageColumn = firstLanguageColumn;
	}

	/**
	 * Method processes a provided Excel file and reads the messages in it.
	 * Therefore all sheets in the Excel file are read and processed.
	 * Each sheet of the file must have the form:
	 * <ul>
	 * <li>Line 1 (Header): KEY | de | fr | more iso-language-codes ...</li>
	 * <li>Line 2..n (Values): message.key | Deutsch | Francaise | other translation</li>
	 * </ul>
	 * <p>
	 * The header columns define the language used for the resulting resource bundle. <br />
	 * Important for treatment:
	 * </p>
	 * <ul>
	 * <li>Header row is mandatory and defining, no empty cells in between languages are allowed</li>
	 * <li>Column header for languages must be valid lowercase two-letter ISO-639 codes.</li>
	 * <li>Empty rows in the content pane are ignored</li>
	 * <li>keys without translations are ignored</li>
	 * <li>Rows with empty KEY cell in the content body are ignored</li>
	 * <li>Empty translation cells in the content body are ignored</li>
	 * <li>Content cells in a column without header are ignored</li>
	 * </ul>
	 * Format may be alteret using configuration parameters of this class.
	 * 
	 * @param file the file to read, must be a valid Excel file (xls, xlsx)
	 * @return a List of all property entries
	 * @throws I18nException when something failed during Reading of the file
	 */
	public List<MessageResourceEntry> processExcel(File file) throws I18nException
	{
		InputStream in = null;
		try
		{
			in = new FileInputStream(file);
			return processExcel(in);
		}
		catch (IOException e)
		{
			// damn library throws general exception
			throw new I18nException("Problem with the input file.", e);
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					LOG.error("problem closing file stream " + e, e);
				}
			}
		}
	}

	/**
	 * Method processes a provided Excel file input stream and reads the messages in it.
	 * Therefore all sheets in the Excel file are read and processed.
	 * Each sheet of the file must have the form:
	 * <ul>
	 * <li>Line 1 (Header): KEY | de | fr | more iso-language-codes ...</li>
	 * <li>Line 2..n (Values): message.key | Deutsch | Francaise | other translation</li>
	 * </ul>
	 * <p>
	 * The header columns define the language used for the resulting resource bundle. <br />
	 * Important for treatment:
	 * </p>
	 * <ul>
	 * <li>Header row is mandatory and defining, no empty cells in between languages are allowed</li>
	 * <li>Column header for languages must be valid lowercase two-letter ISO-639 codes.</li>
	 * <li>Empty rows in the content pane are ignored</li>
	 * <li>keys without translations are ignored</li>
	 * <li>Rows with empty KEY cell in the content body are ignored</li>
	 * <li>Empty translation cells in the content body are ignored</li>
	 * <li>Content cells in a column without header are ignored</li>
	 * </ul>
	 * Format may be altered using configuration parameters of this class.
	 * 
	 * @param input the input stream of the file to read, must be a valid Excel file (xls, xlsx)
	 * @return a List of all property entries
	 * @throws I18nException when something failed during Reading of the file
	 */
	public List<MessageResourceEntry> processExcel(InputStream input) throws I18nException
	{
		InputStream in = null;
		try
		{
			// make sure its buffered and compatible with apache poi requirements
			in = new PushbackInputStream(new BufferedInputStream(input));

			List<MessageResourceEntry> entries = new ArrayList<MessageResourceEntry>();
			Workbook workbook = WorkbookFactory.create(in);
			int sheetCount = workbook.getNumberOfSheets();
			for (int i = 0; i < sheetCount; i++)
			{
				String resourceName = Integer.valueOf(i).toString();
				LOG.info("process sheet number [" + resourceName + "]");

				// read sheet information
				Sheet sheet = workbook.getSheetAt(i);
				resourceName = workbook.getSheetName(i);
				LOG.info("sheet name is [" + resourceName + "]");

				// get language count and names(language keys)
				Map<Integer, Locale> langKeys = parseHeaderRow(sheet);

				if (langKeys != null)
				{
					for (Iterator<?> rit = sheet.rowIterator(); rit.hasNext();)
					{
						Row row = (Row) rit.next();
						if (row.getRowNum() > this.languageHeaderRow)
						{
							parseContentBodyRow(entries, langKeys, row, "DEFAULT".equals(resourceName) ? null : resourceName);
						}
					}
				}
				else
				{
					LOG.info("sheet [" + resourceName + "] has no content.");
				}
			}
			return entries;
		}
		// CHECKSTYLE:OFF
		catch (Exception e)
		{
			// damn library throws general exception
			throw new I18nException("Unspecific Error creating Workbook.", e);
		}
		// CHECKSTYLE:ON
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					LOG.error("problem closing file stream " + e, e);
				}
			}
		}
	}

	/**
	 * Method parses a row of the body content and puts the related entry into the result list (entries).
	 * If the row is invalid in any kind, no modification happens. If any of the parameters provided is null or invalid,
	 * no modification happens.
	 * 
	 * @param entries the list of entries, where the result of the row should be put in
	 * @param langKeys the language information to parse the row correctly
	 * @param row the row to be processed.
	 */
	private void parseContentBodyRow(	List<MessageResourceEntry> entries,
										Map<Integer, Locale> langKeys,
										Row row,
										String type)
	{
		if (entries == null || row == null || langKeys == null || langKeys.isEmpty())
		{
			return;
		}
		MessageResourceEntry entry = new MessageResourceEntry();
		String code = getCellStringValue(row.getCell(this.keyColumn));
		if (!StringUtils.isBlank(code))
		{
			entry.setCodeId(StringUtils.trim(code));
			for (int nameIndex = this.firstLanguageColumn; nameIndex <= langKeys.size(); nameIndex++)
			{
				Cell cell = row.getCell(nameIndex);
				String name = getCellStringValue(cell);
				if (!StringUtils.isBlank(name))
				{
					Locale lang = langKeys.get(nameIndex);
					entry.addLang(lang, name);
				}
			}
			if (entry.size() > 0)
			{
				entry.setType(type);
				entries.add(entry);
			}
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug(ToStringBuilder.reflectionToString(entry, ToStringStyle.SHORT_PREFIX_STYLE));
		}
	}

	/**
	 * Method parses the header row of the sheet to extract the languages.
	 * 
	 * @param sheet the Excel sheet to process
	 * @return a Map with column number to language mapping.
	 */
	private Map<Integer, Locale> parseHeaderRow(Sheet sheet) throws I18nException
	{

		if (sheet == null || sheet.getRow(this.languageHeaderRow) == null)
		{
			return null;
		}
		Map<Integer, Locale> langKeys = new HashMap<Integer, Locale>();
		Map<Integer, String> invalidKeys = new HashMap<Integer, String>();
		for (Iterator<?> cit = sheet.getRow(this.languageHeaderRow).cellIterator(); cit.hasNext();)
		{
			Cell cell = (Cell) cit.next();
			if (cell.getColumnIndex() >= this.firstLanguageColumn)
			{
				String value = cell.getRichStringCellValue().getString();
				try
				{
					Locale locale = LocaleUtils.toLocale(value);
					if (locale != null)
					{
						langKeys.put(cell.getColumnIndex(), locale);
					}
					else
					{
						invalidKeys.put(cell.getColumnIndex(), value);
					}
				}
				catch (IllegalArgumentException e)
				{
					invalidKeys.put(cell.getColumnIndex(), value);
				}
			}
		}
		if (!invalidKeys.isEmpty())
		{
			throw new I18nException("Invalid column header in this sheet:" + invalidKeys.toString());
		}
		if (langKeys.isEmpty())
		{
			return null;
		}
		LOG.info("languages found:  [" + langKeys.values() + "]");
		return langKeys;
	}

	/**
	 * Returns the valid String value of the cell, empty String if not a valid String value.
	 * 
	 * @param cell cell to get the value
	 * @return the value as string if valid, empty string else
	 */
	private String getCellStringValue(Cell cell)
	{
		String result = "";
		if (cell != null && cell.getRichStringCellValue() != null && cell.getRichStringCellValue().getString() != null)
		{
			result = cell.getRichStringCellValue().getString();
		}
		return escape(result);
	}

	/**
	 * Does some escaping of funny excel characters.
	 * 
	 * @param input the string to escape
	 * @return the escaped string
	 */
	private String escape(String input)
	{
		String result = input;
		result = result.replaceAll(ESCAPE_TRIPLECOLON, "...");
		result = result.replaceAll(ESCAPE_DASH, "-");
		result = result.replaceAll(ESCAPE_APOSTROPH, "'");
		result = result.replaceAll(ESCAPE_DOUBLE_QUOTE, "\"");
		return result;
	}

	// CHECKSTYLE:OFF
	public void setLanguageHeaderRow(int languageHeaderRow)
	{
		this.languageHeaderRow = languageHeaderRow;
	}

	public void setFirstLanguageColumn(int firstLanguageColumn)
	{
		this.firstLanguageColumn = firstLanguageColumn;
	}

	public void setKeyColumn(int keyColumn)
	{
		this.keyColumn = keyColumn;
	}
	// CHECKSTYLE:ON
}

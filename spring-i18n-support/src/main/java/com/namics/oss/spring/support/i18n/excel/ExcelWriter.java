/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import com.namics.oss.spring.support.i18n.exception.I18nException;
import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;
import com.namics.oss.spring.support.i18n.util.LocaleComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util class that uses apache poi library to write an excel resource file in namics format.
 * The excel sheet may contain several sheets.
 * Each sheet of the file will have the default form:
 * <ul>
 * <li>Line 1 (Header): KEY | de | fr | more iso-language-codes ...</li>
 * <li>Line 2..n (Values): message.key | Deutsch | Francaise | other translation</li>
 * </ul>
 * The header columns are defined by the provided languages in the resources lists.
 * The concrete row and column numbers can be configured to match more requirements like comments.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 18, 2010
 */
public class ExcelWriter implements Excel
{

	private static final Logger LOG = LoggerFactory.getLogger(ExcelWriter.class);

	/** Default value for languageHeaderRow = 0. */
	public static final int DEFAULT_HEADER_ROW = 0;

	/** Default value for keyColumn = 0. */
	public static final int DEFAULT_KEY_COLUMN = 0;

	/** Default value for columnWidth = 60 letters = 60 * 256. */
	public static final int DEFAULT_COLUMN_WIDTH = 256 * 60;

	/** Default value for firstLanguageColumn = 0. */
	public static final int DEFAULT_FIRST_LANG_COL = 1;

	/** Default value for zoom in sheets 75%. */
	public static final int DEFAULT_ZOOM = 75;

	/** languageHeaderRow defines the row number in the sheet that defines the header (default:0, first row). */
	private int languageHeaderRow = DEFAULT_HEADER_ROW;

	/** keyColumn defines the column that is used for the message code (default:0, the first column). */
	private int keyColumn = DEFAULT_KEY_COLUMN;

	/** firstLanguageColumn defines the first column used for languages (default:1, second column). */
	private int firstLanguageColumn = DEFAULT_FIRST_LANG_COL;

	private String defaultSheetName = "messages";

	/** columnWidth for the language columns, key column is autosized. */
	private int columnWidth = DEFAULT_COLUMN_WIDTH;

	/** Zoom level to use in excel sheets, express in integer percent (default: 75 = 75%). */
	private int zoom = DEFAULT_ZOOM;

	/**
	 * Constructor to create writer for default excel layout.
	 */
	public ExcelWriter()
	{
		super();
	}

	/**
	 * Constructor to create a writer for a customized excel.
	 * You can use the DEFAULT_* constants to set defaults for certain values.
	 * 
	 * @param languageHeaderRow defines the row number in the sheet that defines the header (default:0, first row).
	 * @param keyColumn defines the column that is used for the message code (default:0, the first column).
	 * @param firstLanguageColumn defines the first column used for languages (default:1, second column).
	 */
	public ExcelWriter(int languageHeaderRow, int keyColumn, int firstLanguageColumn)
	{
		super();
		this.languageHeaderRow = languageHeaderRow;
		this.keyColumn = keyColumn;
		this.firstLanguageColumn = firstLanguageColumn;
	}

	/**
	 * Creates an excel file to export message data.
	 * The excel workbook will have one sheet.
	 * Each sheet of the file will have the default form:
	 * <ul>
	 * <li>Line 1 (Header): KEY | de | fr | more iso-language-codes ...</li>
	 * <li>Line 2..n (Values): message.key | Deutsch | Francaise | other translation</li>
	 * </ul>
	 * The header columns are defined by the provided languages in the resources lists.
	 * The concrete row and column numbers can be configured to match more requirements like comments.
	 * 
	 * @param input a list of message information to write to excel
	 * @param output file to create output in.
	 * @param format use constants FORMAT_EXCEL_97 or FORMAT_EXCEL_2007
	 * @throws I18nException if a problem occures
	 */
	public void createExcel(List<MessageResourceEntry> input,
							File output,
							String format) throws I18nException
	{
		FileOutputStream fout = null;
		try
		{
			LOG.info("Create Stream from File");
			fout = new FileOutputStream(output);
			createExcel(input, fout, format);
		}
		catch (IOException e)
		{
			throw new I18nException("Problem writing to stream", e);
		}
		finally
		{
			saveCloseStream(fout);
		}
	}

	/**
	 * Creates an excel file to export message data.
	 * The excel workbook will have one sheet.
	 * Each sheet of the file will have the default form:
	 * <ul>
	 * <li>Line 1 (Header): KEY | de | fr | more iso-language-codes ...</li>
	 * <li>Line 2..n (Values): message.key | Deutsch | Francaise | other translation</li>
	 * </ul>
	 * The header columns are defined by the provided languages in the resources lists.
	 * The concrete row and column numbers can be configured to match more requirements like comments.
	 * 
	 * @param input a list of message information to write to excel
	 * @param output if you want to create xls (Excel 97-2003) files, DO NOT use BufferedOutputStream due to library
	 *            issue.
	 * @param format use constants FORMAT_EXCEL_97 or FORMAT_EXCEL_2007
	 * @throws I18nException if a problem occures
	 */
	public void createExcel(List<MessageResourceEntry> input,
							OutputStream output,
							String format) throws I18nException
	{
		Workbook wb = createWorkbook(format);
		createSheet(input, "DEFAULT", wb);
		try
		{
			LOG.info("Write data to output stream");
			wb.write(output);
		}
		catch (IOException e)
		{
			throw new I18nException("Problem writing to stream", e);
		}
	}

	/**
	 * Creates an excel file to export message data.
	 * The excel workbook will have several sheets named after the map key.
	 * Each sheet of the file will have the default form:
	 * <ul>
	 * <li>Line 1 (Header): KEY | de | fr | more iso-language-codes ...</li>
	 * <li>Line 2..n (Values): message.key | Deutsch | Francaise | other translation</li>
	 * </ul>
	 * The header columns are defined by the provided languages in the resources lists.
	 * The concrete row and column numbers can be configured to match more requirements like comments.
	 * 
	 * @param input a list of message information to write to excel
	 * @param output file to create output in.
	 * @param format use constants FORMAT_EXCEL_97 or FORMAT_EXCEL_2007
	 * @throws I18nException if a problem occures
	 */
	public void createExcel(Map<String, List<MessageResourceEntry>> input,
							File output,
							String format) throws I18nException
	{
		FileOutputStream fout = null;
		try
		{
			LOG.info("Create Stream from File");
			fout = new FileOutputStream(output);
			createExcel(input, fout, format);
		}
		catch (IOException e)
		{
			throw new I18nException("Problem writing to stream", e);
		}
		finally
		{
			saveCloseStream(fout);
		}
	}

	/**
	 * Creates an excel file to export message data.
	 * The excel workbook will have several sheets named after the map key.
	 * Each sheet of the file will have the default form:
	 * <ul>
	 * <li>Line 1 (Header): KEY | de | fr | more iso-language-codes ...</li>
	 * <li>Line 2..n (Values): message.key | Deutsch | Francaise | other translation</li>
	 * </ul>
	 * The header columns are defined by the provided languages in the resources lists.
	 * The concrete row and column numbers can be configured to match more requirements like comments.
	 * 
	 * @param input a list of message information to write to excel
	 * @param output if you want to create xls (Excel 97-2003) files, DO NOT use BufferedOutputStream due to library
	 *            issue.
	 * @param format use constants FORMAT_EXCEL_97 or FORMAT_EXCEL_2007
	 * @throws I18nException if a problem occures
	 */
	public void createExcel(Map<String, List<MessageResourceEntry>> input,
							OutputStream output,
							String format) throws I18nException
	{
		Workbook wb = createWorkbook(format);
		for (Entry<String, List<MessageResourceEntry>> entry : input.entrySet())
		{
			createSheet(entry.getValue(), entry.getKey(), wb);
		}
		try
		{
			LOG.info("Write data to output stream");
			wb.write(output);
		}
		catch (IOException e)
		{
			throw new I18nException("Problem writing to stream", e);
		}
	}

	/**
	 * Creates the workbook in the required format.
	 * 
	 * @param format format use constants FORMAT_EXCEL_97 or FORMAT_EXCEL_2007
	 * @return the workbook
	 */
	private Workbook createWorkbook(String format)
	{
		Workbook wb = null;
		if (FORMAT_EXCEL_2007.equals(format))
		{
			wb = new XSSFWorkbook();
			LOG.info("Create workbook for Excel 2007+");
		}
		else if (FORMAT_EXCEL_97.equals(format))
		{
			wb = new HSSFWorkbook();
			LOG.info("Create workbook for Excel 97-2003");
		}
		else
		{
			wb = new XSSFWorkbook();
			LOG.info("No Type selected. Create workbook for Excel 2007+");
		}
		return wb;
	}

	/**
	 * Creates an excel sheet in the provided workbook using provided parameters.
	 * 
	 * @param input the data to put in the sheet-
	 * @param sheetName the name to user for the sheet.
	 * @param wb the workbook to create the sheet in.
	 */
	private void createSheet(	List<MessageResourceEntry> input,
								String sheetName,
								Workbook wb)
	{
		// create a new sheet
		String name = StringUtils.isBlank(sheetName) ? this.defaultSheetName : sheetName;
		LOG.info("Create sheet with name " + name);
		Sheet sheet = wb.createSheet(name);
		sheet.setZoom(this.zoom, 100);

		Map<Locale, Integer> langs = getLanguageInformation(input);
		createHeader(sheet, langs);

		CellStyle keyStyle = sheet.getWorkbook().createCellStyle();
		keyStyle.setAlignment(CellStyle.ALIGN_LEFT);
		keyStyle.setBorderBottom(CellStyle.BORDER_THIN);
		keyStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
		Font f = sheet.getWorkbook().createFont();
		f.setBoldweight(Font.BOLDWEIGHT_NORMAL);
		keyStyle.setFont(f);

		CellStyle valueStyle = sheet.getWorkbook().createCellStyle();
		valueStyle.setAlignment(CellStyle.ALIGN_LEFT);
		valueStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		valueStyle.setBorderBottom(CellStyle.BORDER_THIN);
		valueStyle.setBorderRight(CellStyle.BORDER_THIN);
		valueStyle.setBorderTop(CellStyle.BORDER_THIN);
		valueStyle.setBorderLeft(CellStyle.BORDER_THIN);
		valueStyle.setFont(f);
		valueStyle.setWrapText(true);

		CellStyle emptyStyle = sheet.getWorkbook().createCellStyle();
		emptyStyle.setAlignment(CellStyle.ALIGN_LEFT);
		emptyStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		emptyStyle.setBorderBottom(CellStyle.BORDER_THIN);
		emptyStyle.setBorderRight(CellStyle.BORDER_THIN);
		emptyStyle.setBorderTop(CellStyle.BORDER_THIN);
		emptyStyle.setBorderLeft(CellStyle.BORDER_THIN);
		emptyStyle.setFont(f);
		emptyStyle.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
		emptyStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		emptyStyle.setWrapText(true);

		LOG.info("Write data to sheet " + name);
		int rowIndex = this.languageHeaderRow + 1;
		for (MessageResourceEntry entry : input)
		{
			Row row = sheet.createRow(rowIndex);
			createContentRow(entry, row, langs, keyStyle, valueStyle, emptyStyle);
			rowIndex++;
		}
		sizeColumns(sheet, langs);
		sheet.createFreezePane(this.firstLanguageColumn, this.languageHeaderRow + 1, this.firstLanguageColumn, this.languageHeaderRow + 1);
	}

	/**
	 * Creates the header row for the sheet provided.
	 * 
	 * @param sheet the sheet to create the header for
	 * @param langs the languages to use in the header
	 */
	private void createHeader(	Sheet sheet,
								Map<Locale, Integer> langs)
	{
		LOG.info("Create header row with languages " + langs.toString());
		CellStyle key = sheet.getWorkbook().createCellStyle();
		key.setAlignment(CellStyle.ALIGN_CENTER);
		key.setBorderBottom(CellStyle.BORDER_MEDIUM);
		key.setBorderRight(CellStyle.BORDER_MEDIUM);
		Font f = sheet.getWorkbook().createFont();
		f.setBoldweight(Font.BOLDWEIGHT_BOLD);
		key.setFont(f);

		CellStyle hlang = sheet.getWorkbook().createCellStyle();
		hlang.setAlignment(CellStyle.ALIGN_CENTER);
		hlang.setBorderBottom(CellStyle.BORDER_MEDIUM);
		hlang.setBorderRight(CellStyle.BORDER_THIN);
		hlang.setFont(f);

		Row row = sheet.createRow(this.languageHeaderRow);
		Cell cell = row.createCell(this.keyColumn);
		cell.setCellStyle(key);
		cell.setCellValue("KEY");
		for (Entry<Locale, Integer> lang : langs.entrySet())
		{
			cell = row.createCell(lang.getValue());
			cell.setCellStyle(hlang);
			cell.setCellValue(lang.getKey().toString());
		}
	}

	/**
	 * Does the sizing of the colums, for a first draft the auto size mechanism of excel is used.
	 * 
	 * @param sheet the sheet where the columns should be sized
	 * @param langs the languages to calculate the columns to be sized.
	 */
	private void sizeColumns(	Sheet sheet,
								Map<Locale, Integer> langs)
	{
		LOG.info("Size columns of sheet " + sheet.getSheetName());
		sheet.autoSizeColumn(this.keyColumn, true);
		for (Integer lang : langs.values())
		{
			sheet.setColumnWidth(lang, this.columnWidth);
		}
	}

	/**
	 * Creates a single content row using the provided styles and data.
	 * 
	 * @param entry the data to be filled in that line.
	 * @param row the row to write the cells in
	 * @param langs the languages to get column indexes.
	 * @param keyStyle style to use for the key column (this.keyColumn)
	 * @param valueStyle the style to use for a content cell with content.
	 * @param emptyStyle the style to use for a content cell without content.
	 */
	private void createContentRow(	MessageResourceEntry entry,
									Row row,
									Map<Locale, Integer> langs,
									CellStyle keyStyle,
									CellStyle valueStyle,
									CellStyle emptyStyle)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Create content row [" + row.getRowNum() + "] with "
					+ ToStringBuilder.reflectionToString(entry, ToStringStyle.SHORT_PREFIX_STYLE));
		}
		Cell cell = row.createCell(this.keyColumn);
		cell.setCellValue(entry.getCodeId());
		cell.setCellStyle(keyStyle);
		for (Integer col : langs.values())
		{
			cell = row.createCell(col);
			cell.setCellStyle(emptyStyle);
		}
		for (Entry<Locale, String> message : entry.getNameMappings().entrySet())
		{
			Integer col = langs.get(message.getKey());
			cell = row.getCell(col);
			cell.setCellStyle(valueStyle);
			cell.setCellValue(message.getValue());
		}
	}

	/**
	 * Extracts the language information form the data.
	 * This method iterates all MessagaResourceEntries to get all available languages used in the data.
	 * 
	 * @param entries the entries to extract the used langs
	 * @return the Map to map a certain language to a special column in the process of content row writing
	 */
	private Map<Locale, Integer> getLanguageInformation(List<MessageResourceEntry> entries)
	{
		LOG.info("Extract language information");
		SortedSet<Locale> locales = new TreeSet<Locale>(new LocaleComparator());
		for (MessageResourceEntry entry : entries)
		{
			Map<Locale, String> mappings = entry.getNameMappings();
			if (mappings != null && !mappings.isEmpty())
			{
				for (Locale key : mappings.keySet())
				{
					locales.add(key);
				}
			}
		}
		Map<Locale, Integer> result = new HashMap<Locale, Integer>();
		int colIndex = this.firstLanguageColumn; // calculate the index column for new languages
		for (Locale locale : locales)
		{
			result.put(locale, colIndex);
			colIndex++;
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Extracted languages " + ToStringBuilder.reflectionToString(result, ToStringStyle.SHORT_PREFIX_STYLE));
		}
		return result;
	}

	/**
	 * Saves the Stream seamless, throws i18nException when error.
	 * 
	 * @param fout stream to close.
	 */
	private void saveCloseStream(FileOutputStream fout)
	{
		LOG.info("Try to close output stream seamlessly");
		if (fout != null)
		{
			try
			{
				fout.flush();
				fout.close();
			}
			catch (IOException e)
			{
				throw new I18nException("Problem closing file stream", e);
			}
		}
	}

	// CHECKSTYLE:OFF
	/** Getter for languageHeaderRow. @return the languageHeaderRow */
	public int getLanguageHeaderRow()
	{
		return this.languageHeaderRow;
	}

	/** Getter for keyColumn. @return the keyColumn */
	public int getKeyColumn()
	{
		return this.keyColumn;
	}

	/** Getter for firstLanguageColumn. @return the firstLanguageColumn */
	public int getFirstLanguageColumn()
	{
		return this.firstLanguageColumn;
	}

	// CHECKSTYLE:ON

	/** Getter for defaultSheetName. @return the defaultSheetName */
	public String getDefaultSheetName()
	{
		return this.defaultSheetName;
	}

	/** Setter for defaultSheetName. @param defaultSheetName the defaultSheetName to set */
	public void setDefaultSheetName(String defaultSheetName)
	{
		this.defaultSheetName = defaultSheetName;
	}

	/** Getter for columnWidth. @return the columnWidth */
	public int getColumnWidth()
	{
		return this.columnWidth;
	}

	/** Setter for columnWidth. @param columnWidth the columnWidth to set */
	public void setColumnWidth(int columnWidth)
	{
		this.columnWidth = columnWidth;
	}

	/** Setter for languageHeaderRow. @param languageHeaderRow the languageHeaderRow to set */
	public void setLanguageHeaderRow(int languageHeaderRow)
	{
		this.languageHeaderRow = languageHeaderRow;
	}

	/** Setter for keyColumn. @param keyColumn the keyColumn to set */
	public void setKeyColumn(int keyColumn)
	{
		this.keyColumn = keyColumn;
	}

	/** Setter for firstLanguageColumn. @param firstLanguageColumn the firstLanguageColumn to set */
	public void setFirstLanguageColumn(int firstLanguageColumn)
	{
		this.firstLanguageColumn = firstLanguageColumn;
	}

	/** Getter for zoom. @return the zoom */
	public int getZoom()
	{
		return this.zoom;
	}

	/** Setter for zoom. @param zoom the zoom to set */
	public void setZoom(int zoom)
	{
		this.zoom = zoom;
	}
}

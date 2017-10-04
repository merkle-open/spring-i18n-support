/*
 * Copyright 2000-2012 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.properties;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.namics.oss.spring.support.i18n.exception.I18nException;
import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;
import com.namics.oss.spring.support.i18n.util.LocaleComparator;
import org.apache.commons.lang.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PropertiesWriter.
 * 
 * @author aschaefer, Namics AG
 * @since i18n 1.4 26.04.2012
 */
public class PropertiesWriter
{
	private static final Logger LOG = LoggerFactory.getLogger(PropertiesWriter.class);

	/** Check last modified time stamp default=true. */
	private boolean checkLastModified = true;

	public void writePropertiesSet(	List<MessageResourceEntry> entries,
									String defaultLanguage,
									String resourceName,
									File outputDir,
									String encoding)
	{
		this.writePropertiesSet(entries, defaultLanguage, resourceName, outputDir, encoding, 0L);
	}

	public void writePropertiesSet(	List<MessageResourceEntry> entries,
									String defaultLanguage,
									String resourceName,
									File outputDir,
									String encoding,
									long lastModified)
	{
		this.writePropertiesSet(entries, LocaleUtils.toLocale(defaultLanguage), resourceName, outputDir, encoding, lastModified);
	}

	public void writePropertiesSet(	List<MessageResourceEntry> entries,
									Locale defaultLocale,
									String resourceName,
									File outputDir,
									String encoding)
	{
		this.writePropertiesSet(entries, defaultLocale, resourceName, outputDir, encoding, 0L);
	}

	public void writePropertiesSet(	List<MessageResourceEntry> entries,
									Locale defaultLocale,
									String resourceName,
									File outputDir,
									String encoding,
									long lastModified)
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
		this.writePropertiesSet(entries, locales, defaultLocale, resourceName, outputDir, encoding, lastModified);
	}

	public void writePropertiesSet(	List<MessageResourceEntry> entries,
									Set<Locale> locales,
									Locale defaultLocale,
									String resourceName,
									File outputDir,
									String encoding) throws I18nException
	{
		this.writePropertiesSet(entries, locales, defaultLocale, resourceName, outputDir, encoding, 0L);
	}

	public void writePropertiesSet(	List<MessageResourceEntry> entries,
									Set<Locale> locales,
									Locale defaultLocale,
									String resourceName,
									File outputDir,
									String encoding,
									long lastModified) throws I18nException
	{
		if (!outputDir.exists())
		{
			LOG.info("create output directory  [{}]", outputDir.getName());
			outputDir.mkdirs();
		}

		String fileName = resourceName + ".properties";
		File master = new File(outputDir, fileName);
		if (this.checkLastModified && master.exists() && master.lastModified() >= lastModified)
		{
			LOG.info("skip properties creation, files ({}) are newer then source ({})", new Date(master.lastModified()), new Date(lastModified));
			return;
		}
		LOG.info("write master properties file {}", master.getAbsoluteFile());
		this.writeProperties(entries, defaultLocale, master, encoding);
		for (Locale locale : locales)
		{
			String name = resourceName + "_" + locale.toString() + ".properties";
			File file = new File(outputDir, name);
			LOG.info("write locale [{}] properties file {}", locale, file);
			this.writeProperties(entries, locale, file, encoding);
		}
	}

	public void writeProperties(List<MessageResourceEntry> entries,
								Locale locale,
								File file,
								String encoding) throws I18nException
	{
		try
		{
			this.writeProperties(entries, locale, this.getWriter(file, encoding), encoding);
		}
		catch (Exception e)
		{
			throw new I18nException(e);
		}
	}

	public void writeProperties(List<MessageResourceEntry> entries,
								Locale locale,
								BufferedWriter writer,
								String encoding) throws I18nException
	{
		try
		{
			writer.write("#Generated File by i18n PropertiesWriter.");
			writer.newLine();
			writer.write("#Copyright 2012 Namics AG. All rights reserved.");
			writer.newLine();
			writer.write("#Generation date: " + new Date().toString());
			writer.newLine();
			for (MessageResourceEntry entry : entries)
			{
				String value = this.getOptimizedValue(entry, locale);
				writer.newLine();
				writer.write(entry.getCodeId());
				writer.write("=");
				writer.write(value);
			}
		}
		catch (IOException e1)
		{
			LOG.error("IOException occured. {}", e1);
			throw new I18nException(e1);
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.flush();
					writer.close();
				}
				catch (IOException e)
				{
					LOG.error("Could not close writer. {}", e);
				}
			}
		}
	}

	private String getOptimizedValue(	MessageResourceEntry entry,
										Locale locale)
	{
		String result = "";
		if (entry != null && locale != null)
		{
			result = entry.getName(locale);
			result = result != null ? result : "";
			result = result.trim();
		}
		return result;
	}

	/**
	 * helper method to get a file writer with configured encoding.
	 * 
	 * @param file
	 *            the file to open a writer for
	 * @param encoding
	 *            encoding to be used
	 * @return a Buffered writer with configured encoding.
	 * @throws IOException
	 *             a problem with IO.
	 */
	private BufferedWriter getWriter(	File file,
										String encoding) throws IOException
	{
		OutputStream fout = new FileOutputStream(file);
		return this.getWriter(fout, encoding);
	}

	/**
	 * helper method to get a file writer with configured encoding.
	 * 
	 * @param file
	 *            the file to open a writer for
	 * @param encoding
	 *            encoding to be used
	 * @return a Buffered writer with configured encoding.
	 * @throws IOException
	 *             a problem with IO.
	 */
	private BufferedWriter getWriter(	OutputStream outputStream,
										String encoding) throws IOException
	{
		OutputStream bout = this.getBufferedOutputStream(outputStream);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(bout, encoding));
		return writer;
	}

	/**
	 * OutputStream to be wrapped if it is possible to wrap.
	 * 
	 * @param stream
	 *            Stream that needs to be buffered
	 * @return the wrapped Stream with Buffer
	 */
	private BufferedOutputStream getBufferedOutputStream(OutputStream stream)
	{
		if (stream instanceof BufferedOutputStream)
		{
			return (BufferedOutputStream) stream;
		}
		BufferedOutputStream bout = new BufferedOutputStream(stream);
		return bout;
	}

	/** Getter for checkLastModified default=true. @return the checkLastModified */
	public boolean isCheckLastModified()
	{
		return this.checkLastModified;
	}

	/** Setter for checkLastModified default=true. @param checkLastModified the checkLastModified to set */
	public void setCheckLastModified(boolean checkLastModified)
	{
		this.checkLastModified = checkLastModified;
	}
}

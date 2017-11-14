/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.service;

import com.namics.oss.spring.support.i18n.dao.MessageSourceManagementDao;
import com.namics.oss.spring.support.i18n.dao.jpa.model.MessageResource;
import com.namics.oss.spring.support.i18n.excel.Excel;
import com.namics.oss.spring.support.i18n.excel.ExcelReader;
import com.namics.oss.spring.support.i18n.excel.ExcelWriter;
import com.namics.oss.spring.support.i18n.exception.I18nException;
import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;
import com.namics.oss.spring.support.i18n.text.TextWriter;
import com.namics.oss.spring.support.i18n.util.LocaleComparator;
import com.namics.oss.spring.support.i18n.util.MessageResourceEntryComparator;
import org.apache.commons.lang.LocaleUtils;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.management.MXBean;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.Map.Entry;

/**
 * MessageManagementServiceImpl.
 *
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 19, 2010
 */
@MXBean
public class MessageManagementServiceImpl implements MessageManagementService {

	private MessageSourceManagementDao messageSourceDao;

	private Comparator<MessageResourceEntry> messageResourceEntryComparator = new MessageResourceEntryComparator();

	private Comparator<Locale> localeComparator = new LocaleComparator();

	private ExcelWriter excelWriter;

	private ExcelReader excelReader;

	private TextWriter textWriter;

	@PostConstruct
	public void init() {
		Assert.notNull(this.messageSourceDao, "messageSourceDao must be configured!");
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see MessageManagementService#getMessage(java.lang.String, java.util.Locale)
	 */
	@Override
	public String getMessage(String codeId,
	                         Locale locale) throws I18nException {
		List<MessageResource> messages = this.messageSourceDao.findByCodeAndLang(codeId, locale.toString());
		if (messages != null && messages.size() > 0) {
			return messages.get(0).getMessage();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see MessageManagementService#getAllMessages()
	 */
	@Override
	public List<MessageResourceEntry> getAllMessages() throws I18nException {
		List<MessageResourceEntry> result = convertMessageMapToResourceEntries(this.messageSourceDao.findAll());
		Collections.sort(result, this.messageResourceEntryComparator);
		return result;


	}

	/**
	 * {@inheritDoc}
	 *
	 * @see MessageManagementService#getAllMessagesGrouped()
	 */
	@Override
	public Map<String, List<MessageResourceEntry>> getAllMessagesGrouped() throws I18nException {
		List<MessageResourceEntry> data = getAllMessages();
		Map<String, List<MessageResourceEntry>> result = new HashMap<>(data.size());
		for (MessageResourceEntry entry : data) {
			String type = entry.getType() != null ? entry.getType() : "DEFAULT";
			if (!result.containsKey(type)) {
				result.put(type, new ArrayList<MessageResourceEntry>());
			}
			result.get(type).add(entry);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see MessageManagementService#getMessage(java.lang.String)
	 */
	@Override
	public MessageResourceEntry getMessage(String codeId) throws I18nException {
		List<MessageResourceEntry> messages = getAllMessages();
		for (MessageResourceEntry entry : messages) {
			if (entry.getCodeId().equals(codeId)) {
				return entry;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see MessageManagementService#putMessage(java.lang.String, java.util.Locale,
	 * java.lang.String)
	 */
	@Override
	public void putMessage(String codeId,
	                       Locale locale,
	                       String message,
	                       String type) throws I18nException {
		Assert.hasLength(codeId);
		Assert.notNull(locale);
		LocaleUtils.toLocale(locale.toString()); // this validates the locale
		Assert.hasLength(message);
		MessageResource persisted = this.messageSourceDao.findTopByCodeAndLang(codeId, locale.toString());
		if (persisted != null) {
			//update case
			persisted.message(message).type(type);
		} else {
			//insert case
			persisted = new MessageResource()
					.code(codeId)
					.lang(locale.toString())
					.message(message)
					.type(type);
			persisted.id((type == null ? "" : type) + codeId + locale.toString());
		}
		this.messageSourceDao.save(persisted);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see MessageManagementService#putMessage(java.lang.String, java.util.Locale,
	 * java.lang.String)
	 */
	@Override
	public void putMessage(String codeId,
	                       Locale locale,
	                       String message) throws I18nException {
		putMessage(codeId, locale, message, null);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see MessageManagementService#exportToExcel2007(java.io.OutputStream)
	 */
	@Override
	public void exportToExcel2007(OutputStream output) throws I18nException {
		Assert.notNull(output);
		Assert.notNull(this.excelWriter, "you have to configure an excelWriter to use this operation.");
		Map<String, List<MessageResourceEntry>> exportable = getAllMessagesGrouped();
		this.excelWriter.createExcel(exportable, output, Excel.FORMAT_EXCEL_2007);
	}

	/**
	 * Be careful: Do not use a BufferedOutputStream for this implementation. {@inheritDoc}
	 *
	 * @see MessageManagementService#exportToExcel97(java.io.OutputStream)
	 */
	@Override
	public void exportToExcel97(OutputStream output) throws I18nException {
		Assert.notNull(output);
		Assert.notNull(this.excelWriter, "you have to configure an excelWriter to use this operation.");
		Assert.isTrue(!(output instanceof BufferedOutputStream));
		Map<String, List<MessageResourceEntry>> exportable = getAllMessagesGrouped();
		this.excelWriter.createExcel(exportable, output, Excel.FORMAT_EXCEL_97);
	}

	@Override
	public void exportToText(OutputStream output) throws I18nException {
		Assert.notNull(output);
		Assert.notNull(this.textWriter, "you have to configure a textWriter to use this operation.");
		Map<String, List<MessageResourceEntry>> exportable = getAllMessagesGrouped();
		this.textWriter.createOutput(exportable, output);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see MessageManagementService#importFromExcel(java.io.InputStream)
	 */
	@Override
	public void importFromExcel(InputStream input) throws I18nException {
		Assert.notNull(input);
		Assert.notNull(this.excelReader, "you have to configure an excelReader to use this operation.");
		List<MessageResourceEntry> data = this.excelReader.processExcel(input);
		for (MessageResourceEntry entry : data) {
			String codeId = entry.getCodeId();
			for (Entry<Locale, String> langVal : entry.getNameMappings().entrySet()) {
				putMessage(codeId, langVal.getKey(), langVal.getValue(), entry.getType());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see MessageManagementService#getAvailableLanguages()
	 */
	@Override
	public List<Locale> getAvailableLanguages() throws I18nException {
		List<Locale> locales = convertStringLangsToLocale(this.messageSourceDao.findDistinctLang());
		Collections.sort(locales, this.localeComparator);
		return locales;

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see MessageManagementService#deleteMessage(java.lang.String)
	 */
	@Override
	public void deleteMessage(String codeId) throws I18nException {
		Long rowsAffected = this.messageSourceDao.deleteByCode(codeId);
		if (rowsAffected == null || rowsAffected < 1) {
			throw new I18nException("no message deleted with this code");
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see MessageManagementService#deleteMessage(java.lang.String)
	 */
	@Override
	public void deleteMessageForLanguage(String codeId, Locale locale) throws I18nException {
		Long rowsAffected = this.messageSourceDao.deleteByCodeAndLang(codeId, locale.toString());
		if (rowsAffected == null || rowsAffected < 0) {
			throw new I18nException("error on deleting message with code " + codeId);
		}
	}

	private List<Locale> convertStringLangsToLocale(List<String> languages) {
		List<Locale> locales = new ArrayList<>(languages.size());
		for (String lang : languages) {
			locales.add(LocaleUtils.toLocale(lang));
		}
		return locales;
	}

	private List<MessageResourceEntry> convertMessageMapToResourceEntries(List<MessageResource> input) {
		Map<String, MessageResourceEntry> entries = new HashMap<>(input.size());
		for (MessageResource entry : input) {
			if (entries.containsKey(entry.getCode())) {
				entries.get(entry.getCode()).addLang(LocaleUtils.toLocale(entry.getLang()), entry.getMessage());
			} else {
				MessageResourceEntry data = new MessageResourceEntry();
				data.setCodeId(entry.getCode());
				data.addLang(LocaleUtils.toLocale(entry.getLang()), entry.getMessage());
				data.setType(entry.getType());
				entries.put(entry.getCode(), data);
			}
		}
		List<MessageResourceEntry> result = new ArrayList<>(entries.size());
		result.addAll(entries.values());
		return result;
	}

	// CHECKSTYLE:OFF

	/**
	 * Setter for messageSourceDao. @param messageSourceDao the messageSourceDao to set
	 */
	public void setMessageSourceDao(MessageSourceManagementDao messageSourceDao) {
		this.messageSourceDao = messageSourceDao;
	}

	/**
	 * Setter for excelWriter. @param excelWriter the excelWriter to set
	 */
	public void setExcelWriter(ExcelWriter excelWriter) {
		this.excelWriter = excelWriter;
	}

	/**
	 * Setter for excelReader. @param excelReader the excelReader to set
	 */
	public void setExcelReader(ExcelReader excelReader) {
		this.excelReader = excelReader;
	}

	/**
	 * Setter for messageResourceEntryComparator. @param messageResourceEntryComparator the
	 * messageResourceEntryComparator to set
	 */
	public void setMessageResourceEntryComparator(Comparator<MessageResourceEntry> messageResourceEntryComparator) {
		this.messageResourceEntryComparator = messageResourceEntryComparator;
	}

	/**
	 * Setter for localeComparator. @param localeComparator the localeComparator to set
	 */
	public void setLocaleComparator(Comparator<Locale> localeComparator) {
		this.localeComparator = localeComparator;
	}

	/**
	 * Setter for textWriter. @param textWriter the textWriter to set
	 */
	public void setTextWriter(TextWriter textWriter) {
		this.textWriter = textWriter;
	}

	// CHECKSTYLE:ON
}

/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.service;

import com.namics.oss.spring.support.i18n.excel.ExcelWriter;
import com.namics.oss.spring.support.i18n.exception.I18nException;
import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;
import com.namics.oss.spring.support.i18n.excel.ExcelReader;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A management service providing methods to maintain message resources in the system.
 * You will find it quite hand in combination with jdbc based message infrastructure.
 *
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 19, 2010
 */
@Transactional(readOnly = true, rollbackFor = I18nException.class)
public interface MessageManagementService {
	/**
	 * Gets a single message by code and locale from the backend.
	 *
	 * @param codeId the code to resolve a message for
	 * @param locale the language in which the message should be resolved-s
	 * @return the Message from the datastore
	 * @throws I18nException when something technical went wrong
	 */
	public String getMessage(String codeId,
	                         Locale locale) throws I18nException;

	/**
	 * Gets all translations for a single message by codefrom the backend.
	 *
	 * @param codeId the code to resolve a message for
	 * @return the Message from the datastore
	 * @throws I18nException when something technical went wrong
	 */
	public MessageResourceEntry getMessage(String codeId) throws I18nException;

	/**
	 * Method to resolve all messages available in the maintained data source.
	 *
	 * @return a list with all messages in the maintained data source
	 * @throws I18nException when something technical went wrong
	 */
	public List<MessageResourceEntry> getAllMessages() throws I18nException;

	/**
	 * Method to resolve all messages available in the maintained data source.
	 *
	 * @return a map with all messages in the maintained data source grouped by type
	 * @throws I18nException when something technical went wrong
	 */
	public Map<String, List<MessageResourceEntry>> getAllMessagesGrouped() throws I18nException;

	/**
	 * inserts or updates a single message by code and locale from the backend.
	 * Internally calls the version with type with a default value
	 *
	 * @param codeId  the code to resolve a message for
	 * @param locale  the language in which the message should be resolved-s
	 * @param message the translation to put
	 * @throws I18nException when something technical went wrong
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void putMessage(String codeId,
	                       Locale locale,
	                       String message) throws I18nException;

	/**
	 * inserts or updates a single message by code and locale from the backend.
	 *
	 * @param codeId  the code to resolve a message for
	 * @param locale  the language in which the message should be resolved-s
	 * @param message the translation to put
	 * @param type    the message type
	 * @throws I18nException when something technical went wrong
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void putMessage(String codeId,
	                       Locale locale,
	                       String message,
	                       String type) throws I18nException;

	/**
	 * deletes a single message by code from the backend.
	 *
	 * @param codeId the code of the message to delete
	 * @throws I18nException when something technical went wrong
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteMessage(String codeId) throws I18nException;

	/**
	 * deletes a single message by code and language from the backend.
	 *
	 * @param codeId the code of the message to delete
	 * @param locale the locale of the language of the message to delete
	 * @throws I18nException when something technical went wrong
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	void deleteMessageForLanguage(String codeId, Locale locale) throws I18nException;

	/**
	 * This method will write all messages in the datasource in MS Excel 97 format to the provided
	 * <code>OutputStream</code>.
	 * For more details about the format refer to the <code>ExcelWriter</code>
	 *
	 * @param output a Stream where the excel content should be written to DO NOT use a BufferedOutputStream
	 *               here (Assertion will fail since the library has a problem with this), be sure to close the stream
	 *               after this operation.
	 * @throws I18nException when something technically went wrong
	 * @see ExcelWriter
	 */
	public void exportToExcel97(OutputStream output) throws I18nException;

	/**
	 * This method will write all messages in the datasource in MS Excel 2007 format to the provided
	 * <code>OutputStream</code>.
	 * For more details about the format refer to the <code>ExcelWriter</code>
	 *
	 * @param output a Stream where the excel content should be written to, be sure to close the stream after this
	 *               operation.
	 * @throws I18nException when something technically went wrong
	 * @see ExcelWriter
	 */
	public void exportToExcel2007(OutputStream output) throws I18nException;

	/**
	 * Method writes a text representation for all messages in the system to the output stream.
	 *
	 * @param output Stream to write to
	 * @throws I18nException when something technically went wrong
	 */
	public void exportToText(OutputStream output) throws I18nException;

	/**
	 * This method will read all messages form the MS Excel file content provided as <code>InputStream</code>.
	 * The workbook version (97/2007) will be detected automatically from the content.
	 * For more details about the format refer to the <code>ExcelReader</code>
	 *
	 * @param input an open and valid input stream, be sure to close the stream after this operation.
	 * @throws I18nException when something technically went wrong
	 * @see ExcelReader
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void importFromExcel(InputStream input) throws I18nException;

	/**
	 * Returns a list of currently available languages.
	 *
	 * @return the List with languagaes
	 * @throws I18nException problem with resolving of languages
	 */
	public List<Locale> getAvailableLanguages() throws I18nException;

}

/*
 * Copyright 2000-2011 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.text;

import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.namics.oss.spring.support.i18n.exception.I18nException;
import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;

/**
 * TextWriter.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.3 - Oct 18, 2010
 */
public interface TextWriter
{
	/**
	 * Writes all messages in a configured format to the defined output.
	 * 
	 * @param input a list of message information to write to a text representation
	 * @param output a file to write the generated text to
	 * @throws I18nException
	 */
	public void createOutput(	List<MessageResourceEntry> input,
								File output) throws I18nException;

	/**
	 * Writes all messages in a configured format to the defined output.
	 * 
	 * @param input a list of message information to write to a text representation
	 * @param output a stream to write the generated text to
	 * @throws I18nException if a problem occures
	 */
	public void createOutput(	List<MessageResourceEntry> input,
								OutputStream output) throws I18nException;

	/**
	 * Writes all messages in a configured format to the defined output.
	 * 
	 * @param input a list of message information to write to a text representation
	 * @param output a file to write the generated text to
	 * @throws I18nException if a problem occures
	 */
	public void createOutput(	Map<String, List<MessageResourceEntry>> input,
								File output) throws I18nException;

	/**
	 * Writes all messages in a configured format to the defined output.
	 * 
	 * @param input a list of message information to write to a text representation
	 * @param output a stream to write the generated text to
	 * @throws I18nException if a problem occures
	 */
	public void createOutput(	Map<String, List<MessageResourceEntry>> input,
								OutputStream output) throws I18nException;

}

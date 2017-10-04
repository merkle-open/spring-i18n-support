/*
 * Copyright 2000-2011 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.text;

import com.namics.oss.spring.support.i18n.dao.SimpleJdbcMessageSourceDao;
import com.namics.oss.spring.support.i18n.exception.I18nException;
import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * {@link TextWriter} implementation to write preconfigured SQL scripts.
 * Usually the configuration is analog to {@link SimpleJdbcMessageSourceDao}
 *
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.3 - Oct 18, 2010
 */
public class SqlScriptWriter implements TextWriter {

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SqlScriptWriter.class);

	/**
	 * Template to use for script generation.
	 * To use a custom template you can create a plain text file.
	 * This file must contain a placeholder <code>##BODY##</code> to define
	 * where the body should be placed.
	 * There is a default template provided with this artifact that can be resolved
	 * by a class loader: <code>classpath:templates/template.sql</code> If you have annotation processing enabled for
	 * this package, this template will be
	 * autowired.
	 */
	@Value("${classpath*:/templates/template.sql}")
	protected Resource scriptTemplate;

	/**
	 * Template to use for script generation.
	 * To use a custom template you can create a plain text file.
	 * This file must contain a placeholder <code>##BODY##</code> to define
	 * where the body should be placed.
	 */
	protected String scriptTemplateContent;

	/**
	 * String to create an INSERT statement to insert a single message in a specific language.
	 * During script generation process the following placeholders are resolved:
	 * <ul>
	 * <li>##TABLE## the table name is replaced with parameter <code>table</code></li>
	 * <li>##MESSAGE## the message column name is replaced with parameter <code>message</code></li>
	 * <li>##CODEID##the codeId column name is replaced with parameter <code>codeId</code></li>
	 * <li>##LANGID##the langId column name is replaced with parameter <code>langId</code></li>
	 * </ul>
	 * For a working compatible statement the following is required:
	 * <ul>
	 * <li>parameter for the message is named <code>:message</code></li>
	 * <li>parameter for the codeId is named <code>:codeid</code></li>
	 * <li>parameter for the langId is named <code>:langid</code></li>
	 * <li>parameter for the type is named <code>:type</code></li>
	 * </ul>
	 * The preconfigured default is: <code>INSERT INTO ##TABLE## (##CODEID##, ##LANGID## ,##MESSAGE##) VALUES (:codeid,:langid,:message,:type)</code>
	 */
	protected String insertStatementTemplate = "INSERT INTO ##TABLE## (##CODEID##, ##LANGID##, ##MESSAGE##, ##TYPE##) VALUES (:codeid, :langid, :message, :type);";

	/**
	 * The placeholder used in template to place the content.
	 */
	protected String bodyPlaceholder = "##BODY##";

	/**
	 * value to replace placeholder <code>##TABLE##</code>.
	 */
	protected String table = "MESSAGES";

	/**
	 * value to replace placeholder <code>##CODEID##</code>.
	 */
	protected String codeId = "CODEID";

	/**
	 * value to replace placeholder <code>##LANGID##</code>.
	 */
	protected String langId = "LANGID";

	/**
	 * value to replace placeholder <code>##MESSAGE##</code>.
	 */
	protected String message = "MESSAGE";

	/**
	 * value to replace placeholder <code>##TYPE##</code>.
	 */
	protected String type = "TYPE";

	/**
	 * Character to use for line breaks - current system property <code>line.separator</code> is default .
	 */
	protected String lineBreak = System.getProperty("line.separator", "\n");

	/**
	 * With this map further placeholders can be configured to be replaced in the Templates.
	 * This might be useful when you have more complex structures in the database.<br/>
	 * Using this mechanism you can configure specific setups like environment dependency.
	 * Be careful with placeholders and make sure that they are unique and recognizable.
	 * You can use a pattern like <code>##PLACEHOLDER##</code>.<br/>
	 * There are RESERVED placeholders that are replaced with configured values
	 * before evaluation of this map:
	 * <ul>
	 * <li><code>##TABLE##</code></li>
	 * <li><code>##MESSAGE##</code></li>
	 * <li><code>##LANGID##</code></li>
	 * <li><code>##CODEID##</code></li>
	 * </ul>
	 */
	protected Map<String, String> placeholders;

	/**
	 * Encoding used to write data.
	 */
	protected String encoding = "UTF-8";

	/**
	 * {@inheritDoc}
	 *
	 * @see TextWriter#createOutput(java.util.List, java.io.File)
	 */
	@Override
	public void createOutput(List<MessageResourceEntry> input,
	                         File output) throws I18nException {
		Map<String, List<MessageResourceEntry>> wrapper = new HashMap<String, List<MessageResourceEntry>>(1);
		wrapper.put("DUMMY", input);
		String script = createScript(wrapper);
		try {
			FileUtils.writeStringToFile(output, script, encoding);
		} catch (IOException e) {
			throw new I18nException("Problem writing to output file");
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see TextWriter#createOutput(java.util.List, java.io.OutputStream)
	 */
	@Override
	public void createOutput(List<MessageResourceEntry> input,
	                         OutputStream output) throws I18nException {
		Map<String, List<MessageResourceEntry>> wrapper = new HashMap<String, List<MessageResourceEntry>>(1);
		wrapper.put("DUMMY", input);
		String script = createScript(wrapper);
		try {
			IOUtils.write(script, output, encoding);
		} catch (IOException e) {
			throw new I18nException("Problem writing to output file");
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see TextWriter#createOutput(java.util.Map, java.io.File)
	 */
	@Override
	public void createOutput(Map<String, List<MessageResourceEntry>> input,
	                         File output) throws I18nException {
		String script = createScript(input);
		try {
			FileUtils.writeStringToFile(output, script, encoding);
		} catch (IOException e) {
			throw new I18nException("Problem writing to output file");
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see TextWriter#createOutput(java.util.Map, java.io.OutputStream)
	 */
	@Override
	public void createOutput(Map<String, List<MessageResourceEntry>> input,
	                         OutputStream output) throws I18nException {
		String script = createScript(input);
		try {
			IOUtils.write(script, output, encoding);
		} catch (IOException e) {
			throw new I18nException("Problem writing to output file");
		}
	}

	private String createScript(Map<String, List<MessageResourceEntry>> input) {
		StringBuilder body = new StringBuilder();
		for (Entry<String, List<MessageResourceEntry>> entry : input.entrySet()) {
			body
					.append(this.lineBreak)
					.append("-- ------------------------")
					.append(this.lineBreak)
					.append("-- ")
					.append(entry.getKey())
					.append(this.lineBreak)
					.append("-- ------------------------")
					.append(this.lineBreak)
					.append(this.lineBreak);

			List<MessageResourceEntry> entires = entry.getValue();
			Collections.sort(entires, new Comparator<MessageResourceEntry>() {
				@Override
				public int compare(MessageResourceEntry o1, MessageResourceEntry o2) {
					return o1.getCodeId().compareToIgnoreCase(o2.getCodeId());
				}
			});

			for (MessageResourceEntry msgEntry : entires) {
				createEntryLines(msgEntry, body);
			}
		}
		try {
			if (!StringUtils.hasText(scriptTemplateContent)) {
				scriptTemplateContent = IOUtils.toString(this.scriptTemplate.getInputStream());
			}
			String script = scriptTemplateContent;
			script = replacePlaceholders(script);
			String bodyText = body.toString().replaceAll("\\$", "#dollar#");
			script = script.replaceAll(this.bodyPlaceholder, bodyText);
			script = script.replaceAll("##DATE##", SimpleDateFormat.getDateTimeInstance().format(new Date()));
			script = script.replaceAll("#dollar#", "\\$");
			script = script.replaceAll("##ENCODING##", encoding);
			return script;
		} catch (IOException e) {
			throw new I18nException("Problem with script creation template " + scriptTemplate, e);
		}
	}

	/**
	 * Creates the text lines for the values of the entry.
	 *
	 * @param entry the data to be filled in that line.
	 * @param sb    string builder to append lines for this entry
	 */
	private void createEntryLines(MessageResourceEntry entry,
	                              StringBuilder sb) {
		for (Entry<Locale, String> message : entry.getNameMappings().entrySet()) {
			// :codeid,:langid,:message,:type
			String line = this.insertStatementTemplate;
			line = replacePlaceholders(line);
			line = line.replaceAll(":codeid", "'" + entry.getCodeId().replaceAll("\\$", "#dollar#") + "'");
			line = line.replaceAll(":type", entry.getType() == null ? "NULL" : "'" + entry.getType().replaceAll("\\$", "#dollar#") + "'");
			line = line.replaceAll(":langid", "'" + message.getKey().toString().replaceAll("\\$", "#dollar#") + "'");
			String msg = message.getValue().replaceAll("'", "''").replaceAll("\\$", "#dollar#");
			line = line.replaceAll(":message", "'" + msg + "'");
			sb.append(line).append(this.lineBreak);
		}
	}

	/**
	 * Replaces the placeholders in the query template with correct configured item names.
	 */
	private String replacePlaceholders(String template) {
		LOG.info("Replace placeholders " + template);
		String result = template.replaceAll("##MESSAGE##", this.message);
		result = result.replaceAll("##TABLE##", this.table);
		result = result.replaceAll("##CODEID##", this.codeId);
		result = result.replaceAll("##LANGID##", this.langId);
		result = result.replaceAll("##TYPE##", this.type);
		if (!CollectionUtils.isEmpty(this.placeholders)) {
			for (Entry<String, String> entry : this.placeholders.entrySet()) {
				result = result.replaceAll(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	/**
	 * Setter for scriptTemplate. @param scriptTemplate the scriptTemplate to set
	 */
	public void setScriptTemplate(Resource scriptTemplate) {
		this.scriptTemplate = scriptTemplate;
	}

	/**
	 * Setter for insertStatementTemplate. @param insertStatementTemplate the insertStatementTemplate to set
	 */
	public void setInsertStatementTemplate(String insertStatementTemplate) {
		this.insertStatementTemplate = insertStatementTemplate;
	}

	/**
	 * Setter for bodyPlaceholder. @param bodyPlaceholder the bodyPlaceholder to set
	 */
	public void setBodyPlaceholder(String bodyPlaceholder) {
		this.bodyPlaceholder = bodyPlaceholder;
	}

	/**
	 * Setter for table. @param table the table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * Setter for codeId. @param codeId the codeId to set
	 */
	public void setCodeId(String codeId) {
		this.codeId = codeId;
	}

	/**
	 * Setter for langId. @param langId the langId to set
	 */
	public void setLangId(String langId) {
		this.langId = langId;
	}

	/**
	 * Setter for message. @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Setter for type. @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Setter for lineBreak. @param lineBreak the lineBreak to set
	 */
	public void setLineBreak(String lineBreak) {
		this.lineBreak = lineBreak;
	}

	/**
	 * Setter for placeholders. @param placeholders the placeholders to set
	 */
	public void setPlaceholders(Map<String, String> placeholders) {
		this.placeholders = placeholders;
	}


	public void setScriptTemplateContent(String scriptTemplateContent) {
		this.scriptTemplateContent = scriptTemplateContent;
	}


	public SqlScriptWriter scriptTemplate(Resource scriptTemplate) {
		setScriptTemplate(scriptTemplate);
		return this;
	}

	public SqlScriptWriter scriptTemplateContent(String scriptTemplateContent) {
		setScriptTemplateContent(scriptTemplateContent);
		return this;
	}

	public SqlScriptWriter insertStatementTemplate(String insertStatementTemplate) {
		setInsertStatementTemplate(insertStatementTemplate);
		return this;
	}

	public SqlScriptWriter bodyPlaceholder(String bodyPlaceholder) {
		setBodyPlaceholder(bodyPlaceholder);
		return this;
	}

	public SqlScriptWriter table(String table) {
		setTable(table);
		return this;
	}

	public SqlScriptWriter codeId(String codeId) {
		setCodeId(codeId);
		return this;
	}

	public SqlScriptWriter langId(String langId) {
		setLangId(langId);
		return this;
	}

	public SqlScriptWriter message(String message) {
		setMessage(message);
		return this;
	}

	public SqlScriptWriter type(String type) {
		setType(type);
		return this;
	}

	public SqlScriptWriter lineBreak(String lineBreak) {
		setLineBreak(lineBreak);
		return this;
	}

	public SqlScriptWriter placeholders(Map<String, String> placeholders) {
		setPlaceholders(placeholders);
		return this;
	}
}

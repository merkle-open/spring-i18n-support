/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.dao;

import com.namics.oss.spring.support.i18n.dao.jpa.model.MessageResource;
import com.namics.oss.spring.support.i18n.exception.DataAccessException;
import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * SimpleJdbcMessageSourceDao provides a basic spring based implementation for using SQL Database with JDBC. You can configure SQL queries including
 * parameter replacement. Watch concrete property descriptions for details.
 *
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 18, 2010
 */
public class SimpleJdbcMessageSourceDao extends JdbcDaoSupport implements MessageSourceManagementDao {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleJdbcMessageSourceDao.class);

	/**
	 * Query to use to resolve a single message in a specific language. The query is compiled at initialization time,
	 * this means some placeholders are
	 * resolved:
	 * <ul>
	 * <li>##TABLE## the table name is replaced with parameter <code>table</code></li>
	 * <li>##MESSAGE## the message column name is replaced with parameter <code>message</code></li>
	 * <li>##CODEID##the codeId column name is replaced with parameter <code>codeId</code></li>
	 * <li>##LANGID##the langId column name is replaced with parameter <code>langId</code></li>
	 * </ul>
	 * For a working compatible query the following is required:
	 * <ul>
	 * <li>message column is aliased to <code>message</code></li>
	 * <li>codeId column is aliased to <code>codeid</code></li>
	 * <li>langId column is aliased to <code>langid</code></li>
	 * <li>type column is aliased to <code>type</code></li>
	 * <li>name of the named parameter for codeId is codeid</li>
	 * <li>name of the named parameter for langId is langid</li>
	 * </ul>
	 * The preconfigured default is: <code>SELECT ##MESSAGE## message,##CODEID## codeid,##LANGID## langid, ##TYPE## mtype FROM ##TABLE## WHERE ##CODEID##=:codeid AND ##LANGID##=:langid</code>
	 */
	private String singleMessageQuery = "SELECT ##MESSAGE## message,##CODEID## codeid,##LANGID## langid, ##TYPE## mtype FROM ##TABLE## WHERE ##CODEID##=:codeid AND ##LANGID##=:langid";

	/**
	 * Query to use to resolve a all messages from the table. The query is compiled at initialization time, this means some placeholders are
	 * resolved:
	 * <ul>
	 * <li>##TABLE## the table name is replaced with parameter <code>table</code></li>
	 * <li>##MESSAGE## the message column name is replaced with parameter <code>message</code></li>
	 * <li>##CODEID##the codeId column name is replaced with parameter <code>codeId</code></li>
	 * <li>##LANGID##the langId column name is replaced with parameter <code>langId</code></li>
	 * <li>##TYPE##the type column name is replaced with parameter <code>type</code></li>
	 * </ul>
	 * For a working compatible query the following is required:
	 * <ul>
	 * <li>message column is aliased to <code>message</code></li>
	 * <li>codeId column is aliased to <code>codeid</code></li>
	 * <li>langId column is aliased to <code>langid</code></li>
	 * <li>type column is aliased to <code>type</code></li>
	 * </ul>
	 * The preconfigured default is: <code>SELECT ##MESSAGE## message,##CODEID## codeid,##LANGID## langid, ##TYPE## type  FROM ##TABLE##</code>
	 */
	private String allMessagesQuery = "SELECT ##MESSAGE## message,##CODEID## codeid,##LANGID## langid, ##TYPE## mtype FROM ##TABLE##";

	/**
	 * Query to insert a single message in a specific language. The query is compiled at initialization time, this means some placeholders are
	 * resolved:
	 * <ul>
	 * <li>##TABLE## the table name is replaced with parameter <code>table</code></li>
	 * <li>##MESSAGE## the message column name is replaced with parameter <code>message</code></li>
	 * <li>##CODEID##the codeId column name is replaced with parameter <code>codeId</code></li>
	 * <li>##LANGID##the langId column name is replaced with parameter <code>langId</code></li>
	 * </ul>
	 * For a working compatible query the following is required:
	 * <ul>
	 * <li>parameter for the message is named <code>:message</code></li>
	 * <li>parameter for the codeId is named <code>:codeid</code></li>
	 * <li>parameter for the langId is named <code>:langid</code></li>
	 * <li>parameter for the type is named <code>:type</code></li>
	 * </ul>
	 * The preconfigured default is: <code>INSERT INTO ##TABLE## (##CODEID##, ##LANGID## ,##MESSAGE##) VALUES (:codeid,:langid,:message,:type)</code>
	 */
	private String insertMessageQuery = "INSERT INTO ##TABLE## (##CODEID##, ##LANGID## ,##MESSAGE##, ##TYPE##) VALUES (:codeid,:langid,:message," +
	                                    ":type)";

	/**
	 * Query update a single message in a specific language. The query is compiled at initialization time, this means some placeholders are resolved:
	 * <ul>
	 * <li>##TABLE## the table name is replaced with parameter <code>table</code></li>
	 * <li>##MESSAGE## the message column name is replaced with parameter <code>message</code></li>
	 * <li>##CODEID##the codeId column name is replaced with parameter <code>codeId</code></li>
	 * <li>##LANGID##the langId column name is replaced with parameter <code>langId</code></li>
	 * </ul>
	 * For a working compatible query the following is required:
	 * <ul>
	 * <li>parameter for the message is named <code>:message</code></li>
	 * <li>parameter for the codeId is named <code>:codeid</code></li>
	 * <li>parameter for the langId is named <code>:langid</code></li>
	 * </ul>
	 * The preconfigured default is: <code>UPDATE ##TABLE## SET ##MESSAGE## = :message WHERE ##CODEID## = :codeid AND ##LANGID## = :langid</code>
	 */
	private String updateMessageQuery = "UPDATE ##TABLE## SET ##MESSAGE## = :message WHERE ##CODEID## = :codeid AND ##LANGID## = :langid";

	/**
	 * Query deletes a single message . The query is compiled at initialization time, this means some placeholders are resolved:
	 * <ul>
	 * <li>##TABLE## the table name is replaced with parameter <code>table</code></li>
	 * <li>##MESSAGE## the message column name is replaced with parameter <code>message</code></li>
	 * <li>##CODEID##the codeId column name is replaced with parameter <code>codeId</code></li>
	 * <li>##LANGID##the langId column name is replaced with parameter <code>langId</code></li>
	 * </ul>
	 * For a working compatible query the following is required:
	 * <ul>
	 * <li>parameter for the codeId is named <code>:codeid</code></li>
	 * </ul>
	 * The preconfigured default is: <code>DELETE FROM ##TABLE## WHERE ##CODEID## = :codeid</code>
	 */
	private String deleteMessageQuery = "DELETE FROM ##TABLE## WHERE ##CODEID## = :codeid";
	/**
	 * Query deletes a single message . The query is compiled at initialization time, this means some placeholders are resolved:
	 * <ul>
	 * <li>##TABLE## the table name is replaced with parameter <code>table</code></li>
	 * <li>##MESSAGE## the message column name is replaced with parameter <code>message</code></li>
	 * <li>##CODEID##the codeId column name is replaced with parameter <code>codeId</code></li>
	 * <li>##LANGID##the langId column name is replaced with parameter <code>langId</code></li>
	 * </ul>
	 * For a working compatible query the following is required:
	 * <ul>
	 * <li>parameter for the codeId is named <code>:codeid</code></li>
	 * </ul>
	 * The preconfigured default is: <code>DELETE FROM ##TABLE## WHERE ##CODEID## = :codeid</code>
	 */
	private String deleteMessageForLangQuery = "DELETE FROM ##TABLE## WHERE ##CODEID## = :codeid AND ##LANGID##=:langid";

	/**
	 * value to replace placeholder <code>##TABLE##</code>.
	 */
	private String table = "MESSAGES";

	/**
	 * value to replace placeholder <code>##CODEID##</code>.
	 */
	private String codeId = "CODEID";

	/**
	 * value to replace placeholder <code>##LANGID##</code>.
	 */
	private String langId = "LANGID";

	/**
	 * value to replace placeholder <code>##MESSAGE##</code>.
	 */
	private String message = "MESSAGE";

	/**
	 * value to replace placeholder <code>##MESSAGE##</code>.
	 */
	private String type = "TYPE";

	/**
	 * With this map further placeholders can be configured to be replaced in the Queries. This might be useful when you have more complex structures
	 * in the database.<br/>
	 * Using this mechanism you can configure specific setups like environment dependency. Be careful with placeholders and make sure that they are
	 * unique and recognizable. You can use a pattern like <code>##PLACEHOLDER##</code>.<br/>
	 * There are RESERVED placeholders that are replaced with configured values before evaluation of this map:
	 * <ul>
	 * <li><code>##TABLE##</code></li>
	 * <li><code>##MESSAGE##</code></li>
	 * <li><code>##LANGID##</code></li>
	 * <li><code>##CODEID##</code></li>
	 * </ul>
	 */
	private Map<String, String> placeholders;

	/**
	 * Sql query for a single message with replaced placeholders.
	 */
	private String compiledSingleMessageQuery;

	/**
	 * Sql query for all messages with replaced placeholders.
	 */
	private String compiledAllMessagesQuery;

	/**
	 * Sql query to insert a new message with replaced placeholders.
	 */
	private String compiledInsertMessageQuery;

	/**
	 * Sql query to update a message with replaced placeholders.
	 */
	private String compiledUpdateMessageQuery;

	/**
	 * Sql query to delete a message with replaced placeholders.
	 */
	private String compiledDeleteMessageQuery;

	/**
	 * Sql query to delete a message with replaced placeholders.
	 */
	private String compiledDeleteMessageForLangQuery;

	/**
	 * required to excecute the sql insert query.
	 */
	private SqlUpdate insertMessageSqlUpdate;

	/**
	 * required to excecute the sql update query.
	 */
	private SqlUpdate updateMessageSqlUpdate;

	/**
	 * required to execute the delete query.
	 */
	private SqlUpdate deleteMessageSqlUpdate;

	/**
	 * required to execute the delete query.
	 */
	private SqlUpdate deleteMessageForLangSqlUpdate;

	/**
	 * required to excecute the sql queries.
	 */
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private List<String> availableLanguages;

	/**
	 * {@inheritDoc}
	 *
	 * @see org.springframework.dao.support.DaoSupport#initDao()
	 */
	@Override
	protected void initDao() throws Exception {
		Assert.hasText(this.singleMessageQuery);
		Assert.hasText(this.allMessagesQuery);
		Assert.hasText(this.insertMessageQuery);
		Assert.hasText(this.updateMessageQuery);
		Assert.hasText(this.deleteMessageQuery);
		Assert.hasText(this.deleteMessageForLangQuery);

		this.compiledSingleMessageQuery = this.compileQuery(this.singleMessageQuery);
		this.compiledAllMessagesQuery = this.compileQuery(this.allMessagesQuery);
		this.compiledInsertMessageQuery = this.compileQuery(this.insertMessageQuery);
		this.compiledUpdateMessageQuery = this.compileQuery(this.updateMessageQuery);
		this.compiledDeleteMessageQuery = this.compileQuery(this.deleteMessageQuery);
		this.compiledDeleteMessageForLangQuery = this.compileQuery(this.deleteMessageForLangQuery);

		this.insertMessageSqlUpdate = new SqlUpdate(this.getDataSource(), this.compiledInsertMessageQuery, new int[] { Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR, });

		this.insertMessageSqlUpdate.compile();

		this.updateMessageSqlUpdate = new SqlUpdate(this.getDataSource(), this.compiledUpdateMessageQuery, new int[] { Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, });

		this.updateMessageSqlUpdate.compile();

		this.deleteMessageSqlUpdate = new SqlUpdate(this.getDataSource(), this.compiledDeleteMessageQuery, new int[] { Types.VARCHAR });

		this.deleteMessageSqlUpdate.compile();

		this.deleteMessageForLangSqlUpdate = new SqlUpdate(this.getDataSource(), this.compiledDeleteMessageForLangQuery, new int[] { Types.VARCHAR, Types.VARCHAR });

		this.deleteMessageForLangSqlUpdate.compile();

		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.getDataSource());

	}

//	@Override
//	public List<MessageResource> findAll() {
//		return null;
//	}

//	@Override
//	public List<MessageResource> findByCodeAndLang(String code, String lang) {
//		return null;
//	}

	@Override
	public List<String> findDistinctLang() {
		if (this.availableLanguages == null) {
			LOG.info("Initialize available Languages.");
			List<String> locales = new ArrayList<>();
			List<MessageResource> messages = this.findAll();
			for (MessageResource resource : messages) {
				String locale = resource.getLang();
				if (!locales.contains(locale)) {
					locales.add(locale);
				}
			}
			this.availableLanguages = locales;
			LOG.info("Available locales are: " + ToStringBuilder.reflectionToString(locales, ToStringStyle.SHORT_PREFIX_STYLE));
		}
		return this.availableLanguages;
	}


	@Override
	public List<MessageResource> findAll() {
		List<TableEntry> messages = this.getMessageData();
		List<MessageResource> result = new ArrayList<>();
		if (messages != null) {
			for (TableEntry entry : messages) {
				result.add(new MessageResource().type(entry.getType()).lang(entry.langId).code(entry.codeId).message(entry.message));
			}
		}
		return result;
	}


	public List<MessageResourceEntry> getAllMessageResourceEntries() {
		List<TableEntry> messages = this.getMessageData();
		return this.convertMessageMapToResourceEntries(messages);
	}

	/**
	 * Gathers the data of all messages from the database.
	 *
	 * @return a list with all message table entries
	 * @throws org.springframework.dao.DataAccessException when data access fails.
	 */
	private List<TableEntry> getMessageData() throws org.springframework.dao.DataAccessException {
		Map<String, Object> namedParameters = new HashMap<String, Object>(0);
		List<TableEntry> messages;
		try {
			messages = this.namedParameterJdbcTemplate.query(this.compiledAllMessagesQuery, namedParameters, new TableEntryRowMapper());
		} catch (EmptyResultDataAccessException e) {
			LOG.warn("No messages found.");
			return null;
		}
		return messages;
	}

	private List<MessageResourceEntry> convertMessageMapToResourceEntries(List<TableEntry> input) {
		Map<String, MessageResourceEntry> entries = new HashMap<String, MessageResourceEntry>(input.size());
		for (TableEntry entry : input) {
			if (entries.containsKey(entry.getCodeId())) {
				entries.get(entry.getCodeId()).addLang(LocaleUtils.toLocale(entry.getLangId()), entry.message);
			} else {
				MessageResourceEntry data = new MessageResourceEntry();
				data.setCodeId(entry.getCodeId());
				data.addLang(LocaleUtils.toLocale(entry.getLangId()), entry.getMessage());
				data.setType(entry.getType());
				entries.put(entry.getCodeId(), data);
			}
		}
		List<MessageResourceEntry> result = new ArrayList<MessageResourceEntry>(entries.size());
		result.addAll(entries.values());
		return result;
	}

	@Override
	public List<MessageResource> findByCodeAndLang(String code, String lang) {
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("codeid", code);
		namedParameters.put("langid", lang);

		List<MessageResource> messageResources = new ArrayList<>();
		try {
			TableEntry mR = this.namedParameterJdbcTemplate.queryForObject(this.compiledSingleMessageQuery, namedParameters,
					new TableEntryRowMapper());

			messageResources.add(new MessageResource().code(mR.getCodeId()).lang(mR.getLangId()).message(mR.getMessage()).type(mR.getType()));

		} catch (EmptyResultDataAccessException e) {
			LOG.info("no message found for code {} and lang {}", code, lang);
		}
		return messageResources;
	}

	@Override
	public MessageResource findTopByCodeAndLang(String code, String lang) {
		List<MessageResource> byCodeAndLang = findByCodeAndLang(code, lang);
		if (CollectionUtils.isEmpty(byCodeAndLang)) {
			return null;
		}
		return byCodeAndLang.get(0);
	}


	public Long deleteByCode(String code) {
		Map<String, Object> namedParameters = new HashMap<>();
		namedParameters.put("codeid", code);
		return Long.valueOf(this.deleteMessageSqlUpdate.updateByNamedParam(namedParameters));
	}

	@Override
	public Long deleteByCodeAndLang(String code, String lang) {
		Map<String, Object> namedParameters = new HashMap<>();
		namedParameters.put("codeid", code);
		namedParameters.put("langid", lang);
		return Long.valueOf(this.deleteMessageForLangSqlUpdate.updateByNamedParam(namedParameters));
	}


	@Override
	public MessageResource save(MessageResource messageResource) {
		boolean update = false;
		List<MessageResource> msg = this.findByCodeAndLang(messageResource.getCode(), messageResource.getLang());
		if (!isEmpty(msg)) {
			update = true;
			LOG.info("Message[" + messageResource.getCode() + "] found for locale [" + messageResource.getLang() + "]. Update existing one.");
		}
		if (update) {
			this.updateResourceMessage(messageResource.getCode(), messageResource.getLang(), messageResource.getMessage());
		} else {
			this.insertResourceMessage(messageResource.getCode(), messageResource.getLang(), messageResource.getMessage(),
			                           messageResource.getType());
		}
		// check exisiting locales to be refreshed
		if (!this.findDistinctLang().contains(messageResource.getLang().toString())) {
			this.findDistinctLang().add(messageResource.getLang().toString());
		}
		return messageResource;
	}

	/**
	 * Inserts a new message into the DB.
	 *
	 * @param code     the message code
	 * @param language the locale of the message
	 * @param message  the text content of the message
	 * @throws DataAccessException when a problem occurs, especially unique constraints
	 */
	private void insertResourceMessage(String code,
	                                   String language,
	                                   String message,
	                                   String type) {
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("codeid", code);
		namedParameters.put("langid", language);
		namedParameters.put("message", message);
		namedParameters.put("type", type);
		this.insertMessageSqlUpdate.updateByNamedParam(namedParameters);
	}

	/**
	 * Updates an existing message into the DB.
	 *
	 * @param code     the message code
	 * @param language the locale of the message
	 * @param message  the text content of the message
	 * @throws DataAccessException when a problem occurs, especially no data found
	 */
	private void updateResourceMessage(String code,
	                                   String language,
	                                   String message) {
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("codeid", code);
		namedParameters.put("langid", language);
		namedParameters.put("message", message);
		this.updateMessageSqlUpdate.updateByNamedParam(namedParameters);
	}

	/**
	 * Replaces the placeholders in the query template with correct configured item names.
	 */
	private String compileQuery(String query) {
		LOG.info("Compile SQL " + query);
		String compiled = query.replaceAll("##MESSAGE##", this.message);
		compiled = compiled.replaceAll("##TABLE##", this.table);
		compiled = compiled.replaceAll("##CODEID##", this.codeId);
		compiled = compiled.replaceAll("##LANGID##", this.langId);
		compiled = compiled.replaceAll("##TYPE##", this.type);
		if (!isEmpty(this.placeholders)) {
			for (Entry<String, String> entry : this.placeholders.entrySet()) {
				compiled = compiled.replaceAll(entry.getKey(), entry.getValue());
			}
		}
		return compiled;
	}

	/**
	 * Setter for table name. @param table the table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * Setter for codeId column name. @param codeId the codeId to set
	 */
	public void setCodeId(String codeId) {
		this.codeId = codeId;
	}

	/**
	 * Setter for langId column name. @param langId the langId to set
	 */
	public void setLangId(String langId) {
		this.langId = langId;
	}

	/**
	 * Setter for message column name. @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Setter for more custom queries. @param singleMessageQuery the singleMessageQuery to set
	 */
	public void setSingleMessageQuery(String singleMessageQuery) {
		this.singleMessageQuery = singleMessageQuery;
	}

	/**
	 * Setter for allMessagesQuery more custom queries. @param allMessagesQuery the allMessagesQuery to set
	 */
	public void setAllMessagesQuery(String allMessagesQuery) {
		this.allMessagesQuery = allMessagesQuery;
	}

	/**
	 * Setter for insertMessageQuery. @param insertMessageQuery the insertMessageQuery to set
	 */
	public void setInsertMessageQuery(String insertMessageQuery) {
		this.insertMessageQuery = insertMessageQuery;
	}

	/**
	 * Setter for updateMessageQuery. @param updateMessageQuery the updateMessageQuery to set
	 */
	public void setUpdateMessageQuery(String updateMessageQuery) {
		this.updateMessageQuery = updateMessageQuery;
	}

	/**
	 * Setter for placeholders. @param placeholders the placeholders to set
	 */
	public void setPlaceholders(Map<String, String> placeholders) {
		this.placeholders = placeholders;
	}

	/**
	 * Setter for type. @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Maps select result to a language table entry for easier select mappings.
	 *
	 * @author aschaefer, namics ag
	 * @since Namics commons i18n 1.2
	 */
	protected static class TableEntryRowMapper implements RowMapper<TableEntry> {
		@Override
		public TableEntry mapRow(ResultSet rs,
		                         int rowNum) throws SQLException {
			return new TableEntry(rs.getString("codeid"), rs.getString("langid"), rs.getString("message"), rs.getString("mtype"));
		}
	}

	/**
	 * A language table entry for easier select mappings.
	 *
	 * @author aschaefer, namics ag
	 * @since Namics commons i18n 1.0 - Oct 19, 2010
	 */
	protected static class TableEntry {
		// CHECKSTYLE:OFF
		protected String codeId;

		protected String langId;

		protected String message;

		protected String type;

		protected TableEntry(String codeId, String langId, String message, String type) {
			super();
			this.codeId = codeId;
			this.langId = langId;
			this.message = message;
			this.type = type;
		}

		protected TableEntry() {
			super();
		}

		public String getCodeId() {
			return this.codeId;
		}

		public String getLangId() {
			return this.langId;
		}

		public String getMessage() {
			return this.message;
		}

		// CHECKSTYLE:ON

		/**
		 * Getter for type. @return the type
		 */
		public String getType() {
			return this.type;
		}
	}

}

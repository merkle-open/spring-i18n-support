package com.namics.oss.spring.support.i18n.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SpringI18nSupportProperties.
 *
 * @author rgsell, Namics AG
 * @since 13.09.17 16:25
 */
@ConfigurationProperties(prefix = SpringI18nSupportProperties.NAMICS_SPRING_I18N_PROPERTIES_PREFIX)
public class SpringI18nSupportProperties {
	public static final String NAMICS_SPRING_I18N_PROPERTIES_PREFIX = "com.namics.oss.spring.support.i18n";

	private Web web = new Web();

	private DataSource dataSource = new DataSource();

	public Web getWeb() {
		return web;
	}

	public void setWeb(Web web) {
		this.web = web;
	}

	public SpringI18nSupportProperties web(Web web) {
		setWeb(web);
		return this;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public SpringI18nSupportProperties dataSource(DataSource dataSource) {
		setDataSource(dataSource);
		return this;
	}


	public static class Web {
		public static final String NAMICS_SPRING_I18N_PROPERTIES_WEB = NAMICS_SPRING_I18N_PROPERTIES_PREFIX + ".web";

		/**
		 * the servlet-name
		 */
		private String servletName;

		/**
		 * the  mapping
		 */
		private String servletMapping;

		public String getServletName() {
			return servletName;
		}

		public void setServletName(String servletName) {
			this.servletName = servletName;
		}

		public String getServletMapping() {
			return servletMapping;
		}

		public void setServletMapping(String servletMapping) {
			this.servletMapping = servletMapping;
		}
	}

	public static class DataSource {
		public static final String NAMICS_SPRING_I18N_PROPERTIES_DATA_SOURCE = NAMICS_SPRING_I18N_PROPERTIES_PREFIX + ".dataSource";

		/**
		 * the table name
		 */
		private String tableName = "MESSAGES";


		/**
		 * column name for code id.
		 */
		private String codeIdColumnName = "CODEID";

		/**
		 * column name for the language id
		 */
		private String langIdColumnName = "LANGID";

		/**
		 * column name for the message
		 */
		private String messageColumnName = "MESSAGE";

		/**
		 * column name for message type.
		 */
		private String typeColumnName = "TYPE";

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public String getCodeIdColumnName() {
			return codeIdColumnName;
		}

		public void setCodeIdColumnName(String codeIdColumnName) {
			this.codeIdColumnName = codeIdColumnName;
		}

		public String getLangIdColumnName() {
			return langIdColumnName;
		}

		public void setLangIdColumnName(String langIdColumnName) {
			this.langIdColumnName = langIdColumnName;
		}

		public String getMessageColumnName() {
			return messageColumnName;
		}

		public void setMessageColumnName(String messageColumnName) {
			this.messageColumnName = messageColumnName;
		}

		public String getTypeColumnName() {
			return typeColumnName;
		}

		public void setTypeColumnName(String typeColumnName) {
			this.typeColumnName = typeColumnName;
		}
	}

}

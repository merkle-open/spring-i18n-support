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

}

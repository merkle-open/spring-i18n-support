package com.namics.oss.spring.support.i18n.autoconfigure;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * SpringI18nSupportStarterAutoConfiguration.
 *
 * @author rgsell, Namics AG
 * @since 14.09.17 09:21
 */
@Configuration
@Import({ SpringI18nSupportWebAutoConfiguration.class, SpringI18nSupportAutoConfiguration.class})
public class SpringI18nSupportStarterAutoConfiguration {

}

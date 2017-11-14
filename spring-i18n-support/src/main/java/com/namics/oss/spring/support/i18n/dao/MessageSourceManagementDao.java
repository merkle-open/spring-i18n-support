/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.dao;

import com.namics.oss.spring.support.i18n.dao.jpa.model.MessageResource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MessageSourceManagementDao extends a message source dao with management functionality.
 *
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 19, 2010
 */
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public interface MessageSourceManagementDao extends MessageSourceDao {
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	MessageResource save(MessageResource messageResource);

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	Long deleteByCode(String code);

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	Long deleteByCodeAndLang(String code, String lang);
}

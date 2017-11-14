/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.dao;

import com.namics.oss.spring.support.i18n.dao.jpa.model.MessageResource;

import java.util.List;

/**
 * Interface for getting language dependent message sources from the datastore.
 *
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 14, 2010
 */
public interface MessageSourceDao {
	List<MessageResource> findAll();

	List<MessageResource> findByCodeAndLang(String code, String lang);

	MessageResource findTopByCodeAndLang(String code, String lang);

	/**
	 * Returns a list of currently available languages.
	 *
	 * @return the List with languagaes
	 */
	List<String> findDistinctLang();
}

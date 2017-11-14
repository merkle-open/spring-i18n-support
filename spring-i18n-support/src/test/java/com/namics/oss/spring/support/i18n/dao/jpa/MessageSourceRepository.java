/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.dao.jpa;

import com.namics.oss.spring.support.i18n.dao.MessageSourceManagementDao;
import com.namics.oss.spring.support.i18n.dao.jpa.model.MessageResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * MessageSourceRepository.
 *
 * @author lboesch, Namics AG
 * @since 02.09.2014
 */
@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
public interface MessageSourceRepository extends JpaRepository<MessageResource, String>, MessageSourceManagementDao {

	@Override
	@Query("select distinct m.lang from #{#entityName} m")
	List<String> findDistinctLang();

}

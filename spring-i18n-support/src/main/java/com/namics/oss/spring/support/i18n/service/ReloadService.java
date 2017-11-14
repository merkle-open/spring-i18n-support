/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.service;

import com.namics.oss.spring.support.i18n.context.support.Reloadable;

/**
 * ReloadService to provide an interface to reload all reloadable classes.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Nov 19, 2010
 */
public interface ReloadService
{
	/**
	 * Method to trigger a reload() of all {@link Reloadable} implementing classes.
	 */
	public void reload();
}

/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.context.support;

import com.namics.oss.spring.support.i18n.exception.ReloadableResourceException;

/**
 * Interface to implement a relead functionality in sping beans which holds additional resources in a internal cache.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 14, 2010
 */
public interface Reloadable
{
	/**
	 * Trigger method to reload the resources.
	 * 
	 * @throws ReloadableResourceException
	 *             if reload fails.
	 */
	public void reload() throws ReloadableResourceException;
}

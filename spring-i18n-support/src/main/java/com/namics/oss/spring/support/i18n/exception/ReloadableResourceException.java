/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.exception;

/**
 * Runtime exception that indicates a failure when reloading a resource.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 14, 2010
 */
public final class ReloadableResourceException extends RuntimeException
{
	/** serial version id. */
	private static final long serialVersionUID = -5153713020100190773L;

	/** Name of the resource. */
	private final String resourceName;

	/**
	 * Default constructor.
	 * 
	 * @param msg
	 *            the exception msg.
	 * @param ex
	 *            the caused exception.
	 */
	public ReloadableResourceException(String msg, Throwable ex)
	{
		super(msg, ex);
		this.resourceName = "not specified";
	}

	/**
	 * Default constructor.
	 * 
	 * @param msg
	 *            the exception msg.
	 * @param ex
	 *            the caused exception.
	 * @param resourceName
	 *            the name of the resource
	 */
	public ReloadableResourceException(String msg, Throwable ex, String resourceName)
	{
		super(msg, ex);
		this.resourceName = resourceName;
	}

	// CHECKSTYLE:OFF
	public String getResourceName()
	{
		return this.resourceName;
	}
	// CHECKSTYLE:ON
}

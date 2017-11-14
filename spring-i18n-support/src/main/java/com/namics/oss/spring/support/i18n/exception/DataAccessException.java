/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.exception;

/**
 * DataAccessException.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 14, 2010
 */
public class DataAccessException extends Exception
{

	/** serialVersionUID. */
	private static final long serialVersionUID = 3280929161125844941L;

	/**
	 * DataAccessException.
	 */
	public DataAccessException()
	{
		super();
	}

	/**
	 * DataAccessException.
	 * 
	 * @param message message
	 */
	public DataAccessException(String message)
	{
		super(message);
	}

	/**
	 * JavaDoc DataAccessException.
	 * 
	 * @param cause the causing throwable
	 */
	public DataAccessException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * DataAccessException.
	 * 
	 * @param message message
	 * @param cause causing throwable
	 */
	public DataAccessException(String message, Throwable cause)
	{
		super(message, cause);
	}

}

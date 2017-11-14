/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.exception;


/**
 * I18nException the master type of exception thrown by this library.
 *
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 13, 2010
 *
 * @see java.lang.RuntimeException
 */
public class I18nException extends RuntimeException
{

	/** serialVersionUID. */
	private static final long serialVersionUID = -5658015962409395301L;

	/**
	 * Creates plain Exception.
	 */
	public I18nException()
	{
		
	}

	/**Creates Exception with message.
	 * @param message The message
	 */
	public I18nException(String message)
	{
		super(message);
	}

	/**Creates Exception with causing throwable.
	 * @param cause the reason for this exception.
	 */
	public I18nException(Throwable cause)
	{
		super(cause);
	}

	/**Creates Exception with specific message and causing throwable.
	 * @param message The message
	 * @param cause the reason for this exception.
	 */
	public I18nException(String message, Throwable cause)
	{
		super(message, cause);
	}

}

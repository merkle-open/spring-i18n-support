/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.util;

import java.io.Serializable;
import java.util.Comparator;

import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;

/**
 * Comparator class for MessageResourceEntry.
 * 
 * @author aschaefer, namics ag
 * @since M-connect 1.2
 */
public class MessageResourceEntryComparator implements Comparator<MessageResourceEntry>, Serializable
{
	/** serialVersionUID. */
	private static final long serialVersionUID = 2520056228811015430L;

	@Override
	public int compare(	MessageResourceEntry o1,
						MessageResourceEntry o2)
	{
		if (o1 != null && o2 != null && o1.getCodeId() != null && o2.getCodeId() != null)
		{
			return o1.getCodeId().compareTo(o2.getCodeId());
		}
		else
		{
			return Integer.MAX_VALUE;
		}
	}
}

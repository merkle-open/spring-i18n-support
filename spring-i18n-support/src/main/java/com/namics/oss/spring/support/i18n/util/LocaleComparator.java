
package com.namics.oss.spring.support.i18n.util;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

/**
 * LocaleComparator implemantation to compare locales with each other.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Nov 12, 2010
 */
public final class LocaleComparator implements Comparator<Locale>, Serializable
{
	/** serialVersionUID. */
	private static final long serialVersionUID = -788189508768273896L;

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(	Locale locale1,
						Locale locale2)
	{
		if (locale1 == null && locale2 == null)
		{
			return 0;
		}
		else if (locale1 == null)
		{
			return Integer.MAX_VALUE;
		}
		else if (locale2 == null)
		{
			return Integer.MIN_VALUE;
		}
		else
		{
			return locale1.toString().compareTo(locale2.toString());
		}
	}
}
/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.service;

import java.util.Map;

import com.namics.oss.spring.support.i18n.context.support.Reloadable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.namics.oss.spring.support.i18n.exception.ReloadableResourceException;

/**
 * ReloadServiceImpl does an application context scan for {@link Reloadable} classes and calls their reload() method.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Nov 19, 2010
 */
public class ReloadServiceImpl implements ReloadService, ApplicationContextAware
{

	private static final Logger LOG = LoggerFactory.getLogger(ReloadServiceImpl.class);

	private ApplicationContext applicationContext;

	/**
	 * {@inheritDoc}
	 * 
	 * @see MessageManagementService#reload()
	 */
	@Override
	public void reload()
	{

		if (this.applicationContext != null)
		{
			LOG.info("Start reloading beans in application context...");
			Map<String, Reloadable> beans = this.applicationContext.getBeansOfType(Reloadable.class);
			for (Reloadable reloadableBean : beans.values())
			{
				try
				{
					LOG.debug("Trying to reload bean " + reloadableBean.getClass().getName());
					reloadableBean.reload();
				}
				catch (ReloadableResourceException rre)
				{
					LOG.warn("Reloading bean " + reloadableBean.getClass().getName() + " failed!", rre);
				}
			}
			LOG.info("End reloading beans in application context...");
		}
		else
		{
			// we are not in a spring context
			LOG.info("Start reloading beans in the JVM...");
			LOG.warn("At the moment this function is supported in a spring context only!");
			LOG.info("End reloading beans in the JVM...");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;
	}

}

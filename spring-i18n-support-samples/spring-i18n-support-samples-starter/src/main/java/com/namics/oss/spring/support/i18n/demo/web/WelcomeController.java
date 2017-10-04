/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * DummyController.
 *
 * @author rgsell, Namics AG
 * @since 15.09.17 12:55
 */
@Controller
public class WelcomeController
{
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String get() {
		return "i18n";
	}
}

/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.web.bean;

import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;

import java.util.List;

/**
 * MessageSourceTableBean.
 *
 * @author lboesch, Namics AG
 * @since 04.09.2014
 */
public class MessageSourceTableBean {
	protected String name;
	protected List<MessageResourceEntry> messages;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MessageSourceTableBean name(String name) {
		setName(name);
		return this;
	}

	public List<MessageResourceEntry> getMessages() {
		return messages;
	}

	public void setMessages(List<MessageResourceEntry> messages) {
		this.messages = messages;
	}

	public MessageSourceTableBean messages(List<MessageResourceEntry> messages) {
		setMessages(messages);
		return this;
	}
}

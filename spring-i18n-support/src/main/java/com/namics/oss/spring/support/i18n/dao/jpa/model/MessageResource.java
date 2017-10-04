/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.dao.jpa.model;


import javax.persistence.*;

/**
 * MessageResource.
 *
 * @author aschaefer, Namics AG
 * @since 18.03.14 15:33
 */
@Entity
public class MessageResource
{
	@Id
	private String id;

	@Column(length = 255)
	private String type;
	@Column(length = 255)
	private String code;
	@Column(length = 255)
	private String lang;
	@Lob
	private String message;

	@PrePersist
	public void updateId()
	{
		if (this.id == null)
		{
			this.id = this.type + this.code + this.lang;
		}
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getType()
	{
		return type;
	}

	//FIXME: The following chained accessors are named as setter methods. Should be considered to be refactored/removed.
	public MessageResource setType(String type)
	{
		this.type = type;
		return this;
	}

	public String getCode()
	{
		return code;
	}

	public MessageResource setCode(String code)
	{
		this.code = code;
		return this;
	}

	public String getLang()
	{
		return lang;
	}

	public MessageResource setLang(String lang)
	{
		this.lang = lang;
		return this;
	}

	public String getMessage()
	{
		return message;
	}

	public MessageResource setMessage(String message)
	{
		this.message = message;
		return this;
	}


	//Chained accessors
	public MessageResource id(String id) {
		this.id = id;
		return this;
	}

	public MessageResource type(String type) {
		this.type = type;
		return this;
	}

	public MessageResource code(String code) {
		this.code = code;
		return this;
	}

	public MessageResource lang(String lang) {
		this.lang = lang;
		return this;
	}

	public MessageResource message(String message) {
		this.message = message;
		return this;
	}
}

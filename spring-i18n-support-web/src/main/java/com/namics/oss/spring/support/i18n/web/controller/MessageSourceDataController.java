/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.web.controller;

import com.namics.oss.spring.support.i18n.exception.I18nException;
import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;
import com.namics.oss.spring.support.i18n.service.MessageManagementService;
import com.namics.oss.spring.support.i18n.service.ReloadService;
import com.namics.oss.spring.support.i18n.web.MessageSourceManagementController;
import com.namics.oss.spring.support.i18n.web.bean.MessageSourceTableBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.springframework.util.Assert.notNull;
import static org.springframework.util.StringUtils.hasText;

/**
 * MessageSourceDataController.
 * rest api for messages
 *
 * @author lboesch, Namics AG
 * @since 04.09.2014
 */
@RestController
@EnableAsync
@RequestMapping("/messages")
public class MessageSourceDataController {

	protected static final String CODE = "code";
	private static final Logger LOG = LoggerFactory.getLogger(MessageSourceManagementController.class);
	protected MessageManagementService messageManagementService;

	protected ReloadService reloadService;

	public MessageSourceDataController(MessageManagementService messageManagementService, ReloadService reloadService) {
		notNull(messageManagementService);
		this.messageManagementService = messageManagementService;
		this.reloadService = reloadService;
	}

	public MessageSourceDataController(MessageManagementService messageManagementService) {
		this(messageManagementService, null);
	}

	@RequestMapping(method = RequestMethod.GET)
	public List<MessageSourceTableBean> getMessages() {
		Map<String, List<MessageResourceEntry>> allMessagesGrouped = this.messageManagementService.getAllMessagesGrouped();
		List<MessageSourceTableBean> tables = new ArrayList<>();
		for (String key : allMessagesGrouped.keySet()) {
			tables.add(new MessageSourceTableBean().name(key).messages(allMessagesGrouped.get(key)));
		}

		return tables;
	}

	@RequestMapping(value = "/{code:.*}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageResourceEntry> getMessage(@PathVariable(CODE) String code) {
		MessageResourceEntry message = this.messageManagementService.getMessage(code);
		if (message != null) {
			return new ResponseEntity<>(message, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/{code:.*}", method = RequestMethod.POST)
	public ResponseEntity putMessage(@PathVariable(CODE) String code, @ModelAttribute MessageResourceEntry
			messageResourceEntry) {
		Assert.hasText(messageResourceEntry.getCodeId(), "message code must be set!");
		String type = hasText(messageResourceEntry.getType()) ? messageResourceEntry.getType() : null;
		boolean savedOneMessage = false;
		for (Map.Entry<Locale, String> entry : messageResourceEntry.getNameMappings().entrySet()) {
			if (hasText(entry.getValue())) {
				this.messageManagementService.putMessage(messageResourceEntry.getCodeId(), entry.getKey(), entry.getValue(), type);
				savedOneMessage = true;
			}
		}
		if (!savedOneMessage) {
			return new ResponseEntity<>("At least one message needs text. Nothing saved.", HttpStatus.BAD_REQUEST);
		}
		//delete entries if one message is sent by client
		for (Map.Entry<Locale, String> entry : messageResourceEntry.getNameMappings().entrySet()) {
			if (!hasText(entry.getValue())) {
				this.messageManagementService.deleteMessageForLanguage(messageResourceEntry.getCodeId(), entry.getKey());
			}
		}
		return new ResponseEntity<>(this.messageManagementService.getMessage(code), HttpStatus.OK);
	}

	@RequestMapping(value = "/locales", method = RequestMethod.GET)
	public List<Locale> getAvailableLocales() {
		return this.messageManagementService.getAvailableLanguages();
	}

	@RequestMapping(value = "/{code:.*}", method = RequestMethod.DELETE)
	public ResponseEntity deleteByCode(@PathVariable(CODE) String code) {
		try {
			this.messageManagementService.deleteMessage(code);
			return new ResponseEntity(HttpStatus.OK);
		} catch (I18nException exception) {
			LOG.info("Exception during deletion of message with code {}", code, exception);
		}
		return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping(value = "/reload", method = RequestMethod.POST)
	public ResponseEntity reload() {
		if (this.reloadService != null) {
			try {
				this.reloadService.reload();
				return new ResponseEntity(HttpStatus.OK);
			} catch (Exception e) {
				LOG.error("Exception during reloading of message sources" + e);
				return new ResponseEntity<>("Unknown error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<>("No reload service is defined", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping("/download/messages-{time}.{type}")
	public void export(@PathVariable("time") String timestamp, @PathVariable("type") String type, HttpServletResponse response) {
		try {
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "private");
			if (type != null && "xls".equals(type)) {
				response.setContentType("application/vnd.ms-excel");
				this.messageManagementService.exportToExcel97(response.getOutputStream());
			} else if (type != null && "xlsx".equals(type)) {
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				this.messageManagementService.exportToExcel2007(response.getOutputStream());
			} else if (type != null && "sql".equals(type)) {
				response.setContentType("text/plain");
				this.messageManagementService.exportToText(response.getOutputStream());
			}
		} catch (IOException e) {
			LOG.error("IO exception occured " + e);
			LOG.info("IO exception occured ", e);
//			return this.exportErrorView;
		}
	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file, UriComponentsBuilder builder) throws IOException {
		InputStream input = file.getInputStream();
		this.messageManagementService.importFromExcel(input);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(builder.path("/upload.html").build().toUri());
		return new ResponseEntity<>("/admin/", headers, HttpStatus.OK);
	}


	@ExceptionHandler(value = IllegalArgumentException.class)
	public ResponseEntity notValidMessage(IllegalArgumentException exception) {
		return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}
}

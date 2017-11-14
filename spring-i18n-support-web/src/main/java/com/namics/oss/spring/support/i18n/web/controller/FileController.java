/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.web.controller;

import com.namics.oss.spring.support.i18n.exception.I18nException;
import com.namics.oss.spring.support.i18n.service.MessageManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

import static org.springframework.util.Assert.notNull;

/**
 * FileController.
 *
 * @author lboesch, Namics AG
 * @since 11.09.2014
 */
@Controller
@RequestMapping("/files")
public class FileController {
	private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

	protected MessageManagementService messageManagementService;

	public FileController(MessageManagementService messageManagementService) {
		notNull(messageManagementService);
		this.messageManagementService = messageManagementService;
	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public View uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			InputStream input = file.getInputStream();
			this.messageManagementService.importFromExcel(input);
		} catch (Exception e) {
			LOG.error("error on uploading messages", e);
			return new RedirectView("../files.html?uploadSuccess=no&message=" + e.getMessage().toString());
		}
		return new RedirectView("../files.html?uploadSuccess=yes");
	}

	@RequestMapping("/messages-{time}.{type}")
	public View export(@PathVariable("time") String timestamp, @PathVariable("type") String type, HttpServletRequest request,
			HttpServletResponse response) {
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
			return new RedirectView("../files.html?downloadSuccess=no&message=" + e.getMessage().toString());
		} catch (I18nException e) {
			LOG.error("I18nException occured " + e);
			LOG.info("I18nException occured ", e);
			return new RedirectView("../files.html?downloadSuccess=no&message=" + e.getMessage().toString());
		} catch (IllegalArgumentException e) {
			LOG.error("IllegalArgumentException occured " + e);
			LOG.info("IllegalArgumentException occured ", e);
			return new RedirectView("../files.html?downloadSuccess=no&message=" + e.getMessage().toString());
		}
		return null;
	}
}

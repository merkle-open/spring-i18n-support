/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.oss.spring.support.i18n.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.namics.oss.spring.support.i18n.exception.I18nException;
import com.namics.oss.spring.support.i18n.model.MessageResourceEntry;
import com.namics.oss.spring.support.i18n.service.MessageManagementService;
import com.namics.oss.spring.support.i18n.service.ReloadService;

/**
 * MessageSourceManagementController handles management of labels for web purpose. Please be aware that all URLs used in this controller should be
 * secured by a security framework like Spring Security to prevent misuse of the functionality.
 * 
 * @author aschaefer, namics ag
 * @since Namics commons i18n 1.0 - Oct 25, 2010
 */
@Controller("messageSourceManagementController")
@RequestMapping("/messages/admin")
public class MessageSourceManagementController
{

	@Autowired(required = true)
	@Qualifier("messageManagementService")
	private MessageManagementService managementService;

	@Autowired(required = false)
	@Qualifier("messageSourceReloadService")
	private ReloadService reloadService;

	protected static final String CODE = "code";

	protected static final String RESOURCE = "resource";

	protected static final String LANGS = "langs";

	protected static final String MESSAGES = "messages";

	protected static final String SUCCESS = "success";

	protected static final String ERROR = "error";

	protected static final String REDIRECT_UPLOAD = "redirect:upload.html?";

	protected static final String REDIRECT_EDIT = "redirect:edit.html?";

	protected static final String REDIRECT_INDEX = "redirect:index.html?";

	private String listView = "messages.admin.list";

	private String uploadView = "messages.admin.upload";

	private String editView = "messages.admin.edit";

	private String putSuccessView = REDIRECT_INDEX + SUCCESS + "=success.admin.messages.put.success";

	private String putFailView = REDIRECT_EDIT + ERROR + "=error.admin.messages.put.error";

	private String confirmDelete = "messages.admin.delete.confirm";

	private String uploadSuccessView = REDIRECT_UPLOAD + SUCCESS + "=success.admin.messages.upload.success";

	private String uploadFailView = REDIRECT_UPLOAD + ERROR + "=error.admin.messages.upload.fail";

	private String exportErrorView = REDIRECT_INDEX + ERROR + "=error.admin.messages.export.fail";

	private String deleteFailView = REDIRECT_INDEX + ERROR + "=error.admin.messages.delete.fail";

	private String deleteAbortView = "redirect:index.html";

	private String deleteSuccessView = REDIRECT_INDEX + SUCCESS + "=success.admin.messages.delete.success";

	private String reloadFailView = REDIRECT_INDEX + ERROR + "=error.admin.messages.reload.fail";

	private String reloadSuccessView = REDIRECT_INDEX + SUCCESS + "=success.admin.messages.reload.success";

	private static final Logger LOG = LoggerFactory.getLogger(MessageSourceManagementController.class);

	private String imagesDir = "/src/main/resources/images/";

	/**
	 * Triggers the reload method on the corresponding Service. Method Requires {@link ReloadService} to be configured. If it is not, nothing will
	 * happen.
	 * 
	 * @return
	 */
	@RequestMapping("/reload.html")
	public String reload()
	{
		if (this.reloadService != null)
		{
			try
			{
				this.reloadService.reload();
				return this.reloadSuccessView;
			}
			catch (Exception e)
			{
				LOG.error("Exception during reloading of message sources" + e);
			}
		}
		return this.reloadFailView;
	}

	/**
	 * Displays a list of all labels in system.
	 * 
	 * @param success
	 *            an additional success message to display
	 * @param error
	 *            and additional error messare to display
	 * @return the model and view to be assembled by the framework
	 */
	@RequestMapping("/index.html")
	public ModelAndView list(	@RequestParam(value = SUCCESS, required = false) String success,
								@RequestParam(value = ERROR, required = false) String error)
	{
		ModelAndView mav = new ModelAndView(this.listView);
		Map<String, List<MessageResourceEntry>> messages = this.managementService.getAllMessagesGrouped();
		List<Locale> langs = this.managementService.getAvailableLanguages();
		mav.addObject(MESSAGES, messages);
		mav.addObject(LANGS, langs);
		if (StringUtils.hasText(error))
		{
			mav.addObject(ERROR, error);
		}
		if (StringUtils.hasText(success))
		{
			mav.addObject(SUCCESS, success);
		}
		return mav;
	}

	/**
	 * Displays a confirmation Dialog for delte of a message.
	 * 
	 * @param code
	 *            the message code to be deleted
	 * @return the model and view to be assembled by the framework
	 */
	@RequestMapping("/confirmDelete.html")
	public ModelAndView confirmDelete(@RequestParam(CODE) String code)
	{
		MessageResourceEntry message = null;
		try
		{
			message = this.managementService.getMessage(code);
		}
		catch (I18nException e)
		{
			return new ModelAndView(this.deleteFailView);
		}
		if (message != null)
		{
			ModelAndView mav = new ModelAndView(this.confirmDelete);
			mav.addObject(RESOURCE, message);
			return mav;
		}
		else
		{
			return new ModelAndView(this.deleteFailView);
		}
	}

	/**
	 * Finally deletes a message in the system.
	 * 
	 * @param code
	 *            code of message to be deleted
	 * @param confirm
	 *            the confirmation check made in confirmation dialog
	 * @return the success/fail view name to be used by framework.
	 */
	@RequestMapping("/delete.htm")
	public String delete(	@RequestParam(CODE) String code,
							@RequestParam("confirm") boolean confirm)
	{
		if (!confirm)
		{
			return this.deleteAbortView;
		}
		try
		{
			this.managementService.deleteMessage(code);
		}
		catch (I18nException e)
		{
			return this.deleteFailView;
		}
		return this.deleteSuccessView;
	}

	/**
	 * Displays a file upload dialog for uploading an excel file.
	 * 
	 * @param success
	 *            an optional success message to be displayed
	 * @param error
	 *            an optional error message to be displayed
	 * @param model
	 *            the model were to put the messages
	 * @return the view name of the dialog
	 */
	@RequestMapping("/upload.html")
	public String uploadView(	@RequestParam(value = SUCCESS, required = false) String success,
								@RequestParam(value = ERROR, required = false) String error,
								Model model)
	{
		if (StringUtils.hasText(error))
		{
			model.addAttribute(ERROR, error);
		}
		if (StringUtils.hasText(success))
		{
			model.addAttribute(SUCCESS, success);
		}
		return this.uploadView;
	}

	/**
	 * File upload method processing a multipart file upload form and puts the excel file content in the system.
	 * 
	 * @param file
	 *            file upload form
	 * @return success/fail view to be shown by framework
	 */
	@RequestMapping(value = "/uploadFile.html", method = RequestMethod.POST)
	public String uploadFile(@RequestParam("file") MultipartFile file)
	{
		try
		{
			InputStream input = file.getInputStream();
			this.managementService.importFromExcel(input);
		}
		catch (Exception e)
		{
			LOG.error("faild to upload file", e);
			return this.uploadFailView;
		}
		return this.uploadSuccessView;
	}

	/**
	 * Exports all messages in the system to an excel file.
	 * 
	 * @param timestamp
	 *            the timestamp, when the file link was generated (not a releavant parameter).
	 * @param type
	 *            the requested file type, currently <code>xls</code> and <code>xlsx</code> are supported
	 * @param request
	 *            the request obejct
	 * @param response
	 *            the response object to write the data to
	 * @return the view name in case of an error, null when successful
	 */
	@RequestMapping("/messages-{time}.{type}")
	public String export(	@PathVariable("time") String timestamp,
							@PathVariable("type") String type,
							HttpServletRequest request,
							HttpServletResponse response)
	{
		try
		{

			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "private");
			if (type != null && "xls".equals(type))
			{
				response.setContentType("application/vnd.ms-excel");
				this.managementService.exportToExcel97(response.getOutputStream());
			}
			else if (type != null && "xlsx".equals(type))
			{
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				this.managementService.exportToExcel2007(response.getOutputStream());
			}
			else if (type != null && "sql".equals(type))
			{
				response.setContentType("text/plain");
				response.setCharacterEncoding("UTF-8");
				this.managementService.exportToText(response.getOutputStream());
			}
			else
			{
				return this.exportErrorView;
			}
		}
		catch (IOException e)
		{
			LOG.error("IO exception occured " + e);
			LOG.info("IO exception occured ", e);
			return this.exportErrorView;
		}
		catch (I18nException e)
		{
			LOG.error("I18nException occured " + e);
			LOG.info("I18nException occured ", e);
			return this.exportErrorView;
		}
		catch (IllegalArgumentException e)
		{
			LOG.error("IllegalArgumentException occured " + e);
			LOG.info("IllegalArgumentException occured ", e);
			return this.exportErrorView;
		}
		return null;
	}

	/**
	 * Displays an edit dialog to the user, prefilled with the optional message code.
	 * 
	 * @param code
	 *            optional message code to load data of an existing message
	 * @param error
	 *            an optional error message to be displayed (validation...)
	 * @param success
	 *            an optional success message to be displayed
	 * @return the Model and View required to display the form
	 */
	@RequestMapping("/edit.html")
	public ModelAndView edit(	@RequestParam(value = CODE, required = false) String code,
								@RequestParam(value = ERROR, required = false) String error,
								@RequestParam(value = SUCCESS, required = false) String success)
	{
		ModelAndView mav = new ModelAndView(this.editView);
		if (StringUtils.hasText(code))
		{
			try
			{
				MessageResourceEntry entry = this.managementService.getMessage(code);
				if (entry != null)
				{
					mav.addObject(RESOURCE, entry);
				}
			}
			catch (I18nException e)
			{
				LOG.error("Problem resolving code from backend " + e + " " + e.getCause());
			}

		}
		if (StringUtils.hasText(error))
		{
			mav.addObject(ERROR, error);
		}
		if (StringUtils.hasText(success))
		{
			mav.addObject(SUCCESS, success);
		}
		mav.addObject(LANGS, this.managementService.getAvailableLanguages());
		return mav;
	}

	/**
	 * Method inserts a new/updates an existing message in the system based on the submitted edit form.
	 * 
	 * @param entry
	 *            the message bound from the post
	 * @param errors
	 *            Errors that occured during binding on the bean
	 * @return the success/error view, might be a redirect.
	 */
	@RequestMapping("/put.html")
	public String put(	MessageResourceEntry entry,
						BindingResult errors)
	{
		if (!errors.hasErrors() && entry != null && StringUtils.hasText(entry.getCodeId()) && entry.size() > 0)
		{
			try
			{
				for (Entry<Locale, String> msg : entry.getNameMappings().entrySet())
				{
					if (StringUtils.hasLength(msg.getValue()))
					{
						this.managementService.putMessage(entry.getCodeId(), msg.getKey(), msg.getValue(), entry.getType());
					}
				}
				return this.putSuccessView;
			}
			catch (I18nException e)
			{
				LOG.error("Error putting message to backend" + e + " " + e.getCause());
			}
			catch (IllegalArgumentException e)
			{
				LOG.error("Error putting message to backend " + e + " " + e.getCause());
			}
			return this.putFailView + "&" + CODE + "=" + entry.getCodeId();
		}
		return this.putFailView;
	}

	/**
	 * Method resolves file provided by name as ResourceStream from classloader and writes its content to the outputstream provided. Additionally a
	 * cache control header is set on a provided response object.
	 * 
	 * @param name
	 *            filename
	 * @param output
	 *            outputstream
	 * @param response
	 *            respons object to set cache control header in
	 */
	@RequestMapping(value = "/image/{name}", method = RequestMethod.GET)
	public void image(	@PathVariable("name") String name,
						OutputStream output,
						HttpServletResponse response)
	{
		try
		{
			response.setHeader("Cache-Control", "public");
			FileCopyUtils.copy(this.getClass().getResourceAsStream(this.imagesDir + name), output);
		}
		catch (IOException e)
		{
			return;
		}
		catch (IllegalArgumentException e)
		{
			return;
		}
	}

	// CHECKSTYLE:OFF

	/** Setter for managementService. @param managementService the managementService to set */
	public void setManagementService(MessageManagementService managementService)
	{
		this.managementService = managementService;
	}

	/** Setter for listView. @param listView the listView to set */
	public void setListView(String listView)
	{
		this.listView = listView;
	}

	/** Setter for uploadView. @param uploadView the uploadView to set */
	public void setUploadView(String uploadView)
	{
		this.uploadView = uploadView;
	}

	/** Setter for editView. @param editView the editView to set */
	public void setEditView(String editView)
	{
		this.editView = editView;
	}

	/** Setter for putSuccessView. @param putSuccessView the putSuccessView to set */
	public void setPutSuccessView(String putSuccessView)
	{
		this.putSuccessView = putSuccessView;
	}

	/** Setter for putFailView. @param putFailView the putFailView to set */
	public void setPutFailView(String putFailView)
	{
		this.putFailView = putFailView;
	}

	/** Setter for confirmDelete. @param confirmDelete the confirmDelete to set */
	public void setConfirmDelete(String confirmDelete)
	{
		this.confirmDelete = confirmDelete;
	}

	/** Setter for uploadSuccessView. @param uploadSuccessView the uploadSuccessView to set */
	public void setUploadSuccessView(String uploadSuccessView)
	{
		this.uploadSuccessView = uploadSuccessView;
	}

	/** Setter for uploadFailView. @param uploadFailView the uploadFailView to set */
	public void setUploadFailView(String uploadFailView)
	{
		this.uploadFailView = uploadFailView;
	}

	/** Setter for exportErrorView. @param exportErrorView the exportErrorView to set */
	public void setExportErrorView(String exportErrorView)
	{
		this.exportErrorView = exportErrorView;
	}

	/** Setter for deleteFailView. @param deleteFailView the deleteFailView to set */
	public void setDeleteFailView(String deleteFailView)
	{
		this.deleteFailView = deleteFailView;
	}

	/** Setter for deleteAbortView. @param deleteAbortView the deleteAbortView to set */
	public void setDeleteAbortView(String deleteAbortView)
	{
		this.deleteAbortView = deleteAbortView;
	}

	/** Setter for deleteSuccessView. @param deleteSuccessView the deleteSuccessView to set */
	public void setDeleteSuccessView(String deleteSuccessView)
	{
		this.deleteSuccessView = deleteSuccessView;
	}

	/** Setter for imagesDir. @param imagesDir the imagesDir to set */
	public void setImagesDir(String imagesDir)
	{
		this.imagesDir = imagesDir;
	}

	/** Setter for reloadService. @param reloadService the reloadService to set */
	public void setReloadService(ReloadService reloadService)
	{
		this.reloadService = reloadService;
	}

	/** Setter for reloadSuccessView. @param reloadSuccessView the reloadSuccessView to set */
	public void setReloadSuccessView(String reloadSuccessView)
	{
		this.reloadSuccessView = reloadSuccessView;
	}

	/** Setter for reloadFailView. @param reloadFailView the reloadFailView to set */
	public void setReloadFailView(String reloadFailView)
	{
		this.reloadFailView = reloadFailView;
	}

	// CHECKSTYLE:ON
}

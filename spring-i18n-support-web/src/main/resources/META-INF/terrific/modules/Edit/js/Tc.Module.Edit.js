(function ($) {
	Tc.Module.Edit = Tc.Module.extend({

		url: '',
		locales_url: '',
		code: '',
		tpl: null,

		on: function (callback) {
			var self = this;
			if (self.$ctx.data('message-url') && self.$ctx.data('locales-url')) {
				self.url = self.$ctx.data('message-url');
				self.locales_url = self.url + self.$ctx.data('locales-url');
				self.tpl = doT.template(self.$ctx.find('.tpl-message').text());
				self.code = Tc.Utils.Helper.getUrlVars()['code'];
				if (self.code) {
					$.getJSON(self.url + "/" + self.code, function (message) {
						self._loadLocalesAndFillTemplate(self, message);
					});
				} else {
					self._loadLocalesAndFillTemplate(self, {});
				}
			} else {
				callback();
			}

			// save messages
			self.$ctx.on('click', '#saveMessage', function (e) {
				var $this = $(this),
					msgEditForm = $('#msgEditForm');

				$('#alert-area > .alert').remove();

				self.code = $('#codeId').val() ? $('#codeId').val() : 'new';


				$('[name=langId]').each(function (index, value) {
					var hiddenId = 'hid' + Math.floor(Math.random() * 1000);
					var locale = $(value).val();
					var id = $(value).attr('id').substring(6);
					msgEditForm.append('<input type="hidden" class="langEntryToSave" id="' + hiddenId + '" name="nameMappings[' + locale + ']" />');
					$('#' + hiddenId).val($('#message' + id).val());
				});

				// ajax then reload, no errorhandling
				var jqxhr = $.ajax({
					url: self.url + "/" + self.code,
					type: 'post',
					timeout: 6000,
					data: msgEditForm.serialize(),
					success: function (data, text) {
						self._loadLocalesAndFillTemplate(self, data, function () {
							Tc.Utils.Helper.addAlert('alert-success', 'Save success', 'Message with code <strong>' + self.code + '</strong> saved successfully!');
						});
					},
					error: function (request, status, error) {
						Tc.Utils.Helper.addAlert('alert-danger', 'Oh snap! You got an error!', request.responseText)
						msgEditForm.find('.langEntryToSave').remove();
					}

				});
				return false;
			});

			// add new language
			self.$ctx.on('click', '#addNewLanguage', function (e) {
				var rand = Math.floor(Math.random() * 201);
				var insert = '<div style="display:none;" class="form-group" id="ctMsg' + rand + '"><label class="col-md-2 control-label" for="langId' + rand + '">Language</label>';
				insert += '<div class="col-md-2"><input id="langId' + rand + '" name="langId" type="text" class="input-md form-control"/></div>';
				insert += '<label for="message' + rand + '" class="col-md-2 control-label">Message</label>';
				insert += '<div class="col-md-4"><input id="message' + rand + '" name="message" class="input-md form-control"/></div>';
				insert += '<div class="col-md-2"><a id="' + rand + '" name="message" class="btn btn-default removeNewLanguage"><i class="glyphicon glyphicon-trash"></i> Remove</a></div></div>';
				self.$ctx.find('#languageContainer').append(insert);
				self.$ctx.find('#ctMsg' + rand).fadeIn(250);
				return false;
			});

			self.$ctx.on('click', '.removeNewLanguage', function (e) {
				var idSuffix = this.id;
				self.$ctx.find('#ctMsg' + idSuffix).remove();
				return false;
			});

		},
		_loadLocalesAndFillTemplate: function (self, message, callback) {
			$.getJSON(self.locales_url, function (locales) { //get all available locales
				self.$ctx.find('.message').html(self.tpl({message: message,
					locales: locales}));
				if (callback) {
					callback();
				}
			});
		}
	});
})(Tc.$);
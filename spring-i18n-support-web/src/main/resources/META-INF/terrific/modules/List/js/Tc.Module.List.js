(function ($) {
	Tc.Module.List = Tc.Module.extend({

		url: '',
		locales_url: '',
		tpl: null,

		on: function (callback) {
			var self = this;
			if (self.$ctx.data('messages-url') && self.$ctx.data('locales-url')) {
				self.url = self.$ctx.data('messages-url');
				self.locales_url = self.url + self.$ctx.data('locales-url');
				self.tpl = doT.template(self.$ctx.find('.tpl-messages').text());
				self._loadMessagesAndLocalesAndFillTemplate(self);
			}
			else {
				callback();
			}

			// confirmation modal for deletion
			self.$ctx.on('click', '.js-confirm', function (e) {
				var $this = $(this),
					codeId = $this.data('codeid'),
					type = $this.data('type'),
					modal_id = $this.attr('href'),
					$modal = $(modal_id);

				$modal.modal({show: true});

				// replace
				$modal.find('.js-codeId').text(codeId);
				$modal.find('.js-type').text(type);


				// bind confiramtion
				$('.js-delete', $modal).off('click').on('click', function (e) {

					// ajax then reload, no errorhandling
					var jqxhr = $.ajax({
						url: self.url + '/' + codeId,
						type: 'post',
						timeout: 6000,
						data: {
							'_method': 'delete'
						}

					}).always(function () {
						$modal.modal('hide');
						self._loadMessagesAndLocalesAndFillTemplate(self);
					});

					return false;

				});


			});

			//table sorter
			$.extend($.tablesorter.themes.bootstrap, {
				// these classes are added to the table. To see other table classes available,
				// look here: http://twitter.github.com/bootstrap/base-css.html#tables
				table: 'table table-striped',
				caption: 'caption',
				header: 'bootstrap-header', // give the header a gradient background
				footerRow: '',
				footerCells: '',
				icons: '', // add "icon-white" to make them white; this icon class is added to the <i> in the header
				sortNone: 'bootstrap-icon-unsorted',
				sortAsc: 'glyphicon glyphicon-chevron-up',     // includes classes for Bootstrap v2 & v3
				sortDesc: 'glyphicon glyphicon-chevron-down', // includes classes for Bootstrap v2 & v3
				active: '', // applied when column is sorted
				hover: '', // use custom css here - bootstrap class may not override it
				filterRow: '', // filter row class
				even: '', // odd row zebra striping
				odd: ''  // even row zebra striping
			});

		},
		_loadMessagesAndLocalesAndFillTemplate: function (self) {
			$.getJSON(self.url, function (messages) {
				$.getJSON(self.locales_url, function (locales) {
					self.$ctx.find('.messages').html(self.tpl({messages: messages,
						locales: locales}));
					self.initTable();
				});

			});
		},
		initTable: function () {
			var self = this,
				$t = self.$ctx;

			var actionColumn = $('.tablesorter th:contains("Actions")', $t).index();

			var options = {
				theme: "bootstrap",
				headerTemplate: '{content} {icon}',
				widgets: [ "uitheme", "filter", "zebra" ],
				sortList: [
					[0, 0]
				],
				widgetOptions: {
					zebra: ["even", "odd"],
					filter_reset: ".reset"
				}
			};

			if (actionColumn != -1) {
				options.headers = {};
				options.headers[actionColumn] = { sorter: false, filter: false };
			}

			$('.tablesorter', $t).tablesorter(options);
		}
	});
})(Tc.$);
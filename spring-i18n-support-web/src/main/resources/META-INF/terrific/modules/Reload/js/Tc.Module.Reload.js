(function ($) {
	Tc.Module.Reload = Tc.Module.extend({

		url: null,

		on: function (callback) {
			var self = this;
			if (this.$ctx.data('reload-url')) {
				self.url = this.$ctx.data('reload-url');
			}

			self.$ctx.on('click', '#reloadButton', function (e) {
				if (self.url) {
					var jqxhr = $.ajax({
						url: self.url,
						type: 'post',
						timeout: 6000,
						success: function (data, text) {
							Tc.Utils.Helper.addAlert('alert-success', 'Reload successful', '');
						},
						error: function (data, text) {
							Tc.Utils.Helper.addAlert('alert-danger', 'Reload failed', data.responseText);
						}
					});
				}
				return false;
			});

			callback();
		}


	});
})(Tc.$);
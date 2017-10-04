(function ($) {
	Tc.Module.Upload = Tc.Module.extend({

		on: function (callback) {
			var self = this;

			var vars = Tc.Utils.Helper.getUrlVars();

			if (vars['uploadSuccess']) {
				if (vars['uploadSuccess'] === 'yes') {
					Tc.Utils.Helper.addAlert('alert-success', 'upload successful', 'the upload worked like a charm!');
				} else {
					Tc.Utils.Helper.addAlert('alert-danger', 'upload failed', decodeURIComponent(vars['message']));
				}
			}
		}
	});
})(Tc.$);
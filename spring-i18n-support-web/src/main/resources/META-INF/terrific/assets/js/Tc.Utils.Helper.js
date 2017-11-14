(function ($) {

	Tc.Utils.Helper = {

		addAlert: function (type, title, message) {
			$("#alert-area").append('<div class="alert ' + type + ' fade in"  role="alert">' +
				'<button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>' +
				'<h4>' + title + '</h4><p> ' + message + ' </p></div>');
			$("#alert-area > .alert").delay(5000).fadeOut("slow", function () {
				$(this).remove();
			});
		},
		getUrlVars: function () {
			var vars = [], hash;
			var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
			for (var i = 0; i < hashes.length; i++) {
				hash = hashes[i].split('=');
				vars.push(hash[0]);
				vars[hash[0]] = hash[1];
			}
			return vars;
		}

	};

})(Tc.$);
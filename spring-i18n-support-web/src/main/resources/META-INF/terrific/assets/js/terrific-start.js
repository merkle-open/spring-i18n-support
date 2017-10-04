if (!Array.prototype.filter) {
	Array.prototype.filter = function (fun /*, thisArg */) {
		"use strict";

		if (this === void 0 || this === null)
			throw new TypeError();

		var t = Object(this);
		var len = t.length >>> 0;
		if (typeof fun != "function")
			throw new TypeError();

		var res = [];
		var thisArg = arguments.length >= 2 ? arguments[1] : void 0;
		for (var i = 0; i < len; i++) {
			if (i in t) {
				var val = t[i];
				if (fun.call(thisArg, val, i, t))
					res.push(val);
			}
		}

		return res;
	};
}

/** Initializer */
(function ($) {
	$(document).ready(function () {
		var start = function () {
			var $page = $('html');
			var application = new Tc.Application($page);
			application.registerModules();
			application.start();
		}

		var modulesToLoad = 0;
		var $modules = $('[data-module]');
		if ($modules.length > 0) {
			$modules.each(function () {
				modulesToLoad++;
				$module = $(this);
				var url = $module.data('module');
				$module.load(url, function () {
					$(this).contents().unwrap();
					modulesToLoad--;
					if (modulesToLoad <= 0) {
						start();
					}
				})
			});
		}
		else {
			start();
		}

	});
})(Tc.$);


(function ($) {
	Tc.Module.Navigation = Tc.Module.extend({

		tplNavi: null,

		on: function (callback) {
			var self = this;
			self.tplNavi = doT.template(this.$ctx.find('.tpl-navi').text());
			if (this.$ctx.data('items')) {
				var items = this.$ctx.data('items');
				$.each(items, function () {

					if (location.href.indexOf(this.url) != -1) {
						this.classes = "active";
					}
				});
				this.$ctx.find('.items').replaceWith(self.tplNavi(items));
			}
			callback();
		}


	});
})(Tc.$);
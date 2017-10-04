(function ($) {
    Tc.Module.Download = Tc.Module.extend({

        on: function (callback) {
            var self = this;

            var vars = Tc.Utils.Helper.getUrlVars();

            if (vars['downloadSuccess']) {
                if (vars['downloadSuccess'] !== 'yes') {
                    Tc.Utils.Helper.addAlert('alert-danger', 'download failed', decodeURIComponent(vars['message']));
                }
            }


            self.$ctx.find('.jsDownloadLink').each(function (index, value) {
                var link = $(this);
                link.attr('href', link.data('href') + self.getFormattedDate() + link.data('file-ending'));
            });


        }, getFormattedDate: function () {
            var d = new Date();
            return d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + '-' + d.getHours() + '-' +
                d.getMinutes() + '-' + d.getSeconds();
        }


    });
})(Tc.$);
var exec = require('cordova/exec');

exports.payment = function(payInfo, success, error) {
    exec(success, error, "alipay", "payment", [payInfo]);
};
exports.authLogin = function(payInfo, success, error) {
    exec(success, error, "alipay", "authLogin", [payInfo]);
};


var dbClient = require('./mongodbsetup');

function getUserInfo(body, callback) {
	var account = body.account;

	dbClient(function (db) {
		db.collection('userinfo').findOne({'account', account}, function(err, account) {
			if (err || !user) {
				callback(null);
				return;
			}

			callback(account);
		});
	});
}

modules.export = getUserInfo;

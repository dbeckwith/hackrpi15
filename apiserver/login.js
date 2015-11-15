var dbClient = require('./mongodbsetup');

/*
 * Add new user to database
 *
 * userName: string
 */
function login(body, callback) {
	var userName = body.userName;

	dbClient(function (db) {
		db.collection('useractivity').find({'userName': userName}).toArray(function (err, result) {
			if (!result || !result.length) {
				db.collection('useractivity').insert(
					{'userName': userName, 'runs': [], 'metric': null}, function(err, result) {
						callback();
					});
			} else {
				callback();
			}
		});
	});
}

module.exports = login;

var dbClient = require('./mongodbsetup');

/*
 * Add new user to database
 *
 * userName: string
 */
function addUser(body, callback) {
	var userName = body.userName;

	dbClient(function (db) {
		db.collection('useractivity').insert(
			{'userName': userName, 'runs': [], 'metric': null}, function(err, result) {
				callback();
			});
	});
}

module.exports = addUser;

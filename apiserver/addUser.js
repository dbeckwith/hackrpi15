var dbClient = require('./mongodbsetup');

/*
 * Add new user to database
 *
 * accountName: string
 */
function addUser(body, callback) {
	var accountName = body.accountName;

	dbClient(function (db) {
		db.collection('activitybuddy').insert(
			{'userName': accountName, 'runs': [], 'metrics': null},
			callback);
	});
}

modules.export = addUser;

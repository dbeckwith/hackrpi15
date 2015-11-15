var dbClient = require('./mongodbsetup');

/*
 * Return list of potential matches
 * 
 * user: string
 */
function getBuddyMatches(body, callback) {
	var userName = body.userName;

	dbClient(function (db) {
		db.collections('useractivity').find({'userName': userName}).toArray(function(err, userResult) {
			db.collections('useractivity').find({'userName': {$ne: userName}}).toArray(function(err, results) {
				var compareUsers = [];

				for (var i = 0; i < results.length; i++) {
					var score = getComparisonScore(userResult, results[i]);
					if (score < 5) {
						collections.push(results[i]);
					}
				}
				callback({'matches': compareUsers);
			});
		});
	});
}

function getComparisonScore(userResult, result) {
	var score = Math.abs(result.distance - userResult.distance) * 6.2E-6 * 5
	+ circularDistance(result.timeOfDay, userResult.timeOfDay) / 0.5 * 10
	+ Math.abs(result.speed - userResult.speed) * 6.2E-6 * 3600 * 5
	+ Math.abs(result.calories - userResult.calories) * 2;
}

function circularDistance(a, b) {
	if (b < a) return circularDistance(b, a);
	return Math.min(b - a, 1 - b + a);
}

modules.export = getBuddyMatches;

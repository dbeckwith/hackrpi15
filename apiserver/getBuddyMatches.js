var dbClient = require('./mongodbsetup');

/*
 * Return list of potential matches
 * 
 * user: string
 */
function getBuddyMatches(body, callback) {
	var userName = body.userName;

	dbClient(function (db) {
		db.collection('useractivity').findOne({'userName': userName}, function(err, userResult) {
			db.collection('useractivity').find({'userName': {$ne: userName}}).toArray(function(err, results) {
				var compareUsers = [];

				for (var i = 0; i < results.length; i++) {
					console.log(results[i]);
					var score = getComparisonScore(userResult.metric, results[i].metric);
					console.log(score);

					if (score < 5) {
						compareUsers.push({'userName': results[i].userName, 'weight': score});
					}
				}
				callback({'matches': compareUsers});
			});
		});
	});
}

// GMT time
function getComparisonScore(userResult, result) {
	var score = Math.abs(result.distance - userResult.distance) * 6.2E-6 * 5
	+ circularDistance(result.timeOfDay, userResult.timeOfDay) / (1000 * 60 * 60 * 24 * 0.5) * 10
	+ Math.abs(result.speed - userResult.speed) * 6.2E-6 * 3600 * 5
	+ Math.abs(result.calories - userResult.calories) * 2;
	return score;
}

function circularDistance(a, b) {
	if (b < a) return circularDistance(b, a);
	return Math.min(b - a, 1 - b + a);
}

module.exports = getBuddyMatches;

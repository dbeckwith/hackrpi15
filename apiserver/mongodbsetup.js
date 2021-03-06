// Get MongoDB client
var dbClient = require('mongodb').MongoClient;
var db;

// Connect to MongoDB
function getConnection(callback) {
	if (db) {
		callback(db);
	}
	else {
		dbClient.connect("mongodb://localhost/activitybuddy", function(err, dbconn) {
		  if (!err && dbconn) {
		    console.log("Connection made");
		    db = dbconn;
		  	callback(db);
		  }
		  else {
		  	console.log("Error connecting: " + err);
		  }
		});
	}
}

module.exports = getConnection;

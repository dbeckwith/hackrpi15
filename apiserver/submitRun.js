var dbClient = require('./mongodbsetup');
var _ = require('lodash');

/*
 * Return nothing
 * 
 * userName: string
 * runInfo: string
 */
function submitRun(body, callback) {
    var userName = body.userName;
    var runInfo = body.runInfo;

    dbClient(function (db) {
      var useractivity = db.collection('useractivity');

      useractivity.update({ 'userName': userName }, { $push: { 'runs': runInfo } });

      useractivity.findOne({ 'userName': userName }, function (err, user) {
        if (!err && user) {
          var metric = {};

          metric.timeOfDay = 0;
          metric.distance = 0;
          metric.speed = 0;
          metric.calories = 0;

          _.forEach(user.runs, function (run) {
            metric.timeOfDay += run.timestamp % (1000 * 60 * 60 * 24);
            metric.distance += run.distance;
            metric.speed += run.speed;
            metric.calories += run.calories / run.distance;
          });

          metric.timeOfDay /= user.runs.length;
          metric.distance /= user.runs.length;
          metric.speed /= user.runs.length;
          metric.calories /= user.runs.length;

          useractivity.update({ 'userName': userName }, { $set: { 'metric': metric } });

          callback();
        }
        else {
          console.log('error calculating user metric: ' + err);
        }
      });
    });
}

module.exports = submitRun;

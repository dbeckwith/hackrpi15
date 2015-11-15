var dbClient = require('./mongodbsetup');

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
      callback();
    });
}

modules.export = submitRun;

var PORT = 8080;

var express = require('express');
var bodyParser = require('body-parser');
var _ = require('lodash');

var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
  extended: true
}));

app.use(function (req, res, next) {
  console.log('API request: ' + req.method + ' ' + req.path);
  res.set('Access-Control-Allow-Origin', 'http://activitybuddy.cloudapp.net');
  next();
});

var apiEndpoints = [
  'getBuddyMatches',
  'addUser',
  'requestBuddy',
  'submitRun'
];

_.each(apiEndpoints, function (funcName) {
  var func = require('./' + funcName);
  app.post('/' + funcName, function (req, res) {
    func(req.body, res.send);
  });
});

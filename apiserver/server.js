var PORT = 8080;

var express = require('express');
var bodyParser = require('body-parser');
var getBuddyMatches = require('./getBuddyMatches');
var addUser = require('./addUser');
var requestBuddy = require('./requestBuddy');

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

app.post('/getBuddyMatches', function (req, res) {
  getBuddyMatches(req.body, res.send);
});

app.post('/addUser', function (req, res) {
  addUser(req.body,  res.send);
});

app.post('/requestBuddy', function (req, res) {
  requestBuddy(req.body,  res.send);
});
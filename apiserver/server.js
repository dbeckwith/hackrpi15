var PORT = 8080;

var express = require('express');
var bodyParser = require('body-parser');

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

app.post('/getBuddy', function (req, res) {
  console.log(req.body);
  getBuddyMatches(req.body, function (name) {
    console.log(name);
    res.send(name);
  });
});
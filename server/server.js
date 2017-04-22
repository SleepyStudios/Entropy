var express = require('express');
var cors = require('cors');
var bodyParser = require('body-parser');
var low = require('lowdb');
var app = express();

app.use(cors());
app.use(bodyParser.json());

var server = app.listen(3000, function () {
  var port = server.address().port;
  console.log("LD38 Server listening on port " + port);
});

// set defaults
var db = low('db.json');
db.defaults({ users: [], map: {} }) 
  .write();

// players
var players = [];

app.post('/player', function (req, res) {
  var ip = req.headers['x-forwarded-for'] || 
           req.connection.remoteAddress || 
           req.socket.remoteAddress ||
           req.connection.socket.remoteAddress;

  var player = {name: req.body.name.value, id: ip};
  players.push(player);
  console.log("Added player: " + req.body.name.value);

  res.json({head: "login_accept", name: player.name, id: player.id});
});

app.get('/players', function (req, res) {
  res.json({head: "players", players: players});
});
var express = require('express');
var cors = require('cors');
var bodyParser = require('body-parser');
var low = require('lowdb');
var uuidV4 = require('uuid/v4');
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

// ip
function getIP(req) {
  return req.headers['x-forwarded-for'] || 
            req.connection.remoteAddress || 
            req.socket.remoteAddress ||
            req.connection.socket.remoteAddress;
}

// player by id
function getPlayerByID(id) {
  for(var i=0; i<players.length; i++) {
    if(players[i].id==id) {
      return i;
    }
  }
  return -1;
}

// joining
app.post('/player', function (req, res) {
  var id = uuidV4();
  var ip = getIP(req);
  var x = Math.floor(20+Math.random()*620);
  var y = Math.floor(20+Math.random()*420);
  var player = {name: req.body.name.value, id: id, ip: ip, x: x, y: y};
  
  players.push(player);
  console.log("Added player: " + req.body.name.value);

  res.json({head: "login_accept", name: player.name, id: player.id, x: player.x, y: player.y});
});

// player list
app.get('/players', function (req, res) {
  res.json({head: "players", players: players});
});

// exiting
app.delete('/player', function (req, res) {
  var ip = getIP(req);

  for(var i=0; i<players.length; i++) {
    if(players[i].ip==ip) {
      console.log("Removed player: " + players[i].name);
      players.splice(i, 1);
      return res.json({head: "exit"});
    }
  }

  res.json({head: "exit"});
});

// movement
app.put('/move', function (req, res) {
  var i = getPlayerByID(req.body.id.value);
  if(i!=-1) {
    players[i].x = req.body.x.value;
    players[i].y = req.body.y.value;
  }

  res.json({head: "move"});
});
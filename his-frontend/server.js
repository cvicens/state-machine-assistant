const express = require('express');
const corser = require("corser");

const keycloakConfig = require('./config/keycloak.config');
const serverConfig = require('./config/server.config');

const app = express();
app.use(corser.create());

app.use(express.static(__dirname + '/dist/his-frontend'));

app.options("*", function (req, res) {
  // CORS
  res.writeHead(204);
  res.end();
});

// Used for App health checking
app.get('/sys/info/ping', function(req, res, next) {
  res.end('"OK"');
});

// keycloak config 
app.get('/keycloak.json', function(req, res, next) {
  res.json(keycloakConfig);
});
// server config
app.get('/server.json', function(req, res, next) {
  const fullUrl = req.protocol + '://' + req.get('host') + req.originalUrl;
  console.log(`fullUrl: ${fullUrl}`);
  res.json(serverConfig(fullUrl));
});

const port = process.env.PORT || process.env.HIS_FRONTEND_CUSTOM_PORT || 8080;
const host = process.env.IP || process.env.HIS_FRONTEND_CUSTOM_HOST || '0.0.0.0';
const server = app.listen(port, host, function() {
  console.log("App started at: " + new Date() + " on port: " + port);
});
module.exports = server;
const TOKEN = process.env.TELEGRAM_TOKEN;
const DEV_MODE = process.env.DEV_MODE || true;

const url = process.env.ROUTE_URL;

const TelegramBot = require('node-telegram-bot-api');
const express = require('express');
const json = require('body-parser');
console.log(`DEV: ${DEV_MODE}`);
// No need to pass any parameters as we will handle the updates with Express
const bot = new TelegramBot(TOKEN, {polling: DEV_MODE ? true : false});

// This informs the Telegram servers of the new webhook.
if (!DEV_MODE) {
  bot.setWebHook(`${url}/bot${TOKEN}`);
}

const app = express();

// parse the updates to JSON
app.use(json());

// Logging pings...
app.use(function (req, res, next) {
  const fullUrl = `${req.protocol}://${req.get('host')}${req.originalUrl}`;
  console.log(`>>> ${fullUrl} Request: ${JSON.stringify(req.body)}`);
  next();
});

// We are receiving updates at the route below!
app.post(`/bot${TOKEN}`, (req, res) => {
  bot.processUpdate(req.body);
  res.sendStatus(200);
});

// Relay message
// eslint-disable-next-line no-unused-vars
app.post('/new-message', function(req, res, next) {
  const { message, patientId, personalId } = req.body;
  const chatId = personalId; // TODO look personalId in DB to get chatId or die
  console.log('new-message for', message, personalId, 'with external chatId', chatId, 'and patientId', patientId);
  if (chatId) {
    bot.sendMessage(chatId, message)
      .then((result) => {
        console.log('new-message result', result);
        res.end('ok');  
      })
      .catch((error) => {
        console.error('new-message error', error);
        res.status(404).end('no chatId could be found for ID(' + personalId + ')');
      });
  } else {
    res.status(404).end('no chatId could be found for ID(' + personalId + ')');
  }
});

// Customer registration
const db = require('./lib/db');
const customersRoute = require('./lib/routes/customers');
const customers = require('./lib/api/customers');

app.use('/api', customersRoute);

db.init().then(() => {
  console.log('Database init\'d');
}).catch(err => {
  console.log(err);
});

// Start Express Server
const port = process.env.PORT || process.env.TELEGRAM_BOT_CUSTOM_PORT || 8080;
const host = process.env.IP || process.env.TELEGRAM_BOT_CUSTOM_HOST || '0.0.0.0';
app.listen(port, host, () => {
  console.log(`Telegram Bot started at: ${new Date()} on port: ${port}`);
});

app.use('/api/health', (request, response) => {
  response.send({status: 'ok'});
});

// Just to ping!
//bot.on('message', msg => {
//  bot.sendMessage(msg.chat.id, 'I am alive!');
//});

// eslint-disable-next-line no-unused-vars
bot.onText(/\/help/, function(msg, match) {
  console.log('/help handler');
  const fromId = msg.from.id;
  bot.sendMessage(fromId, 
    'I can help you sending updates to you when you\'re in a heath care facility.' +
    'To help you I have few commands:\n/help\n/start\n/signup <Personal ID>\n/update <Personal ID>\n/quit');
});

// eslint-disable-next-line no-unused-vars
bot.onText(/\/myid/, function(msg, match) {
  const fromId = msg.from.id;
  bot.sendMessage(fromId, `This is your id: ${fromId}`);
});

// Matches "/signup [whatever]"
bot.onText(/\/signup(\s*(.+)?)/, (msg, match) => {
  console.log('/signup handler');
  const chatId = msg.chat.id;
  const personalId = match[2];

  if (personalId) {
    bot.sendMessage(chatId, 'Creating a profile for you...');
    customers.create(chatId, personalId).then(result => {
      console.log(`signup result: ${result.rows[0]}`);
      bot.sendMessage(chatId, `You've been added with id: ${personalId}`);
    }).catch(err => {
      console.error(`Error while signup: ${JSON.stringify(err)}`);
      if (err.code === '23505') { // unique_violation
        bot.sendMessage(chatId, `Your user already exists, maybe you want to /update ${personalId}`);  
      } else {
        bot.sendMessage(chatId, 'There was a problem signing you up :-(');
      }
    });
  }
  else {
    bot.sendMessage(chatId, 'You didn\'t send any personal id, please try again :-)');
  }
});

// Matches "/update [whatever]"
bot.onText(/\/update(\s*(.+)?)/, (msg, match) => {
  console.log('/update handler');
  const chatId = msg.chat.id;
  const personalId = match[2];

  if (personalId) {
    bot.sendMessage(chatId, 'Updating you ID...');
    customers.update({chatId, personalId}).then(result => {
      if (result.rowCount === 0) {
        bot.sendMessage(chatId, `I'm afraid you haven't signed up yet... maybe you want to /signup ${personalId}`);
      } else {
        console.log(`update result: ${result.rows[0]}`);
        bot.sendMessage(chatId, `Your ID was updated to: ${personalId}`);
      }
    }).catch(err => {
      console.error(`Error while updating: ${JSON.stringify(err)}`);
      bot.sendMessage(chatId, 'There was a problem updating your ID :-(');
    });
  }
  else {
    bot.sendMessage(chatId, 'You didn\'t send any personal id, please try again :-)');
  }
});

bot.onText(/\/quit/, (msg) => {
  console.log('/quit handler');
  const chatId = msg.chat.id;

  bot.sendMessage(chatId, 'Deleting your profile');
  // Let's create a customer entry
  customers.remove(chatId).then(result => {
    if (result.rowCount === 0) {
      bot.sendMessage(chatId, 'I\'m afraid you never signed up ;-)');
    } else {
      console.log(`delete result: ${result.rows[0]}`);
      bot.sendMessage(chatId, 'We have succesfully deleted your profile');
    }
  }).catch(err => {
    console.error(`Error while quitting: ${JSON.stringify(err)}`);
    bot.sendMessage(chatId, 'There was a problem deleting your profile :-(, will fix it and inform you.');
  });
});

// eslint-disable-next-line no-unused-vars
bot.onText(/\/start/, function(msg, match) {
  const fromId = msg.from.id;
  bot.sendMessage(fromId, 'They call me Health Assistant Bot. ' +
    'I will help you sending updates to you when you\'re in a heath care facility.\n'+
    'Here you are a few commands I have to help you:\n/help\n/start\n/signup <Personal ID>\n/update <Personal ID>\n/quit');
});
console.log('Health Assistant Bot has started. Start conversations in your Telegram.');
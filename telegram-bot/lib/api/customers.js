'use strict';

const db = require('../db');

function find (personalId) {
  return db.query('SELECT * FROM customers WHERE personal_id = $1', [personalId]);
}

function findAll () {
  return db.query('SELECT * FROM customers');
}

function create (chatId, personalId) {
  console.log(`create with ${chatId} and ${personalId}`);
  return db.query('INSERT INTO customers (chat_id, personal_id) VALUES ($1, $2) RETURNING *', [chatId, personalId]);
}

function update (options = {}) {
  return db.query('UPDATE customers SET personal_id = $2 WHERE chat_id = $1 RETURNING *', [options.chatId, options.personalId]);
}

function remove (chatId) {
  return db.query('DELETE FROM customers WHERE chat_id = $1', [chatId]);
}

module.exports = {
  find,
  findAll,
  create,
  update,
  remove
};

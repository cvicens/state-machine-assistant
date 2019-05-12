'use strict';

const express = require('express');
/* eslint new-cap: "warn" */
const router = express.Router();

const validations = require('../validations');
const customers = require('../api/customers');

router.get('/customers/:personalId', (request, response) => {
  const {personalId} = request.params;
  customers.find(personalId).then(result => {
    if (result.rowCount === 0) {
      response.status(404);
      return response.send(`Item ${personalId} not found`);
    }
    return response.send(result.rows[0]);
  }).catch(() => {
    response.sendStatus(400);
  });
});

router.get('/customers', (request, response) => {
  customers.findAll().then(results => {
    response.send(results.rows);
  }).catch(() => {
    response.sendStatus(400);
  });
});

router.post('/customers', validations.validateCreateRequest, (request, response) => {
  const {chatId, personalId} = request.body;
  return customers.create(chatId, personalId).then(result => {
    response.status(201);
    return response.send(result.rows[0]);
  }).catch(err => {
    response.status(400);
    response.send(err);
  });
});

router.put('/customers/:chatId', validations.validateUpdateRequest, (request, response) => {
  const {personalId} = request.body;
  const chatId = parseInt(request.params.chatId);
  customers.update({chatId, personalId}).then(result => {
    if (result.rowCount === 0) {
      response.status(404);
      return response.send(`Unknown item ${chatId}`);
    }
    response.status(200);
    return response.send(result.rows[0]);
  }).catch(err => {
    response.status(400);
    response.send(err);
  });
});

router.delete('/customers/:chatId', (request, response) => {
  const {chatId} = request.params;
  customers.remove(chatId).then(result => {
    if (result.rowCount === 0) {
      response.status(404);
      return response.send(`Unknown item ${chatId}`);
    }
    return response.sendStatus(204);
  }).catch(err => {
    response.status(400);
    response.send(err);
  });
});

module.exports = router;

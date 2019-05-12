'use strict';

function validateCreateRequest (request, response, next) {
  if (Object.keys(request.body).length === 0) {
    response.status(415);
    return response.send('Invalid payload!');
  }
  // No need to check for no body, express will make body an empty object
  const {chat_id, personal_id} = request.body;

  if (!personal_id) {
    response.status(422);
    return response.send('The personal_id is required!');
  }

  if (chat_id === null || isNaN(chat_id) || chat_id <= 0) {
    response.status(422);
    return response.send('The chat_id must be greater than 0!');
  }

  next();
}

function validateUpdateRequest(request, response, next) {
  if (Object.keys(request.body).length === 0) {
    response.status(415);
    return response.send('Invalid payload!');
  }
  // No need to check for no body, express will make body an empty object
  const {personal_id, chat_id} = request.body;

  if (!personal_id) {
    response.status(422);
    return response.send('Data should have at least a personal_id value!');
  }

  if (chat_id && chat_id !== request.params.chat_id) {
    response.status(422);
    return response.send('chat_id was invalidly set on request.');
  }

  next();
}

module.exports = {
  validateCreateRequest,
  validateUpdateRequest
};

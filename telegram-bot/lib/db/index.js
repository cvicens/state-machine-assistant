'use strict';

const { Pool } = require('pg');

const serviceHost = process.env.DB_HOST || 'localhost';
const user = process.env.DB_USERNAME || 'luke';
const password = process.env.DB_PASSWORD || 'secret';
const connectionString = `postgresql://${user}:${password}@${serviceHost}:5432/my_data`;

const pool = new Pool({
  connectionString
});


// -- Create the customers table if not present
const initScript = `CREATE TABLE IF NOT EXISTS customers (
  chat_id      NUMERIC(12) NOT NULL PRIMARY KEY,
  personal_id  VARCHAR(40) NOT NULL
);
`;

module.exports = {
  query: (text, params) => {
    return pool.query(text, params);
  },
  init: () => {
    return pool.query(initScript);
  }
};

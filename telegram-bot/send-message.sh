#!/bin/sh
CHAT_ID=$1
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"message":"This is your message from XYZ"}' \
  http://localhost:8080/new-message/${CHAT_ID}
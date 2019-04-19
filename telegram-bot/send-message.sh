#!/bin/sh
CHAT_ID=$1
URL=${2:-http://localhost:8080}

curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"message":"This is your message from XYZ"}' \
  ${URL}/new-message/${CHAT_ID}
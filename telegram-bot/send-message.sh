#!/bin/sh

if [ "$#" -ne 2 ]; then
    echo "$0 <personal_id> <service_url>"
    exit 1
fi

PERSONAL_ID=$1
URL=$2

curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"personalId":"'"${PERSONAL_ID}"'","patientId":"PATID1234","message":"Patient JOHN SMITH with ID(PATID1234) has been admitted (ZZZ)"}' \
  ${URL}/new-message



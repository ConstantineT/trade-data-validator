#!/bin/bash
if [ $# -eq 0 ]
  then
    echo "Please provide payload file location as a first argument"
    exit 1
fi

ACCEPT="Content-Type:application/json"
CONTENT_TYPE="Content-Type:application/json"
PAYLOAD_FILE=@$1

COMMAND="curl -s -H $ACCEPT -H $CONTENT_TYPE -X POST -d $PAYLOAD_FILE http://localhost:8080/trades/validation"

RESPONSE=`$COMMAND`
echo -e "Response:\n$RESPONSE"
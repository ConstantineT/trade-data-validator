#!/bin/bash
if [ $# -lt 2 ]
  then
    echo "Please provide user as a first argument and password as a second argument"
    exit 1
fi

COMMAND="curl -s --user $1:$2 -X POST http://localhost:8080/shutdown"

RESPONSE=`$COMMAND`
echo -e "Response:\n$RESPONSE"